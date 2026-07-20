package com.example.sdfguanlixt.config;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.common.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_CLAIMS = "jwtClaims";

    private final JwtUtil jwtUtil;

    public JwtAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BusinessException(401, BizConstants.MSG_UNAUTHORIZED);
        }

        String token = auth.substring(7).trim();
        try {
            Claims claims = jwtUtil.parse(token);
            request.setAttribute(ATTR_CLAIMS, claims);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(401, BizConstants.MSG_UNAUTHORIZED);
        }
    }
}
