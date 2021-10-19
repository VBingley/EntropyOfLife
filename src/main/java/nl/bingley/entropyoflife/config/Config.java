package nl.bingley.entropyoflife.config;

import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import nl.bingley.entropyoflife.actionlisteners.RenderUpdateActionListener;
import nl.bingley.entropyoflife.actionlisteners.UniverseUpdateActionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;

@ComponentScan("nl.bingley.entropyoflife")
@Configuration
public class Config {

    @Bean
    public Universe universe(UniverseProperties universeProperties) {
        LifeProperties lifeProperties = universeProperties.getLifeProperties();
        if (universeProperties.isRandomized()) {
            long seed = universeProperties.getRandomSeed() != 0 ? universeProperties.getRandomSeed() : System.nanoTime();
            return new Universe(universeProperties.getSize(), universeProperties.getSpawnSize(), seed,
                    lifeProperties.getHighEnergyState(), lifeProperties.getLowEnergyState());
        } else {
            return new Universe(universeProperties.getSize());
        }
    }

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
