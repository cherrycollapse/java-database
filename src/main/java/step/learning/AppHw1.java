package step.learning;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.Random;

public class AppHw1 {
    public void run() {
        //  Регистрируем драйвер для MySQL базы
        Driver mysqlDriver;
        try {
            mysqlDriver = new Driver();
            DriverManager.registerDriver(mysqlDriver);
        } catch (SQLException ex) {
            System.out.println("Driver ini error: " + ex.getMessage());
            return;
        }
        // connection to СУБД->БД
        String connectionString = "jdbc:mysql://localhost:3306/java191" +  // location
                "?useUnicode=true&characterEncoding=UTF-8";               // encoding
        Connection connection;
        try {
            connection = DriverManager.getConnection(connectionString, "user191", "pass191");
        } catch (SQLException ex) {
            System.out.println("DB Connection error: " + ex.getMessage());
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS randoms ( " +
                "id BIGINT PRIMARY KEY," +
                "num INT NOT NULL," +
                "str VARCHAR(64) NULL" +
                ") Engine=InnoDB DEFAULT CHARSET = UTF8";

        // Написать запрос INSERT, вносящий данные (сформировать случайно) в таблицу randoms
        Random rnd = new Random();

        String insertSql ="INSERT INTO randoms VALUES ( "
                + rnd.nextInt(1000) + ","
                + rnd.nextInt(20) + ","
                + String.valueOf(rnd.nextInt(20))
                + " )";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.executeUpdate(insertSql);
            System.out.println("Query OK");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }

        try {
            connection.close() ;
            DriverManager.deregisterDriver( mysqlDriver ) ;
        } catch( SQLException ignored ) {}

    }

}
