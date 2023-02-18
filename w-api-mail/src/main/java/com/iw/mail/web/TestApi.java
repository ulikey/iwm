package com.iw.mail.web;

import com.iw.mail.service.MailSendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "testaa")
@RestController
@RequestMapping("/test")
@RefreshScope
public class TestApi {
    @Autowired
    private MailSendService mailSendService;

    @Operation(summary = "aa", description = "tt")
    @GetMapping("/info")
    public String info() {
        String toEmail = "2213402017@qq.com";
        String ccEmail = "2213402017@qq.com";
        String subject = "测试发邮件";
        String content = "测试发邮件内容";
        mailSendService.sendMail(toEmail, ccEmail, subject, content, null);
        return "success";
    }
}
