package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import com.company.draw.*;
import org.ejml.simple.*;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static java.lang.Math.*;

public class Curve extends SOReflect implements Drawable, Selectable, Layout {
//	TODO: implement translation for grid, in case the top/left change

	public SA points;
	public double thickness;
	public SO border;
	public SO fill;
	public double columnSpan;


	private ArrayList<Point> curvePoints = null;

	private double width = -1;
	private double height = -1;
	private double originalWidth = -1;
	private double originalHeight = -1;

	private ArrayList<Point> pointsList = null;
	private SimpleMatrix cr;
	private SimpleMatrix pts;
	private SimpleMatrix t;
	private Point currPoint = new Point(0, 0);

	@Override
	public void paint(Graphics g) {
		if (this.curvePoints == null) loadPoints();
		Graphics2D g2 = (Graphics2D) g;

		if (thickness > 0) {
			Stroke stroke = new BasicStroke((int) thickness);
			g2.setStroke(stroke);
		} else {
			Stroke stroke = new BasicStroke(1);
			g2.setStroke(stroke);
		}

//		CHECK IF CLOSED
		if (isClosed()) {
			if (fill != null) {
				Double red = fill.get("r").getDouble();
				Double green = fill.get("g").getDouble();
				Double blue = fill.get("b").getDouble();
				Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
				g2.setColor(lineColor);
			} // Draw fill
			g2.fillPolygon(getCurvePoints("X"), getCurvePoints("Y"), points.size());

			if (border != null) {
				Double red = border.get("r").getDouble();
				Double green = border.get("g").getDouble();
				Double blue = border.get("b").getDouble();
				Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
				g2.setColor(lineColor);
			} // Draw border
			g2.drawPolygon(getCurvePoints("X"), getCurvePoints("Y"), points.size());
		} else {
			if (border != null) {
				Double red = border.get("r").getDouble();
				Double green = border.get("g").getDouble();
				Double blue = border.get("b").getDouble();
				Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
				g2.setColor(lineColor);
			}

			generatePathPoints();
			for (Point pt : pointsList) {
				g2.fillRect((int) pt.getX() - 2, (int) pt.getY() - 2, (int) thickness, (int) thickness);
			}
		}
	}

	private void generatePathPoints() {
		this.pointsList = new ArrayList<>();
		for (int index = 0; index < getPoints("X").length - 1; index++) {
			double sliderVal = 0;
			for (double i = 0.1; i < 1; i = i + 0.02) {
				this.pts = updatePointsMatrix(index);
				this.t = updateMatrixT(sliderVal);
				setSliderPoint();
				sliderVal = i;
				this.pointsList.add(new Point(this.currPoint.getX(), this.currPoint.getY()));
			}
		}
	}

	private void setSliderPoint() {
		Point updatedSlideLocation = getNewSlideLoc(this.pts, this.t);
		this.currPoint.setX(updatedSlideLocation.getX());
		this.currPoint.setY(updatedSlideLocation.getY() - 2);
	}


	private void loadPoints() {
		this.curvePoints = new ArrayList<>();
		//		Make point arrays
		int[] xArray = getPoints("X");
		int[] yArray = getPoints("Y");
		for (int index = 0; index < xArray.length; index++) {
			curvePoints.add(new Point(xArray[index], yArray[index]));
		}
	}

	private boolean isClosed() {
		return curvePoints.get(curvePoints.size() - 1).getX() == curvePoints.get(0).getX() &&
				curvePoints.get(curvePoints.size() - 1).getY() == curvePoints.get(0).getY();
	}

	private int[] getCurvePoints(String coord) {
		if (this.curvePoints == null) loadPoints();
		int[] xArray = new int[curvePoints.size()];
		int[] yArray = new int[curvePoints.size()];
		for (int i = 0; i < curvePoints.size(); i++) {
			xArray[i] = (int) curvePoints.get(i).getX();
			yArray[i] = (int) curvePoints.get(i).getY();
		}
		if (coord.equals("X")) return xArray;
		else return yArray;
	}

