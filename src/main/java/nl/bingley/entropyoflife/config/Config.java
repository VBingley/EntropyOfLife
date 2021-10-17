package nl.bingley.entropyoflife.config;

import nl.bingley.entropyoflife.timers.Renderer;
import nl.bingley.entropyoflife.timers.UniverseUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;

@ComponentScan("nl.bingley.entropyoflife")
@Configuration
public class Config {

    @Bean
    public Timer rendererTimer(Renderer renderer) {
        Timer timer = new Timer(20, renderer);
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
