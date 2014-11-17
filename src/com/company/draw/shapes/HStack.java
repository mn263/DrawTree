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

	public HStack() { }


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

	private double getChildHeight(sizeType size) {
		double widestChild = 0;
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			Layout layout = (Layout) sv.getSO();
			if (size == sizeType.MIN) {
				if (widestChild < layout.getMinHeight()) {
					widestChild = layout.getMinHeight();
				}
			} else if (size == sizeType.DESIRED) {
				if (widestChild < layout.getDesiredHeight()) {
					widestChild = layout.getDesiredHeight();
				}
			} else if (size == sizeType.MAX) {
				if (widestChild < layout.getMaxHeight()) {
					widestChild = layout.getMaxHeight();
				}
			}
		}
		return widestChild;
	}

	@Override
	public double getMinHeight() {
		return getChildHeight(sizeType.MIN);
	}

	@Override
	public double getDesiredHeight() {
		return getChildHeight(sizeType.DESIRED);
	}

	@Override
	public double getMaxHeight() {
		return getChildHeight(sizeType.MAX);
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
		double min = getMinHeight();
		double max = getMaxHeight();
		double desired = getDesiredHeight();
		double height = right - left;

		if (min >= height) {
			double childLeft = left;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childWidth = child.getMinHeight();
				child.setHBounds(childLeft, childLeft + childWidth);
				childLeft += childWidth;
			}
		} else if (desired >= height) {
			double desiredMargin = (desired - min);
			if(desiredMargin == 0) desiredMargin = 1;
			double fraction = (height - min) / desiredMargin;
			double childLeft = left;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childMinHeight = child.getMinHeight();
				double childDesiredHeight = child.getDesiredHeight();
				double childWidth = childMinHeight + (childDesiredHeight - childMinHeight) * fraction;
				child.setHBounds(childLeft, childLeft + childWidth);
				childLeft += childWidth;
			}
		} else {
			double maxMargin = (max - desired) == 0 ? 1 : (max - desired);
			double fraction = (height - desired) / maxMargin;
			double childLeft = left;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childDesiredHeight = child.getDesiredHeight();
				double childMaxHeight = child.getMaxHeight();
				double childWidth = childDesiredHeight + (childMaxHeight - childDesiredHeight) * fraction;
				child.setHBounds(childLeft, childLeft + childWidth);
				childLeft += childWidth;
			}
		}
	}
}
