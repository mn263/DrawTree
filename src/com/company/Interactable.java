package com.company;

import com.company.draw.shapes.*;

import java.awt.geom.*;

public interface Interactable {

    // If the object is Root then it returns itself. Otherwise this method is called on the parent()
    Root getPanel();

	// This is called whenever a mouse event is received. It returns true if the object handled this event, false otherwise.
	boolean key( char key );

	// This is called whenever a mouse down event is received. It returns true if the object handled this event, false otherwise.
	boolean mouseDown( double x, double y, AffineTransform myTransform);

	// This is called whenever a mouse move event is received. It returns true if the object handled this event, false otherwise.
	boolean mouseMove( double x, double y, AffineTransform myTransform);

	//This is called whenever a mouse up event is received. It returns true if the object handled this event, false otherwise.
	boolean mouseUp( double x, double y, AffineTransform myTransform);
}
