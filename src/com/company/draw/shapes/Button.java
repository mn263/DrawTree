package com.company.draw.shapes;

import com.company.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.geom.*;
import java.util.*;

public class Button extends SOReflect implements Interactable {

	public String label;
	public SA contents;
	public String state;
	public SO idle;
	public SO hover;
	public SO active;
	public SA model;
	public double value;

	public boolean mouseIsDown = false;

	@Override
	public Root getPanel() {
		throw new NotImplementedException();
	}

	@Override
	public boolean key(char key) {
		throw new NotImplementedException();
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		mouseIsDown = true;
		return handleMouseEvent(x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return handleMouseEvent(x, y, myTransform);
	}


	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
//		TODO:
		// If a mouseUp() occurs while the button is in the active state then the model attribute should be used
		// as a path from the Root's model to identify an attribute to be changed. The model attribute
		// should be changed to the value of the Button's value attribute.
		mouseIsDown = false;
		return handleMouseEvent(x, y, myTransform);
	}

	private boolean handleMouseEvent(double x, double y, AffineTransform myTransform) {
		if (mouseIsOver(x, y, myTransform)) {
			if (this.mouseIsDown) {
				changeState(this.active);
			} else {
				changeState(this.hover);
			}
			return true;
		} else {
			changeState(this.idle);
			return false;
		}
	}

	private boolean mouseIsOver(double x, double y, AffineTransform myTransform) {
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			Selectable selectable = (Selectable) so;
			ArrayList<Integer> path = selectable.select(x, y, i, myTransform);
			if (path != null) {
				return true;
			}
		}
		return false;
	}

	public void changeState(SO newState) {

//		TODO:
//		If contents has a Text object that has an attribute 'class:"label"' then replace the text attribute of
// 		that object with the label attribute of the button.
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Selectable selectable = (Selectable) so;
			if (selectable.getClassStatus().equals("active")) {
				selectable.setBackgroundColor(newState);
			}
		}
	}

//The Text object will have its text attribute replaced by "Hit" and the color of the first Ellipse object
// will change in response to the mouse.

//	Button{ label:"Hit",
//			contents:[
//		Ellipse{ class:"active", left:100, top:200, width:100, height:50 }
//		Ellipse{ left:110, top:210, width:80, height:30,
//				fill:{r:255, g:255, b:255 }
//		},
//		Text{ class:"label", x:120, y:230, font:"sans-serif", size:15}
//		],
//		state:"idle"
//	}

}
