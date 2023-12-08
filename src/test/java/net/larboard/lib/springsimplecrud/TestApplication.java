package net.larboard.lib.springsimplecrud;

import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public SimpleCrudManager simpleCrudManager(EntityManager entityManager, ApplicationEventPublisher applicationEventPublisher) {
        return SimpleCrudManager.getInstance(entityManager, applicationEventPublisher);
    }

}
