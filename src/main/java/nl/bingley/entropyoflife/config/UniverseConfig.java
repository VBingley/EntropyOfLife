package nl.bingley.entropyoflife.config;

import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan({
        "nl.bingley.entropyoflife.config.properties",
        "nl.bingley.entropyoflife.kernels"})
@Configuration
public class UniverseConfig {

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
}
