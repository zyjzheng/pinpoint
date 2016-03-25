package com.navercorp.pinpoint.web.alarm;

import java.util.List;

import javax.mail.internet.MimeMessage.RecipientType;

import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.email.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.navercorp.pinpoint.web.alarm.checker.AlarmChecker;
import com.navercorp.pinpoint.web.config.ConfigProperties;
import com.navercorp.pinpoint.web.service.UserGroupService;

@Component
public class SimpleMessageSender implements AlarmMessageSender {

    @Autowired
    private ConfigProperties webProperties;

    @Autowired
    private UserGroupService userGroupService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void sendSms(AlarmChecker checker, int sequenceCount) {

    }

    @Override
    public void sendEmail(AlarmChecker checker, int sequenceCount) {
        List<String> receivers = userGroupService.selectEmailOfMember(checker.getuserGroupId());

        if (receivers.size() == 0) {
            return;
        }

        Email email = new Email();
        email.setFromAddress(webProperties.getSmtpUser(), webProperties.getSmtpUser());
        email.setSubject(checker.getEmailMessage());
        email.setText(checker.getEmailMessage());
        for (String recv : receivers) {
            email.addRecipient(recv, recv, RecipientType.TO);
        }
        Mailer mailler = new Mailer(webProperties.getSmtpServer(),
                Integer.parseInt(webProperties.getSmtpPort()), webProperties.getSmtpUser(),
                webProperties.getSmtpPassword());
        mailler.sendMail(email);
    }

}
