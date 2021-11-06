package nl.bingley.entropyoflife.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@ComponentScan("nl.bingley.entropyoflife.config")
@SpringBootApplication
public class SpringApplication {

    private final Runner runner;

    public SpringApplication(Runner runner) {
        this.runner = runner;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringApplication.class).headless(false).run(args);
    }

    @PostConstruct
    public void run() {
        runner.run();
    }
}
