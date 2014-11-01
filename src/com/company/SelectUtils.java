package com.company;

import com.company.draw.shapes.*;

import java.util.*;

public class SelectUtils {

	public static boolean checkIfInLine(double x1, double x2, double y1, double y2, double x, double y, double allowance) {
		boolean isOutOfRange = (x < Math.min(x1 , x2) || x > Math.max(x1 , x2) || y < Math.min(y1, y2)
				|| y > Math.max(y1, y2));

		if (!isOutOfRange) {
			Point normal = getVectorNormal(x1, y1, x2, y2);
			double distFromOrigin = x1 * normal.getY() + y1 * normal.getX();
			double distFromXYtoLine = Math.abs(x * normal.getY() + y * normal.getX() - distFromOrigin);

			if (distFromXYtoLine <= allowance) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Integer> checkIfInBoxShape(Rect rectangle, double mouseX, double mouseY) {
		boolean isSelected =
				(mouseX >= rectangle.left) && (mouseX <= rectangle.left + rectangle.width) &&
				(mouseY >= rectangle.top) && (mouseY <= rectangle.top + rectangle.height);

		if (isSelected) {
			return new ArrayList<Integer>();
		} else {
			return null;
		}
	}
	public static ArrayList<Integer> checkIfInText(Text text, double mouseX, double mouseY) {
		boolean isSelected =
				(mouseX >= text.x) && (mouseX <= text.x + text.text.length() * text.size) &&
				(mouseY >= text.y - text.size) && (mouseY <= text.y);

		if (isSelected) {
			return new ArrayList<Integer>();
		} else {
			return null;
		}
	}

	public static boolean checkIfInOvalShape(Ellipse ellipse, double x, double y) {
		Point center = new Point(ellipse.left + ellipse.width/2, ellipse.top + ellipse.height/2);
		double distanceX = Math.pow(center.getX() - x, 2) / Math.pow(ellipse.width/2, 2);
		double distanceY = Math.pow(center.getY() - y, 2) / Math.pow(ellipse.height/2, 2);
		return distanceX + distanceY <= 1;
	}

	private static Point getVectorNormal(double x1, double y1, double x2, double y2) {
		double normalY = (-(y2 - y1)) / (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
		double normalX = (x2 - x1) / (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
		return new Point(normalX, normalY);
	}

}
