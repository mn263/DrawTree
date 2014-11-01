package com.company.draw.shapes;

import com.company.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.geom.*;

public class TextBox implements Interactable {

	public String state;
	public SA contents;
	public SO idle;
	public SO hover;
	public SO active;
	public SA model;


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
		throw new NotImplementedException();
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		throw new NotImplementedException();
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		throw new NotImplementedException();
	}
}
