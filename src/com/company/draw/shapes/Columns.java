package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;

/**
 * This object computes a minimum, desired and maximum column width for each of its children.
 * For each of these values it takes the maximum for all of its children.
 * It then computes its own width as (maxChildWidth*nColumns)+(gutter*(nColumns-1)).
 * It does this separately for min, desired and max.
 */
public class Columns extends SOReflect implements Layout, Drawable, Interactable  {
	// Assumes also that some of its contents objects will have a "columnSpan:" attribute

	public SA contents;
	public double nColumns; //If there is no such attribute, then the default is 1.
	public double gutter;
	public double columnSpan;

	private ArrayList<Layout> children = null;

	public enum size {MIN, DESIRED, MAX}
	private double width;
	private double columnWidths;

	private ArrayList<ArrayList<Layout>> grid = new ArrayList<>();
	private ArrayList<Layout> currRow = new ArrayList<>();

	public Columns(){}

	public Columns(SA contents, double nColumns, double gutter) {
		this.contents = contents;
		this.nColumns = nColumns;
		this.gutter = gutter;
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

//	INTERACTABLE
	@Override
	public Root getPanel() {
		throw new NotImplementedException();
	}

	@Override
	public boolean key(char key) {
		return false;
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(WidgetUtils.mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(WidgetUtils.mouseType.MOVE, x, y, myTransform);
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(WidgetUtils.mouseType.UP, x, y, myTransform);
	}

	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform transform) {
		return handleMouse(contents, x, y, transform, mouseType);
	}


	//	LAYOUT
	@Override
	public double getColumnSpan() {
		return columnSpan;
	}


	@Override
	public double getMinWidth() {
		return (getMaxWidthForChildren(size.MIN) * nColumns) + (gutter * (nColumns - 1));
	}

	@Override
	public double getDesiredWidth() {
		return (getMaxWidthForChildren(size.DESIRED) * nColumns) + (gutter * (nColumns - 1));
	}

	@Override
	public double getMaxWidth() {
		return (getMaxWidthForChildren(size.MAX) * nColumns) + (gutter * (nColumns - 1));
	}

	@Override
	public void setHBounds(double left, double right) {
//		(width-(nColumns-1)*gutter)/nColumns)
		this.width = right - left;
		this.columnWidths = (this.width - (nColumns - 1) * this.gutter) / nColumns;
		reLocateChildren();
	}

	private void reLocateChildren() {
		grid = new ArrayList<>();
//		TODO: test this for bugs
		if(children == null) loadChildren();
		for (int i = 0; i < children.size(); i++) {
			Layout child = children.get(i);
			if (child.getMinWidth() > width) { // give it the whole row
				currRow.add(child);
				child.setHBounds(0, width);
			} else { // fit as many as you can onto the row
				double widthLeftInRow = width - columnWidths;
				int currentColumn = 0;
				int cellWidth = 1;
				while (widthLeftInRow > 0) { // child fits in cellWidth add it and continue
					if (child.getMinWidth() < cellWidth * columnWidths) {
						currRow.add(child);
						double left = currentColumn * columnWidths;
						double right = left + (cellWidth * columnWidths);
						child.setHBounds(left, right);
						currentColumn += cellWidth;
						cellWidth = 1;
						i++;
						if (i == children.size()) {
							widthLeftInRow = -1;
						} else {
							child = children.get(i);
						}
					} else { // increase columnSpan and continue
						cellWidth++;
					}
					widthLeftInRow -= columnWidths;
				}
			}
			saveRow();
		}
	}

	private void saveRow() {
		grid.add(currRow);
		currRow = new ArrayList<>();
	}

	@Override
	public double getMinHeight() {
		return getMaxHeightForChildren(size.MIN);
	}

	@Override
	public double getDesiredHeight() {
		return getMaxHeightForChildren(size.DESIRED);
	}

	@Override
	public double getMaxHeight() {
		return getMaxHeightForChildren(size.MAX);
	}

	@Override
	public void setVBounds(double top, double bottom) {
		ArrayList<Double> minHeights = getRowHeights(size.MIN);
		double minHeight = sumHeights(minHeights);
		ArrayList<Double> desiredHeights = getRowHeights(size.DESIRED);
		double desiredHeight = sumHeights(desiredHeights);
		ArrayList<Double> maxHeights = getRowHeights(size.MAX);
		double maxHeight = sumHeights(maxHeights);

		double height = bottom - top;
		double currTop = top;
		if (minHeight > height) {
			for (int i = 0; i < grid.size(); i++) {
				ArrayList<Layout> row = grid.get(i);
				double minRowHeight = minHeights.get(i);
				for (Layout child : row) {
					child.setVBounds(currTop, currTop + minRowHeight);
				}
				currTop += minRowHeight;
			}
		} else if (desiredHeight >= height) {  // Give min, plus excess per row
			for (int i = 0; i < grid.size(); i++) {
				ArrayList<Layout> row = grid.get(i);
				double rowHeight = minHeights.get(i);
				for (Layout child : row) {
					child.setVBounds(currTop, currTop + rowHeight);
				}
				currTop += rowHeight;
			}
		} else if (desiredHeight < height && maxHeight > height) {  // Give desired, plus excess per row
			for (int i = 0; i < grid.size(); i++) {
				ArrayList<Layout> row = grid.get(i);
				double rowHeight = minHeights.get(i);
				for (Layout child : row) {
					child.setVBounds(currTop, currTop + rowHeight);
				}
				currTop += rowHeight;
			}
		} else {  // Give the max height for each row
			for (int i = 0; i < grid.size(); i++) {
				ArrayList<Layout> row = grid.get(i);
				double rowHeight = maxHeights.get(i);
				for (Layout child : row) {
					child.setVBounds(currTop, currTop + rowHeight);
				}
				currTop += rowHeight;
			}
		}
	}

	private double sumHeights(ArrayList<Double> heights) {
		double sum = 0;
		for (Double height : heights) {
			sum += height;
		}
		return sum;
	}

	private ArrayList<Double> getRowHeights(size sizeType) {
		ArrayList<Double> rowMaxes = new ArrayList<>();
		for (ArrayList<Layout> row : grid) {
			double rowMax = 0;
			for (Layout child : row) {
				double childMax = 0;
				if (sizeType == size.MIN) {
					childMax = child.getMinHeight();
				} else if (sizeType == size.DESIRED) {
					childMax = child.getDesiredHeight();
				} else if (sizeType == size.MAX) {
					childMax = child.getMaxHeight();
				}
				if (childMax > rowMax) rowMax = childMax;
			}
			rowMaxes.add(rowMax);
		}
		return rowMaxes;
	}

	private double getMaxWidthForChildren(size max) {
		double maxOfChildren = 0;
		if (children == null) loadChildren();
		for (Layout layout : children) {
			double childMax = -1;
			if (max == size.MAX) {
				childMax = layout.getMaxWidth();
			} else if (max == size.DESIRED) {
				childMax = layout.getDesiredWidth();
			} else if (max == size.MIN) {
				childMax = layout.getMinWidth();
			}
			if (layout.getColumnSpan() > 0) {
				childMax = (childMax - (layout.getColumnSpan()-1)*gutter) / layout.getColumnSpan();
			}
			if (childMax > maxOfChildren) maxOfChildren = childMax;
		}
		return maxOfChildren;
	}

	private double getMaxHeightForChildren(size max) {
		double runningHeight = 0;
		if (children == null) loadChildren();
		for (Layout child : children) {
			double childMax = -1;
			if (max == size.MAX) {
				childMax = child.getMaxWidth();
			} else if (max == size.DESIRED) {
				childMax = child.getDesiredWidth();
			} else if (max == size.MIN) {
				childMax = child.getMinWidth();
			}
			runningHeight += childMax;
		}
		return runningHeight;
	}

	private void loadChildren() {
		children = new ArrayList<>();
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			Layout layout = (Layout) so;
			children.add(layout);
		}
	}
}
