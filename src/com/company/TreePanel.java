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
	public static Select selected;

	public TreePanel(Mouse mouseListener) {
		this.mouseListener = mouseListener;
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
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
		if (TreePanel.selected != null) {
			TreePanel.selected.paintSelected(g);
		}
	}
}