package com.mygdx.hadal.client;

import com.badlogic.gdx.math.Vector2;

/**
 * A Client Prediction Frame contains information about the client player's movement for a single frame.
 * These are collected by the ClientPlayer to predict and extrapolate their position
 * @author Lardwig Lucroix
 */
public class ClientPredictionFrame {

	public final Vector2 positionChange = new Vector2();
	public final Vector2 velocity = new Vector2();
	public float delta;
	
	public ClientPredictionFrame(float delta) {
		this.delta = delta;
	}
}
