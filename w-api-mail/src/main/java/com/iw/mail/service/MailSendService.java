package com.iw.mail.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

/**
 * 发送邮件
 */
@Slf4j
@Component
@RefreshScope
public class MailSendService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送邮件
     * @param toEmail 接收方邮箱
     * @param ccEmail 抄送邮箱
     * @param subject 主题
     * @param content 内容
     * @param filePathList 附件
     */
    public void sendMail(String toEmail, String ccEmail, String subject, String content, List<String> filePathList) {
        log.info("邮件准备发送：主题-{} ｜ from-{} | to-{} | cc-{}", subject, fromEmail, toEmail, ccEmail);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail); // 发送者
            helper.setTo(toEmail.split(";")); // 接收者
            if (StringUtils.isNotBlank(ccEmail)) {
                helper.setCc(ccEmail.split(";")); // 抄送人
            }
            helper.setSubject(subject);
            helper.setText(content, true);
            if (!CollectionUtils.isEmpty(filePathList)) {
                filePathList.forEach(i -> {
                    FileSystemResource file = new FileSystemResource(new File(i));
                    String fileNames = i.substring(i.lastIndexOf(File.separator) + 1);
                    // 添加多个附件可以使用多条
                    try {
                        helper.addAttachment(fileNames, file);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
            }
            javaMailSender.send(message);
            log.info("邮件发送成功：主题-{} ｜ to-{}", subject, toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("邮件发送失败：主题-{} ", subject);
        }
    }
}
