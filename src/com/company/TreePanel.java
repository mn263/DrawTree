package com.company;

import com.company.draw.shapes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public  class TreePanel extends JPanel implements ComponentListener {

	public java.util.List<Drawable> drawables = new ArrayList<>();
	public Mouse mouseListener;
	public Select selected;

	public TreePanel(Mouse mouseListener) {
		this.mouseListener = mouseListener;
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		this.addComponentListener(this);
		this.setBackground(Color.lightGray);
		setSize(900,600);
	}

	public void addDrawable(Drawable drawable) {
		this.drawables.add(drawable);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		for (Drawable drawable : drawables) {
			drawable.paint(g);
		}
		if (this.selected != null) {
			this.selected.paintSelected(g);
		}
	}

	public void checkMouseEvent(MouseEvent e) {
		for(int i = 0; i < drawables.size(); i++) {
			Drawable drawable = drawables.get(i);
			if(drawable instanceof Group) {
				Group group = (Group) drawable;
				this.selected = new Select(group);
				ArrayList<Integer> path = selected.select(e.getX(), e.getY(), i, new AffineTransform());
				if (path != null) {
					return;
				}
			}
		}
	}


	//	WINDOW LISTENER METHODS:
	@Override
	public void componentResized(ComponentEvent e) {
		Root root = SwingTree.getRoot();
		if (root != null) {
			root.handleComponentResize(e);
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		System.out.println("componentMoved()");
	}

	@Override
	public void componentShown(ComponentEvent e) {
		System.out.println("componentShown()");
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		System.out.println("componentHidden()");
	}
}