package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import spark.data.*;

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
//	TODO: make sure that if the button doesn't fit that i drops it to a new row
	public SA contents;
	public double nColumns; //If there is no such attribute, then the default is 1.
	public double gutter;
	public double columnSpan;

	private ArrayList<Layout> children = null;

	public enum size {MIN, DESIRED, MAX}
	private double width;
	private double columnWidths;

	private double top;
	private double bottom;

	private ArrayList<ArrayList<Layout>> grid;
	private ArrayList<Layout> currRow;

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
		SParented parent = myParent();
		while(!(parent instanceof Interactable)){
			parent = parent.myParent();
		}
		Interactable InteractableParent = (Interactable)parent;
		return InteractableParent.getPanel();
	}

	@Override
	public boolean key(char key) {
		return false;
	}

	@Override
	public void makeIdle() {
		for (int j = 0; j < contents.size(); j++) {
			SV sv = contents.get(j);
			SO so = sv.getSO();
			if (so instanceof Interactable) {
				Interactable interactable = (Interactable) so;
				interactable.makeIdle();
			}
		}
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
	public double getColSpan() {
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
			if (layout.getColSpan() > 0) {
				childMax = (childMax - (layout.getColSpan()-1)*gutter) / layout.getColSpan();
			}
			if (childMax > maxOfChildren) maxOfChildren = childMax;
		}
		return maxOfChildren;
	}

	@Override
	public void setHBounds(double left, double right) {
		this.width = right - left;
		this.columnWidths = (this.width - (nColumns - 1) * this.gutter) / nColumns;

		putChildrenInRows();
		updateChildrenLayoutsByGrid();
	}

	private void putChildrenInRows() {
		this.grid = new ArrayList<>();
		if(this.children == null) loadChildren();

		currRow = new ArrayList<>();
		int columnIndex = 0;
		for (Layout child : this.children) {
			int minColSpan = getMinColSpan(child);

			if (child.getMinWidth() > this.width) { // give it the whole row
				if (columnIndex != 0)  saveRow(); // save what is in the row to create a new row
				currRow.add(child); // add the child to a guaranteed clean row
				saveRow();
				columnIndex = 0;
			} else if (minColSpan > (nColumns - columnIndex)) {
			// Child's width won't fit in what's left so create a new row and add it
				saveRow();
				columnIndex = minColSpan;
				currRow.add(child);
			} else if (minColSpan == nColumns - columnIndex) { // Child's colspan fits exactly so add it and save the row
				if (columnIndex == nColumns - 1 && child.getMinWidth() > columnWidths) {
					saveRow();
					currRow.add(child);
					columnIndex = minColSpan;
				} else {
					currRow.add(child);
					saveRow();
					columnIndex = 0;
				}
			} else if (minColSpan < nColumns - columnIndex) { // Child's colspan fits with extra room, so add it to the row and go get the next child
				currRow.add(child);
				columnIndex += minColSpan;
			}
		}
		saveRow();
	}

	private int getMinColSpan(Layout child) {
		int colSpan = (int) child.getColSpan();
		int colWidthSpan = (int) Math.ceil(child.getMinWidth() / (this.columnWidths + gutter));
		return Math.max(colSpan, colWidthSpan);
	}

	private void saveRow() {
		grid.add(currRow);
		currRow = new ArrayList<>();
	}

	@Override
	public double getMinHeight() {
		return getTotalHeightForChildren(size.MIN);
	}

	@Override
	public double getDesiredHeight() {
		return getTotalHeightForChildren(size.DESIRED);
	}

	@Override
	public double getMaxHeight() {
		return getTotalHeightForChildren(size.MAX);
	}


	private double getTotalHeightForChildren(size max) {
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

	@Override
	public void setVBounds(double top, double bottom) {
		this.top = top;
		this.bottom = bottom;

//		int count = 0;
//		for (ArrayList<Layout> row : grid) {
//			for (Layout child : row) {
//				child.setVBounds(count * 50, (count + 1) * 50);
//			}
//			count++;
//		}





//		ArrayList<Double> minHeights = getRowHeights(size.MIN);
//		double minHeight = sumHeights(minHeights);
//		ArrayList<Double> desiredHeights = getRowHeights(size.DESIRED);
//		double desiredHeight = sumHeights(desiredHeights);
//		ArrayList<Double> maxHeights = getRowHeights(size.MAX);
//		double maxHeight = sumHeights(maxHeights);
//
//		double height = bottom - top;
//		double currTop = top;
//		if (minHeight > height) {
//			for (int i = 0; i < grid.size(); i++) {
//				ArrayList<Layout> row = grid.get(i);
//				double minRowHeight = minHeights.get(i);
//				for (Layout child : row) {
//					child.setVBounds(currTop, currTop + minRowHeight);
//				}
//				currTop += minRowHeight;
//			}
//		} else if (desiredHeight >= height) {  // Give min, plus excess per row
//			for (int i = 0; i < grid.size(); i++) {
//				ArrayList<Layout> row = grid.get(i);
//				double rowHeight = minHeights.get(i);
//				for (Layout child : row) {
//					child.setVBounds(currTop, currTop + rowHeight);
//				}
//				currTop += rowHeight;
//			}
//		} else if (desiredHeight < height && maxHeight > height) {  // Give desired, plus excess per row
//			for (int i = 0; i < grid.size(); i++) {
//				ArrayList<Layout> row = grid.get(i);
//				double rowHeight = minHeights.get(i);
//				for (Layout child : row) {
//					child.setVBounds(currTop, currTop + rowHeight);
//				}
//				currTop += rowHeight;
//			}
//		} else {  // Give the max height for each row
//			for (int i = 0; i < grid.size(); i++) {
//				ArrayList<Layout> row = grid.get(i);
//				double rowHeight = maxHeights.get(i);
//				for (Layout child : row) {
//					child.setVBounds(currTop, currTop + rowHeight);
//				}
//				currTop += rowHeight;
//			}
//		}
	}


	private void updateChildrenLayoutsByGrid() {
		// SET CHILD WIDTHS
		setChildWidths();

		// SET CHILD HEIGHTS
		setChildHeights();
	}

	private void setChildWidths() {
		for (ArrayList<Layout> row : this.grid) {
			double left = 0;
			for (Layout child : row) {
				double minColSpan = Math.min(nColumns, getMinColSpan(child));
				double childWidth = (minColSpan * columnWidths) + (minColSpan * gutter);
				child.setHBounds(left, left + childWidth - gutter);
				left = left + childWidth;
			}
		}
	}


	private void setChildHeights() {
		double min = getRowHeight(size.MIN);
		double desired = getRowHeight(size.DESIRED);
		double height = bottom - top;

		if (min >= height) { //give all children their minimum and let them be clipped
			double childTop = top;
			for (ArrayList<Layout> row : this.grid) {
				double childHeight = getHeight(size.MIN, row);
				for(Layout child : row) {
					child.setVBounds(childTop, childTop + childHeight);
				}
				childTop += childHeight + 1;
			}
		} else if (desired >= height) { //give min to all and proportional for what's left
			double desiredMargin = (desired - min);
			if(desiredMargin <= 0) desiredMargin = 1;
			double fraction = (height - min) / desiredMargin;
			double childTop = top;
			for (ArrayList<Layout> row : this.grid) {
				double childMinHeight = getHeight(size.MIN, row);
				double childDesiredHeight = getHeight(size.DESIRED, row);
				double childHeight = childMinHeight + (childDesiredHeight - childMinHeight) * fraction;
				for(Layout child : row) {
					child.setVBounds(childTop, childTop + childHeight);
				}
				childTop += childHeight;
			}
		} else { //allocate what remains based on maximum widths
			double difference = height - desired;
			double childTop = top;
			for (ArrayList<Layout> row : this.grid) {
				double childHeight = getHeight(size.DESIRED, row) + (getHeight(size.DESIRED, row) / desired) * difference;
				for(Layout child : row) {
					child.setVBounds(childTop, childTop + childHeight);
				}
				childTop += childHeight;
			}
		}
	}

	private double getRowHeight(size daSize) {
		double total = 0;
		for (ArrayList<Layout> row : this.grid) {
			double largest = 0;
			for (Layout child : row) {
				double childSize;
				if (daSize == size.MIN) childSize = child.getMinHeight();
				else if (daSize == size.DESIRED) childSize = child.getDesiredHeight();
				else childSize = child.getMaxHeight();

				if (largest < childSize) largest = childSize;
			}
			total += largest;
		}
		return total;
	}

	private double getHeight(size daSize, ArrayList<Layout> row) {
		double minHeight = 0;
		for (Layout child : row) {
			if (daSize == size.MIN) {
				if (minHeight < child.getMinHeight())
					minHeight = child.getMinHeight();
			} else if (daSize == size.DESIRED) {
				if (minHeight < child.getDesiredHeight())
					minHeight = child.getDesiredHeight();
			} else if (daSize == size.MAX) {
				if (minHeight < child.getMaxHeight())
					minHeight = child.getMaxHeight();
			}
		}
		return minHeight;
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
