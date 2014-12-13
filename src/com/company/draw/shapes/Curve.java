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

	public double currTop = 0;
	public double currLeft = 0;

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
				// Draw fill
				g2.fillPolygon(getCurvePoints("X"), getCurvePoints("Y"), points.size());
			}

			if (border != null) {
				Double red = border.get("r").getDouble();
				Double green = border.get("g").getDouble();
				Double blue = border.get("b").getDouble();
				Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
				g2.setColor(lineColor);
				// Draw border
				g2.drawPolygon(getCurvePoints("X"), getCurvePoints("Y"), points.size());
			}
		} else {
			if (border != null) {
				Double red = border.get("r").getDouble();
				Double green = border.get("g").getDouble();
				Double blue = border.get("b").getDouble();
				Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
				g2.setColor(lineColor);
			}

			generatePathPoints();
			for (int i = 0; i < pointsList.size() - 1; i++) {
				Point start = pointsList.get(i);
				Point end = pointsList.get(i + 1);
				g2.drawLine((int) start.getX(), (int) start.getY(),
						(int) end.getX(), (int) end.getY());
			}
		}
	}

	private void generatePathPoints() {
		this.pointsList = new ArrayList<>();
		for (int index = 0; index < getPoints().size() - 1; index++) {
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

		if (isClosed() && this.fill != null) {
			if (PolyContains.contains(x, y, xArray, yArray)) return new ArrayList<>();
		} else if (isClosed()) {
			for (int i = 0; i < curvePoints.size() - 1; i++) {
				Point one = curvePoints.get(i);
				Point two = curvePoints.get(i + 1);
				boolean selected = SelectUtils.checkIfInLine(one.getX(), two.getX(), one.getY(), two.getY(),
						ptDst.getX(), ptDst.getY(), 3);
				if (selected) {
					return new ArrayList<>();
				}
			}
		} else {
			for (Point currPoint : pointsList) {
				double xSqr = pow(currPoint.getX() - ptDst.getX(), 2);
				double ySqr = pow(currPoint.getY() - ptDst.getY(), 2);
				double distance = pow(xSqr + ySqr, 0.5);
				if (distance <= 6) {
					return new ArrayList<>();
				}
			}
		}
		return null;
	}

	@Override
	public Point2D[] controls() {
		Point2D[] controlPoints = new Point2D[curvePoints.size()];
		for (int i = 0; i < curvePoints.size(); i++) {
			controlPoints[i] = curvePoints.get(i);
		}
		return controlPoints;
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
		if(this.originalWidth == -1) {
			if (this.width == -1) {
				curvePoints = getPoints();
				double minX = curvePoints.get(0).getX();
				double maxX = curvePoints.get(0).getX();
				for (Point point : curvePoints) {
					if (point.getX() < minX) minX = point.getX();
					if (point.getX() > maxX) maxX = point.getX();
				}
				this.originalWidth = maxX - minX + 20;
			} else {
				this.originalWidth = this.width;
			}
		}
		return originalWidth;
	}

	private double getOriginalHeight(){
		if(this.originalHeight == -1) {
			if(this.height == -1) {
				curvePoints = getPoints();
				double minY = curvePoints.get(0).getY();
				double maxY = curvePoints.get(0).getX();
				for (Point point : curvePoints) {
					if(point.getY() < minY) minY = point.getY();
					if(point.getY() > maxY) maxY = point.getY();
				}
				this.originalHeight = maxY - minY + 20;
			} else {
				this.originalHeight = this.height;
			}
		}
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
		this.width = right - left;
		if (originalWidth == -1) originalWidth = getOriginalWidth();
		this.currLeft = left;
		recalibratePoints();
	}

	@Override
	public void setVBounds(double top, double bottom) {
		this.height = bottom - top;
		if(originalHeight == -1) originalHeight = getOriginalHeight();
		currTop = top;
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
		this.curvePoints = new ArrayList<>();
		for (Point point : originalPoints) {
			point.setX((point.getX() + currLeft) * horzDiffRatio);
			point.setY((point.getY() + currTop) * vertDiffRatio);
//			point.setX(point.getX() * horzDiffRatio);
//			point.setY(point.getY() * vertDiffRatio);
			pointsList.add(point);
			curvePoints.add(point);
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
}
