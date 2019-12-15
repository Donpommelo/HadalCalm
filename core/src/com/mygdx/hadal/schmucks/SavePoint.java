package com.mygdx.hadal.schmucks;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;

public class SavePoint {

	private Vector2 location;
	private float zoom;
	private HadalEntity zoomPoint;
	
	public SavePoint(Vector2 location, float zoom, HadalEntity zoomPoint) {
		this.location = location;
		this.zoom = zoom;
		this.zoomPoint = zoomPoint;
	}

	public Vector2 getLocation() {
		return location;
	}

	public void setLocation(Vector2 location) {
		this.location = location;
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public HadalEntity getZoomPoint() {
		return zoomPoint;
	}

	public void setZoomPoint(HadalEntity zoomPoint) {
		this.zoomPoint = zoomPoint;
	}
	
}
