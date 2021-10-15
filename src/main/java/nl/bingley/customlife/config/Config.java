package nl.bingley.customlife.config;

import nl.bingley.customlife.timer.Renderer;
import nl.bingley.customlife.timer.UniverseUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;

@ComponentScan("nl.bingley.customlife")
@Configuration
public class Config {

    @Bean
    public Timer rendererTimer(Renderer renderer) {
        Timer timer = new Timer(32, renderer);
        timer.start();
        return timer;
    }

    @Bean
    public Timer universeUpdateTimer(UniverseUpdater updater) {
        Timer timer = new Timer(250, updater);
        timer.start();
        return timer;
    }
}
