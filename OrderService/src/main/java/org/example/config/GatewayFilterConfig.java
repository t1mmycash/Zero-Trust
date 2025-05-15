package org.example.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр, который проверяет, что каждый запрос пришёл через API‑Gateway:
 * в нём должен присутствовать заголовок X-Request-Source=gateway.
 * Если заголовок отсутствует или имеет другое значение — возвращаем 403.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayFilterConfig extends OncePerRequestFilter {

    private static final String HEADER_NAME  = "X-Request-Source";
    private static final String HEADER_VALUE = "gateway";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String source = request.getHeader(HEADER_NAME);
        if (!HEADER_VALUE.equals(source)) {
            // Отправляем 403 и выходим
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden: missing or invalid " + HEADER_NAME);
            return;
        }
        // всё ок — пропускаем дальше
        filterChain.doFilter(request, response);
    }
}
