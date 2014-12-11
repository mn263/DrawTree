package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import com.company.draw.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;
import static com.company.draw.shapes.WidgetUtils.mouseType.*;
import static java.lang.Math.abs;

public class ScrollH extends SOReflect implements ModelListener, Layout, Drawable, Interactable {
//	TODO: when loading layout2.draw it didn't initialize properly
	public String state;
	public ArrayList<Drawable> contents = new ArrayList<>();
	public SO idle;
	public SO hover;
	public SO active;
	public SA model;
	public double max;
	public double min;
	public double step;
	private Point sliderLast;
	private Point range;
	private double leftDifference;
	public double columnSpan;

	private Rect activeRect = null;
	private Rect rangeRect = null;
	private Polygon upPolygon = null;
	private Polygon downPolygon = null;
	private Rect slideRect = null;

	private double rangeMax = -1;
	private double rangeLeft;
	private double maxMinDiff;

	public ScrollH() {
		WidgetUtils.addListener(this);
	}

	//	INTERACTABLE
	@Override
	public Root getPanel() {
		SParented parent = myParent();
		while(!(parent instanceof Interactable)){
			parent = parent.myParent();
		}
		Interactable InteractableParent = (Interactable)parent;
		return InteractableParent.getPanel();
	}

	@Override
	public boolean key(char key) {
		throw new NotImplementedException();
	}

	@Override
	public void makeIdle() {
		activeRect.setBackgroundColor(this.idle);
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		this.leftDifference = x - getSliderLeft();
		return callHandleMouse(WidgetUtils.mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		if (WidgetUtils.sliderBeingUsed(this)) {
			moveSlider(fromWindowCoords(x - leftDifference));
			return true;
		} else {
			return callHandleMouse(WidgetUtils.mouseType.MOVE, x, y, myTransform);
		}
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(UP, x, y, myTransform);
	}

	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		if(rangeRect == null) return false;
		if (sliderLast == null) sliderLast = new Point(fromWindowCoords(getSliderLeft()), 0);
		boolean isHandled = handleMouse(contents, x, y, myTransform, mouseType);
		if (!isHandled) {
			this.state = "idle";
			changeState(this.idle, x, y, myTransform, mouseType);
		} else {
			if (WidgetUtils.getMouseStatus() == WidgetUtils.MouseStatus.PRESSED) {
				this.state = "active";
				changeState(this.active, x, y, myTransform, mouseType);
			} else {
				this.state = "hover";
				changeState(this.hover, x, y, myTransform, mouseType);
			}
		}
		return isHandled;
	}

	public void changeState(SO newState, double x, double y, AffineTransform myTransform, WidgetUtils.mouseType mouseType) {
		//UPDATE THE COLOR
		activeRect.setBackgroundColor(newState);
		if (mouseType == UP) { //MOVE SCROLL BAR IF "RELEASED" IS PRESSED
			if (upPolygon.select(x, y, 0, myTransform) != null) {
				moveBar(-step);
			}
			// MOVE SCROLL BAR IF "DOWN" IS PRESSED
			if (downPolygon.select(x, y, 0, myTransform) != null) {
				moveBar(step);
			}
		}
		if (mouseType == DOWN) { //MOVE SCROLL BAR IF "SLIDER" IS PRESSED
			if (slideRect.select(x, y, 0, myTransform) != null) {
				this.sliderLast = new Point(x, y);
				WidgetUtils.setSliderBeingUsed(this);
			}
		}
	}

	// WIDGET METHODS
	private void moveBar(double step) {
		if(model == null) return;
		double newValue = sliderLast.getX() + step;
		if (newValue < min) setSlider(min);
		else if (newValue > max) setSlider(max);
		else setSlider(newValue);
	}

	private void moveSlider(double x) {
		if (model == null || x == sliderLast.getX()) return; //NO NEED TO UPDATE IF IT IS THE SAME
		if (x < min) setSlider(min);
		else if (x > max) setSlider(max);
		else setSlider(x);
	}

	private void setSlider(double value) {
		sliderLast = new Point(value, 0);
		double slideCoords = toSliderCoords(value);
		slideRect.setLeft(slideCoords);
		if(model != null) WidgetUtils.updateModel(model, String.valueOf(value));
	}

	private double toSliderCoords(double value) {
		if (range == null) loadConversionDoubles();
		return ((value - min) * (rangeMax / maxMinDiff)) + rangeLeft;
	}

	private double fromWindowCoords(double x) {
		if (range == null) loadConversionDoubles();
		double yOrigin = x - rangeLeft;
		double yNomalized = yOrigin * (maxMinDiff / rangeMax);
		return yNomalized + min;
	}

	private double getSliderLeft() {
		if(slideRect == null) return 0;
		return slideRect.left;
	}

	private void loadConversionDoubles() {
		double rangeWidth;
		double sliderWidth;
		rangeLeft = rangeRect.left;
		rangeWidth = rangeRect.width;
		sliderWidth = slideRect.width;
		rangeMax = rangeWidth - sliderWidth;
		maxMinDiff = max - min;
	}

