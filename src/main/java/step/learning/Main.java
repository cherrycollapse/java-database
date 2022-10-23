package step.learning;

import com.google.inject.Guice;

public class Main {
    public static void main( String[] args ) {
        try( ConfigModule configModule = new ConfigModule() ) {
            Guice
                    .createInjector( configModule )
                    .getInstance( AppUser.class )
                    .run() ;
        }
        catch( Exception ex ) {
            System.out.println( "Program terminated: " + ex.getMessage() ) ;
        }
    }
}

/*
Начало: новый проект - Maven
Подключаем зависимость Guice
Создаем стартовую точку и конфигурацию
 */