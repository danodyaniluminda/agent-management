package com.biapay.agentmanagement.filter;


import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class MDCFilter extends OncePerRequestFilter {
    public static final String REQUEST_ID = "X-Request-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String requestId;

            if (httpServletRequest.getHeader(REQUEST_ID) == null){
                requestId = httpServletRequest.getHeader(REQUEST_ID);
            }
            else{
                requestId = generateRequestId();
            }

            MDC.put(REQUEST_ID, requestId);
            httpServletResponse.setHeader(REQUEST_ID, requestId);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            MDC.clear();
        }
    }

    private static String generateRequestId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
