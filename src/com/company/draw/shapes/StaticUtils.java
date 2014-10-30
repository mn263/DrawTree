package com.company.draw.shapes;

import com.company.*;
import spark.data.*;

import java.awt.geom.*;

public class StaticUtils {

	public static Root root;
	public static enum mouseType { UP, DOWN, MOVE }

	public static AffineTransform getTransform(double tx, double ty, double sx, double sy, double rotate) {
		AffineTransform transform = new AffineTransform();
		transform.translate((int) -tx, (int) -ty);
		transform.rotate(-Math.toRadians(rotate));
		transform.scale(1 / sx, 1 / sy);
		return transform;
	}

	public static boolean handleMouse(SA contents, double x, double y, AffineTransform myTransform, mouseType mouseType) {

		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			Interactable interactable = (Interactable) so;
			boolean wasHandled = false;
			if (mouseType == StaticUtils.mouseType.UP) {
				wasHandled = interactable.mouseUp(x, y, myTransform);
			} else if (mouseType == StaticUtils.mouseType.DOWN) {
				wasHandled = interactable.mouseUp(x, y, myTransform);
			} else if (mouseType == StaticUtils.mouseType.MOVE) {
				wasHandled = interactable.mouseUp(x, y, myTransform);
			} else {
				try {
					throw new Exception("mouse type must have been invalid");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (wasHandled) {
				return true;
			}
		}
		return false;
	}

	public static void buttonPressed(SA model) {
		System.out.println("Here");
//		model.get
	}
}
