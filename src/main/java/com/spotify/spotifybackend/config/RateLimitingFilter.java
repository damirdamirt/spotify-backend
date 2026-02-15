package com.spotify.spotifybackend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter implements Filter {

    private final Map<String, CopyOnWriteArrayList<Long>> requestLogs = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 10; // Povecali smo na 10 da bude lakse za test
    private static final long TIME_WINDOW = 10 * 1000; // 10 sekundi

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. PROPUSTI "OPTIONS" ZAHTEVE (Preflight)
        // Browseri salju ovo da provere CORS. To ne smemo da blokiramo.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        requestLogs.putIfAbsent(clientIp, new CopyOnWriteArrayList<>());
        CopyOnWriteArrayList<Long> timestamps = requestLogs.get(clientIp);

        // Ocisti stare zapise
        timestamps.removeIf(timestamp -> currentTime - timestamp > TIME_WINDOW);

        // Proveri limit
        if (timestamps.size() >= MAX_REQUESTS) {
            // Rucno dodajemo CORS headere za gresku
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            response.setHeader("Access-Control-Allow-Credentials", "true");

            response.getWriter().write("Too many requests - Rate limit exceeded.");
            return;
        }

        timestamps.add(currentTime);
        filterChain.doFilter(request, response);
    }
}