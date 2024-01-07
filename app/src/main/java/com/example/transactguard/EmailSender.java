package com.example.transactguard;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private static final String SENDER_EMAIL = "comptech2023@gmail.com";
    private static final String SENDER_PASSWORD = "Ctech@2024";
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 465;


    public interface EmailCallback {
        void onEmailSent(boolean success);
    }

    public static void sendWelcomeEmailInBackground(String userEmail, EmailCallback callback) {
        new SendEmailTask(callback).execute(userEmail);
    }

    private static class SendEmailTask extends AsyncTask<String, Void, Boolean> {

        private final EmailCallback callback;

        public SendEmailTask(EmailCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String userEmail = params[0];
            return sendEmailInBackground(userEmail);
        }

        private boolean sendEmailInBackground(String userEmail) {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", HOST);
            properties.put("mail.smtp.port", String.valueOf(PORT));
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            try {
                Message mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(SENDER_EMAIL));
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
                mimeMessage.setSubject("Welcome to TransactGuard");
                mimeMessage.setText("Dear User,\n\nWelcome to TransactGuard! With TransactGuard, you can perform secure credit card transactions.\n\nCheers!\nTransact Guard");

                Transport.send(mimeMessage);
                return true; // Email sent successfully
            } catch (MessagingException e) {
                Log.e("EmailSender", "Error sending email", e);
                return false; // Email sending failed
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                callback.onEmailSent(success);
            }
        }
    }
}
