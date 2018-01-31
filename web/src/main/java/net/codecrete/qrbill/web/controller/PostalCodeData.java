//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipInputStream;

public class PostalCodeData {

    private static final int MAX_SUGGESTED_ITEMS = 20;
    private static final List<PostalCode> EMPTY_RESULT = Collections.emptyList();

    private PostalCode[] sortedByPostalCode;
    private PostalCode[] sortedByTown;


    private void load() {
        List<PostalCode> postalCodeList = new ArrayList<>();
        try {
            URL u = new URL("https://data.geo.admin.ch/ch.swisstopo-vd.ortschaftenverzeichnis_plz/PLZO_CSV_LV03.zip");
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.connect();
            try (InputStream in = connection.getInputStream();
                 ZipInputStream zis = new ZipInputStream(in)) {
                zis.getNextEntry();

                try (InputStreamReader reader = new InputStreamReader(zis, StandardCharsets.UTF_8);
                     BufferedReader lineReader = new BufferedReader(reader)) {

                    lineReader.readLine();

                    while (true) {
                        String line = lineReader.readLine();
                        if (line == null)
                            break;

                        processLine(line, postalCodeList);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupSortedArrays(postalCodeList);
    }

    private void processLine(String line, List<PostalCode> postalCodeList) {
        String[] values = line.split(";");
        if (values.length >= 2) {
            PostalCode pc = new PostalCode(values[1], values[0]);
            postalCodeList.add(pc);
        }
    }

    private void setupSortedArrays(List<PostalCode> postalCodeList) {
        sortedByPostalCode = new PostalCode[postalCodeList.size()];
        sortedByPostalCode = postalCodeList.toArray(sortedByPostalCode);
        Arrays.sort(sortedByPostalCode, Comparator.comparing(pc -> pc.postalCode));

        sortedByTown = new PostalCode[postalCodeList.size()];
        sortedByTown = postalCodeList.toArray(sortedByTown);
        Arrays.sort(sortedByTown, Comparator.comparing(pc -> pc.townLowercase));
    }

    public List<PostalCode> suggestPostalCodes(String country, String substring) {

        if (country != null && country.length() != 0 && !country.equals("CH"))
            return EMPTY_RESULT;

        if (sortedByPostalCode == null)
            load();

        if (substring == null || substring.length() == 0)
            return EMPTY_RESULT;

        substring = substring.trim();
        if (isNumeric(substring))
            return getPostalCodeMatches(substring);
        else
            return getTownMatches(substring);
    }

    private List<PostalCode> getPostalCodeMatches(String substring) {
        // Search for postal codes starting with the substring first
        int index = binarySearchForPostalCode(sortedByPostalCode, substring);
        List<PostalCode> result = new ArrayList<>();
        int arrayLen = sortedByPostalCode.length;
        while (index < arrayLen && sortedByPostalCode[index].postalCode.startsWith(substring)
                && result.size() < MAX_SUGGESTED_ITEMS) {
            result.add(sortedByPostalCode[index]);
            index++;
        }

        if (substring.length() <= 2 || result.size() >= 6)
            return result;

        // Search for postal codes containing the substring
        for (PostalCode pc : sortedByPostalCode) {
            if (pc.postalCode.contains(substring) && !result.contains(pc)) {
                result.add(pc);
                if (result.size() >= MAX_SUGGESTED_ITEMS)
                    break;
            }
        }

        return result;
    }

    private List<PostalCode> getTownMatches(String substring) {
        substring = substring.toLowerCase(Locale.FRENCH);

        // Search for towns starting with the substring first
        int index = binarySearchForTown(sortedByTown, substring);
        List<PostalCode> result = new ArrayList<>();
        int arrayLen = sortedByTown.length;
        while (index < arrayLen && sortedByTown[index].townLowercase.startsWith(substring)
                && result.size() < MAX_SUGGESTED_ITEMS) {
            result.add(sortedByTown[index]);
            index++;
        }

        if (substring.length() <= 2 || result.size() >= 6)
            return result;

        // Search for postal codes containing the substring
        for (PostalCode pc : sortedByTown) {
            if (pc.townLowercase.contains(substring) && !result.contains(pc)) {
                result.add(pc);
                if (result.size() >= MAX_SUGGESTED_ITEMS)
                    break;
            }
        }

        return result;
    }

    private static boolean isNumeric(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (ch < '0' || ch > '9')
                return false;
        }
        return true;
    }

    /**
     * Searches the array for the specified postal code.
     * <p>
     *     In contrast to {@link Arrays#binarySearch(Object[], Object)}, the first matching element
     *     is returned if the the array contains several matching ones.
     * </p>
     * @param a the array to be searched (must be sorted in ascending order)
     * @param postalCode the postal code to search for
     * @return the index of the first matching element (if found) or the index where the element would need to
     * be inserted to retain the order (if not found)
     */
    private static int binarySearchForPostalCode(PostalCode[] a, String postalCode) {
        int lower = 0;
        int upper = a.length;

        while (lower < upper) {
            int mid = (lower + upper) >>> 1;
            String midValue = a[mid].postalCode;
            int cmp = midValue.compareTo(postalCode);

            if (cmp < 0)
                lower = mid + 1;
            else
                upper = mid;
        }

        return lower;
    }


    /**
     * Searches the array for the specified town.
     * <p>
     *     In contrast to {@link Arrays#binarySearch(Object[], Object)}, the first matching element
     *     is returned if the the array contains several matching ones.
     * </p>
     * @param a the array to be searched (must be sorted in ascending order)
     * @param town the towb to search for
     * @return the index of the first matching element (if found) or the index where the element would need to
     * be inserted to retain the order (if not found)
     */
    private static int binarySearchForTown(PostalCode[] a, String town) {
        int lower = 0;
        int upper = a.length;

        while (lower < upper) {
            int mid = (lower + upper) >>> 1;
            String midValue = a[mid].townLowercase;
            int cmp = midValue.compareTo(town);

            if (cmp < 0)
                lower = mid + 1;
            else
                upper = mid;
        }

        return lower;
    }


    public static class PostalCode {

        public String postalCode;
        public String town;
        String townLowercase;

        PostalCode(String postalCode, String town) {
            this.postalCode = postalCode;
            this.town = town;
            this.townLowercase = town.toLowerCase(Locale.FRENCH);
        }
    }
}
