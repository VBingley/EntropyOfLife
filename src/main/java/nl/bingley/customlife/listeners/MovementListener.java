package nl.bingley.customlife.listeners;

import nl.bingley.customlife.UniversePanel;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

@Component
public class MovementListener implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final UniversePanel universePanel;
    private Point origin;

    public MovementListener(UniversePanel universePanel) {
        this.universePanel = universePanel;
        origin = new Point();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        origin = mouseEvent.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        Point target = mouseEvent.getPoint();
        universePanel.addTranslateX(target.x - origin.x);
        universePanel.addTranslateY(target.y - origin.y);
        origin = target;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        Point point = mouseWheelEvent.getPoint();
        if (mouseWheelEvent.getWheelRotation() < 0) {
            universePanel.zoomIn(-point.x, -point.y);
        } else {
            universePanel.zoomOut(-point.x, -point.y);
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
