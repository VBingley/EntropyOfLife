package nl.bingley.entropyoflife.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class GifGeneratorProperties {

    @Value("${life.gif-renderer.save-location}")
    private String saveLocation;

    @Value("${life.gif-renderer.cell-size}")
    private int cellSize;

    @Value("${life.gif-renderer.end-generation}")
    private int endGeneration;

    @Value("${life.gif-renderer.gen-per-second}")
    private int genPerSec;

    public String getSaveLocation() {
        return saveLocation;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getEndGeneration() {
        return endGeneration;
    }

    public int getGenPerSec() {
        return genPerSec;
    }
}
