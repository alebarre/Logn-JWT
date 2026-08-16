package com.br.login_jwt;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class LoginJwtApplication {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(LoginJwtApplication.class);

        app.addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) ctx -> {
            Dotenv dotenv = Dotenv.configure()
                    .directory(findEnvDirectory())
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

    /**
     * O .env único do projeto fica na raiz do repositório; a aplicação pode ser
     * iniciada tanto da raiz quanto de backend/, então sobe na árvore de
     * diretórios até encontrá-lo.
     */
    private static String findEnvDirectory() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (dir != null && !Files.exists(dir.resolve(".env"))) {
            dir = dir.getParent();
        }
        return dir != null ? dir.toString() : "./";
    }
}
