package nl.bingley.entropyoflife.listeners;

import nl.bingley.entropyoflife.UniversePanel;
import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.model.Cell;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@Component
public class MouseInputListener implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final UniversePanel universePanel;
    private final LifeProperties props;
    private Point origin;
    private float paintValue;

    public MouseInputListener(UniversePanel universePanel, UniverseProperties properties) {
        this.universePanel = universePanel;
        origin = new Point();
        props = properties.getLifeProperties();
        paintValue = props.getHighEnergyState();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        origin = mouseEvent.getPoint();
        paintValue = universePanel.findCellAtPixel(origin.x, origin.y).value > props.getLifeEnergyThreshold() ?
                props.getLowEnergyState() : props.getHighEnergyState();
        handleMouseButtons(mouseEvent);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        Point target = mouseEvent.getPoint();
        handleMouseButtons(mouseEvent);
        origin = target;
    }

    private void handleMouseButtons(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            paintCell(mouseEvent.getPoint());
        } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            translateScreen(mouseEvent.getPoint());
        }
    }

    private void translateScreen(Point target) {
        universePanel.addTranslateX(target.x - origin.x);
        universePanel.addTranslateY(target.y - origin.y);
    }

    private void paintCell(Point point) {
        Cell cell = universePanel.findCellAtPixel(point.x, point.y);
        cell.value = paintValue;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        Point point = mouseWheelEvent.getPoint();
        if (mouseWheelEvent.getWheelRotation() < 0) {
            universePanel.zoomIn(point.x, point.y);
        } else {
            universePanel.zoomOut(point.x, point.y);
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
