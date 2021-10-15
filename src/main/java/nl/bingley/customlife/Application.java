package nl.bingley.customlife;

import nl.bingley.customlife.listeners.MouseInputListener;
import nl.bingley.customlife.listeners.KeyInputListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@ComponentScan("nl.bingley.customlife.config")
@SpringBootApplication
public class Application {

    private final UniversePanel universePanel;
    private final KeyInputListener keyInputListener;
    private final MouseInputListener mouseInputListener;

    public Application(UniversePanel universePanel, KeyInputListener keyInputListener, MouseInputListener mouseInputListener) {
        this.universePanel = universePanel;
        this.keyInputListener = keyInputListener;
        this.mouseInputListener = mouseInputListener;
    }

    //Guns, these produce gliders
    private static final String PATTERN_GOSPER_GLIDER_GUN = "000000000000000000000000100000000000" +
        "000000000000000000000010100000000000" +
        "000000000000110000001100000000000011" +
        "000000000001000100001100000000000011" +
        "110000000010000010001100000000000000" +
        "110000000010001011000010100000000000" +
        "000000000010000010000000100000000000" +
        "000000000001000100000000000000000000" +
        "000000000000110000000000000000000000"; //x=36

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).headless(false).run(args);
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
        frame.setSize(1000, 1000);
        frame.setVisible(true);
    }
}
