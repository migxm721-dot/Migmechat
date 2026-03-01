package com.projectgoth.fusion.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Email client that sends transactional emails via the Brevo (formerly Sendinblue) API.
 */
public class BrevoEmailClient {

    private static final Logger log = Logger.getLogger(BrevoEmailClient.class);

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final String apiKey;
    private final String fromEmail;
    private final String fromName;

    public BrevoEmailClient(String apiKey, String fromEmail, String fromName) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    public boolean sendVerificationEmail(String toEmail, String username, String verificationCode) {
        String subject = "Verify Your Migmechat Account";
        String htmlContent = buildVerificationEmailHTML(username, verificationCode);
        return sendEmail(toEmail, subject, htmlContent);
    }

    public boolean sendPasswordResetEmail(String toEmail, String username, String resetCode) {
        String subject = "Reset Your Migmechat Password";
        String htmlContent = buildPasswordResetEmailHTML(username, resetCode);
        return sendEmail(toEmail, subject, htmlContent);
    }

    private boolean sendEmail(String toEmail, String subject, String htmlContent) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(BREVO_API_URL);
            post.setHeader("api-key", apiKey);
            post.setHeader("content-type", "application/json");
            post.setHeader("accept", "application/json");

            String body = buildRequestBody(toEmail, subject, htmlContent);
            post.setEntity(new StringEntity(body, "UTF-8"));

            CloseableHttpResponse response = httpClient.execute(post);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    log.info("Email sent via Brevo to: " + toEmail);
                    return true;
                } else {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    log.error("Brevo API error " + statusCode + " sending to " + toEmail + ": " + responseBody);
                    return false;
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            log.error("Failed to send email via Brevo to " + toEmail, e);
            return false;
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.warn("Failed to close HTTP client", e);
            }
        }
    }

    private String buildRequestBody(String toEmail, String subject, String htmlContent) {
        String escapedHtml = htmlContent.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
        return "{"
                + "\"sender\":{\"email\":\"" + fromEmail + "\",\"name\":\"" + fromName + "\"},"
                + "\"to\":[{\"email\":\"" + toEmail + "\"}],"
                + "\"subject\":\"" + subject + "\","
                + "\"htmlContent\":\"" + escapedHtml + "\""
                + "}";
    }

    private String buildVerificationEmailHTML(String username, String verificationCode) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<title>Verify Your Migmechat Account</title></head>"
                + "<body style='margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif'>"
                + "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:20px'>"
                + "<table width='600' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:8px;overflow:hidden'>"
                + "<tr><td style='background:#25d366;padding:30px;text-align:center'>"
                + "<h1 style='color:#ffffff;margin:0;font-size:28px'>Welcome to Migmechat!</h1>"
                + "</td></tr>"
                + "<tr><td style='padding:40px 30px'>"
                + "<p style='font-size:16px;color:#333'>Hello <strong>" + escapeHtml(username) + "</strong>,</p>"
                + "<p style='font-size:16px;color:#333'>Thank you for registering. Please use the code below to verify your email address:</p>"
                + "<div style='text-align:center;margin:30px 0'>"
                + "<div style='display:inline-block;background:#f8f9fa;border:2px solid #25d366;border-radius:8px;padding:20px 40px'>"
                + "<span style='font-size:36px;font-weight:bold;letter-spacing:8px;color:#25d366'>" + escapeHtml(verificationCode) + "</span>"
                + "</div></div>"
                + "<div style='text-align:center;margin:20px 0'>"
                + "<span style='background:#25d366;color:#ffffff;padding:14px 32px;border-radius:6px;display:inline-block;font-size:16px;font-weight:bold'>Verify Account</span>"
                + "</div>"
                + "<p style='font-size:14px;color:#666'>This code expires in <strong>10 minutes</strong>.</p>"
                + "<p style='font-size:14px;color:#666'>If you did not create an account, please ignore this email.</p>"
                + "</td></tr>"
                + "<tr><td style='background:#f8f9fa;padding:20px;text-align:center'>"
                + "<p style='font-size:12px;color:#999;margin:0'>&copy; Migmechat. All rights reserved.</p>"
                + "</td></tr>"
                + "</table></td></tr></table>"
                + "</body></html>";
    }

    private String buildPasswordResetEmailHTML(String username, String resetCode) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<title>Reset Your Migmechat Password</title></head>"
                + "<body style='margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif'>"
                + "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:20px'>"
                + "<table width='600' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:8px;overflow:hidden'>"
                + "<tr><td style='background:#e74c3c;padding:30px;text-align:center'>"
                + "<h1 style='color:#ffffff;margin:0;font-size:28px'>Password Reset Request</h1>"
                + "</td></tr>"
                + "<tr><td style='padding:40px 30px'>"
                + "<p style='font-size:16px;color:#333'>Hello <strong>" + escapeHtml(username) + "</strong>,</p>"
                + "<p style='font-size:16px;color:#333'>We received a request to reset your password. Use the code below:</p>"
                + "<div style='text-align:center;margin:30px 0'>"
                + "<div style='display:inline-block;background:#f8f9fa;border:2px solid #e74c3c;border-radius:8px;padding:20px 40px'>"
                + "<span style='font-size:36px;font-weight:bold;letter-spacing:8px;color:#e74c3c'>" + escapeHtml(resetCode) + "</span>"
                + "</div></div>"
                + "<div style='text-align:center;margin:20px 0'>"
                + "<span style='background:#e74c3c;color:#ffffff;padding:14px 32px;border-radius:6px;display:inline-block;font-size:16px;font-weight:bold'>Reset Password</span>"
                + "</div>"
                + "<p style='font-size:14px;color:#666'>This code expires in <strong>30 minutes</strong>.</p>"
                + "<p style='font-size:14px;color:#666'>If you did not request a password reset, please ignore this email and your password will remain unchanged.</p>"
                + "</td></tr>"
                + "<tr><td style='background:#f8f9fa;padding:20px;text-align:center'>"
                + "<p style='font-size:12px;color:#999;margin:0'>&copy; Migmechat. All rights reserved.</p>"
                + "</td></tr>"
                + "</table></td></tr></table>"
                + "</body></html>";
    }

    private static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
