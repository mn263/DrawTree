package com.company;

import com.company.draw.*;
import com.company.draw.shapes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public  class TreePanel extends JPanel {

	public java.util.List<Drawable> drawables = new ArrayList<Drawable>();
	public Mouse mouseListener;
	public Select selected;

	public TreePanel(Mouse mouseListener) {
		this.mouseListener = mouseListener;
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		this.setBackground(Color.black);
		setSize(800,800);
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
			this.selected.paint(g);
		}
	}

	public void checkMouseEvent(MouseEvent e) {
		System.out.println();
		System.out.println();
		System.out.println("(" + e.getX() + ", " + e.getY() + ")");
		for(int i = 0; i < drawables.size(); i++) {
			Drawable drawable = drawables.get(i);
			if("class com.company.draw.shapes.Group".equals(drawable.getClass().toString())) {
				Group group = (Group) drawable;
				this.selected = new Select(group);
				ArrayList<Integer> path = selected.select(e.getX(), e.getY(), i, new AffineTransform());
				if (path != null) {
					return;
				}
			}
		}
	}
}