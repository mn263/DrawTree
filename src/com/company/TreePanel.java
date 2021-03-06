package com.company;

import com.company.draw.shapes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public  class TreePanel extends JPanel {

	public java.util.List<Drawable> drawables = new ArrayList<>();
	public Mouse mouseListener;
	public Select selected;

	public TreePanel(Mouse mouseListener) {
		this.mouseListener = mouseListener;
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
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
}