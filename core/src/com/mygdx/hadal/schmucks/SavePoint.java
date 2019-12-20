package com.mygdx.hadal.schmucks;

import com.badlogic.gdx.math.Vector2;

public class SavePoint {

	private Vector2 location;
	private Vector2 zoomLocation;
	private float zoom;
	
	public SavePoint(Vector2 location, Vector2 zoomLocation, float zoom) {
		this.location = location;
		this.zoomLocation = zoomLocation;
		this.zoom = zoom;
	}

	public Vector2 getLocation() {
		return location;
	}

	public void setLocation(Vector2 location) {
		this.location = location;
	}
	
	public Vector2 getZoomLocation() {
		return zoomLocation;
	}

	public void setZoomLocation(Vector2 zoomLocation) {
		this.zoomLocation = zoomLocation;
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}	
}
