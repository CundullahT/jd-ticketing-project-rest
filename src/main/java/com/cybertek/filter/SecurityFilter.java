package com.cybertek.filter;

import com.cybertek.entity.User;
import com.cybertek.service.SecurityService;
import com.cybertek.util.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Service
public class SecurityFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final SecurityService securityService;

    public SecurityFilter(JWTUtil jwtUtil, SecurityService securityService) {
        this.jwtUtil = jwtUtil;
        this.securityService = securityService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authorizationHeader != null) {
            token = authorizationHeader.replace("Bearer","");
            username = jwtUtil.extractUsername(token);
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = securityService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails) && checkIfUserIsValid(username)) {

                UsernamePasswordAuthenticationToken currentUser =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                currentUser
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(currentUser);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);



    }

    private boolean checkIfUserIsValid(String username) throws AccessDeniedException {
        User currentUser = securityService.loadUser(username);
        return currentUser != null && currentUser.getEnabled();
    }
}