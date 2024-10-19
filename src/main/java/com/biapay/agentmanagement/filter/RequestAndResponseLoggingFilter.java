package com.biapay.agentmanagement.filter;

import com.biapay.agentmanagement.domain.RequestResponseLog;
import com.biapay.agentmanagement.repository.RequestResponseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Slf4j
@Order(2)
public class RequestAndResponseLoggingFilter extends OncePerRequestFilter {

  private static final Map<String, String> requestDataMap = new HashMap<>();
  private static final List<MediaType> VISIBLE_TYPES =
      Arrays.asList(
          MediaType.valueOf("text/*"),
          MediaType.APPLICATION_FORM_URLENCODED,
          MediaType.APPLICATION_JSON,
          MediaType.APPLICATION_XML,
          MediaType.valueOf("application/*+json"),
          MediaType.valueOf("application/*+xml"),
          MediaType.MULTIPART_FORM_DATA);
  /**
   * List of HTTP headers whose values should not be logged.
   */
  private static final List<String> SENSITIVE_HEADERS =
      Arrays.asList("authorization", "proxy-authorization");
  private final RequestResponseRepository requestResponseRepository;

  public RequestAndResponseLoggingFilter(RequestResponseRepository requestResponseRepository) {
    this.requestResponseRepository = requestResponseRepository;
    requestDataMap.clear();
  }

  private static void logRequestHeader(ContentCachingRequestWrapper request, String prefix) {
    String queryString = request.getQueryString();
    if (queryString == null) {
      log.info(String.format("%s %s %s", prefix, request.getMethod(), request.getRequestURI()));
    } else {
      log.info(String.format("%s %s %s?%s", prefix, request.getMethod(), request.getRequestURI(),
          queryString));
    }
    log.info(String.format("%s Request IP: %s", prefix, getIpAddress(request)));

    try {
      String headers = getRequestHeaders(request);

      log.info(String.format("%s Request Headers: %s", prefix, headers));
    } catch (Exception ex) {
      log.error(String.format("%s Error: %s", prefix, "Failed to parse request headers!"));
    }
  }

  private static void logRequestBody(ContentCachingRequestWrapper request, String prefix) {
    byte[] content = request.getContentAsByteArray();
    if (content.length > 0) {
      logContent(
          content, request.getContentType(), request.getCharacterEncoding(), prefix, "REQUEST");
    }
  }

  private static void logResponse(ContentCachingResponseWrapper response, String prefix) {
    int status = response.getStatus();
    log.info(
        String.format("%s %s %s", prefix, status, HttpStatus.valueOf(status).getReasonPhrase()));

    try {
      String headers = getResponseHeaders(response);

      log.info(String.format("%s Response Headers: %s", prefix, headers));
    } catch (Exception ex) {
      log.error(String.format("%s Error: %s", prefix, "Failed to parse response headers!"));
    }

    byte[] content = response.getContentAsByteArray();
    if (content.length > 0) {
      logContent(content, response.getContentType(), response.getCharacterEncoding(), prefix,
          "RESPONSE");
    }
  }

  private static void logContent(
      byte[] content, String contentType, String contentEncoding, String prefix, String logFor) {
    try {
      String contentString = parsePayload(content, contentType, contentEncoding);

      if (logFor.equalsIgnoreCase("REQUEST")) {
        log.info(String.format("%s Request Payload: %s", prefix, contentString));
      } else if (logFor.equalsIgnoreCase("RESPONSE")) {
        log.info(String.format("%s Response Payload: %s", prefix, contentString));
      }
    } catch (UnsupportedEncodingException e) {
      log.info(String.format("%s [%d bytes content]", prefix, content.length));
    }
  }

  /**
   * Determine if a given header name should have its value logged.
   *
   * @param headerName HTTP header name.
   * @return True if the header is sensitive (i.e. its value should <b>not</b> be logged).
   */
  private static boolean isSensitiveHeader(String headerName) {
    return SENSITIVE_HEADERS.contains(headerName.toLowerCase());
  }

