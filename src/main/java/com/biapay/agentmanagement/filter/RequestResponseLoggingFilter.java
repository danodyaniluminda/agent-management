//package com.biapay.agentmanagement.filter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Order(2)
//@Slf4j
//public class RequestResponseLoggingFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) request;
//        HttpServletResponse res = (HttpServletResponse) response;
//        log.info("Logging Request  {} : {}", req.getMethod(), req.getRequestURI());
//        chain.doFilter(request, response);
//        log.info("Logging Response :{}", res.getContentType());
//    }
//    // other methods
//}