package com.navercorp.pinpoint.plugin.dubbo.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.ThreadContext;

import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;

public class DubboDispatcherInterceptor implements AroundInterceptor {

    private final MethodDescriptor descriptor;
    private final TraceContext traceContext;
    private final PLogger logger = PLoggerFactory.getLogger(getClass());

    public DubboDispatcherInterceptor(TraceContext traceContext, MethodDescriptor descriptor) {
        this.descriptor = descriptor;
        this.traceContext = traceContext;
    }

    @Override
    public void before(Object target, Object[] args) {
        Trace trace = traceContext.currentTraceObject();

        if (trace == null) {
            return;
        }

        HttpServletRequest req = (HttpServletRequest) args[0];
        String ua = req.getHeader("User-Agent");

        ThreadContext.put("User-Agent", ua);

    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {

        Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        ThreadContext.remove("User-Agent");
    }

}
