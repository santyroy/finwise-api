package com.roy.finwise.service.impl;

import com.roy.finwise.entity.OtpPurpose;
import com.roy.finwise.event.OtpSentEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${application.otp.expiry}")
    private String otpExpiry;

    @Async
    public void sendEmail(String otp, String email, OtpPurpose otpPurpose) {
        try {
            // Send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(getEmailSubject(otpPurpose));
            message.setText("Your OTP code is: " + otp + ". It will expire in " + otpExpiry + " minutes.");

            mailSender.send(message);
            log.info("Successfully sent OTP email to: {}", email);

            // Publish success event
            eventPublisher.publishEvent(new OtpSentEvent(email, true));
        } catch (Exception e) {
            // Publish failure event
            log.error("Failed to send OTP to {}: {}", email, e.getMessage(), e);
            eventPublisher.publishEvent(new OtpSentEvent(email, false, e.getMessage()));
        }
    }

    @Async
    public void sendMimeEmail(String otp, String email, OtpPurpose otpPurpose) {
        try {
            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("no-reply@finwise.com", "Finwise");
            helper.setTo(email);
            helper.setSubject(getEmailSubject(otpPurpose));
            helper.setText(getContent(otp, email), true);

            ClassPathResource resource = new ClassPathResource("static/logo.png");
            helper.addInline("logoImage", resource);

            mailSender.send(message);
            log.info("Successfully sent OTP email to: {}", email);

            // Publish success event
            eventPublisher.publishEvent(new OtpSentEvent(email, true));
        } catch (Exception e) {
            // Publish failure event
            log.error("Failed to send OTP to {}: {}", email, e.getMessage(), e);
            eventPublisher.publishEvent(new OtpSentEvent(email, false, e.getMessage()));
        }
    }

    private String getEmailSubject(OtpPurpose otpPurpose) {
        return switch (otpPurpose) {
            case ACCOUNT_VERIFICATION -> "Your OTP for Account Verification";
            case PASSWORD_RESET -> "Your OTP for Password Reset";
            case EMAIL_CHANGE -> "Your OTP for Email Change";
        };
    }

    private String getContent(String otp, String email) {
        String safeOtp = HtmlUtils.htmlEscape(otp);
        String safeEmail = HtmlUtils.htmlEscape(email);
        return """
                <body
                    style="
                      font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI',
                        Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue',
                        sans-serif;
                    "
                  >
                    <main
                      style="
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        align-items: center;
                        text-align: center;
                        border-radius: 10px;
                        padding: 10px;
                        box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);
                      "
                    >
                      <header>
                        <img src="cid:logoImage" alt="logo" style="height: 30px; width: 30px" />
                        <span
                          style="
                            font-size: 24px;
                            font-weight: 600;
                            letter-spacing: 2px;
                            padding-left: 5px;
                          "
                        >
                          Finwise
                        </span>
                      </header>
                      <section>
                        <h2 style="font-size: 28px">Hey User,</h2>
                        <p style="font-weight: 600">
                          Use this code to proceed with your request on Finwise. The OTP will expire in %s
                          minutes.
                        </p>
                      </section>
                      <section>
                        <p style="font-size: 32px; font-weight: bold; letter-spacing: 30px">
                          %s
                        </p>
                      </section>
                      <section>
                        <p style="font-weight: 600">
                          To ensure secure access, this code is associated with
                          <span style="color: #7f3dff">%s</span>
                        </p>
                      </section>
                      <footer>
                        <p style="font-size: 14px; color: #91919f">
                          If you didn't request this email, you can safely ignore it.
                        </p>
                      </footer>
                    </main>
                  </body>
                """.formatted(otpExpiry, safeOtp, safeEmail);
    }
}
