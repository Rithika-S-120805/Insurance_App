# Role-Based Access Control (RBAC) - 403 Error Fix

## Problem Analysis
The 403 Forbidden errors were caused by **improper security configuration**:

1. **SecurityConfig**: Endpoints were set to `.permitAll()` - bypassing authentication entirely
2. **Controllers**: Had `@PreAuthorize` annotations expecting authenticated users with roles
3. **Mismatch**: No authentication context → role checks fail → 403 error

## Root Causes Fixed

### Issue 1: permitAll() Bypasses Authentication
**File**: `SecurityConfig.java`
**Problem**: 
```java
.requestMatchers("/api/users", "/api/users/**", "/api/policies", "/api/policies/**", "/api/claims", "/api/claims/**").permitAll()
```
This allowed unauthenticated access, but controllers expected authenticated users.

**Fix**: Removed these endpoints from `.permitAll()` - now they fall through to `.anyRequest().authenticated()`, forcing authentication before hitting controller methods.

### Issue 2: Unprotected Admin Dashboard
**File**: `DashboardController.java` (Line 58)
**Problem**: 
```java
@GetMapping("/admin")
public ResponseEntity<?> getAdminDashboard() {
```
No role protection despite being a sensitive admin endpoint.

**Fix**: Added `@PreAuthorize("hasRole('ADMIN')")`

### Issue 3: Missing Authentication Checks
**Files**: `PolicyController.java` & `ClaimController.java`
**Problem**: `getAllPolicies()` and `getAllClaims()` had no `@PreAuthorize` annotation
**Fix**: Added `@PreAuthorize("isAuthenticated()")`

---

## Complete Fixed Code

### 1. SecurityConfig.java
```java
package com.kgisl.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kgisl.pos.security.TokenAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, TokenAuthenticationFilter tokenAuthenticationFilter, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/favicon.ico", "/static/**").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/test").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:4200",
            "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 2. TokenAuthenticationFilter.java (Already correct, showing for reference)
```java
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
                        System.out.println("[AUTH-FILTER] ✅ Authentication set for user: " + username + " with authority: " + role);
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
```

### 3. DashboardController.java (Key update)
```java
/**
 * ADMIN DASHBOARD - Get complete system analytics
 * Only ADMIN can access
 */
@PreAuthorize("hasRole('ADMIN')")  // ← ADDED
@GetMapping("/admin")
public ResponseEntity<?> getAdminDashboard() {
    // ... rest of the method
}
```

### 4. PolicyController.java (Key updates)
```java
// READ ALL - Different results based on role
@PreAuthorize("isAuthenticated()")  // ← ADDED
@GetMapping
public ResponseEntity<?> getAllPolicies() {
    // ... rest of the method
}

// TEST ENDPOINT - Returns ALL policies (authenticated users only)
@PreAuthorize("hasRole('ADMIN')")  // ← CHANGED
@GetMapping("/test/all-policies")
public ResponseEntity<?> getAllPoliciesForTest() {
    // ... rest of the method
}
```

### 5. ClaimController.java (Key update)
```java
// READ ALL - Different results based on role
@PreAuthorize("isAuthenticated()")  // ← ADDED
@GetMapping
public ResponseEntity<?> getAllClaims() {
    // ... rest of the method
}
```

---

## Flow Diagram: How It Works Now

```
Client Request with JWT Token
         ↓
SecurityConfig permits public endpoints?
    ↓ NO → 
TokenAuthenticationFilter extracts token
         ↓
Token valid?
    ↓ YES → 
Extract username & role, set SecurityContext
         ↓
Request reaches Controller
         ↓
@PreAuthorize checks role in SecurityContext
    ✅ Match → Proceeds to method
    ❌ No match → 403 Forbidden
```

---

## Testing the Fix

### 1. Login to get token
```bash
POST /api/auth/login
{
  "email": "admin@example.com",
  "password": "password"
}

Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "role": "ADMIN"
}
```

### 2. Use token in requests
```bash
GET /api/dashboard/admin
Header: Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

✅ Works (ADMIN role required)
```

### 3. Without token
```bash
GET /api/dashboard/admin

❌ 403 Forbidden (no token provided)
```

---

## Summary of Changes

| File | Change | Reason |
|------|--------|--------|
| SecurityConfig.java | Removed `.permitAll()` from protected endpoints | Force authentication via token filter |
| DashboardController.java | Added `@PreAuthorize("hasRole('ADMIN')")` | Protect admin dashboard |
| PolicyController.java | Added `@PreAuthorize("isAuthenticated()")` to `getAllPolicies()` | Ensure auth context exists |
| PolicyController.java | Added `@PreAuthorize("hasRole('ADMIN')")` to test endpoint | Restrict test endpoint |
| ClaimController.java | Added `@PreAuthorize("isAuthenticated()")` to `getAllClaims()` | Ensure auth context exists |

---

## Common Issues & Solutions

### Still getting 403?
1. **Check token in header**: `Authorization: Bearer <token>`
2. **Verify token role**: Decode at jwt.io and check the `role` claim
3. **Check token expiry**: Tokens expire after 24 hours by default
4. **Frontend CORS**: Ensure frontend sends Authorization header

### Getting 401 instead of 403?
- Token validation failed
- Check token signature matches `app.jwt.secret`
- Verify token hasn't expired

