package nl.bingley.entropyoflife.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(
        value="life.gif-renderer.enabled",
        havingValue = "true",
        matchIfMissing = false)
@ComponentScan({
        "nl.bingley.entropyoflife.application.gifrendererapplication"})
@Configuration
public class GifRendererConfig {
}
