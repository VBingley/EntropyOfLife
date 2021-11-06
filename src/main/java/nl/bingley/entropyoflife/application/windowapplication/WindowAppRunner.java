package nl.bingley.entropyoflife.application.windowapplication;

import nl.bingley.entropyoflife.application.Runner;
import nl.bingley.entropyoflife.inputlisteners.KeyInputListener;
import nl.bingley.entropyoflife.inputlisteners.MouseInputListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class WindowAppRunner implements Runner {

    private final UniversePanel universePanel;
    private final KeyInputListener keyInputListener;
    private final MouseInputListener mouseInputListener;

    public WindowAppRunner(UniversePanel universePanel, KeyInputListener keyInputListener, MouseInputListener mouseInputListener) {
        this.universePanel = universePanel;
        this.keyInputListener = keyInputListener;
        this.mouseInputListener = mouseInputListener;
    }

    @Override
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

        frame.pack();
        Insets insets = frame.getInsets();
        frame.setSize(1024 + insets.left + insets.right, 1024 + insets.top + insets.bottom);
        frame.setVisible(true);
    }
}
