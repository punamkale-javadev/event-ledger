package com.eventledger.gateway.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.UUID;
@Component
public class TraceFilter
        implements Filter {

    @Override
    public void doFilter(

            ServletRequest req,

            ServletResponse res,

            FilterChain chain

    )
            throws IOException,
            ServletException {

        HttpServletRequest request =
                (HttpServletRequest) req;

        String traceId =
                request.getHeader(
                        "traceId"
                );

        if(traceId == null){

            traceId =
                    UUID
                            .randomUUID()
                            .toString();
        }

        MDC.put(
                "traceId",
                traceId
        );

        try {

            chain.doFilter(
                    req,
                    res
            );

        }

        finally {

            MDC.remove(
                    "traceId"
            );
        }
    }

}
