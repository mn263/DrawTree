package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import org.omg.CORBA.DynAnyPackage.*;
import spark.data.*;

import java.util.*;

public class HStack extends SOReflect implements Layout {

	public SA contents;

	private Point minSize;
	private Point desiredSize;
	private Point maxSize;
	private boolean sizesAreValid;
	public double columnSpan;

	private ArrayList<Layout> stackContents = null;

	public HStack(){}
	public HStack(SA contents) {
		this.contents = contents;
	}


	//	LAYOUT
	@Override
	public double getColumnSpan() {
		return columnSpan;
	}

	@Override
	public double getMinWidth() {
		if (sizesAreValid) return minSize.getX();
		if (this.stackContents == null) fillStackContents();

		for (Layout layout : stackContents) {
			minSize.setX( minSize.getX() + layout.getMinWidth());
			if (minSize.getY() < layout.getMinHeight()) minSize.setY(layout.getMinHeight());
		}

		sizesAreValid = true;
		return minSize.getX();
	}

	@Override
	public double getDesiredWidth() {
		if (sizesAreValid) return desiredSize.getX();
		if (this.stackContents == null) fillStackContents();

		for (Layout layout : stackContents) {
			desiredSize.setX( desiredSize.getX() + layout.getDesiredWidth());
			if (desiredSize.getY() < layout.getDesiredHeight()) desiredSize.setY(layout.getDesiredHeight());
		}

		sizesAreValid = true;
		return desiredSize.getX();
	}

	@Override
	public double getMaxWidth() {
		if (sizesAreValid) return maxSize.getX();
		if (this.stackContents == null) fillStackContents();

		for (Layout layout : stackContents) {
			maxSize.setX( maxSize.getX() + layout.getMaxWidth());
			if (maxSize.getY() < layout.getMaxHeight()) maxSize.setY(layout.getMaxHeight());
		}

		sizesAreValid = true;
		return maxSize.getX();
	}

	@Override
	public double getMinHeight() {
		getMinWidth();
		return minSize.getY();
	}

	@Override
	public double getDesiredHeight() {
		getMinWidth();
		return minSize.getY();
	}

	@Override
	public double getMaxHeight() {
		getMaxWidth();
		return maxSize.getY();
	}

//	TODO: redo again according to pg 119
	@Override
	public void setHBounds(double left, double right) {
		if (this.stackContents == null) fillStackContents();
		invalidateSizes();
		double individualSize = (right - left) / stackContents.size();
		double curr_left = left;
		for (Layout layout : stackContents) {
			layout.setHBounds(curr_left, curr_left + individualSize);
			curr_left += individualSize;
		}
	}

	@Override
	public void setVBounds(double top, double bottom) {
		if (this.stackContents == null) fillStackContents();
		invalidateSizes();
		for (Layout layout : stackContents) {
			layout.setVBounds(top, bottom);
		}
	}


	private void invalidateSizes() { sizesAreValid = false; }

	private void fillStackContents() {
		try {
			for (int i = 0; i < contents.size(); i++) {
				SV sv = contents.get(i);
				SO so = sv.getSO();
				if(!(so instanceof Layout)) {
					throw new InvalidValue("All contents must be of type layout");
				} else {
					Layout layout = (Layout) so;
					stackContents.add(layout);
				}
			}
		} catch (InvalidValue invalidValue) {
			invalidValue.printStackTrace();
		}
	}
}
