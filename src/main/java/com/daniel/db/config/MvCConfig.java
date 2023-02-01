package com.daniel.db.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
/**
 * Class to configure CORS
 * @author dfcastro
 *
 */
public class MvCConfig {
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
      }	

}
