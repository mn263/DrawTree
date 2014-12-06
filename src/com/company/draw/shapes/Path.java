package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import com.company.draw.*;
import org.ejml.simple.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;
import static java.lang.Math.*;

public class Path extends SOReflect implements Drawable, Interactable, Layout, ModelListener {
//	TODO: implement translation for grid, in case the top/left change
	public SA contents;
	public SA path;
	public SA model;
	public double width;
	public double height;
	public double columnSpan;
	public SO slider;
	public double sliderVal;

	private ArrayList<Point> pointsList = null;
	private int pointCount = 1;
	private double originalWidth = -1;
	private double originalHeight = -1;

	private SimpleMatrix cr;
	private SimpleMatrix pts;
	private SimpleMatrix t;
	private int currsliderSegment = 0;

	public Path() {
		WidgetUtils.addListener(this);
	}


	// DRAWABLE
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Drawable drawable = (Drawable) so;
			drawable.paint(g2);
		}
		setSliderPoint();
		double rotation = rotateSlider();
		double oldRotation = getSliderGroup().rotate;
		getSliderGroup().rotate -= Math.toDegrees(rotation);
		getSliderGroup().paint(g);
		getSliderGroup().rotate = oldRotation;

//		g.setColor(Color.black);
//		for (Point catmullPoint : pointsList) {
//			g2.drawRect((int) catmullPoint.getX() - 2, (int) catmullPoint.getY() - 2, 4, 4);
//		}
//		g.setColor(Color.darkGray);
//		ArrayList<Point> pathPoints = generatePathPoints();
//		for (int i = 0; i < pathPoints.size(); i++) {
//			if (i == 10) g.setColor(Color.blue);
//			if (i == 20) g.setColor(Color.orange);
//			if (i == 30) g.setColor(Color.green);
//			Point catmullPoint = pathPoints.get(i);
//			g2.drawRect((int) catmullPoint.getX() - 2, (int) catmullPoint.getY() - 2, 4, 4);
//		}
//	}
//	private ArrayList<Point> generatePathPoints() {
//		ArrayList<Point> pathPoints = new ArrayList<>();
//		double realSlideVal = this.sliderVal;
//		int realcurrsliderSegment = this.currsliderSegment;
//		this.pointCount = getPoints("X").length;
//		for (int index = 0; index < pointCount - 1; index++) {
//			this.currsliderSegment = index;
//			for (double i = 0.1; i < 1; i = i + 0.1) {
//				this.pts = updatePointsMatrix(currsliderSegment);
//				this.t = updateMatrixT(this.sliderVal);
//				this.sliderVal = i;
//				setSliderPoint();
//				pathPoints.add(new Point(getSliderGroup().tx, getSliderGroup().ty));
//			}
//		}
//		this.sliderVal = realSlideVal;
//		this.currsliderSegment = realcurrsliderSegment;
//		return pathPoints;
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
	public void makeIdle() {}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		if (mouseIsOnSlider(WidgetUtils.mouseType.DOWN, x, y, myTransform)) {
			WidgetUtils.setSliderBeingUsed(this);
			return true;
		} else {
			return handleMouse(contents, x, y, myTransform, WidgetUtils.mouseType.DOWN);
		}
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		if (WidgetUtils.sliderBeingUsed(this)) {
			Point2D ptSrc = new Point(x, y);
			Point2D ptDst = myTransform.transform(ptSrc, null);
			Point nearestPoint = findNearestPoint(new Point(ptDst.getX(), ptDst.getY()));
			this.getSliderGroup().tx = nearestPoint.getX();
			this.getSliderGroup().ty = nearestPoint.getY();
			return true;
		}
		return handleMouse(contents, x, y, myTransform, mouseType.MOVE);
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return handleMouse(contents, x, y, myTransform, WidgetUtils.mouseType.UP);
	}

	//	LAYOUT
	@Override
	public double getColSpan() {
		return columnSpan;
	}

	private double getOriginalWidth(){
		if(this.originalWidth == -1) this.originalWidth = this.width;
		return originalWidth;
	}

	private double getOriginalHeight(){
		if(this.originalHeight == -1) this.originalHeight = this.height;
		return originalHeight;
	}

	@Override
	public double getMinWidth() {
		return getOriginalWidth();
	}

	@Override
	public double getDesiredWidth() {
		return getOriginalWidth();
	}

	@Override
	public double getMaxWidth() {
		return getOriginalWidth();
	}

	@Override
	public double getMinHeight() {
		return getOriginalHeight();
	}

	@Override
	public double getDesiredHeight() {
		return getOriginalHeight();
	}

	@Override
	public double getMaxHeight() {
		return getOriginalHeight();
	}


	//	MODEL LISTENER
	@Override
	public void modelUpdated(ArrayList<String> modelPath, String newValue) {
		if (slider == null || modelPath.size() != model.size()) return;

		for (int i = 0; i < model.size(); i++) // Verify that it matches
			if (!modelPath.get(i).equals(model.getString(i))) return; //IT WASN'T A MATCH
		getWindowCoordsFromSlideVal(Double.valueOf(newValue));
	}

	@Override
	public void setHBounds(double left, double right) {
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			if (so instanceof Layout) {
				Layout layout = (Layout) so;
				layout.setHBounds(left, right);
			}
		}
		getSliderGroup().setHBounds(left, right);
		if (originalWidth == -1) originalWidth = this.width;
		this.width = right - left;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			if (so instanceof Layout) {
				Layout layout = (Layout) so;
				layout.setVBounds(top, bottom);
			}
		}
		getSliderGroup().setVBounds(top, bottom);
		if(originalHeight == -1) originalHeight = this.height;
		this.height = bottom - top;
		recalibratePoints();
	}



	///////////////////////////////////////////////////////////////////////////////////
	/////PRIVATE CLASS METHODS/////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	private void createMatrices() {
		cr = new SimpleMatrix(4, 4);
		cr.set(0, 0, -0.5);
		cr.set(0, 1, 1);
		cr.set(0, 2, -0.5);
		cr.set(0, 3, 0);
		cr.set(1, 0, 1.5);
		cr.set(1, 1, -2.5);
		cr.set(1, 2, 0);
		cr.set(1, 3, 1);
		cr.set(2, 0, -1.5);
		cr.set(2, 1, 2);
		cr.set(2, 2, 0.5);
		cr.set(2, 3, 0);
		cr.set(3, 0, 0.5);
		cr.set(3, 1, -0.5);
		cr.set(3, 2, 0);
		cr.set(3, 3, 0);

		this.pts = updatePointsMatrix(currsliderSegment);
		this.t = updateMatrixT(this.sliderVal);
	}

	private SimpleMatrix updatePointsMatrix(int segNum) {
		SimpleMatrix newPts = new SimpleMatrix(2, 4);
//		Get "i-1"
		if (segNum == 0) {
			newPts.set(0, 0, pointsList.get(0).getX());
			newPts.set(1, 0, pointsList.get(0).getY());
		} else {
			newPts.set(0, 0, pointsList.get(segNum - 1).getX());
			newPts.set(1, 0, pointsList.get(segNum - 1).getY());
		}
//		Get "i"
		newPts.set(0, 1, pointsList.get(segNum).getX());
		newPts.set(1, 1, pointsList.get(segNum).getY());
//		Get "i+1"
		if (segNum + 1 < pointsList.size()) {
			newPts.set(0, 2, pointsList.get(segNum + 1).getX());
			newPts.set(1, 2, pointsList.get(segNum + 1).getY());
		} else {
			newPts.set(0, 2, pointsList.get(segNum).getX());
			newPts.set(1, 2, pointsList.get(segNum).getY());
		}
//		Get "i+2"
		if (segNum + 2 < pointsList.size()) {
			newPts.set(0, 3, pointsList.get(segNum + 2).getX());
			newPts.set(1, 3, pointsList.get(segNum + 2).getY());
		} else if (segNum + 1 < pointsList.size()) {
			newPts.set(0, 3, pointsList.get(segNum + 1).getX());
			newPts.set(1, 3, pointsList.get(segNum + 1).getY());
		} else {
			newPts.set(0, 3, pointsList.get(segNum).getX());
			newPts.set(1, 3, pointsList.get(segNum).getY());
		}
		return newPts;
	}

	private SimpleMatrix updateMatrixT(double slideVal) {
		SimpleMatrix newT = new SimpleMatrix(4, 1);
		newT.set(0, 0, pow(slideVal, 3));
		newT.set(1, 0, pow(slideVal, 2));
		newT.set(2, 0, slideVal);
		newT.set(3, 0, 1);
		return newT;
	}

	private SimpleMatrix getDerivativeOfT() {
		double slideVal = sliderVal;
		SimpleMatrix newT = new SimpleMatrix(4, 1);
		newT.set(0, 0, 3 * pow(slideVal, 2));
		newT.set(1, 0, 2 * slideVal);
		newT.set(2, 0, 1);
		newT.set(3, 0, 0);
		return newT;
	}

	private void recalibratePoints() {
		ArrayList<Point> originalPoints = getPoints();
		double vertDiffRatio = 1 + ((this.height - this.originalHeight) / this.originalHeight);
		double horzDiffRatio = 1 + ((this.width - this.originalWidth) / this.originalWidth);
		this.pointsList = new ArrayList<>();
		for (Point point : originalPoints) {
			point.setX(point.getX() * horzDiffRatio);
			point.setY(point.getY() * vertDiffRatio);
			pointsList.add(point);
		}
		this.pts = updatePointsMatrix(currsliderSegment);
		this.t = updateMatrixT(this.sliderVal);
	}

	private ArrayList<Point> getPoints() {
		int[] xArray = getPoints("X");
		int[] yArray = getPoints("Y");
		ArrayList<Point> points = new ArrayList<>();
		for (int i = 0; i < xArray.length; i++) {
			Point point = new Point(xArray[i], yArray[i]);
			points.add(point);
		}
		return points;
	}

	private int[] getPoints(String coord) {
		int[] xArray = new int[path.size()];
		int[] yArray = new int[path.size()];
		for (int i = 0; i < path.size(); i++) {
			SV xPoint = path.get(i).get("x");
			SV yPoint = path.get(i).get("y");
			try {
				Long x = xPoint.getLong();
				Long y = yPoint.getLong();
				xArray[i] = x.intValue();
				yArray[i] = y.intValue();
			} catch (Exception e) {
				double x = xPoint.getDouble();
				double y = yPoint.getDouble();
				xArray[i] = (int) x;
				yArray[i] = (int) y;
			}
		}
		if (coord.equals("X")) {
			return xArray;
		} else {
			return yArray;
		}
	}

	private double rotateSlider() {
		SimpleMatrix derivT = getDerivativeOfT();
		Point point = getNewSlideLoc(this.pts, derivT);
		return Math.atan2(point.getY(), point.getX());
	}

	private void setSliderPoint() {
		if (model == null) return;
		Point updatedSlideLocation = getNewSlideLoc();
		getSliderGroup().tx = updatedSlideLocation.getX();
		getSliderGroup().ty = updatedSlideLocation.getY() - 2;
		double modelValue = (sliderVal + this.currsliderSegment) / (this.pointCount - 1);
		WidgetUtils.updateModel(model, String.valueOf(modelValue));
	}

	private void getWindowCoordsFromSlideVal(Double modelValue) {
//		TODO: we may need to normalize the modelValue to be b/w 0-1.
		if (this.pointCount <= 1) {
			this.pointsList = getPoints();
			this.pointCount = this.pointsList.size();
		} else if (sliderVal == ((this.pointCount - 1) * modelValue) - this.currsliderSegment) return;

		this.currsliderSegment = (int) Math.ceil(modelValue * (this.pointCount - 1)) - 1;
		if (this.currsliderSegment < 0) this.currsliderSegment = 0;

		sliderVal = ((this.pointCount - 1) * modelValue) - this.currsliderSegment;
		this.pts = updatePointsMatrix(currsliderSegment);
		this.t = updateMatrixT(this.sliderVal);
		setSliderPoint();
	}


	public Group getSliderGroup() {
		return (Group) slider;
	}

	private boolean mouseIsOnSlider(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform oldTrans) {
		ArrayList<Drawable> drawables = new ArrayList<>();
		drawables.add(getSliderGroup());
		return handleMouse(drawables, x, y, oldTrans, mouseType);
	}

	public Point getNewSlideLoc() {
		if (cr == null) createMatrices();
		return getNewSlideLoc(this.pts, this.t);
	}

	public Point getNewSlideLoc(SimpleMatrix points, SimpleMatrix tVals) {
		if (cr == null) createMatrices();
		SimpleMatrix pointsWithCatmull = points.mult(cr);
		SimpleMatrix newPoint = pointsWithCatmull.mult(tVals);
		return new Point(newPoint.get(0),newPoint.get(1));
	}

	private static class tuple {
		public double sliderValue;
		public Point closestPoint;
		public tuple(double sliderValue, Point closestPoint) {
			this.sliderValue = sliderValue;
			this.closestPoint = closestPoint;
		}
	}

	private Point findNearestPoint(Point convertedPoint) {
		double dist = Double.MAX_VALUE;
		Point returnPoint = null;
		double bestSliderVal = 0;
		for (int i = 0; i < this.pointCount - 1; i++) {
			tuple bestTuple = getClosestPointInSegment(i, convertedPoint, 0, 1);
			Point closestInSegment = bestTuple.closestPoint;
			double closestInSegDist = getDistance(convertedPoint, closestInSegment);
			if (closestInSegDist < dist) {
				this.currsliderSegment = i;
				bestSliderVal = bestTuple.sliderValue;
				dist = closestInSegDist;
				returnPoint = closestInSegment;
			}
		}
		this.t = updateMatrixT(bestSliderVal);
		this.pts = updatePointsMatrix(this.currsliderSegment);
		this.sliderVal = bestSliderVal;
		return returnPoint;
	}


	private tuple getClosestPointInSegment(int segmentNum, Point clickPoint, double low, double high) {
		SimpleMatrix points = updatePointsMatrix(segmentNum);
		SimpleMatrix lowTVals = updateMatrixT(low);
		Point lowPoint = getNewSlideLoc(points, lowTVals);
		double lowDist = getDistance(clickPoint, lowPoint);

		SimpleMatrix highTVals = updateMatrixT(high);
		Point highPoint = getNewSlideLoc(points, highTVals);
		double highDist = getDistance(clickPoint, highPoint);

		double diff = high - low;
		if (diff < 0.05) {
			if (lowDist < highDist) return new tuple(low, lowPoint);
			else return new tuple(high, highPoint);
		} else if (lowDist < highDist) {
			return getClosestPointInSegment(segmentNum, clickPoint, low, (low + (diff / 2)));
		} else {
			return getClosestPointInSegment(segmentNum, clickPoint, (low + (diff / 2)), high);
		}
	}

	private double getDistance(Point from, Point to) {
		return Math.sqrt((from.getX() - to.getX()) * (from.getX() - to.getX()) + (from.getY() - to.getY()) * (from.getY() - to.getY()));
	}
}