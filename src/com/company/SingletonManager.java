package com.company;

import java.awt.*;

public class SingletonManager {
	private static SingletonManager instance;

	protected SingletonManager() {
	}

	public static SingletonManager inst() {
		if (instance == null) {
			instance = new SingletonManager();
		}
		return instance;
	}

	public Point centerBeforeDrag;
	private static Shape selectedShape;

	public Shape getselectedShape() {
		return selectedShape;
	}

	public void setSelectedShape(Shape shape) {
//		if (shape != null) {
//			centerBeforeDrag = shape.getCenter();
//		}
		selectedShape = shape;
	}
}
