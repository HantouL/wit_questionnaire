package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    @Resource
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String username;


    @RabbitHandler
    public void sendMailMessage(Map<String,Object> data){
        String email = data.get("email").toString();
        Integer code = (Integer)data.get("code");
        String type = data.get("type").toString();
        SimpleMailMessage message = switch (type){
            case "register" -> createMessage("欢迎注册,请查收您的验证码",
                    "您的验证码是: "+code+" , 五分钟内有效; 为了保障您的安全,请勿向他人泄露验证码信息; 若非本人操作,请无视本消息",
                    email);
            case "reset" -> createMessage("密码重置验证码",
                    "您正在重置账户密码,验证码是: "+code+" , 五分钟内有效; 为了保障您的安全,请勿向他人泄露验证码信息; 若非本人操作,请及时修改密码",
                    email);
            case "modify" -> createMessage("邮箱重置验证码",
                    "您正在重置账户邮箱,验证码是: "+code+" , 五分钟内有效; 为了保障您的安全,请勿向他人泄露验证码信息; 若非本人操作,请及时修改密码",
                    email);
            default -> null;
        };
        if(message==null) return;
        javaMailSender.send(message);
    }

    private SimpleMailMessage createMessage(String title,String content,String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
