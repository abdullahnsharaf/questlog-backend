package com.questlog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // 2. If no token or wrong format, skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (remove "Bearer " prefix)
        final String jwt = authHeader.substring(7);
        final String email = jwtUtil.extractEmail(jwt);

        // 4. If we got an email and user isn't already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Load the user from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 6. Validate the token
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {

                // 7. Create authentication object and set it in Spring Security context
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}