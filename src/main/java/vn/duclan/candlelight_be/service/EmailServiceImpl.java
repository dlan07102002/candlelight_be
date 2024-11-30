package vn.duclan.candlelight_be.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String src, String dst, String subject, String text) {
        // Check String empty or not
        if (!StringUtils.hasText(src) || !StringUtils.hasText(dst) || !StringUtils.hasText(subject)
                || !StringUtils.hasText(text)) {
            log.error("Invalid email parameters: src={}, dst={}, subject={}", src, dst, subject);
            throw new IllegalArgumentException("Email parameters cannot be null or empty");
        }

        try {
            MimeMessage message = createEmailMessage(src, dst, subject, text);
            mailSender.send(message);
            log.info("Email sent successfully from {} to {}", src, dst);
        } catch (MessagingException e) {
            log.error("Failed to send email from {} to {}", src, dst, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private MimeMessage createEmailMessage(String src, String dst, String subject, String text)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(src);
        helper.setTo(dst);
        helper.setSubject(subject);
        helper.setText(text, true); // Set HTML content
        return message;
    }
}
