package com.company;


import java.awt.geom.*;

/**
 * @author mn263
 *         Date: 9/13/13
 *         Time: 1:03 PM
 */
public class Point extends Point2D {

	private double x;
	private double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getHeight() {
		return getY();
	}

	public double getWidth() {
		return getX();
	}

	public double getY() {
		return y;
	}

	@Override
	public void setLocation(double x, double y) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}
	public void setY(double y) {
		this.y = y;
	}
}
