package com.nncloudtv.filter;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (request.getHeader("Origin") != null) {
            response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.addHeader("Access-Control-Allow-Credentials", "true");
        } else if (request.getHeader("Referer") != null) {
            URL refererUrl = new URL(request.getHeader("Referer"));
            String domain = refererUrl.getProtocol() + "://" + refererUrl.getHost();
            if (refererUrl.getPort() != -1) {
                domain = domain + ":" + refererUrl.getPort();
            }
            response.addHeader("Access-Control-Allow-Origin", domain);
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
        
        if ((request.getHeader("Access-Control-Request-Method") != null) && (request.getMethod().equals("OPTIONS"))) {
            // CORS "pre-flight" request
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "content-type, cookie");
            response.addHeader("Access-Control-Max-Age", "1728000");
            return ;
        }

        filterChain.doFilter(request, response);
    }
    
}
