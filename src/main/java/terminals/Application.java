package terminals;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;


@SpringBootApplication
public class Application {

    private final static String RESOURCES_DIR = "c:/terminals_resources";

    private final static String DB_DIR = "c:/terminals_resources/db";

    private final static String TERMINALS_RESOURCES_DB = "c:/terminals_resources/photos/terminalsphoto";

    private final static String USERS_RESOURCES_DB = "c:/terminals_resources/photos/usersphoto";

    private static void checkAndCreateDirs() {
        new File(RESOURCES_DIR).mkdirs();
        new File(DB_DIR).mkdirs();
        new File(TERMINALS_RESOURCES_DB).mkdirs();
        new File(USERS_RESOURCES_DB).mkdirs();
    }

    public static void main(String[] args) {
        checkAndCreateDirs();
        SpringApplication.run(Application.class, args);
    }

}
