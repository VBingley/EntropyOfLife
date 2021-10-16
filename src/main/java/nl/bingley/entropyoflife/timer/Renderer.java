package nl.bingley.entropyoflife.timer;

import nl.bingley.entropyoflife.UniversePanel;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class Renderer implements ActionListener {

    private final UniversePanel universePanel;

    public Renderer(UniversePanel universePanel) {
        this.universePanel = universePanel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            universePanel.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
