package uk.gov.cshr.civilservant.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@Component
public class AccessLogFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest requestMade = ((HttpServletRequest)request);
        LOGGER.info(String.format("Request: %s %s", requestMade.getMethod(), requestMade.getRequestURI()));
        String headerName;
        Enumeration headers = requestMade.getHeaderNames();
        while(headers.hasMoreElements()) {
            headerName = headers.nextElement().toString();
            LOGGER.info(String.format("Header[%s]= %s", headerName, requestMade.getHeader(headerName)));
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
