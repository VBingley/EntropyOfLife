package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.inputlisteners.MouseInputListener;
import nl.bingley.entropyoflife.inputlisteners.KeyInputListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@ComponentScan("nl.bingley.entropyoflife.config")
@SpringBootApplication
public class SpringApplication {

    private final UniversePanel universePanel;
    private final KeyInputListener keyInputListener;
    private final MouseInputListener mouseInputListener;

    public SpringApplication(UniversePanel universePanel, KeyInputListener keyInputListener, MouseInputListener mouseInputListener) {
        this.universePanel = universePanel;
        this.keyInputListener = keyInputListener;
        this.mouseInputListener = mouseInputListener;
    }

    public static void main(String[] args) {

        new SpringApplicationBuilder(SpringApplication.class).headless(false).run(args);
    }

    @PostConstruct
    public void run() {
        JFrame frame = new JFrame();
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 20);
        universePanel.setFont(font);
        universePanel.setFocusable(true);
        universePanel.addKeyListener(keyInputListener);
        universePanel.addMouseListener(mouseInputListener);
        universePanel.addMouseMotionListener(mouseInputListener);
        universePanel.addMouseWheelListener(mouseInputListener);
        frame.getContentPane().add(universePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 1024);
        frame.setVisible(true);
    }
}
