package com.fintara.security;

import com.fintara.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    JwtBlacklist jwtBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // ✅ Skip JWT Filter untuk endpoint public
        if (path.startsWith("api/v1/plafonds/all") ||
                path.startsWith("api/v1/auth/") ||
                path.startsWith("api/v1/loan-requests/loan-simulate") ||
                path.startsWith("api/v1/loan-requests/loan-web-simulate") ||
                path.startsWith("api/v1/profilecustomer/upload-ktp") ||
                path.startsWith("api/v1/profilecustomer/upload-selfie-ktp") ||
                path.startsWith("api/v1/cloudinary/") ||
                path.startsWith("api/v1/notifications/") ||
                path.startsWith("api/v1/repayments/") ||
                path.startsWith("/download/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = parseJwt(request);
            if (StringUtils.hasText(jwt)) {
                // 🔥 Cek blacklist
                if (jwtBlacklist.isBlacklisted(jwt)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token sudah tidak valid (blacklisted)");
                    return;
                }

                String username = jwtUtils.getUsername(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            logger.error("Access Denied: " + ex.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: Access Denied");
        } catch (Exception ex) {
            logger.error("Internal Server Error in JWT Filter: " + ex.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }



    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer")){
            return headerAuth.substring(7);
        }
        return null;
    }
}