package com.jw.home.common;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class LoggerFilter extends OncePerRequestFilter {

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String logTraceId = request.getHeader("TRANSACTION_ID");
		if (logTraceId == null) {
			logTraceId = UUID.randomUUID().toString();
		}
		MDC.put("TRACE_ID", logTraceId);
		filterChain.doFilter(request, response);
	}

}
