package com.navercorp.pinpoint.plugin.dubbo.interceptor;

import com.alibaba.dubbo.remoting.exchange.Request;
import com.alibaba.dubbo.rpc.Invocation;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanId;
import com.navercorp.pinpoint.bootstrap.context.SpanRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.interceptor.SpanSimpleAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;
import com.navercorp.pinpoint.plugin.dubbo.DubboConstants;

public class DubboProtocolInterceptor2 extends SpanSimpleAroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    public DubboProtocolInterceptor2(TraceContext traceContext, MethodDescriptor methodDescriptor) {
        super(traceContext, methodDescriptor, DubboProtocolInterceptor2.class);
    }

    @Override
    protected Trace createTrace(Object target, Object args[]) {
        Trace trace = null;

        Request req = (Request) args[1];

        Object msg = req.getData();
        if (msg instanceof Invocation) {
            Invocation invocation = (Invocation) msg;

            if (DubboConstants.MONITOR_SERVICE_FQCN.equals(invocation.getMethodName())) {
                return traceContext.disableSampling();
            }
            if (invocation.getAttachment(DubboConstants.META_DO_NOT_TRACE) != null) {
                return traceContext.disableSampling();
            }
            String transactionId = invocation.getAttachment(DubboConstants.META_TRANSACTION_ID);

            if (transactionId == null) {
                trace = traceContext.newTraceObject();
            } else {
                long parentSpanID = NumberUtils.parseLong(
                        invocation.getAttachment(DubboConstants.META_PARENT_SPAN_ID), SpanId.NULL);
                long spanID = NumberUtils.parseLong(
                        invocation.getAttachment(DubboConstants.META_SPAN_ID), SpanId.NULL);
                short flags = NumberUtils
                        .parseShort(invocation.getAttachment(DubboConstants.META_FLAGS), (short) 0);
                TraceId traceId =
                        traceContext.createTraceId(transactionId, parentSpanID, spanID, flags);

                trace = traceContext.continueTraceObject(traceId);

            }
        }
        return trace;
    }

    @Override
    protected void doInBeforeTrace(SpanRecorder recorder, Object target, Object[] args) {}

    @Override
    protected void doInAfterTrace(SpanRecorder recorder, Object target, Object[] args,
            Object result, Throwable throwable) {}

}
