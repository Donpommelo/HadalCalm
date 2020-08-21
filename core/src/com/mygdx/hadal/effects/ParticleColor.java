package com.mygdx.hadal.effects;

/**
 * A particle color represents the rgb of a color that a particle can be tinted
 * @author Zachary Tu
 */
public enum ParticleColor {

	NOTHING(0.0f, 0.0f, 0.0f),
	RANDOM(0.0f, 0.0f, 0.0f),
	CHANGING(0.0f, 0.0f, 0.0f),
	
	WHITE(0.0f, 0.0f, 0.0f),
	BLACK(1.0f, 1.0f, 1.0f),
	
	RED(1.0f, 0.0f, 0.0f),
	GREEN(0.0f, 1.0f, 0.0f),
	BLUE(0.0f, 0.0f, 1.0f),
	YELLOW(1.0f, 1.0f, 0.0f),
	
	CYAN(0.0f, 1.0f, 1.0f),
	MAGENTA(1.0f, 0.0f, 1.0f),
	ORANGE(1.0f, 0.6f, 0.0f),
	PURPLE(0.5f, 0.0f, 0.5f),
	SILVER(0.75f, 0.75f, 1.75f),

	;
	
	private float r, g, b;
	
	ParticleColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public float getR() { return r; }
	
	public float getG() { return g; }
	
	public float getB() { return b; }
	
}
