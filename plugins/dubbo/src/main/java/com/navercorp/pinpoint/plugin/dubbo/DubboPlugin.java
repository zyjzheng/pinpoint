package com.navercorp.pinpoint.plugin.dubbo;

import java.security.ProtectionDomain;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;

/**
 * @author Jinkai.Ma
 */
public class DubboPlugin implements ProfilerPlugin, TransformTemplateAware {

    private final PLogger logger = PLoggerFactory.getLogger(getClass());

    private TransformTemplate transformTemplate;

    @Override
    public void setup(ProfilerPluginSetupContext context) {
        this.addApplicationTypeDetector(context);
        this.addTransformers();
    }

    private void addTransformers() {
        transformTemplate.transform("com.alibaba.dubbo.rpc.cluster.support.AbstractClusterInvoker",
                new TransformCallback() {
                    @Override
                    public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader,
                            String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
                                    throws InstrumentException {
                        InstrumentClass target =
                                instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                        target.getDeclaredMethod("invoke", "com.alibaba.dubbo.rpc.Invocation")
                                .addInterceptor(
                                        "com.navercorp.pinpoint.plugin.dubbo.interceptor.DubboConsumerInterceptor");

                        return target.toBytecode();
                    }
                });

        transformTemplate.transform("com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker",
                new TransformCallback() {
                    @Override
                    public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader,
                            String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
                                    throws InstrumentException {
                        InstrumentClass target =
                                instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                        target.getDeclaredMethod("invoke", "com.alibaba.dubbo.rpc.Invocation")
                                .addInterceptor(
                                        "com.navercorp.pinpoint.plugin.dubbo.interceptor.DubboProviderInterceptor2");

                        return target.toBytecode();
                    }
                });

        transformTemplate.transform(
                "com.alibaba.dubbo.remoting.exchange.support.header.HeaderExchangeHandler",
                new TransformCallback() {

                    @Override
                    public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader,
                            String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
                                    throws InstrumentException {
                        InstrumentClass target = instrumentor.getInstrumentClass(classLoader,
                                className, classfileBuffer);
                        InstrumentMethod instrumentMethod =
                                target.getDeclaredMethod("handleRequest",
                                        "com.alibaba.dubbo.remoting.exchange.ExchangeChannel",
                                        "com.alibaba.dubbo.remoting.exchange.Request");

                        if (instrumentMethod == null) {
                            logger.info(
                                    "------------------------------- Failed to init DubboProtocolInterceptor");
                            return null;
                        }

                        instrumentMethod.addInterceptor(
                                "com.navercorp.pinpoint.plugin.dubbo.interceptor.DubboProtocolInterceptor");

                        return target.toBytecode();
                    }
                });
    }

    /**
     * Pinpoint profiler agent uses this detector to find out the service type of current
     * application.
     */
    private void addApplicationTypeDetector(ProfilerPluginSetupContext context) {
        context.addApplicationTypeDetector(new DubboProviderDetector());
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
