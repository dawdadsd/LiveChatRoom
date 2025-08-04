package xiaowu.social_network_demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("xiaowu.social_network_demo.mapper")
@SpringBootApplication
public class SocialNetworkDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkDemoApplication.class, args);
    }

}
