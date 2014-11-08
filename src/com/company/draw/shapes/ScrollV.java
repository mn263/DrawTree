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

public class ScrollV extends SOReflect implements Layout, ModelListener, Drawable, Interactable {

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

	private Rect activeRect = null;
	private Rect rangeRect = null;
	private Polygon upPolygon = null;
	private Polygon downPolygon = null;
	private Rect slideRect = null;

	private double rangeMax = -1;
	private double rangeTop;
	private double maxMinDiff;

	private double downDifference;

	public ScrollV() {
		WidgetUtils.addListener(this);
	}

	//	INTERACTABLE
	@Override
	public Root getPanel() {
		throw new NotImplementedException();
	}

	@Override
	public boolean key(char key) {
		throw new NotImplementedException();
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		this.downDifference = y - getSliderTop();
		return callHandleMouse(WidgetUtils.mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		if (WidgetUtils.sliderBeingUsed(this)) {
			moveSlider(fromWindowCoords(y - downDifference));
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
		if (sliderLast == null) sliderLast = new Point(0, fromWindowCoords(getSliderTop()));

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

	// CLASS METHODS
	private void moveBar(double step) {
		double newValue = sliderLast.getY() + step;
		if (newValue < min) setSlider(slideRect, min);
		else if (newValue > max) setSlider(slideRect, max);
		else setSlider(slideRect, newValue);
	}

	private void moveSlider(double y) {
		if (y == sliderLast.getY()) return; //NO NEED TO UPDATE IF IT IS THE SAME
		if (y < min) setSlider(slideRect, min);
		else if (y > max) setSlider(slideRect, max);
		else setSlider(slideRect, y);
	}

	private void setSlider(Rect slide, double value) {
		sliderLast = new Point(0, value);
		double slideCoords = toSliderCoords(value);
		slide.setTop(slideCoords);
		WidgetUtils.updateModel(model, String.valueOf(value));
	}

	private double toSliderCoords(double value) {
		if (rangeMax == -1) loadConversionDoubles();
		return ((value - min) * (rangeMax / maxMinDiff)) + rangeTop;
	}

	private double fromWindowCoords(double y) {
		if (rangeMax == -1) loadConversionDoubles();
		double yOrigin = y - rangeTop;
		double yNomalized = yOrigin * (maxMinDiff / rangeMax);
		return yNomalized + min;
	}

	private double getSliderTop() {
		return slideRect.top;
	}

	private void loadConversionDoubles() {
		double rangeHeight = 0;
		double sliderHeight = 0;
		rangeTop = rangeRect.top;
		rangeHeight = rangeRect.height;
		sliderHeight = slideRect.height;
		rangeMax = rangeHeight - sliderHeight;
		maxMinDiff = max - min;
	}

	//	DRAWABLE
	@Override
	public void paint(Graphics g) {
		if (this.slideRect == null) initializeContents();
		Graphics2D g2 = (Graphics2D) g;
		for (Drawable drawable : contents) {
			drawable.paint(g2);
		}
	}

	//	MODEL LISTENER
	@Override
	public void modelUpdated(ArrayList<String> modelPath, String newValue) {
		if (modelPath.size() == model.size()) {
			for (int i = 0; i < model.size(); i++) {
				if (!modelPath.get(i).equals(model.getString(i))) {
					return; //IT WASN'T A MATCH
				}
			}
			if (sliderLast == null) sliderLast = new Point(0, fromWindowCoords(getSliderTop()));
			moveSlider(Double.valueOf(newValue)); //IT WAS A MATCH SO UPDATE THE LABEL
		}
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
		double left = 0;
		double top = 0;
		double width = 20;
		double height = 190;
		double thickness = 5;
		activeRect = new Rect(left, top, width, height, thickness, getFill(10, 140, 100));
		top = 32;
		height = 136;
		rangeRect = new Rect(left, top, width, height, thickness, getFill(200, 100, 100));
		slideRect = new Rect(12, 100, 26, 30, thickness, getFill(30, 30, 30));
		thickness = 2;
		upPolygon = new Polygon(getPoints(4, 20, 12, 4, 20, 20), thickness, getFill(20, 20, 20));
		downPolygon = new Polygon(getPoints(12, 170, 20, 198, 28, 170), thickness, getFill(0, 0, 0));
		this.contents.add(activeRect);
		this.contents.add(rangeRect);
		this.contents.add(upPolygon);
		this.contents.add(downPolygon);
		this.contents.add(slideRect);

		setHBounds(0, 10);
		setVBounds(0, 100);
	}


	@Override
	public double getMinWidth() {
		return 8;
	}

	@Override
	public double getDesiredWidth() {
		return 12;
	}

	@Override
	public double getMaxWidth() {
		return 18;
	}

	@Override
	public double getMinHeight() {
		return 30;
	}

	@Override
	public double getDesiredHeight() {
		return 200;
	}

	@Override
	public double getMaxHeight() {
		return 10000000;
	}

	@Override
	public void setHBounds(double left, double right) {
		double newWidth = right - left;
		if (getMinWidth() >= newWidth) newWidth = getMinWidth();
		else if (newWidth >= getMaxWidth()) newWidth = getMaxWidth();
		activeRect.left = left;
		rangeRect.left = left;
		slideRect.left = left;
		activeRect.width = newWidth;
		rangeRect.width = newWidth;
		slideRect.width = newWidth;
		setPolygonPoints(newWidth);
	}

	@Override
	public void setVBounds(double top, double bottom) {
		double oldTop = rangeRect.top;
		double oldHeight = activeRect.height;
		double newHeight = bottom - top;
		activeRect.top = top;
		rangeRect.top = top + 12;

		if (getMinHeight() >= newHeight) newHeight = getMinHeight();
		else if (newHeight >= getMaxHeight()) newHeight = getMaxHeight();

		double heightRatio = newHeight / oldHeight;
		slideRect.height = slideRect.height * heightRatio;
		slideRect.top = ((slideRect.top - oldTop) * heightRatio) + rangeRect.top;

		activeRect.height = newHeight;
		rangeRect.height = newHeight - 24;

		setPolygonPoints(activeRect.width);
	}

	private void setPolygonPoints(double width) {
		double scrollLength = activeRect.height;
		double left = 0;
		double center = width / 2;
		double right = width;
		double top = activeRect.top;
		double bottom_of_up = activeRect.top + 9;
		double top_of_down = activeRect.top + scrollLength - 9;
		double bottom = activeRect.top + scrollLength;
		upPolygon.points = getPoints(left, bottom_of_up, center, top, right, bottom_of_up);
		downPolygon.points = getPoints(left, top_of_down, center, bottom, right, top_of_down);
	}
}
