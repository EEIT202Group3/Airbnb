package com.EEITG3.Airbnb.jwt;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private JavaMailSender mailSender;
//	@Value("${mail.from.address:eeit202@gmail.com}") 
//	private String fromAddress;
//
//	@Value("${mail.from.name:Ctwify}")
//	private String fromName;

	@Autowired
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendCustomerVerificationEmail(String to, String token, String username) {
		String url = "http://localhost:8080/api/customers/verify?token=" + token;

		// MimeMessageHelper 是 Spring 提供的一個輔助類，用來簡化設定 Mail 寄送的內容
		MimeMessage message = mailSender.createMimeMessage();
		try {
			// 用剛剛產生的郵件物件來設定這個helper，true代表支援multipart(因為我們要傳html回去)
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			// 設定送件人名稱
			helper.setFrom("eeit202@gmail.com", "Ctwify");
			// 設定收件人
			helper.setTo(to);
			// 設定主旨
			helper.setSubject("歡迎加入！請完成帳號驗證");
			String content = """
					<html>
					<body style="font-family: Arial, sans-serif; padding: 20px;">
					    <h2 style="color: #2c3e50;">歡迎加入 Ctwify！</h2>
					        <p>%s 您好，請點擊下方按鈕以完成帳號驗證：</p>
					        <p>
					            <a href="%s" style="background-color: #FF8000; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
					                 驗證我的帳號
					            </a>
					        </p>
					        <p style="margin-top: 20px; color: gray; font-size: 12px;">
					           如果您沒有註冊本平台，請忽略此封信。
					        </p>
					</body>
					</html>
					"""
					.formatted(username, url);
			// 設定內容，true表示html格式
			helper.setText(content, true);
			// 發送
			mailSender.send(message);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void sendHostVerificationEmail(String to, String token, String username) {
		String url = "http://localhost:8080/api/hosts/verify?token=" + token;

		// MimeMessageHelper 是 Spring 提供的一個輔助類，用來簡化設定 Mail 寄送的內容
		MimeMessage message = mailSender.createMimeMessage();
		try {
			// 用剛剛產生的郵件物件來設定這個helper，true代表支援multipart(因為我們要傳html回去)
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			// 設定送件人名稱
			helper.setFrom("eeit202@gmail.com", "Ctwify");
			// 設定收件人
			helper.setTo(to);
			// 設定主旨
			helper.setSubject("歡迎您成為房東！請完成帳號驗證");
			String content = """
					<html>
					<body style="font-family: Arial, sans-serif; padding: 20px;">
					    <h2 style="color: #2c3e50;">歡迎加入 Ctwify！</h2>
					        <p>%s 您好，請點擊下方按鈕以完成帳號驗證：</p>
					        <p>
					            <a href="%s" style="background-color: #FF8000; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
					                 驗證我的帳號
					            </a>
					        </p>
					        <p style="margin-top: 20px; color: gray; font-size: 12px;">
					           如果您沒有註冊本平台，請忽略此封信。
					        </p>
					</body>
					</html>
					"""
					.formatted(username, url);
			// 設定內容，true表示html格式
			helper.setText(content, true);
			// 發送
			mailSender.send(message);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void sendCustomerForgetPwdEmail(String to, String token) {
		String url = "http://localhost:8080/api/customers/pwdverify?token=" + token;
		// MimeMessageHelper 是 Spring 提供的一個輔助類，用來簡化設定 Mail 寄送的內容
		MimeMessage message = mailSender.createMimeMessage();
		try {
			// 用剛剛產生的郵件物件來設定這個helper，true代表支援multipart(因為我們要傳html回去)
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			// 設定送件人名稱
			helper.setFrom("eeit202@gmail.com", "Ctwify");
			// 設定收件人
			helper.setTo(to);
			// 設定主旨
			helper.setSubject("重設密碼");
			String content = """
					<html>
					<body style="font-family: Arial, sans-serif; padding: 20px;">
					    <h2 style="color: #2c3e50;">重設密碼</h2>
					        <p>請點擊下方按鈕以重設密碼：</p>
					        <p>
					            <a href="%s" style="background-color: #FF8000; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
					                 重設密碼
					            </a>
					        </p>
					        <p style="margin-top: 20px; color: gray; font-size: 12px;">
					           如果您沒有註冊本平台，請忽略此封信。
					        </p>
					</body>
					</html>
					"""
					.formatted(url);
			// 設定內容，true表示html格式
			helper.setText(content, true);
			// 發送
			mailSender.send(message);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void sendOrderCreateEmail(String to, String username,
            String orderId,
            String houseName,
            String address,
            String checkinDate,
            String checkoutDate,
            String people,
            String mentStatus,
            String grandTotal,
            String createdTime,
            String orderDetailUrl,
            String supportEmail) {
		// MimeMessageHelper 是 Spring 提供的一個輔助類，用來簡化設定 Mail 寄送的內容
		MimeMessage message = mailSender.createMimeMessage();
		try {
			// 用剛剛產生的郵件物件來設定這個helper，true代表支援multipart(因為我們要傳html回去)
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			// 設定送件人名稱
			helper.setFrom("eeit202@gmail.com", "Ctwify");
			// 設定收件人
			helper.setTo(to);
			// 設定主旨
			helper.setSubject("Ctwify 感謝您的預訂");
			 String content = """
	                    <!DOCTYPE html>
	                    <html lang="zh-Hant">
	                    <head>
	                      <meta charset="UTF-8">
	                      <title>訂單已成立 - Ctwify</title>
	                    </head>
	                    <body style="margin:0; padding:0; background:#f4f6f8; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Noto Sans TC', 'PingFang TC', Arial, sans-serif; color:#2c3e50;">
	                      <div style="padding:24px;">
	                        <div style="max-width:600px; margin:0 auto;">
	                          <div style="background:#ffffff; border:1px solid #eaeaea; border-radius:12px; overflow:hidden;">
	                            <div style="padding:20px 24px; background:#fff7e8; border-bottom:1px solid #f2eadf;">
	                              <h2 style="margin:0; font-size:20px; color:#2c3e50;">訂單已成立 ✅</h2>
	                              <div style="margin-top:6px; font-size:14px; color:#6b7280;">感謝您的預訂，我們已收到您的款項並確認訂單。</div>
	                            </div>

	                            <div style="padding:20px 24px;">
	                              <p style="margin:0 0 12px 0; font-size:16px;">%s 您好，</p>
	                              <p style="margin:0; font-size:15px; color:#4b5563;">以下是您的訂單資訊，您也可至官網查看完整明細。</p>
	                            </div>

	                            <div style="padding:8px 24px 0 24px;">
	                              <div style="background:#fafafa; border:1px solid #eee; border-radius:10px; padding:14px 16px;">
	                                <div style="font-size:14px; color:#6b7280; margin-bottom:6px;">訂單編號</div>
	                                <div style="font-size:18px; font-weight:600; letter-spacing:0.3px;">%s</div>
	                              </div>
	                            </div>

	                            <div style="padding:16px 24px 8px 24px;">
	                              <table role="presentation" cellpadding="0" cellspacing="0" style="border-collapse:collapse; width:auto;">
	                                <tbody>
	                                  <tr>
	                                    <td style="padding:10px 8px; color:#6b7280; font-size:14px;">房源</td>
	                                    <td style="padding:10px 8px; color:#111827; font-size:15px; font-weight:600;">%s</td>
	                                  </tr>
	                                  <tr>
	                                    <td style="padding:10px 8px; color:#6b7280; font-size:14px;">地址</td>
	                                    <td style="padding:10px 8px; color:#111827; font-size:15px;">%s</td>
	                                  </tr>
	                                  <tr>
	                                    <td style="padding:10px 8px; color:#6b7280; font-size:14px;">入住</td>
	                                    <td style="padding:10px 8px; color:#111827; font-size:15px;">%s</td>
	                                  </tr>
	                                  <tr>
	                                    <td style="padding:10px 8px; color:#6b7280; font-size:14px;">退房</td>
	                                    <td style="padding:10px 8px; color:#111827; font-size:15px;">%s</td>
	                                  </tr>
	                                  <tr>
	                                    <td style="padding:10px 8px; color:#6b7280; font-size:14px;">人數</td>
	                                    <td style="padding:10px 8px; color:#111827; font-size:15px;">%s 位</td>
	                                  </tr>
	                                  <tr>
	                                    <td style="padding:10px 8px; color:#6b7280; font-size:14px;">付款方式</td>
	                                    <td style="padding:10px 8px; color:#111827; font-size:15px;">%s</td>
	                                  </tr>
	                                  <tr>
	                                    <td style="padding:10px 8px; color:#6b7280; font-size:14px;">訂單金額</td>
	                                    <td style="padding:10px 8px; color:#111827; font-size:16px; font-weight:700;">NT$ %s</td>
	                                  </tr>
	                                  <tr>
	                                    <td style="padding:10px 8px; color:#6b7280; font-size:14px;">成立時間</td>
	                                    <td style="padding:10px 8px; color:#111827; font-size:15px;">%s</td>
	                                  </tr>
	                                </tbody>
	                              </table>
	                            </div>

	                            <div style="padding:0 24px 18px 24px;">
	                              <div style="background:#fffaf5; border:1px dashed #f0d2a6; border-radius:10px; padding:12px 14px; color:#8a6d3b; font-size:13px;">
	                                入住前請務必確認入住時間、門禁/取鑰方式與房屋規範。若需要更改或取消，請於規定時間內向客服提出申請。
	                              </div>
	                            </div>

	                            <div style="padding:14px 24px 24px 24px; border-top:1px solid #f1f1f1; color:#6b7280; font-size:12px; line-height:1.6;">
	                              這是一封系統通知信，請勿直接回覆。若有任何問題，請聯絡客服：<a style="color:#2563eb; text-decoration:none;" href="mailto:%s">%s</a><br>
	                              © Ctwify — 期待為您帶來愉快的旅宿體驗
	                            </div>
	                          </div>
	                        </div>
	                      </div>
	                    </body>
	                    </html>
	                    """;
			 String html = String.format(
	                    content,
	                    username,   // greeting
	                    orderId,
	                    houseName,
	                    address,
	                    checkinDate,
	                    checkoutDate,
	                    people,
	                    mentStatus,  // 先付款方式
	                    grandTotal,// 再金額
	                    createdTime,
	                    orderDetailUrl,
	                    supportEmail,   // footer mailto
	                    supportEmail    // footer display
	            );
			// 設定內容，true表示html格式
			helper.setText(html, true); // 保持你原本的寫法

			// 發送
			mailSender.send(message);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
