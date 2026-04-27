package com.kgisl.pos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    public TokenAuthenticationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);
            logger.debug("[AUTH-FILTER] Request URI: " + request.getRequestURI());
            logger.debug("[AUTH-FILTER] Token present: " + (token != null));

            if (token != null) {
                logger.debug("[AUTH-FILTER] Token found, validating...");
                boolean isValid = tokenProvider.validateToken(token);
                logger.debug("[AUTH-FILTER] Token valid: " + isValid);
                
                if (isValid) {
                    String username = tokenProvider.getUsernameFromToken(token);
                    String role = tokenProvider.getRoleFromToken(token);
                    
                    logger.debug("[AUTH-FILTER] Username from token: " + username);
                    logger.debug("[AUTH-FILTER] Role from token: " + role);

                    // Ensure role has ROLE_ prefix
                    if (role != null && !role.isEmpty()) {
                        if (!role.startsWith("ROLE_")) {
                            role = "ROLE_" + role;
                        }
                        
                        logger.debug("[AUTH-FILTER] Final role with prefix: " + role);

                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(username, null, 
                                Collections.singletonList(authority));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        logger.debug("[AUTH-FILTER] ✅ Authentication set for user: " + username + " with authority: " + role);
                    } else {
                        logger.warn("[AUTH-FILTER] ⚠️ Role is null or empty for token of user: " + username);
                    }
                } else {
                    logger.warn("[AUTH-FILTER] ⚠️ Token validation failed");
                }
            } else {
                logger.debug("[AUTH-FILTER] No token found in request");
            }
        } catch (Exception ex) {
            logger.error("[AUTH-FILTER] ❌ Error during authentication filter", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
