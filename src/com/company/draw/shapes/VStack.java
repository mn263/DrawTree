package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import spark.data.*;

import java.awt.*;

public class VStack extends SOReflect implements Layout, Drawable {

	public SA contents;
	public double columnSpan;

	private enum sizeType { MIN, DESIRED, MAX }

	public VStack() { }


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
		double max = getMaxHeight();
		double desired = getDesiredHeight();
		double height = bottom - top;

		if (min >= height) {
			double childTop = top;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childHeight = child.getMinHeight();
				child.setVBounds(childTop, childTop + childHeight);
				childTop += childHeight;
			}
		} else if (desired >= height) {
			double desiredMargin = (desired - min);
			if(desiredMargin == 0) desiredMargin = 1;
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
		} else {
			double maxMargin = (max - desired) == 0 ? 1 : (max - desired);
			double fraction = (height - desired) / maxMargin;
			double childTop = top;
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				Layout child = (Layout) sv.getSO();
				double childDesiredHeight = child.getDesiredHeight();
				double childMaxHeight = child.getMaxHeight();
				double childHeight = childDesiredHeight + (childMaxHeight - childDesiredHeight) * fraction;
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