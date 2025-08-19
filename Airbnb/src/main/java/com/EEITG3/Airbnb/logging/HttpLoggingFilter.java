package com.EEITG3.Airbnb.logging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//這個class的功能是...
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10) // 靠後，可拿到 response status
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        // 只在「已登入且非 anonymousUser」時設定 userId
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
            String principalStr = String.valueOf(auth.getPrincipal());
            if (!"anonymousUser".equals(principalStr)) {
                MDC.put("userId", auth.getName()); // 也可改成你的 domain userId
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;

            // INFO：純摘要（method/uri/status/cost）
            log.info("{} {} status={} cost={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    cost
            );

            // DEBUG：需要時再看 UA / IP
            if (log.isDebugEnabled()) {
                String ua = oneLine(request.getHeader("User-Agent"));
                String ip = clientIp(request);
                log.debug("ua=\"{}\" ip={}", ua, ip);
            }
        }
    }

    private static String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            return (comma > 0 ? xff.substring(0, comma) : xff).trim();
        }
        String realIp = req.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) return realIp.trim();
        return req.getRemoteAddr();
    }

    private static String oneLine(String s) {
        if (s == null) return "";
        return s.replaceAll("[\\r\\n]+", " ");
    }
}
