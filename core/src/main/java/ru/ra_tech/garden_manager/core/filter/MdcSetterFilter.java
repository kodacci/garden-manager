package ru.ra_tech.garden_manager.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.ra_tech.garden_manager.core.security.JwtPrincipal;

import java.io.IOException;

@Slf4j
@Component("mdcSetterFilter")
public class MdcSetterFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("Setting MDC");

        val rqUid = request.getHeader("rqUid");
        if (rqUid != null) {
            MDC.put("rqUid", rqUid);
        }
        val rqTm = request.getHeader("rqTm");
        if (rqTm != null) {
            MDC.put("rqTm", rqTm);
        }

        val auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            val principal = auth.getPrincipal();
            if (principal instanceof JwtPrincipal) {
                MDC.put("userId", Long.toString(((JwtPrincipal) principal).id()));
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.debug("Clearing MDC");
            MDC.clear();
        }
    }
}
