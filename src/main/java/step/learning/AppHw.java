package step.learning;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

// Д.З. (На базе повтора пройденных упражнений)
//Создать таблицу randoms2 (через команду Java)
//- id по стандарту UUID (полный)
//- поля для чисел: целые и дробные (хотя бы int, float)
//- поле для символьных значений (string)
//- поле для даты-времени
//Обеспечить команды случайного заполнения, использовать подготовленные запросы
//Реализовать вывод содержимого на консоль.

public class AppHw {

    public void run(){

        //  Регистрируем драйвер для MySQL базы
        Driver mysqlDriver;

        try {
            mysqlDriver = new Driver();
            DriverManager.registerDriver(mysqlDriver);
        } catch (SQLException ex) {
            System.out.println("Driver ini error: " + ex.getMessage());
            return;
        }

        // подключение к СУБД->БД
        String connectionString = "jdbc:mysql://localhost:3306/java191" +  // location
                "?useUnicode=true&characterEncoding=UTF-8";               // encoding
        Connection connection;
        try {
            connection = DriverManager.getConnection(connectionString, "user191", "pass191");
        } catch (SQLException ex) {
            System.out.println("DB Connection error: " + ex.getMessage());
            return;
        }

        createTable(connection);
        fillTable(connection);
        showTable(connection);

        try {
            connection.close();
            DriverManager.deregisterDriver(mysqlDriver);
        } catch (SQLException ignored) {
        }

    }


    void createTable(Connection connection) {
        String sql = "CREATE TABLE IF NOT EXISTS randoms2 ( " +
                "id BINARY(16) PRIMARY KEY," +                             //- id по стандарту UUID (полный)
                "numint INT NOT NULL," +                                    //- поля для чисел: целые и дробные (хотя бы int, float)
                "numfloat float NOT NULL," +
                "str VARCHAR(64) NOT NULL," +                               //- поле для символьных значений (string)
                "datatime date NOT NULL" +                                  //- поле для даты-времени
                " ) Engine=InnoDB DEFAULT CHARSET = UTF8";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println("DB connection error! " + ex.getMessage());
            System.out.println(sql);
            return;
        }
    }

    void fillTable(Connection connection) {
        Random rnd = new Random();
        String rndStr = "Str";
        Date date= new Date(new java.util.Date().getTime());                    // данные

        String sql = "INSERT INTO randoms2 VALUES (UUID(), ?, ?, ?, ?)";        // запрос

        try (PreparedStatement prep = connection.prepareStatement(sql)){
            for(int i = 0; i < 10; i++){
                prep.setInt(1, i + rnd.nextInt());
                prep.setFloat(2, rnd.nextFloat());
                prep.setString(3, rndStr + rnd.nextInt());
                prep.setDate(4,  date);
                prep.executeUpdate();
            }

        } catch (SQLException ex) {
            System.out.println("Fill table error. " + ex.getMessage());
        }
    }

    void showTable(Connection connection){
        String sql = "SELECT * FROM randoms2" ;

        try (Statement statement = connection.createStatement()) {
            ResultSet res = statement.executeQuery(sql);
            while (res.next()) {  // ADO ~ .Read()  -- построчное считывание
                System.out.printf("%d  %d %f  %s %s %n",
                        res.getString(1), res.getInt(2), res.getFloat(3),res.getString(4), res.getDate(5));
            }
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
            System.out.println(sql);
            return;
        }


    }




}



