package com.mygdx.hadal.client;

import com.badlogic.gdx.math.Vector2;

public class ClientPredictionFrame {

	public Vector2 positionChange = new Vector2();
	public Vector2 velocity = new Vector2();
	public float delta;
	
	public ClientPredictionFrame(float delta) {
		this.delta = delta;
	}
}
