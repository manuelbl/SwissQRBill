//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import java.io.Serializable;
import java.util.Objects;

/**
 * Alternative payment scheme instructions
 */
public class AlternativeScheme implements Serializable {

    private static final long serialVersionUID = -8304082204378228870L;

    /** Scheme name */
    private String name;
    /** Payment instruction */
    private String instruction;

    /**
     * Creates a new instance
     */
    public AlternativeScheme() {

    }

    /**
     * Creates an instance and sets name and instruction.
     *
     * @param name        scheme name
     * @param instruction payment instruction
     */
    public AlternativeScheme(String name, String instruction) {
        this.name = name;
        this.instruction = instruction;
    }

    /**
     * Get the payment scheme name
     *
     * @return scheme name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the payment scheme name
     *
     * @param name scheme name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the payment instruction for a given bill.
     * <p>
     * The instruction consists of a two letter abbreviation for the scheme, a separator characters
     * and a sequence of parameters (separated by the character at index 2).
     * </p>
     *
     * @return instruction
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * Gets the payment instruction for a given bill
     * <p>
     * The instruction consists of a two letter abbreviation for the scheme, a separator characters
     * and a sequence of parameters (separated by the character at index 2).
     * </p>
     *
     * @param instruction instruction
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlternativeScheme that = (AlternativeScheme) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(instruction, that.instruction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return Objects.hash(name, instruction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AlternativeScheme{" +
                "name='" + name + '\'' +
                ", instruction='" + instruction + '\'' +
                '}';
    }
}
