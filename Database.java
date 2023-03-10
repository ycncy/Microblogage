import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\talha\\Desktop\\Dev\\Java\\Microblogage\\db.sqlite");

            Statement stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE Users (UserId INTEGER PRIMARY KEY AUTOINCREMENT, Username VARCHAR(255))");
            stmt.executeUpdate("CREATE TABLE Posts (MessageId INTEGER PRIMARY KEY AUTOINCREMENT, UserId INT, Content VARCHAR(255), FOREIGN KEY (UserId) REFERENCES Users (UserId))");

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
