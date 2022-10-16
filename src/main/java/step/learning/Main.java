package step.learning;

import com.google.inject.Guice;

public class Main {
    public static void main(String[] args) {
        Guice.createInjector(new ConfigModule()).getInstance(App.class).run();
    }
}