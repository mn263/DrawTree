package com.company;

import com.company.draw.shapes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class TreePanel extends JPanel implements ComponentListener {

	public java.util.List<Drawable> drawables = new ArrayList<>();
	public Mouse mouseListener;
	public static Select selected;
	public static int width = 900;
	public static int height = 600;

	public TreePanel(Mouse mouseListener) {
		this.mouseListener = mouseListener;
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		this.addComponentListener(this);
		this.setBackground(Color.lightGray);
		setSize(width, height);
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

//	public void checkMouseEvent(MouseEvent e) {
//		for(int i = 0; i < drawables.size(); i++) {
//			Drawable drawable = drawables.get(i);
//			if(drawable instanceof Group) {
//				Group group = (Group) drawable;
//				TreePanel.selected = new Select(group);
//				ArrayList<Integer> path = selected.select(e.getX(), e.getY(), i, new AffineTransform());
//				if (path != null) {
//					return;
//				}
//			} else if(drawable instanceof Root) {
//				Root root = (Root) drawable;
//				TreePanel.selected = new Select(root);
//				ArrayList<Integer> path = selected.select(e.getX(), e.getY(), i, new AffineTransform());
//				if (path != null) {
//					return;
//				}
//			}
//		}
//	}

	public void checkMouseEvent(MouseEvent e) {
		for(int i = 0; i < drawables.size(); i++) {
			Drawable drawable = drawables.get(i);
//			if(drawable instanceof Group) {
//				Group group = (Group) drawable;
//				TreePanel.selected = new Select(group);
			if(drawable instanceof Select) {
				TreePanel.selected = (Select) drawable;
				ArrayList<Integer> path = selected.select(e.getX(), e.getY(), i, new AffineTransform());
				if (path != null) {
					return;
				}
			}
		}
		selected = null;
	}
}