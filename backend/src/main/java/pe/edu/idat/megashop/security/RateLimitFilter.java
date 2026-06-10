package pe.edu.idat.megashop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final int LIMIT = 200;
    private static final long WINDOW_SECONDS = 900;
    private final ConcurrentHashMap<String, Window> requests = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String key = request.getRemoteAddr();
        long now = Instant.now().getEpochSecond();
        Window window = requests.compute(key, (ignored, current) ->
                current == null || now - current.startedAt >= WINDOW_SECONDS
                        ? new Window(now, new AtomicInteger(1))
                        : new Window(current.startedAt, new AtomicInteger(current.count.incrementAndGet()))
        );
        if (window.count.get() > LIMIT) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":{\"message\":\"Demasiadas solicitudes\",\"status\":429}}");
            return;
        }
        chain.doFilter(request, response);
    }

    private record Window(long startedAt, AtomicInteger count) {}
}
