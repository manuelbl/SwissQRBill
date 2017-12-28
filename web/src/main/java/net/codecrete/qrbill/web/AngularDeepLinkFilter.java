//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Poor man's filter to support Angular deep links.
 * <p>
 *     The filter tries to tell API requests and static resource request apart from Angular deep link request.
 *     Angular deep link requests are redirected to "/" (which is internally translated to /index.html).
 *     So for Angular deep links, index.html will be served and Angular's route configuration displays
 *     the correct page.
 * </p>
 * <p>
 *     You might need to fine-tune {@link #isApiCall(String)} and {@link #isStaticResource(String)}.
 * </p>
 */
@Component
public class AngularDeepLinkFilter implements Filter {

    private static final String FILTER_MARKER_KEY = "net.codecrete.qrbill.ANGULAR_DEEP_LINK_FILTER_MARKER";

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest.getAttribute(FILTER_MARKER_KEY) == null
                && servletRequest instanceof HttpServletRequest) {
            String path = urlPathHelper.getPathWithinApplication((HttpServletRequest)servletRequest);
            if (!path.equals("/") && !isApiCall(path) && !isStaticResource(path)) {
                forward(servletRequest, servletResponse);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // nothing to do
    }


    private void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        // Mark request to prevent recursion
        servletRequest.setAttribute(FILTER_MARKER_KEY, 1);
        servletRequest.getRequestDispatcher("/").forward(servletRequest, servletResponse);
    }


    private static boolean isApiCall(String uri) {
        return uri.startsWith("/api/");
    }

    private static boolean isStaticResource(String uri) {
        int dot = uri.lastIndexOf('.');
        return dot > 0 && uri.length() - dot <= 11 && uri.indexOf('/', dot) == -1;
    }
}