	private int[] getPoints(String coord) {
		int[] xArray = new int[points.size()];
		int[] yArray = new int[points.size()];
		for (int i = 0; i < points.size(); i++) {
			SV xPoint = points.get(i).get("x");
			SV yPoint = points.get(i).get("y");
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


	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		int[] xArray = getCurvePoints("X");
		int[] yArray = getCurvePoints("Y");

		Point2D ptSrc = new Point(x, y);
		Point2D ptDst = transform.transform(ptSrc, null);
		x = ptDst.getX();
		y = ptDst.getY();

		if (isClosed()) {
//			TODO: test this
			if (PolyContains.contains(x, y, xArray, yArray)) return new ArrayList<>();
		} else {
			for (int i = 0; i < xArray.length; i++) {
//				TODO: test this
				Point currPoint = new Point(xArray[i], yArray[i]);
				double xSqr = pow(currPoint.getX() - ptDst.getX(), 2);
				double ySqr = pow(currPoint.getY() - ptDst.getY(), 2);
				double distance = pow(xSqr + ySqr, 0.5);
				if (distance <= 3) {
					System.out.println("Selected");
					return new ArrayList<>();
				}
			}
		}
		return null;


//		double dist = Double.MAX_VALUE;
//		Point returnPoint = null;
//		double bestSliderVal = 0;
//		for (int i = 0; i < this.pointCount - 1; i++) {
//			tuple bestTuple = getClosestPointInSegment(i, convertedPoint, 0, 1);
//			Point closestInSegment = bestTuple.closestPoint;
//			double closestInSegDist = getDistance(convertedPoint, closestInSegment);
//			if (closestInSegDist < dist) {
////				this.currsliderSegment = i;
//				bestSliderVal = bestTuple.sliderValue;
//				dist = closestInSegDist;
//				returnPoint = closestInSegment;
//			}
//		}
////		this.t = updateMatrixT(bestSliderVal);
////		this.pts = updatePointsMatrix(this.currsliderSegment);
////		this.sliderVal = bestSliderVal;
//
//		return returnPoint;
	}

	@Override
	public Point2D[] controls() {
//		TODO: implement this
		throw new UnsupportedOperationException("This method is not implemented");
	}

	@Override
	public void setBackgroundColor(SO newColor) {
		this.fill = newColor;
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


	@Override
	public void setHBounds(double left, double right) {
//		for (int i = 0; i < contents.size(); i++) {
//			SV sv = contents.get(i);
//			SO so = sv.getSO();
//			if (so instanceof Layout) {
//				Layout layout = (Layout) so;
//				layout.setHBounds(left, right);
//			}
//		}
//		getSliderGroup().setHBounds(left, right);
//		if (originalWidth == -1) originalWidth = this.width;
		this.width = right - left;
	}

	@Override
	public void setVBounds(double top, double bottom) {
//		for (int i = 0; i < contents.size(); i++) {
//			SV sv = contents.get(i);
//			SO so = sv.getSO();
//			if (so instanceof Layout) {
//				Layout layout = (Layout) so;
//				layout.setVBounds(top, bottom);
//			}
//		}
//		getSliderGroup().setVBounds(top, bottom);
//		if(originalHeight == -1) originalHeight = this.height;
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
	}

	private SimpleMatrix updatePointsMatrix(int segNum) {
		SimpleMatrix newPts = new SimpleMatrix(2, 4);
//		Get "i-1"
		if (segNum == 0) {
			newPts.set(0, 0, curvePoints.get(0).getX());
			newPts.set(1, 0, curvePoints.get(0).getY());
		} else {
			newPts.set(0, 0, curvePoints.get(segNum - 1).getX());
			newPts.set(1, 0, curvePoints.get(segNum - 1).getY());
		}
//		Get "i"
		newPts.set(0, 1, curvePoints.get(segNum).getX());
		newPts.set(1, 1, curvePoints.get(segNum).getY());
//		Get "i+1"
		if (segNum + 1 < curvePoints.size()) {
			newPts.set(0, 2, curvePoints.get(segNum + 1).getX());
			newPts.set(1, 2, curvePoints.get(segNum + 1).getY());
		} else {
			newPts.set(0, 2, curvePoints.get(segNum).getX());
			newPts.set(1, 2, curvePoints.get(segNum).getY());
		}
//		Get "i+2"
		if (segNum + 2 < curvePoints.size()) {
			newPts.set(0, 3, curvePoints.get(segNum + 2).getX());
			newPts.set(1, 3, curvePoints.get(segNum + 2).getY());
		} else if (segNum + 1 < curvePoints.size()) {
			newPts.set(0, 3, curvePoints.get(segNum + 1).getX());
			newPts.set(1, 3, curvePoints.get(segNum + 1).getY());
		} else {
			newPts.set(0, 3, curvePoints.get(segNum).getX());
			newPts.set(1, 3, curvePoints.get(segNum).getY());
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

	public Point getNewSlideLoc(SimpleMatrix points, SimpleMatrix tVals) {
		if (cr == null) createMatrices();
		SimpleMatrix pointsWithCatmull = points.mult(cr);
		SimpleMatrix newPoint = pointsWithCatmull.mult(tVals);
		return new Point(newPoint.get(0),newPoint.get(1));
	}

//	private static class tuple {
//		public double sliderValue;
//		public Point closestPoint;
//		public tuple(double sliderValue, Point closestPoint) {
//			this.sliderValue = sliderValue;
//			this.closestPoint = closestPoint;
//		}
//	}

//	private tuple getClosestPointInSegment(int segmentNum, Point clickPoint, double low, double high) {
//		SimpleMatrix points = updatePointsMatrix(segmentNum);
//		SimpleMatrix lowTVals = updateMatrixT(low);
//		Point lowPoint = getNewSlideLoc(points, lowTVals);
//		double lowDist = getDistance(clickPoint, lowPoint);
//
//		SimpleMatrix highTVals = updateMatrixT(high);
//		Point highPoint = getNewSlideLoc(points, highTVals);
//		double highDist = getDistance(clickPoint, highPoint);
//
//		double diff = high - low;
//		if (diff < 0.05) {
//			if (lowDist < highDist) return new tuple(low, lowPoint);
//			else return new tuple(high, highPoint);
//		} else if (lowDist < highDist) {
//			return getClosestPointInSegment(segmentNum, clickPoint, low, (low + (diff / 2)));
//		} else {
//			return getClosestPointInSegment(segmentNum, clickPoint, (low + (diff / 2)), high);
//		}
//	}
//
//	private double getDistance(Point from, Point to) {
//		return Math.sqrt((from.getX() - to.getX()) * (from.getX() - to.getX()) + (from.getY() - to.getY()) * (from.getY() - to.getY()));
//	}
}