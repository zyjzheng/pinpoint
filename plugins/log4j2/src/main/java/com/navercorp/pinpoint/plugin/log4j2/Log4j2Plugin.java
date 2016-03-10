/**
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
package com.navercorp.pinpoint.plugin.log4j2;

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
 * @author zyuanjie
 */
public class Log4j2Plugin implements ProfilerPlugin, TransformTemplateAware {
    private final PLogger logger = PLoggerFactory.getLogger(getClass());
    private TransformTemplate transformTemplate;


    @Override
    public void setup(ProfilerPluginSetupContext context) {
        Log4j2Config config = new Log4j2Config(context.getConfig());

        if (!config.isLog4j2LoggingTransactionInfo()) {
            logger.info(
                    "Log4j2 plugin is not executed because log4j2 transform enable config value is false.");
            return;
        }

        transformTemplate.transform("org.apache.logging.log4j.core.impl.DefaultLogEventFactory",
                new TransformCallback() {

                    @Override
                    public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader,
                            String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
                                    throws InstrumentException {
                        InstrumentClass target = instrumentor.getInstrumentClass(classLoader,
                                className, classfileBuffer);

                        InstrumentMethod instrumentMethod = target.getDeclaredMethod("createEvent",
                                "java.lang.String", "org.apache.logging.log4j.Marker",
                                "java.lang.String", "org.apache.logging.log4j.Level",
                                "org.apache.logging.log4j.message.Message", "java.util.List",
                                "java.lang.Throwable");

                        if (instrumentMethod == null) {
                            logger.info(
                                    "------------------------------- Failed to init Log4jLogEventOfLog4j2Interceptor");
                            return null;
                        }

                        instrumentMethod.addInterceptor(
                                "com.navercorp.pinpoint.plugin.log4j2.interceptor.Log4jLogEventOfLog4j2Interceptor");

                        logger.info(
                                "------------------------------- Success to init Log4jLogEventOfLog4j2Interceptor");
                        return target.toBytecode();
                    }
                });

    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
