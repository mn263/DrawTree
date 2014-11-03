package com.company.draw;

public interface Layout {


	// Returns the minimum width that the widget will accept for drawing.
	double getMinWidth();

	// Returns the desired width that the widget wants for drawing.
	double getDesiredWidth();

	// Returns the maximum width that the widget can use for drawing.
	double getMaxWidth();

	// This sets the horizontal range that the object can display within. This will also set up the necessary information for the height information.
	void setHBounds( double left, double right);

	// Returns the minimum height that the widget will accept for drawing.
	double getMinHeight();

	// Returns the desired height that the widget wants for drawing.
	double getDesiredHeight();

	// Returns the maximum height that the widget can use for drawing.
	double getMaxHeight();

	// This sets the vertical bounds for the object and causes it to layout any of its children.
	void setVBounds( double top, double bottom);
}
