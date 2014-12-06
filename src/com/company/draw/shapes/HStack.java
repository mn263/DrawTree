package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;

import static com.company.draw.shapes.WidgetUtils.handleMouse;

public class HStack extends SOReflect implements Layout, Interactable, Drawable {

	public SA contents;
	public double columnSpan;

	private enum sizeType { MIN, DESIRED, MAX }

	double left;

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
	public void makeIdle() {}

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
		return getTotalChildWidth(sizeType.MIN);
	}

	@Override
	public double getDesiredWidth() {
		return getTotalChildWidth(sizeType.DESIRED);
	}

	@Override
	public double getMaxWidth() {
		return getTotalChildWidth(sizeType.MAX);
	}

	private double getTotalChildWidth(sizeType size) {
		double runningTotal = 0;
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			Layout layout = (Layout) sv.getSO();
			if (size == sizeType.MIN) {
				runningTotal += layout.getMinWidth();
			} else if (size == sizeType.DESIRED) {
				runningTotal += layout.getDesiredWidth();
			} else if (size == sizeType.MAX) {
				runningTotal += layout.getMaxWidth();
			}
		}
		return runningTotal;
	}

	private double getTallestChild(sizeType size) {
		double tallestChild = 0;
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			Layout layout = (Layout) sv.getSO();
			if (size == sizeType.MIN) {
				if (tallestChild < layout.getMinHeight()) {
					tallestChild = layout.getMinHeight();
				}
			} else if (size == sizeType.DESIRED) {
				if (tallestChild < layout.getDesiredHeight()) {
					tallestChild = layout.getDesiredHeight();
				}
			} else if (size == sizeType.MAX) {
				if (tallestChild < layout.getMaxHeight()) {
					tallestChild = layout.getMaxHeight();
				}
			}
		}
		return tallestChild;
	}

	@Override
	public double getMinHeight() {
		return getTallestChild(sizeType.MIN);
	}

	@Override
	public double getDesiredHeight() {
		return getTallestChild(sizeType.DESIRED);
	}

	@Override
	public double getMaxHeight() {
		return getTallestChild(sizeType.MAX);
	}


	@Override
	public void setVBounds(double top, double bottom) {
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			Layout child = (Layout) sv.getSO();
			child.setVBounds(top, bottom);
		}
	}

	@Override
	public void setHBounds(double left, double right) {
		double min = getMinWidth();
		double desired = getDesiredWidth();
		double width = right - left;

		if (min >= width) {
			double childLeft = left;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childWidth = child.getMinWidth();
				child.setHBounds(childLeft, childLeft + childWidth);
				childLeft += childWidth;
			}
		} else if (desired >= width) {
			double desiredMargin = (desired - min);
			if(desiredMargin <= 0) desiredMargin = 1;
			double fraction = (width - min) / desiredMargin;
			double childLeft = left;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childMinWidth = child.getMinWidth();
				double childDesiredWidth = child.getDesiredWidth();
				double childWidth = childMinWidth + (childDesiredWidth - childMinWidth) * fraction;
				child.setHBounds(childLeft, childLeft + childWidth);
				childLeft += childWidth;
			}
		} else {
			double difference = width - desired;
			double childLeft = left;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childWidth = child.getDesiredWidth() + (child.getDesiredWidth() / desired) * difference;
				child.setHBounds(childLeft, childLeft + childWidth);
				childLeft += childWidth;
			}
		}
	}
}
