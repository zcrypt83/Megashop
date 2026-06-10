package pe.edu.idat.megashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MegashopApplication {
    public static void main(String[] args) {
        SpringApplication.run(MegashopApplication.class, args);
    }
}
