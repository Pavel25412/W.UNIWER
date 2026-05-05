import io.javalin.Javalin;

import java.util.Map;

public class MainServer {
    public static void main(String[] args) {
        LoginService loginService = new LoginService();

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.reflectClientOrigin = true;
                    it.allowCredentials = true;
                });
            });
        }).start(7070);

        System.out.println("Сервер W.UNIWER запущен на http://localhost:7070");

        app.post("/api/register", ctx -> {
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
            boolean success = loginService.registerNewCandidate(req.fullName, req.email);
            if (success) ctx.status(201).result("ОК");
            else ctx.status(400).result("Error");
        });

        app.post("/api/set-password", ctx -> {
            PasswordRequest req = ctx.bodyAsClass(PasswordRequest.class);
            System.out.println("Получен запрос на установку пароля!");
            System.out.println("Токен: " + req.token);
            boolean success = loginService.setPassword(req.token, req.password);

            if (success) {
                ctx.status(200).result("Пароль успешно установлен!");
            } else {
                ctx.status(400).result("Ошибка: неверный или устаревший токен.");
            }
        });

        app.post("/api/login", ctx -> {
            LoginRequest req = ctx.bodyAsClass(LoginRequest.class);
            boolean success = loginService.authenticateUser(req.username, req.password);

            if (success) {
                ctx.sessionAttribute("currentUser", req.username);
                ctx.status(200).result("Успех!");
            } else {
                ctx.status(401).result("Доступ запрещен!");
            }
        });

        app.get("/api/check-session", ctx -> {
            String userIdentifier = ctx.sessionAttribute("currentUser");

            if (userIdentifier != null) {
                Map<String, String> userInfo = loginService.getUserInfo(userIdentifier);
                ctx.json(userInfo);
            } else {
                ctx.status(401).result("Не авторизован");
            }
        });
    }
}

class PasswordRequest {
    public String token;
    public String password;
}

class RegisterRequest {
    public String fullName;
    public String email;
}

class LoginRequest {
    public String username;
    public String password;
}