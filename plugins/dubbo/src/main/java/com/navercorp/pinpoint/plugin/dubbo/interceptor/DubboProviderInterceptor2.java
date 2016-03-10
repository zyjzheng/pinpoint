package com.navercorp.pinpoint.plugin.dubbo.interceptor;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.plugin.dubbo.DubboConstants;

/**
 * @author Jinkai.Ma
 */
public class DubboProviderInterceptor2 implements AroundInterceptor {

    private final MethodDescriptor descriptor;
    private final TraceContext traceContext;


    public DubboProviderInterceptor2(TraceContext traceContext, MethodDescriptor descriptor) {
        this.descriptor = descriptor;
        this.traceContext = traceContext;
    }

    private Trace getTrace() {
        return this.traceContext.currentTraceObject();
    }

    @Override
    public void before(Object target, Object[] args) {

        Trace trace = this.getTrace();
        if (trace == null) {
            return;
        }

        RpcInvocation invocation = (RpcInvocation) args[0];
        RpcContext rpcContext = RpcContext.getContext();

        SpanRecorder recorder = trace.getSpanRecorder();

        // You have to record a service type within Server range.
        recorder.recordServiceType(DubboConstants.DUBBO_PROVIDER_SERVICE_TYPE);

        // Record rpc name, client address, server address.
        recorder.recordRpcName(invocation.getInvoker().getInterface().getSimpleName() + ":"
                + invocation.getMethodName());
        recorder.recordEndPoint(rpcContext.getLocalAddressString());
        recorder.recordRemoteAddress(rpcContext.getRemoteAddressString());

        // If this transaction did not begin here, record parent(client who sent this request)
        // information
        if (!recorder.isRoot()) {
            String parentApplicationName =
                    invocation.getAttachment(DubboConstants.META_PARENT_APPLICATION_NAME);

            if (parentApplicationName != null) {
                short parentApplicationType = NumberUtils.parseShort(
                        invocation.getAttachment(DubboConstants.META_PARENT_APPLICATION_TYPE),
                        ServiceType.UNDEFINED.getCode());
                recorder.recordParentApplication(parentApplicationName, parentApplicationType);

                // Pinpoint finds caller - callee relation by matching caller's end point and
                // callee's acceptor host.
                // https://github.com/naver/pinpoint/issues/1395
                recorder.recordAcceptorHost(rpcContext.getLocalAddressString());
            }
        }
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {

        Trace trace = this.getTrace();
        if (trace == null) {
            return;
        }

        RpcInvocation invocation = (RpcInvocation) args[0];

        SpanRecorder recorder = trace.getSpanRecorder();

        recorder.recordApi(descriptor);
        recorder.recordAttribute(DubboConstants.DUBBO_ARGS_ANNOTATION_KEY,
                invocation.getArguments());

        if (throwable == null) {
            recorder.recordAttribute(DubboConstants.DUBBO_RESULT_ANNOTATION_KEY, result);
        } else {
            recorder.recordException(throwable);
        }

    }
}
