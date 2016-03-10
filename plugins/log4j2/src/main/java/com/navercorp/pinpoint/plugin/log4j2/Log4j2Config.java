/**
 * Copyright 2014 NAVER Corp. Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License
 * at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.navercorp.pinpoint.plugin.log4j2;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

/**
 * @author zyuanjie
 *
 */
public class Log4j2Config {
    public static final String LOG4J2_LOGGING_TRANSACTION_INFO =
            "profiler.log4j2.logging.transactioninfo";

    private final boolean log4j2LoggingTransactionInfo;


    public Log4j2Config(ProfilerConfig config) {
        this.log4j2LoggingTransactionInfo =
                config.readBoolean(LOG4J2_LOGGING_TRANSACTION_INFO, false);
    }

    public boolean isLog4j2LoggingTransactionInfo() {
        return log4j2LoggingTransactionInfo;
    }

    @Override
    public String toString() {
        return "Log4j2Config [ log4j2LoggingTransactionInfo=" + log4j2LoggingTransactionInfo + "]";
    }

}
