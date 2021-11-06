package nl.bingley.entropyoflife.actionlisteners;

import com.aparapi.Range;
import nl.bingley.entropyoflife.application.windowapplication.UniversePanel;
import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.kernels.UniverseKernel;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class UniverseUpdateActionListener implements ActionListener {

    private final UniversePanel panel;
    private final UniverseProperties uniProps;
    private final LifeProperties lifeProps;

    private final Universe universe;
    private final UniverseKernel universeKernel;
    private final Range kernelRange;

    private long genPerSecTimer;
    private int genPerSecCounter = 0;

    public UniverseUpdateActionListener(Universe universe, UniversePanel universePanel, UniverseProperties universeProperties, UniverseKernel universeKernel) {
        this.universe = universe;
        this.panel = universePanel;
        this.uniProps = universeProperties;
        lifeProps = uniProps.getLifeProperties();

        kernelRange = Range.create2D(uniProps.getSize(), uniProps.getSize());
        this.universeKernel = universeKernel;
        genPerSecTimer = System.currentTimeMillis();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        updateGenPerSecTimer();
        updateUniverse();
    }

    public void updateUniverse() {
        universe.incrementGeneration();

        universeKernel.put(universe.energyMatrix);
        universeKernel.execute(kernelRange, 2);
        universeKernel.get(universe.energyMatrix);
    }

    private void updateGenPerSecTimer() {
        if (System.currentTimeMillis() > genPerSecTimer + 1000) {
            genPerSecTimer = System.currentTimeMillis();
            panel.setGenPerSec(genPerSecCounter);
            genPerSecCounter = 0;
        }
        genPerSecCounter++;
    }

    public void reset() {
        universe.initializeRandom(uniProps.getSpawnSize(), System.nanoTime(), lifeProps.getHighEnergyState(), lifeProps.getLowEnergyState());
    }
}
