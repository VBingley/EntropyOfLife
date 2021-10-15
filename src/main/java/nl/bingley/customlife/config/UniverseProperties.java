package nl.bingley.customlife.config;

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

    private final LifeProperties lifeProperties;

    public UniverseProperties(Environment env, @Value("${life.mode}") String mode) {
        lifeProperties = new LifeProperties(env, mode);
    }

    public int getSize() {
        return size;
    }

    public int getSpawnSize() {
        return spawnSize;
    }

    public LifeProperties getLifeProperties() {
        return lifeProperties;
    }
}
