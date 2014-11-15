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
		this.setBackground(Color.black);
		setSize(600,600);
	}

	public void addDrawable(Drawable drawable) {
		this.drawables.add(drawable);
	}

	@Override
	public void paint(Graphics g) {
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
			if("class com.company.draw.shapes.Group".equals(drawable.getClass().toString()) ||
			"class com.company.draw.shapes.Root".equals(drawable.getClass().toString()) ||
			"class com.company.draw.shapes.Select".equals(drawable.getClass().toString())) {
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