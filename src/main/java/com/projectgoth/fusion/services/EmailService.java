package com.projectgoth.fusion.services;

import com.projectgoth.fusion.util.BrevoEmailClient;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton service for sending transactional emails via Brevo.
 * Load configuration from etc/email.properties.
 */
public class EmailService {

    private static final Logger log = Logger.getLogger(EmailService.class);

    private static final String CONFIG_FILE = "etc/email.properties";

    private static volatile EmailService instance;

    private final boolean enabled;
    private final BrevoEmailClient brevoClient;

    private EmailService(Properties config) {
        this.enabled = Boolean.parseBoolean(config.getProperty("email.enabled", "false"));
        String apiKey = config.getProperty("brevo.api.key", "");
        String fromEmail = config.getProperty("brevo.from.email", "noreply@migxchat.net");
        String fromName = config.getProperty("brevo.from.name", "Migmechat");
        this.brevoClient = new BrevoEmailClient(apiKey, fromEmail, fromName);
        log.info("Email service initialized with Brevo (enabled=" + enabled + ")");
    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            Properties config = loadConfig();
            instance = new EmailService(config);
        }
        return instance;
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(CONFIG_FILE);
            props.load(in);
        } catch (IOException e) {
            log.warn("Could not load " + CONFIG_FILE + "; email sending will be disabled: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("Failed to close config stream", e);
                }
            }
        }
        return props;
    }

    public boolean sendVerificationEmail(String email, String username, String code) {
        if (!enabled) {
            log.info("Email service disabled. Skipping verification email to: " + email);
            return false;
        }
        return brevoClient.sendVerificationEmail(email, username, code);
    }

    public boolean sendPasswordResetEmail(String email, String username, String code) {
        if (!enabled) {
            log.info("Email service disabled. Skipping password reset email to: " + email);
            return false;
        }
        return brevoClient.sendPasswordResetEmail(email, username, code);
    }
}
