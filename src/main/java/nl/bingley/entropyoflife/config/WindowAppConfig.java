package nl.bingley.entropyoflife.config;

import nl.bingley.entropyoflife.actionlisteners.RenderUpdateActionListener;
import nl.bingley.entropyoflife.actionlisteners.UniverseUpdateActionListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;

@ConditionalOnProperty(
        value="life.gif-renderer.enabled",
        havingValue = "false",
        matchIfMissing = false)
@ComponentScan({
        "nl.bingley.entropyoflife.actionlisteners",
        "nl.bingley.entropyoflife.inputlisteners",
        "nl.bingley.entropyoflife.application.windowapplication"})
@Configuration
public class WindowAppConfig {

    @Bean
    public Timer rendererTimer(RenderUpdateActionListener renderUpdateActionListener) {
        Timer timer = new Timer(20, renderUpdateActionListener);
        timer.start();
        return timer;
    }

    @Bean
    public Timer universeUpdateTimer(UniverseUpdateActionListener updater) {
        Timer timer = new Timer(125, updater);
        timer.start();
        return timer;
    }
}