	//	DRAWABLE
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (Drawable drawable : contents) {
			drawable.paint(g2);
		}
	}

	//	MODEL LISTENER
	@Override
	public void modelUpdated(ArrayList<String> modelPath, String newValue) {
		if (this.slideRect == null) initializeContents();
		if (model != null && modelPath.size() == model.size()) {
			for (int i = 0; i < model.size(); i++) {
				if(!modelPath.get(i).equals(model.getString(i))) {
					return; //IT WASN'T A MATCH
				}
			}
			if (sliderLast == null) sliderLast = new Point(fromWindowCoords(getSliderLeft()), 0);
			double slideCoords = toSliderCoords(Double.valueOf(newValue));
			if (10 < abs(slideCoords - getSliderLeft())) {
				sliderLast.setX(-1);
				moveSlider(Double.valueOf(newValue)); //IT WAS A MATCH SO UPDATE THE LABEL
				repaintAll();
			} else {
				moveSlider(Double.valueOf(newValue)); //IT WAS A MATCH SO UPDATE THE LABEL
			}
		}
	}


	@Override
	public double getColSpan() {
		return columnSpan;
	}


//	LAYOUT

	private SO getFill(double red, double green, double blue) {
		SO fill = new SObj();
		fill.set("r", new SV(red));
		fill.set("g", new SV(green));
		fill.set("b", new SV(blue));
		return fill;
	}

	private SA getPoints(double x1, double y1, double x2, double y2, double x3, double y3) {
		SO point1 = new SObj();
		point1.set("x", x1);
		point1.set("y", y1);
		SO point2 = new SObj();
		point2.set("x", x2);
		point2.set("y", y2);
		SO point3 = new SObj();
		point3.set("x", x3);
		point3.set("y", y3);

		SA points = new SArray();
		points.add(point1);
		points.add(point2);
		points.add(point3);
		return points;
	}

	private void initializeContents() {
		double red = 255;
		double green = 255;
		double blue = 255;
		if (this.idle != null) {
			red = idle.get("r").getDouble();
			green = idle.get("g").getDouble();
			blue = idle.get("b").getDouble();
		}

		double left = 0;
		double top = 0;
		double width = 190;
		double height = 20;
		double thickness = 5;
		activeRect = new Rect(left, top, width, height, thickness, getFill((int) red, (int) green, (int) blue));
		top = 32;
		height = 136;
		rangeRect = new Rect(left, top, width, height, thickness, getFill(200, 100, 100));
		slideRect = new Rect(30, top, 30, height, thickness, getFill(30, 30, 30));
		thickness = 2;
		upPolygon = new Polygon(getPoints(4, 20, 12, 4, 20, 20), thickness, getFill(20, 20, 20));
		downPolygon = new Polygon(getPoints(12, 170, 20, 198, 28, 170), thickness, getFill(0, 0, 0));
		this.contents.add(activeRect);
		this.contents.add(rangeRect);
		this.contents.add(slideRect);
		this.contents.add(upPolygon);
		this.contents.add(downPolygon);
	}

	@Override
	public double getMinWidth() {
		return 30;
	}

	@Override
	public double getDesiredWidth() {
		return 200;
	}

	@Override
	public double getMaxWidth() {
		return 10000000;
	}

	@Override
	public double getMinHeight() {
		return 8;
	}

	@Override
	public double getDesiredHeight() {
		return 12;
	}

	@Override
	public double getMaxHeight() {
		return 18;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		double newHeight = bottom - top;
		if (getMinHeight() >= newHeight) newHeight = getMinHeight();
		else if (newHeight >= getMaxHeight()) newHeight = getMaxHeight();
		slideRect.top = top;
		activeRect.top = top;
		rangeRect.top = top;

		activeRect.height = newHeight;
		rangeRect.height = newHeight;
		rangeRect.top = activeRect.top;
		slideRect.height = newHeight;
		setPolygonPoints(top, newHeight);
	}

	@Override
	public void setHBounds(double left, double right) {
		if (this.slideRect == null) initializeContents();
		if (sliderLast == null) sliderLast = new Point(fromWindowCoords(getSliderLeft()), 0);

		double oldLeft = rangeRect.left;
		double oldWidth = activeRect.width;
		double newWidth = right - left;
		activeRect.left = left;
		rangeRect.left = left + 12;

		if (getMinHeight() >= newWidth) newWidth = getMinWidth();
		else if (newWidth >= getMaxWidth()) newWidth = getMaxWidth();

		double widthRatio = newWidth / oldWidth;
		this.rangeMax = -1; // this will cause the other methods to call a method to convert from screen to object.
		slideRect.width = slideRect.width * widthRatio;
		slideRect.left = ((slideRect.left - oldLeft) * widthRatio) + rangeRect.left;

		activeRect.width = newWidth;
		rangeRect.width = newWidth - 24;

		setPolygonPoints(activeRect.top, activeRect.height);
	}

	private void setPolygonPoints(double top, double height) {
		double scrollLength = activeRect.width;
		double center = top + height / 2;
		double left = activeRect.left;
		double bottom = activeRect.top + height;
		upPolygon.points = getPoints(
				left + 1, center,
				left + 9, top + 2,
				left + 9, bottom - 2);
		downPolygon.points = getPoints(
				left + scrollLength - 2, center,
				left + scrollLength - 10, top + 2,
				left + scrollLength - 10, bottom - 2);
	}
}