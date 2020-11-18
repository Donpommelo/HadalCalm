package com.mygdx.hadal.effects;

/**
 * A particle color represents the rgb of a color that a particle can be tinted
 * @author Nardelia Noms
 */
public enum HadalColor {

	NOTHING(0.0f, 0.0f, 0.0f),
	RANDOM(0.0f, 0.0f, 0.0f),
	CHANGING(0.0f, 0.0f, 0.0f),
	
	WHITE(0.0f, 0.0f, 0.0f),
	BLACK(1.0f, 1.0f, 1.0f),
	
	RED(1.0f, 0.0f, 0.0f),
	GREEN(0.0f, 1.0f, 0.0f),
	BLUE(0.0f, 0.0f, 1.0f),
	YELLOW(1.0f, 1.0f, 0.0f),
	MAGENTA(1.0f, 0.0f, 1.0f),
	CYAN(0.0f, 1.0f, 1.0f),
	
	BROWN(0.64f, 0.16f, 0.16f),
	CHARTREUSE(0.5f, 1.0f, 0.0f),
	CRIMSON(0.86f, 0.078f, 0.235f),
	DARK_GREY(0.663f, 0.663f, 0.663f),
	GOLD(0.855f, 0.647f, 0.043f),
	GREY(0.502f, 0.502f, 0.502f),
	HOT_PINK(1.0f, 0.41f, 0.71f),
	INDIGO(0.29f, 0.0f, 0.51f),
	ORANGE(1.0f, 0.55f, 0.0f),
	PALE_GREEN(0.5f, 1.0f, 0.5f),
	PLUM(0.87f, 0.6f, 0.87f),
	MIDNIGHT_BLUE(0.09f, 0.09f, 0.44f),
	SKY_BLUE(0.53f, 0.8f, 1.0f),
	TAN(0.824f, 0.706f, 0.549f),
	TURQOISE(0.25f, 0.88f, 0.82f),
	VIOLET(0.9f, 0.5f, 0.9f),

	;
	
	private final float r, g, b;
	
	HadalColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public float getR() { return r; }
	
	public float getG() { return g; }
	
	public float getB() { return b; }
	
}
