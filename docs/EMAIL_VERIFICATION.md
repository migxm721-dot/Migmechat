# Email Verification with Brevo

This document describes the Brevo (formerly Sendinblue) email integration used for account verification and password reset in Migmechat.

## Overview

Migmechat uses Brevo's transactional email API to send:
- **Verification emails** – sent when a user registers a new account
- **Password reset emails** – sent when a user requests a password reset

## Brevo Setup

### 1. Create a Brevo Account
1. Sign up at [https://www.brevo.com/](https://www.brevo.com/)
2. Verify your Brevo account email address

### 2. Obtain an API Key
1. Log in to your Brevo dashboard
2. Navigate to **Settings → SMTP & API → API Keys**
3. Click **Generate a new API key**
4. Name it (e.g. `Migmechat Production`) and copy the key (format: `xkeysib-xxxxxxxxxxxxx`)

### 3. Verify the Sender Domain
To send from `noreply@migxchat.net`:
1. In Brevo, go to **Settings → Senders & IPs → Domains**
2. Add the domain `migxchat.net`
3. Add the DNS records Brevo provides (SPF, DKIM, DMARC) to your DNS provider
4. Allow 24–48 hours for DNS propagation and domain verification

## Configuration

Copy the example configuration file and fill in your API key:

```bash
cp etc/email.properties.example etc/email.properties
```

Then edit `etc/email.properties`:

```properties
# Brevo Email Configuration
email.enabled=true

# Brevo API Settings
brevo.api.key=xkeysib-your-actual-api-key-here
brevo.from.email=noreply@migxchat.net
brevo.from.name=Migmechat

# Email Settings
email.verification.subject=Verify Your Migmechat Account
email.verification.expiry.minutes=10
email.password.reset.subject=Reset Your Migmechat Password
email.password.reset.expiry.minutes=30
```

> **Security note:** `etc/email.properties` is listed in `.gitignore` and must never be committed to the repository.

## Email Templates

### Verification Email
- **Subject:** Verify Your Migmechat Account
- **Header:** Green background (`#25d366`) with "Welcome to Migmechat!"
- **Body:** Greeting with username, 6-digit code displayed prominently, verify button, 10-minute expiry notice

### Password Reset Email
- **Subject:** Reset Your Migmechat Password
- **Header:** Red/orange background (`#e74c3c`) with "Password Reset Request"
- **Body:** Reset code displayed prominently, reset button, 30-minute expiry notice

## Integration Points

The `EmailService` singleton (`com.projectgoth.fusion.services.EmailService`) wraps the `BrevoEmailClient` and is used in `UserBean` for:

```java
EmailService emailService = EmailService.getInstance();
boolean sent = emailService.sendVerificationEmail(emailAddress, username, verificationCode);
```

## Testing

### Manual Testing Checklist
- [ ] Register a new user with a real email address
- [ ] Receive the verification email (check spam if needed)
- [ ] Email displays correctly in major clients (Gmail, Outlook)
- [ ] Verification code works end-to-end
- [ ] Password reset email is received and the code works
- [ ] Resend verification works

### Disable for Testing
Set `email.enabled=false` in `etc/email.properties` to skip actual email sending (the service will log skipped sends instead).

## Troubleshooting

| Symptom | Possible Cause | Fix |
|---|---|---|
| Emails not sent, no error logged | `email.enabled=false` | Set to `true` in `etc/email.properties` |
| `Could not load etc/email.properties` in logs | Config file missing | Copy from `etc/email.properties.example` |
| Brevo API returns 401 | Invalid API key | Regenerate and update `brevo.api.key` |
| Brevo API returns 400 | Invalid sender email | Verify domain in Brevo dashboard |
| Emails land in spam | Domain not verified with SPF/DKIM | Complete domain verification in Brevo |

## Deployment

1. Copy configuration: `cp etc/email.properties.example etc/email.properties`
2. Set your API key in `etc/email.properties`
3. Build: `mvn clean package -DskipTests`
4. Copy config to container: `docker cp etc/email.properties migmechat-gateway:/app/etc/`
5. Deploy JAR and restart: `docker restart migmechat-gateway`
6. Monitor logs for: `Email service initialized with Brevo (enabled=true)`

## Rollback

To disable email sending without redeploying:
1. Set `email.enabled=false` in `etc/email.properties`
2. Restart the gateway: `docker restart migmechat-gateway`

Users can still register; email verification will be skipped gracefully.
