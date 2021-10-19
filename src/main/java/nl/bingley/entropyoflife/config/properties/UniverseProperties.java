package nl.bingley.entropyoflife.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class UniverseProperties {

    @Value("${life.universe.size}")
    private int size;
    @Value("${life.universe.spawn}")
    private int spawnSize;
    @Value("${life.universe.random-seed:0}")
    private long randomSeed;

    private final LifeProperties lifeProperties;

    public UniverseProperties(Environment env, @Value("${life.ruleset}") String ruleset) {
        lifeProperties = new LifeProperties(env, ruleset);
    }

    public int getSize() {
        return size;
    }

    public int getSpawnSize() {
        return spawnSize;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public boolean isRandomized() {
        return true;
    }

    public LifeProperties getLifeProperties() {
        return lifeProperties;
    }
}
