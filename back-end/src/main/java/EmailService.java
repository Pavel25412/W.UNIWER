import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    // ВАЖНО: Здесь нужен не обычный пароль от почты, а "Пароль приложения"
    private static final String SENDER_EMAIL = "wuniwer2@gmail.com";
    private static final String APP_PASSWORD = "твой_16_значный_пароль";

    public static void sendRegistrationEmail(String recipientEmail, String loginId, String token) {
        // 1. Настройка свойств SMTP-сервера (пример для Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // 2. Авторизация
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            // 3. Создание письма
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("W.UNIWER - Завершение регистрации");

            // Формируем ссылку (пока localhost, потом заменишь на реальный домен)
            String resetLink = "http://localhost:8080/create-password.html?token=" + token;

            // HTML-содержимое письма
            String htmlContent = "<h3>Добро пожаловать в W.UNIWER!</h3>"
                    + "<p>Ваш уникальный логин кандидата: <b>" + loginId + "</b></p>"
                    + "<p>Чтобы задать пароль и активировать аккаунт, перейдите по ссылке:</p>"
                    + "<a href='" + resetLink + "'>Создать пароль</a>"
                    + "<p><i>Ссылка действительна 24 часа.</i></p>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            // 4. Отправка
            Transport.send(message);
            System.out.println("Письмо успешно отправлено на: " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Ошибка при отправке письма: " + e.getMessage());
            e.printStackTrace();
        }
    }
}