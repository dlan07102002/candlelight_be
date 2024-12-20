package vn.duclan.candlelight_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
// = @EnableAutoConfiguration, @ComponentScan, @Configuration.
@EnableFeignClients
public class CandlelightBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CandlelightBeApplication.class, args);
    }
}
