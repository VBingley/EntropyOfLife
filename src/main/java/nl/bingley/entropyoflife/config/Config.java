package nl.bingley.entropyoflife.config;

import nl.bingley.entropyoflife.models.Universe;
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
    public Timer rendererTimer(Renderer renderer) {
        Timer timer = new Timer(20, renderer);
        timer.start();
        return timer;
    }

    @Bean
    public Timer universeUpdateTimer(UniverseUpdater updater) {
        Timer timer = new Timer(125, updater);
        timer.start();
        return timer;
    }
}
