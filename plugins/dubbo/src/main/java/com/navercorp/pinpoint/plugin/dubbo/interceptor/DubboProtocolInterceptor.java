package com.navercorp.pinpoint.plugin.dubbo.interceptor;

import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.Request;
import com.alibaba.dubbo.rpc.Invocation;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanId;
import com.navercorp.pinpoint.bootstrap.context.SpanRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.interceptor.SpanSimpleAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.plugin.dubbo.DubboConstants;

public class DubboProtocolInterceptor extends SpanSimpleAroundInterceptor {

    public DubboProtocolInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor) {
        super(traceContext, methodDescriptor, DubboProtocolInterceptor.class);
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
    protected void doInBeforeTrace(SpanRecorder recorder, Object target, Object[] args) {
        ExchangeChannel exchangeChannel = (ExchangeChannel) args[0];
        Request req = (Request) args[1];
        Object msg = req.getData();

        if (msg instanceof Invocation) {
            Invocation invocation = (Invocation) msg;

            recorder.recordServiceType(DubboConstants.DUBBO_PROVIDER_SERVICE_TYPE);
            recorder.recordRpcName(invocation.getMethodName());
            recorder.recordEndPoint(exchangeChannel.getLocalAddress().getHostString());
            recorder.recordRemoteAddress(exchangeChannel.getRemoteAddress().getHostString());

            if (!recorder.isRoot()) {
                String parentApplicationName =
                        invocation.getAttachment(DubboConstants.META_PARENT_APPLICATION_NAME);

                if (parentApplicationName != null) {
                    short parentApplicationType = NumberUtils.parseShort(
                            invocation.getAttachment(DubboConstants.META_PARENT_APPLICATION_TYPE),
                            ServiceType.UNDEFINED.getCode());
                    recorder.recordParentApplication(parentApplicationName, parentApplicationType);

                    recorder.recordAcceptorHost(exchangeChannel.getLocalAddress().getHostString());
                }
            }
        }
    }

    @Override
    protected void doInAfterTrace(SpanRecorder recorder, Object target, Object[] args,
            Object result, Throwable throwable) {
        Request req = (Request) args[1];
        Object msg = req.getData();

        if (msg instanceof Invocation) {
            Invocation invocation = (Invocation) msg;
            recorder.recordApi(methodDescriptor);
            recorder.recordAttribute(DubboConstants.DUBBO_ARGS_ANNOTATION_KEY,
                    invocation.getArguments());

            if (throwable == null) {
                recorder.recordAttribute(DubboConstants.DUBBO_RESULT_ANNOTATION_KEY, result);
            } else {
                recorder.recordException(throwable);
            }
        }
    }


}
