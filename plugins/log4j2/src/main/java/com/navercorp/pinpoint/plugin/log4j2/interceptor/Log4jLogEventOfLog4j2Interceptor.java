/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.navercorp.pinpoint.plugin.log4j2.interceptor;

import org.apache.logging.log4j.ThreadContext;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;



/**
 * @author zyuanjie
 */

public class Log4jLogEventOfLog4j2Interceptor implements AroundInterceptor {
    private static final String TRANSACTION_ID = "PtxId";
    private static final String SPAN_ID = "PspanId";

    private final TraceContext traceContext;

    public Log4jLogEventOfLog4j2Interceptor(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    @Override
    public void before(Object target, Object[] args) {

        Trace trace = traceContext.currentTraceObject();

        if (trace == null) {
            ThreadContext.remove(TRANSACTION_ID);
            ThreadContext.remove(SPAN_ID);
            return;
        } else {
            ThreadContext.put(TRANSACTION_ID, trace.getTraceId().getTransactionId());
            ThreadContext.put(SPAN_ID, String.valueOf(trace.getTraceId().getSpanId()));
        }


    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {}

}
