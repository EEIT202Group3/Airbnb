package com.EEITG3.Airbnb.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//這個class的功能在於...
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 最外層
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String MDC_REQUEST_ID = "requestId";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";

    // 若你的服務**確定**在受信任的 Gateway 後面，改成 true 允許沿用上游的 ID
    private static final boolean TRUST_INBOUND_REQUEST_ID = false;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String requestId = null;
        if (TRUST_INBOUND_REQUEST_ID) {
            String inbound = request.getHeader(HEADER_REQUEST_ID);
            if (inbound != null && !inbound.isBlank() && isSafe(inbound)) {
                requestId = inbound.trim();
            }
        }
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_REQUEST_ID, requestId);
        response.setHeader(HEADER_REQUEST_ID, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            // 最外層清理全部 MDC，避免 ThreadLocal 汙染
            MDC.clear();
        }
    }

    // 簡單的安全檢查，避免換行/過長造成日誌注入或資源浪費
    private boolean isSafe(String s) {
        if (s.length() > 128) return false;
        // 僅允許常見字元（UUID/ULID/字母數字/橫線/底線）
        return s.matches("[A-Za-z0-9-_]+");
    }
}
