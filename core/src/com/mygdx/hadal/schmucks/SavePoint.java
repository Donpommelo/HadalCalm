package com.mygdx.hadal.schmucks;

import com.badlogic.gdx.math.Vector2;

/**
 * A save point represents the data necessary to make a player respawn at a certain point.
 * @author Zachary Tu
 *
 */
public class SavePoint {

	//the location that the player would be spawned at.
	private Vector2 location;
	
	//the location that the camera should be locked on when the player respawns. Null means follow the player
	private Vector2 zoomLocation;
	
	//this is the zoom that the camera will be set to when the player respawns
	private float zoom;
	
	public SavePoint(Vector2 location, Vector2 zoomLocation, float zoom) {
		this.location = location;
		this.zoomLocation = zoomLocation;
		this.zoom = zoom;
	}

	public Vector2 getLocation() { return location; }

	public void setLocation(Vector2 location) {	this.location = location; }
	
	public Vector2 getZoomLocation() { return zoomLocation; }

	public void setZoomLocation(Vector2 zoomLocation) {	this.zoomLocation = zoomLocation; }

	public float getZoom() { return zoom; }

	public void setZoom(float zoom) { this.zoom = zoom;	}	
}
