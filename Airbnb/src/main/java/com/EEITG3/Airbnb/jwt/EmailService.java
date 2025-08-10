package com.EEITG3.Airbnb.jwt;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private JavaMailSender mailSender;
	
	@Autowired
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void sendCustomerVerificationEmail(String to, String token, String username) {
		String url = "http://localhost:8080/api/customers/verify?token=" + token;
		
		//MimeMessageHelper 是 Spring 提供的一個輔助類，用來簡化設定 Mail 寄送的內容
		MimeMessage message = mailSender.createMimeMessage();
		try {
			//用剛剛產生的郵件物件來設定這個helper，true代表支援multipart(因為我們要傳html回去)
			MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
			//設定送件人名稱
			helper.setFrom("eeit202@gmail.com", "Ctwify");
			//設定收件人
			helper.setTo(to);
			//設定主旨
			helper.setSubject("歡迎加入！請完成帳號驗證");
			String content = """
					<html>
					<body style="font-family: Arial, sans-serif; padding: 20px;">
					    <h2 style="color: #2c3e50;">歡迎加入 Ctwify！</h2>
					        <p>%s 您好，請點擊下方按鈕以完成帳號驗證：</p>
					        <p>
					            <a href="%s" style="background-color: #3498db; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
					                 驗證我的帳號
					            </a>
					        </p>
					        <p style="margin-top: 20px; color: gray; font-size: 12px;">
					           如果您沒有註冊本平台，請忽略此封信。
					        </p>
					</body>
					</html>
					""".formatted(username, url);
			//設定內容，true表示html格式
			helper.setText(content, true);
			//發送
			mailSender.send(message);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void sendHostVerificationEmail(String to, String token, String username) {
		String url = "http://localhost:8080/api/hosts/verify?token=" + token;
		
		//MimeMessageHelper 是 Spring 提供的一個輔助類，用來簡化設定 Mail 寄送的內容
		MimeMessage message = mailSender.createMimeMessage();
		try {
			//用剛剛產生的郵件物件來設定這個helper，true代表支援multipart(因為我們要傳html回去)
			MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
			//設定送件人名稱
			helper.setFrom("eeit202@gmail.com", "Ctwify");
			//設定收件人
			helper.setTo(to);
			//設定主旨
			helper.setSubject("歡迎您成為房東！請完成帳號驗證");
			String content = """
					<html>
					<body style="font-family: Arial, sans-serif; padding: 20px;">
					    <h2 style="color: #2c3e50;">歡迎加入 Ctwify！</h2>
					        <p>%s 您好，請點擊下方按鈕以完成帳號驗證：</p>
					        <p>
					            <a href="%s" style="background-color: #3498db; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
					                 驗證我的帳號
					            </a>
					        </p>
					        <p style="margin-top: 20px; color: gray; font-size: 12px;">
					           如果您沒有註冊本平台，請忽略此封信。
					        </p>
					</body>
					</html>
					""".formatted(username, url);
			//設定內容，true表示html格式
			helper.setText(content, true);
			//發送
			mailSender.send(message);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
