package com.company;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author mn263
 *         Date: 9/13/13
 *         Time: 12:26 PM
 */
public class Mouse implements MouseListener, MouseMotionListener {

	@Override
	public void mouseClicked(MouseEvent e) {
//		GUIFunctions.refresh();
	}

	@Override
	public void mousePressed(MouseEvent e) {
//		Controller.inst().mousePressedHandler(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
        SwingTree.mouseReleased(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException("This method is not implemented");
	}

	@Override
	public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("This method is not implemented");
	}

	@Override
	public void mouseDragged(MouseEvent e) {
//		Controller.inst().mouseDraggedHandler(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
//        throw new UnsupportedOperationException("This method is not implemented");
	}
}
