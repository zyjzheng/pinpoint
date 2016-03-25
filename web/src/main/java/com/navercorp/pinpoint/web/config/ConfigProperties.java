/*
 * Copyright 2015 NAVER Corp.
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

package com.navercorp.pinpoint.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author HyunGil Jeong
 */
@Component
public class ConfigProperties {

    @Value("#{pinpointWebProps['config.sendUsage'] ?: true}")
    private boolean sendUsage;

    @Value("#{pinpointWebProps['config.editUserInfo'] ?: true}")
    private boolean editUserInfo;

    @Value("#{pinpointWebProps['config.show.activeThread'] ?: false}")
    private boolean showActiveThread;

    @Value("#{pinpointWebProps['smtp.port'] ?: ''}")
    private String smtpPort;
    @Value("#{pinpointWebProps['smtp.user'] ?: ''}")
    private String smtpUser;
    @Value("#{pinpointWebProps['smtp.password'] ?: ''}")
    private String smtpPassword;

    @Value("#{pinpointWebProps['smtp.server'] ?: ''}")
    private String smtpServer;

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUser() {
        return smtpUser;
    }

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public boolean getEditUserInfo() {
        return editUserInfo;
    }

    public boolean getSendUsage() {
        return this.sendUsage;
    }

    public boolean isShowActiveThread() {
        return showActiveThread;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WebProperties{");
        sb.append("sendUsage=").append(sendUsage);
        sb.append(", showActiveThread=").append(showActiveThread);
        sb.append(", editUserInfo=").append(editUserInfo);
        sb.append("}");
        return sb.toString();
    }


}
