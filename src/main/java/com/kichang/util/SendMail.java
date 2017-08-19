package com.kichang.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SendMail {
	static String encoding5601 = "KSC5601";
	static String encoding8859 = "8859_1"; // setSubject의 기본인코딩이 "8859_1"
	static String encodingEuc = "euc-kr";
	static String encoding949 = "MS949";
	static String encodingUtf8 = "UTF-8";

	protected Log logger = LogFactory.getLog(getClass());
	private String smtpIp = "";
	private String id = "";
	private String pw = "";
	private String port = "25";
	private boolean isSSL = false;

	public void setSmtpIp(String smtpIp) {
		this.smtpIp = smtpIp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}
	
	public static void main(String[] args) throws Exception {
			SendMail mail = new SendMail();
			String[] mailTo = {"kcjang@halos.co.kr"};
			mail.setSmtpIp("smtp.gmail.com");
			mail.setSSL(false);
			mail.setId("kcjang@halos.co.kr");
			mail.setPw("yj1772");
			mail.gmail("kcjang@halos.co.kr", "kcjang@halos.co.kr", "TEST Mail", "Test mail ");
	}

	public void smtp(String mailFrom, String[] mailTo, String title,
			String contents) throws Exception {
		String host = this.smtpIp;
		String dateString = Converter.dateToString(new Date());
		String subject = title;
		String content = contents;
		String from = mailFrom;
		String fname = mailFrom;

		try {
			// 프로퍼티 값 인스턴스 생성과 기본세션(SMTP 서버 호스트 지정)
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			//
			if (isSSL) {
				props.put("mail.smtp.starttls.enable", "true");
				props.setProperty("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
			}
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			//props.put("mail.smtp.connectiontimeout", 2000);
			Session sess = Session.getDefaultInstance(props, null);
			sess.setDebug(true);

			Message msg = new MimeMessage(sess);

			InternetAddress fadd = new InternetAddress();
			fadd.setAddress(from); // 보내는 사람 email
			fadd.setPersonal(fname, "EUC-KR"); // 보내는 사람 이름
			msg.setFrom(fadd); // 보내는 사람 설정

			msg.setSubject(subject);// 제목 설정
			msg.setSentDate(new java.util.Date());// 보내는 날짜 설정
			msg.setContent(content, "text/plain;charset=euc-kr"); // 내영 설정 (HTML
																	// 형식)
			List<InternetAddress> addresses = new ArrayList<InternetAddress>();
			
			for(String mail:mailTo) {
				addresses.add(new InternetAddress(mail));
			}
			
			InternetAddress[] address = addresses.toArray(new InternetAddress[addresses.size()]);
			
			msg.setRecipients(Message.RecipientType.TO, address);// 받는 사람설정

			Transport transport = sess.getTransport("smtp");
			//transport.connect(host, this.id, this.pw);
			transport.connect();
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();

			// Transport.send(msg);//메일 보내기
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("error : " + e.getMessage());
		}
	}

	public void gmail(String mailFrom, String mailTo, String subject, String contents)
			throws Exception {
		String host = this.smtpIp;
		String dateString = Converter.dateToString(new Date());
		String from = mailFrom;
		String fname = "휘슬 관리자";
		String to = mailTo;

		try {
			// 프로퍼티 값 인스턴스 생성과 기본세션(SMTP 서버 호스트 지정)
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.starttls.enable", "true");
			if (isSSL)
				props.setProperty("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);

			Session sess = Session.getDefaultInstance(props, null);
			sess.setDebug(true);

			Message msg = new MimeMessage(sess);

			InternetAddress fadd = new InternetAddress();
			fadd.setAddress(from); // 보내는 사람 email
			fadd.setPersonal(fname, "EUC-KR"); // 보내는 사람 이름
			msg.setFrom(fadd); // 보내는 사람 설정

			msg.setSubject(subject);// 제목 설정
			msg.setSentDate(new java.util.Date());// 보내는 날짜 설정
			msg.setContent(contents, "text/html;charset=euc-kr"); // 내영 설정 (HTML
																	// 형식)

			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);// 받는 사람설정

			Transport transport = sess.getTransport("smtp");
			transport.connect(host, this.id, this.pw);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();

			// Transport.send(msg);//메일 보내기
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("error : " + e.getMessage());
		}
	}
	

	public String toHangul(String str) {
		if (str != null) {
			try {
				return new String(str.getBytes("ISO-8859-1"), "euc-kr");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		} else
			return null;
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			String username = id; // gmail 사용자;
			String password = pw; // 패스워드;
			return new PasswordAuthentication(username, password);
		}
	}

}
