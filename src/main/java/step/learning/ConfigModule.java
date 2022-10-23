package step.learning;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mysql.cj.jdbc.Driver;
import step.learning.services.hash.HashService;
import step.learning.services.hash.Sha1HashService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConfigModule extends AbstractModule implements AutoCloseable {
    @Override
    protected void configure() {
        bind( HashService.class ).to( Sha1HashService.class ) ;
    }

    private Connection connection ;
    private Driver mysqlDriver ;
    @Provides
    Connection getConnection() throws SQLException {
        if( connection == null ) {
            mysqlDriver = new com.mysql.cj.jdbc.Driver() ;
            DriverManager.registerDriver( mysqlDriver ) ;
            String connectionString = "jdbc:mysql://localhost:3306/java191" +  // location
                    "?useUnicode=true&characterEncoding=UTF-8" ;
            connection = DriverManager.getConnection( connectionString, "user191", "pass191" ) ;
        }
        return connection ;
    }

    public void close() {
        if( connection != null )
            try { connection.close(); } catch(Exception ignored){}
        if( mysqlDriver != null )
            try { DriverManager.deregisterDriver( mysqlDriver ) ; } catch(Exception ignored){}
    }
}