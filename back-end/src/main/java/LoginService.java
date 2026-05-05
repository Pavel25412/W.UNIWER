import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {
    public String generateCustomLogin() {
        int year = LocalDate.now().getYear();
        int nextId = getNextSequenceValue();
        return String.format("#WU-%d-%04d", year, nextId);
    }

    private int getNextSequenceValue() {
        String sql = "SELECT users_seq.NEXTVAL FROM dual";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean registerNewCandidate(String fullName, String email) {
        int nextId = getNextSequenceValue();
        String loginId = String.format("#WU-%d-%04d", 2026, nextId);
        String token = UUID.randomUUID().toString();

        String sql = "INSERT INTO users (user_id, full_name, email, login_id, verification_token, status) " +
                "VALUES (?, ?, ?, ?, ?, 'Pending')";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nextId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, email);
            pstmt.setString(4, loginId);
            pstmt.setString(5, token);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setPassword(String token, String rawPassword) {
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String sql = "UPDATE users SET password = ?, status = 'Active', " +
                "verification_token = NULL WHERE verification_token = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, token);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Успех: Пароль установлен, аккаунт активирован!");
                return true;
            } else {
                System.err.println("Ошибка: Неверный или устаревший токен.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean authenticateUser(String loginOrEmail, String rawPassword) {
        String sql = "SELECT password FROM users WHERE (login_id = ? OR email = ?) AND status = 'Active'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loginOrEmail);
            pstmt.setString(2, loginOrEmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    return BCrypt.checkpw(rawPassword, hashedPassword);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> getUserInfo(String loginOrEmail) {
        Map<String, String> info = new HashMap<>();
        String sql = "SELECT full_name, login_id FROM users WHERE login_id = ? OR email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loginOrEmail);
            pstmt.setString(2, loginOrEmail);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    info.put("fullName", rs.getString("full_name"));
                    info.put("loginId", rs.getString("login_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static void main(String[] args) {
        LoginService service = new LoginService();

        System.out.println("=== Тестирование регистрации W.UNIWER ===");

        // test request
        String testName = "Kukushka";
        String testEmail = "aloblya.test@example.pl";

        boolean success = service.registerNewCandidate(testName, testEmail);

        if (success) {
            System.out.println("---");
            System.out.println("Пользователь успешно создан!");
            System.out.println("Теперь проверь таблицу USERS в SQL Developer.");
        } else {
            System.err.println("---");
            System.err.println("РЕЗУЛЬТАТ: Ошибка");
        }
    }
}