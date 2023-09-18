import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class PrompterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrompterApiApplication.class, args);
        System.out.println("LocalDateTime.now() = " + LocalDateTime.now());
    }
}
