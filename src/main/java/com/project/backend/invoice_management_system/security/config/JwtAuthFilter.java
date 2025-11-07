package com.project.backend.invoice_management_system.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        final String userEmail;

        // 1. Try to get token from Authorization header first (for backward compatibility)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // "Bearer " is 7 chars
        } 
        // 2. If not in header, try to get token from cookie
        else {
            jwt = getTokenFromCookie(request);
        }

        // 3. If no token found, pass to next filter
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 4. Extract the email from the token
            userEmail = jwtUtils.extractUsername(jwt);

            // 5. Check if the user is already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Load the user from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 7. Validate the token
                if (jwtUtils.isTokenValid(jwt, userDetails)) {
                    // 8. If valid, create an authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // We don't need credentials
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9. Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Handle exceptions like expired or invalid tokens
            // For now, we just pass the request on without authentication
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Helper method to extract JWT token from cookies.
     * @param request HttpServletRequest
     * @return JWT token string or null if not found
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("authToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}