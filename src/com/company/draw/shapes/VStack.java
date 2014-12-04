package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;

import static com.company.draw.shapes.WidgetUtils.handleMouse;

public class VStack extends SOReflect implements Layout, Drawable, Interactable {

	public SA contents;
	public double columnSpan;

	private enum sizeType {MIN, DESIRED, MAX}

	public VStack() {
	}


	//	DRAWABLE
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			Drawable drawable = (Drawable) so;
			drawable.paint(g2);
		}
	}

	// INTERACTABLE
	@Override
	public Root getPanel() {
		return null;
	}

	@Override
	public boolean key(char key) {
		return false;
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform transform) {
		return handleMouse(contents, x, y, transform, WidgetUtils.mouseType.DOWN);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform transform) {
		return handleMouse(contents, x, y, transform, WidgetUtils.mouseType.MOVE);
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform transform) {
		return handleMouse(contents, x, y, transform, WidgetUtils.mouseType.UP);
	}


	//	LAYOUT
	@Override
	public double getColSpan() {
		return columnSpan;
	}

	@Override
	public double getMinWidth() {
		return getWidestChild(sizeType.MIN);
	}

	@Override
	public double getDesiredWidth() {
		return getWidestChild(sizeType.DESIRED);
	}

	@Override
	public double getMaxWidth() {
		return getWidestChild(sizeType.MAX);
	}

	private double getWidestChild(sizeType size) {
		double widestChild = 0;
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			Layout layout = (Layout) sv.getSO();
			if (size == sizeType.MIN) {
				if (widestChild < layout.getMinWidth()) {
					widestChild = layout.getMinWidth();
				}
			} else if (size == sizeType.DESIRED) {
				if (widestChild < layout.getDesiredWidth()) {
					widestChild = layout.getDesiredWidth();
				}
			} else if (size == sizeType.MAX) {
				if (widestChild < layout.getMaxWidth()) {
					widestChild = layout.getMaxWidth();
				}
			}
		}
		return widestChild;
	}

	private double getTotalChildHeight(sizeType size) {
		double runningTotal = 0;
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			Layout layout = (Layout) sv.getSO();
			if (size == sizeType.MIN) {
				runningTotal += layout.getMinHeight();
			} else if (size == sizeType.DESIRED) {
				runningTotal += layout.getDesiredHeight();
			} else if (size == sizeType.MAX) {
				runningTotal += layout.getMaxHeight();
			}
		}
		return runningTotal;
	}

	@Override
	public double getMinHeight() {
		return getTotalChildHeight(sizeType.MIN);
	}

	@Override
	public double getDesiredHeight() {
		return getTotalChildHeight(sizeType.DESIRED);
	}

	@Override
	public double getMaxHeight() {
		return getTotalChildHeight(sizeType.MAX);
	}


	@Override
	public void setVBounds(double top, double bottom) {
		double min = getMinHeight();
		double desired = getDesiredHeight();
		double height = bottom - top;

		if (min >= height) { //give all children their minimum and let them be clipped
			double childTop = top;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childHeight = child.getMinHeight();
				child.setVBounds(childTop, childTop + childHeight);
				childTop += childHeight + 1;
			}
		} else if (desired >= height) { //give min to all and proportional for what's left
			double desiredMargin = (desired - min);
			if (desiredMargin <= 0) desiredMargin = 1;
			double fraction = (height - min) / desiredMargin;
			double childTop = top;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childMinHeight = child.getMinHeight();
				double childDesiredHeight = child.getDesiredHeight();
				double childHeight = childMinHeight + (childDesiredHeight - childMinHeight) * fraction;
				child.setVBounds(childTop, childTop + childHeight);
				childTop += childHeight;
			}
		} else { //allocate what remains based on maximum widths
			double difference = height - desired;
			double childTop = top;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childHeight = child.getDesiredHeight() + (child.getDesiredHeight() / desired) * difference;
				child.setVBounds(childTop, childTop + childHeight);
				childTop += childHeight;
			}
		}
	}

	@Override
	public void setHBounds(double left, double right) {
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			Layout child = (Layout) sv.getSO();
			child.setHBounds(left, right);
		}
	}
}
