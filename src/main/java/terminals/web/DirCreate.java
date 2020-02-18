package terminals.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;


@Component
public class DirCreate {

    @Value("${resources-dir}")
    private String resourcesDir;

    @Value("${db-dir}")
    private String dbDir;

    @Value("${terminals-photos-dir}")
    private String terminalsPhotosDir;

    @Value("${users-photos-dir}")
    private String usersPhotosDir;


//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        new File(resourcesDir).mkdirs();
//        new File(dbDir).mkdirs();
//        new File(terminalsPhotosDir).mkdirs();
//        new File(usersPhotosDir).mkdirs();
//    }

//    @PostConstruct
//    public void init() {
//        new File(resourcesDir).mkdirs();
//        new File(dbDir).mkdirs();
//        new File(terminalsPhotosDir).mkdirs();
//        new File(usersPhotosDir).mkdirs();
//    }



}
