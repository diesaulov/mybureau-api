package de.mybureau.time.config;

import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableJpaRepositories({"de.mybureau.time.repository"})
@EnableTransactionManagement
@EnableWebSecurity
@ComponentScan({"de.mybureau.time"})
@EnableWebMvc
@Import({ServletWebServerFactoryAutoConfiguration.class})
public class ApplicationConfiguration {
}
