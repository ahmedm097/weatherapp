package com.bptn.weatherapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.bptn.weatherapp.jpa.User;
import com.bptn.weatherapp.provider.ResourceProvider;
import com.bptn.weatherapp.security.JwtService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${spring.mail.username}")
	private String emailFrom;

	@Autowired
	JwtService jwtService;

	@Autowired
	ResourceProvider provider;

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	JavaMailSender javaMailSender;

	@Async
	public void sendVerificationEmail(User user) {

		this.sendEmail(user, this.provider.getClientVerifyParam(), "verify_email",
				String.format("Welcome %s %s", user.getFirstName(), user.getLastName()),
				this.provider.getClientVerifyExpiration());
	}

	private void sendEmail(User user, String clientParam, String templateName, String emailSubject, long expiration) {

		try {
			Context context = new Context();

			/* Collect Data for the Email HTML generation */
			context.setVariable("user", user);
			context.setVariable("client", this.provider.getClientUrl());
			context.setVariable("param", clientParam);
			context.setVariable("token", this.jwtService.generateJwtToken(user.getUsername(), expiration));

			/* Process Email HTML Template */
			String process = this.templateEngine.process(templateName, context);

			MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();

			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

			/* Set Email Information */
			mimeMessageHelper.setFrom(this.emailFrom, "WeatherApp - Obsidi Academy");
			mimeMessageHelper.setSubject(emailSubject);
			mimeMessageHelper.setText(process, true);
			mimeMessageHelper.setTo(user.getEmailId());

			/* Send Email */
			this.javaMailSender.send(mimeMessage);

			/* Log after successfully sending email */
			this.logger.debug("Email Sent successfully to {}", user.getEmailId());

		} catch (Exception ex) {
			this.logger.error("Error sending email to Username: " + user.getUsername(), ex);
		}
	}

	@Async
	public void sendResetPasswordEmail(User user) {

		this.sendEmail(user, this.provider.getClientResetParam(), "reset_password", "Reset your password",
				this.provider.getClientResetExpiration());
	}

}