  private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
    if (request instanceof ContentCachingRequestWrapper) {
      return (ContentCachingRequestWrapper) request;
    } else {
      return new ContentCachingRequestWrapper(request);
    }
  }

  private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
    if (response instanceof ContentCachingResponseWrapper) {
      return (ContentCachingResponseWrapper) response;
    } else {
      return new ContentCachingResponseWrapper(response);
    }
  }

  private static String getResponseHeaders(ContentCachingResponseWrapper response)
      throws JsonProcessingException {
    Map<String, String> headers = new HashMap<>();
    response.getHeaderNames()
        .forEach(headerName ->
            response.getHeaders(headerName)
                .forEach(headerValue ->
                {
                  if (isSensitiveHeader(headerName)) {
                    headers.put(headerName, "*******");
                  } else {
                    headers.put(headerName, headerValue);
                  }
                }));

    return new ObjectMapper().writeValueAsString(headers);
  }

  private static String getRequestHeaders(ContentCachingRequestWrapper request)
      throws JsonProcessingException {
    Map<String, String> headers = new HashMap<>();

    Collections.list(request.getHeaderNames())
        .forEach(headerName ->
            Collections.list(request.getHeaders(headerName))
                .forEach(headerValue -> {
                  if (isSensitiveHeader(headerName)) {
                    headers.put(headerName, "*******");
                  } else {
                    headers.put(headerName, headerValue);
                  }
                }));

    return new ObjectMapper().writeValueAsString(headers);
  }

  private static String parsePayload(byte[] content, String contentType, String contentEncoding)
      throws UnsupportedEncodingException {
    MediaType mediaType = MediaType.valueOf(contentType);
    boolean visible = VISIBLE_TYPES.stream()
        .anyMatch(visibleType -> visibleType.includes(mediaType));
    if (visible) {
      return new String(content, contentEncoding);
    }
    return null;
  }

  private static String getIpAddress(ContentCachingRequestWrapper request) {
    // Try to get the client IP address from the X-Forwarded-For header, which may contain a comma-separated list of IPs
    String ipAddress = request.getHeader("X-Forwarded-For");

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      // If X-Forwarded-For is not present, fallback to the actual remote IP
      ipAddress = request.getRemoteAddr();
    }

    // If there is a comma-separated list, use the first IP in the list
    if (ipAddress != null && ipAddress.contains(",")) {
      ipAddress = ipAddress.split(",")[0].trim();
    }

    return ipAddress;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (isAsyncDispatch(request)) {
      filterChain.doFilter(request, response);
    } else {
      doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
    }
  }

  protected void doFilterWrapped(
      ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response,
      FilterChain filterChain)
      throws ServletException, IOException {

    try {
      beforeRequest(request, response);
      filterChain.doFilter(request, response);
    } finally {
      afterRequest(request, response);
      response.copyBodyToResponse();

      requestDataMap.clear();
    }
  }

  protected void beforeRequest(
      ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
    log.info("-- REQUEST for {} --", MDC.get("requestId"));
    logRequestHeader(request, request.getRemoteAddr() + "|>");
    logRequestBody(request, request.getRemoteAddr() + "|<");
  }

  protected void afterRequest(
      ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
//    logRequestBody(request, request.getRemoteAddr() + "|>");
    log.info("-- RESPONSE for {} --", MDC.get("requestId"));
    logResponse(response, request.getRemoteAddr() + "|<");

    requestDataMap.put("requestId", MDC.get("requestId"));
    requestDataMap.put("requestUri", request.getRequestURI());
    requestDataMap.put("requestMethod", request.getMethod());
    requestDataMap.put("clientIp", request.getRemoteAddr());
    requestDataMap.put("clientHost", request.getRemoteHost());
    requestDataMap.put("statusCode", String.valueOf(response.getStatus()));
    saveToDatabase();
  }

  private Map<String, String> hideSensitiveHeaders(Enumeration<String> headerNames,
      Enumeration<String> headers) {
    Map<String, String> finalHeaders = new HashMap<>();

    Collections.list(headerNames)
        .forEach(headerName ->
            Collections.list(headers)
                .forEach(headerValue -> {
                  if (isSensitiveHeader(headerName)) {
                    finalHeaders.put(headerName, "*******");
                  } else {
                    finalHeaders.put(headerName, headerValue);
                  }
                }));

    return finalHeaders;
  }

  @Async
  protected void saveToDatabase() {
    if (!requestDataMap.get("requestMethod").equalsIgnoreCase("GET")) {
      requestResponseRepository.save(RequestResponseLog.builder()
          .requestId(requestDataMap.get("requestId"))
          .clientIp(requestDataMap.get("clientIp"))
          .clientHost(requestDataMap.get("clientHost"))
          .requestUri(requestDataMap.get("requestUri"))
          .requestMethod(requestDataMap.get("requestMethod"))
          .statusCode(Integer.parseInt(requestDataMap.get("statusCode")))
          .requestHeader(requestDataMap.get("requestHeaders"))
          .requestPayload(requestDataMap.get("requestPayload"))
          .responseHeader(requestDataMap.get("responseHeader"))
          .responsePayload(requestDataMap.get("responsePayload"))
          .insertDate(LocalDateTime.now())
          .build());
    }
  }
}
