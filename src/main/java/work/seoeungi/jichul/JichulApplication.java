package work.seoeungi.jichul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JichulApplication {

    public static void main(String[] args) {
        SpringApplication.run(JichulApplication.class, args);
    }
}
