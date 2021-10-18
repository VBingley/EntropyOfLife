package nl.bingley.entropyoflife.timers;

import com.aparapi.Range;
import nl.bingley.entropyoflife.UniversePanel;
import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.kernels.EnergyKernel;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class UniverseUpdater implements ActionListener {

    private final UniversePanel panel;
    private final UniverseProperties uniProps;
    private final LifeProperties lifeProps;

    private final Universe universe;
    private final EnergyKernel energyKernel;
    private final Range kernelRange;

    private boolean isPaused = false;
    private long genPerSecTimer;
    private int genPerSecCounter = 0;

    public UniverseUpdater(Universe universe, UniversePanel universePanel, UniverseProperties universeProperties, EnergyKernel energyKernel) {
        this.universe = universe;
        this.panel = universePanel;
        this.uniProps = universeProperties;
        lifeProps = uniProps.getLifeProperties();

        kernelRange = Range.create2D(uniProps.getSize(), uniProps.getSize());
        this.energyKernel = energyKernel;
        genPerSecTimer = System.currentTimeMillis();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!isPaused) {
            updateGenPerSecTimer();
            updateUniverse();
        }
    }

    public void updateUniverse() {
        universe.incrementGeneration();

        energyKernel.prepareDeltaCalculation();
        energyKernel.execute(kernelRange);
        energyKernel.prepareEnergyCalculation(panel.getWidth(), panel.getHeight());
        energyKernel.execute(kernelRange);
        energyKernel.get(universe.energyMatrix);
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
