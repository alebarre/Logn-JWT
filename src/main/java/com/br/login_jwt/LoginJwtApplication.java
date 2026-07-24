package com.br.login_jwt;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class LoginJwtApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(LoginJwtApplication.class);

		app.addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) ctx -> {
			Dotenv dotenv = Dotenv.configure()
					.directory("./")
					.ignoreIfMalformed()
					.ignoreIfMissing()
					.load();

			Map<String, Object> envMap = new HashMap<>();
			dotenv.entries().forEach(e -> envMap.put(e.getKey(), e.getValue()));

			ctx.getEnvironment().getPropertySources()
					.addFirst(new MapPropertySource("dotenvProperties", envMap));
		});

		app.run(args);
	}
}
