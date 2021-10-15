package nl.bingley.customlife.config;

import nl.bingley.customlife.timertasks.RenderTimerTask;
import nl.bingley.customlife.timertasks.TickTimerTask;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Timer;

@ComponentScan("nl.bingley.customlife")
@Configuration
public class Config {

    @Bean
    public Timer scheduleTimers(RenderTimerTask renderTimerTask, TickTimerTask tickTimerTask) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(renderTimerTask, 0, 8);
        timer.scheduleAtFixedRate(tickTimerTask, 0, 8);
        return timer;
    }
}
