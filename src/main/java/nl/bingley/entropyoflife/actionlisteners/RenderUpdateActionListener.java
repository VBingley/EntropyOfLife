package nl.bingley.entropyoflife.actionlisteners;

import nl.bingley.entropyoflife.application.windowapplication.UniversePanel;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class RenderUpdateActionListener implements ActionListener {

    private final UniversePanel universePanel;

    public RenderUpdateActionListener(UniversePanel universePanel) {
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
