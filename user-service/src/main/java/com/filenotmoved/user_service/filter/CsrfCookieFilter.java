package com.filenotmoved.user_service.filter;

import java.io.IOException;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter that ensures the CSRF token is loaded and the cookie is set on every
 * response.
 * Spring Security's CookieCsrfTokenRepository defers token generation by
 * default,
 * so this filter forces the token to be rendered into the response cookie.
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            // Force the token to be rendered so the cookie is set
            csrfToken.getToken();
        }
        filterChain.doFilter(request, response);
    }

}
