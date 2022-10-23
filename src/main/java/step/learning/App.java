package step.learning;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.Random;

public class App {
    public void run() {
        // Регистрируем драйвер для MySQL базы
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
        // Выполнение команд: отдельные методы для команд с возвратом и без возврата
        // region CREATE TABLE
        String sql = "CREATE TABLE  IF NOT EXISTS  randoms ( " +
                "id BIGINT PRIMARY KEY," +
                "num INT NOT NULL," +
                "str VARCHAR(64) NULL" +
                ") Engine=InnoDB  DEFAULT CHARSET = UTF8";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);   // вариант без возврата данных
            System.out.println("Query OK");
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
            return;
        }
        // endregion

        // Выполнение запросов
        // region INSERT
        Random rnd = new Random();
        int rndInt = rnd.nextInt();
        String rndStr = "Str " + rnd.nextInt();

        sql = String.format(
                "INSERT INTO randoms VALUES ( UUID_SHORT(), %d, '%s' )",
                rndInt, rndStr);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("Insert OK");
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
            System.out.println(sql);
            return;
        }
        // endregion

        // region SELECT
        sql = "SELECT r.id, r.num, r.str FROM randoms AS r";
        try (Statement statement = connection.createStatement()) {
            ResultSet res =   // ADO ~ DataReader
                    statement.executeQuery(sql);
            while (res.next()) {  // ADO ~ .Read()  -- построчное считывание
                System.out.printf("%d  %d  %s %n",
                        res.getLong(1),       // getT - получить значение из "строки" таблицы
                        res.getInt("num"),    // !! JDBC - номерация начинается с 1 (ADO - c 0)
                        res.getString(3)      // можно обращаться по имени поля
                );
            }
            res.close();
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
            System.out.println(sql);
            return;
        }
        //endregion

        // region Подготовленные запросы
        sql = "INSERT INTO randoms VALUES ( UUID_SHORT(), ?, ? )";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            for (int i = 100500; i < 100503; ++i) {
                rndStr = "Prep " + rnd.nextInt();
                prep.setInt(1, i);
                prep.setString(2, rndStr);
                prep.executeUpdate();
            }
            System.out.println("Prep OK");
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
            System.out.println(sql);
            return;
        }

        // endregion

// Задание: написать и обработать запросы, выводящие все записи с положительными числами (num)
// и (отдельно) - с отрицательными
        System.out.println("------------------------------------------------");
        sql = "SELECT * FROM randoms ORDER BY num DESC ";
        try (Statement statement = connection.createStatement();
             ResultSet res = statement.executeQuery(sql)) {
            while (res.next()) {
                System.out.printf("%d  %d  %s %n",
                        res.getLong(1), res.getInt("num"), res.getString(3));
            }
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
            System.out.println(sql);
            return;
        }
/*
Д.З. (На базе повтора пройденных упражнений)
Создать таблицу randoms2 (через команду Java)
- id по стандарту UUID (полный)
- поля для чисел: целые и дробные (хотя бы int, float)
- поле для символьных значений (string)
- поле для даты-времени
Обеспечить команды случайного заполнения, использовать подготовленные запросы
Реализовать вывод содержимого на консоль.
 */

        try {
            connection.close();
            DriverManager.deregisterDriver(mysqlDriver);
        } catch (SQLException ignored) {
        }
    }
}
/*
    Работа с СУБД.
    JDBC - Java Database Connectivity - Java API для работы с СУБД (аналог ADO.NET)
    DBMS(СУБД)--Connector(Driver) ---- JDBC--Lang(Java)
    1. Установка СУБД - Oracle MariaDB (либо MySQL) - [в сборке XAMPP]
    2. Настраиваем БД : создаем схему и пользователя
     - открываем консоль БД (workbench/phpmyadmin)
       = cd mysql/bin    -- заходим в папку mysql
       = mysql -u root   -- если есть пароль: mysql -u root -p
       = CREATE DATABASE java191;  -- создаем БД(схему)
       = SHOW DATABASES; -- проверка
       = GRANT ALL PRIVILEGES ON java191.*
         TO 'user191'@'localhost'
         IDENTIFIED BY 'pass191' ;
            -> На старых версиях - отдельно
               -> CREATE USER 'user191'@'localhost' IDENTIFIED BY 'pass191' ;
               -> GRANT ALL PRIVILEGES ON java191.* TO 'user191'@'localhost' ;
       = FLUSH PRIVILEGES;   -- сохраняем привилегии
       = проверяем: выходим как root и заходим как java191
       = quit
       = mysql -u user191 -p
         Enter password: *******  (pass191)
         -> SHOW DATABASES;
         ...java191...
    3. Настраиваем Driver-JDBC
     Maven (repos) - ищем mysql -- MySQL Connector/J
     добавляем зависимость в pom.xml, синхронизируем
 */
/*
    Подготовленные запросы.
    Безопасность: SQL-injection
    "INSERT INTO randoms VALUES ( UUID_SHORT(), %d, '%s' )", rndInt, rndStr
    rndStr = "Строка"
     INSERT INTO randoms VALUES ( UUID_SHORT(), 10, 'Строка' )
    rndStr = "A'); DROP TABLE randoms --"
     INSERT INTO randoms VALUES ( UUID_SHORT(), 10, 'A'); DROP TABLE randoms --' )
    Подготовленные запросы разделяют код запроса и данные для запроса:
     в запросе места для данных обозначают плейсхолдерами "?"
      sql = "INSERT INTO randoms VALUES ( UUID_SHORT(), ?, ? )"
     запрос "компилируют" - подготавливают
      prep = prepare( sql )
     передают данные - указание типа через сет-тер позволяет избежать преобразований
      prep.setInt(1, 10);
      prep.setString(2, "...");
     запрос выполняется
      prep.execute()   //  prep.execute( [10, "..."] )
   Повтор запросов
    если запрос нужно повторить с измененными данными, то выгоднее его подготовить
    и затем несколько раз выполнить
    sql = "SELECT ... WHERE month = ?"
    prep = prepare( sql )
    for(i) {
      prep.setInt(1, i);
      prep.execute();
    }
 */
/*
ORM - Отображение данных на объекты - создание программных сущностей,
реализующих взаимодействие с реальными данными.
Особенности
 - структура данных часто не соответствует программной (табличное представление)
 - несовместимые или неоднозначно совместимые типы (NULL -- int, string - CHAR/VARCHAR/TEXT)
 - возможность перехода на другой источник (DB / WebAPI)
Для работы с данными в программе выделяется несколько "слоев" (layers)
 - слой сущностей - структур, описывающих данные
 - слой доступа к данным (DAL, DAO)
 - слой логики (BLL)
Задание: реализовать управление пользователями - регистрацию, авторизацию
Анализ:
 определяемся со структурой данных.
 CREATE TABLE Users (
    `id`    CHAR(36)     NOT NULL   COMMENT 'UUID',
    `login` VARCHAR(32)  NOT NULL,
    `pass`  CHAR(40)     NOT NULL   COMMENT 'SHA-160 hash',
    `name`  TINYTEXT     NOT NULL,
    PRIMARY KEY(id)
 ) Engine=InnoDB  DEFAULT CHARSET = UTF8
-ORM-
class User {
    private String id;
    private String login;
    private String pass;
    private String name;
}
 */