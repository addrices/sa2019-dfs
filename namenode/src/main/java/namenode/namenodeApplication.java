package namenode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
class namenodeApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "namenodeApplication");

        SpringApplication.run(namenodeApplication.class, args);
    }

}
