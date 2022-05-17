package com.mygdx.hadal.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A particle color represents the rgb of a color that a particle/sprite can be tinted
 * @author Nardelia Noms
 */
public enum HadalColor {

	NOTHING(1.0f, 1.0f, 1.0f),
	RANDOM(-1.0f, -1.0f, -1.0f),
	CHANGING(0.0f, 0.0f, 0.0f),
	
	WHITE(1.0f, 1.0f, 1.0f),
	BLACK(0.0f, 0.0f, 0.0f),
	
	RED(1.0f, 0.0f, 0.0f),
	GREEN(0.0f, 1.0f, 0.0f),
	BLUE(0.0f, 0.0f, 1.0f),
	YELLOW(1.0f, 1.0f, 0.0f),
	MAGENTA(1.0f, 0.0f, 1.0f),
	CYAN(0.0f, 1.0f, 1.0f),

	BANANA(1.0f, 1.0f, 0.208f),
	BEIGE(0.96f, 0.96f, 0.863f),
	BROWN(0.64f, 0.16f, 0.16f),
	CELADON(0.675f, 0.882f, 0.686f),
	CHARTREUSE(0.5f, 1.0f, 0.0f),
	COQUELICOT(1.0f, 0.22f, 0.0f),
	CRIMSON(0.86f, 0.078f, 0.235f),
	DARK_GREY(0.663f, 0.663f, 0.663f),
	EGGPLANT(0.38f, 0.251f, 0.318f),
	GOLD(0.855f, 0.647f, 0.043f),
	GREY(0.502f, 0.502f, 0.502f),
	HOT_PINK(1.0f, 0.41f, 0.71f),
	INDIGO(0.29f, 0.0f, 0.51f),
	MAUVE(0.718f, 0.518f, 0.518f),
	ORANGE(1.0f, 0.55f, 0.0f),
	PALE_GREEN(0.5f, 1.0f, 0.5f),
	PLUM(0.87f, 0.6f, 0.87f),
	MIDNIGHT_BLUE(0.09f, 0.09f, 0.44f),
	SKY_BLUE(0.53f, 0.8f, 1.0f),
	TAN(0.824f, 0.706f, 0.549f),
	TURQOISE(0.25f, 0.88f, 0.82f),
	VIOLET(0.623f, 0.0f, 1.0f),

	;

	private final Color color;
	private final Vector3 rgb = new Vector3();
	private final Vector3 hsv = new Vector3();

	HadalColor(float r, float g, float b) {
		this.rgb.set(r, g, b);
		color = new Color(r, g, b, 1.0f);

		float[] hsvTemp = new float[3];
		hsvTemp = color.toHsv(hsvTemp);
		this.hsv.set(hsvTemp[0] / 360, hsvTemp[1], hsvTemp[2]);
	}
	
	public Vector3 getRGB() { return rgb; }

	public Vector3 getHSV() { return hsv; }

	public Color getColor() { return color; }

	private static final ObjectMap<String, HadalColor> ColorsByName = new ObjectMap<>();
	static {
		for (HadalColor c : HadalColor.values()) {
			ColorsByName.put(c.toString(), c);
		}
	}
	public static HadalColor getByName(String s) {
		return ColorsByName.get(s, NOTHING);
	}
}
