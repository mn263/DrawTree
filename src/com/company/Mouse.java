package com.company;

import com.company.draw.shapes.*;

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
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingTree.getRoot() != null) {
			WidgetUtils.setMouseStatus(WidgetUtils.MouseStatus.PRESSED);
			SwingTree.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingTree.getRoot() != null) {
			WidgetUtils.setMouseStatus(WidgetUtils.MouseStatus.RELEASED);
			SwingTree.mouseReleased(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingTree.getRoot() != null) {
			SwingTree.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (SwingTree.getRoot() != null) {
			SwingTree.mouseMoved(e);
		}
	}
}
