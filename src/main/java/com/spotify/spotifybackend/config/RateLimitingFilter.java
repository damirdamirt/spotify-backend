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

    private static final int MAX_REQUESTS = 5; // Dozvoljeno 5 zahteva
    private static final long TIME_WINDOW = 10 * 1000; // ...u 10 sekundi

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String clientIp = request.getRemoteAddr();
            long currentTime = System.currentTimeMillis();

            // 1. Uzmi ili kreiraj log za ovu IP adresu
            requestLogs.putIfAbsent(clientIp, new CopyOnWriteArrayList<>());
            CopyOnWriteArrayList<Long> timestamps = requestLogs.get(clientIp);

            // 2. Ocisti stare zahteve (koji su stariji od 10 sekundi)
            timestamps.removeIf(timestamp -> currentTime - timestamp > TIME_WINDOW);

            // 3. Proveri da li je prekoracen limit
            if (timestamps.size() >= MAX_REQUESTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // Vraca 429
                response.getWriter().write("Too many requests - Rate limit exceeded. Try again later.");
                return; // Prekini lanac, ne daj mu da prodje dalje!
            }

            // 4. Zabelezi trenutni zahtev
            timestamps.add(currentTime);

            // 5. Pusti zahtev dalje
            filterChain.doFilter(request, response);
    }
}
