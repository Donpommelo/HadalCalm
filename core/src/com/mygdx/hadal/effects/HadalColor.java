package com.mygdx.hadal.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A particle color represents the rgb of a color that a particle/sprite can be tinted
 * @author Nardelia Noms
 */
public enum HadalColor {

	NOTHING(255,255,255),
	RANDOM(-255,-255,-255),
	CHANGING(0,0,0),
	
	WHITE(255,255,255),
	BLACK(0,0,0),
	
	RED(255,0,0),
	GREEN(0,255,0),
	BLUE(0,0,255),
	YELLOW(255,255,0),
	MAGENTA(255,0,0),
	CYAN(0,255,255),

	BANANA(255,255,53),
	BEIGE(245,245,220),
	BROWN(165,42,42),
	CELADON(172, 225,175),
	CHARTREUSE(127,255,0),
	COQUELICOT(255,56,0),
	CRIMSON(220,20,60),
	DARK_GREY(169,169,169),
	EGGPLANT(97,64,81),
	GOLD(255,215,0),
	GREY(128,128,128),
	HOT_PINK(255,105,180),
	INDIGO(75,0,130),
	MAUVE(224,176,255),
	ORANGE(155,165,0),
	PALE_GREEN(152,251,152),
	PLUM(221,160,221),
	MIDNIGHT_BLUE(25, 25,112),
	SKY_BLUE(135,206, 235),
	TAN(210,180,140),
	TURQOISE(64,224,208),
	VIOLET(238,130,238),

	BASE_SATURATED_DARK(1,3,255),
	BASE_MID_DARK(76,76,255),
	BASE_DESATURATED_DARK(153,153,255),
	BASE_SATURATED_LIGHT(86,255,1),
	BASE_MID_LIGHT(136,255,76),
	BASE_DESATURATED_LIGHT(187,255,153),
	BASE_ACCENT_1(253,255,0),
	BASE_ACCENT_2(255,0 ,4),

	MAXIMILLIAN_SATURATED_DARK(76,51,51),
	MAXIMILLIAN_MID_DARK(186,38,38),
	MAXIMILLIAN_DESATURATED_DARK(244,78,78),
	MAXIMILLIAN_DESATURATED_LIGHT(167,174,185),
	MAXIMILLIAN_ACCENT_1(234,177,70),
	MAXIMILLIAN_ACCENT_2(51,111,68),

	MOREAU_SATURATED_DARK(37,100,26),
	MOREAU_MID_DARK(89,107,90),
	MOREAU_DESATURATED_DARK(116,139,117),
	MOREAU_DESATURATED_LIGHT(130,176,123),
	MOREAU_ACCENT_1(251,224,45),

	ROCLAIRE_SATURATED_DARK(58,99,144),
	ROCLAIRE_MID_DARK(141,97,56),
	ROCLAIRE_DESATURATED_DARK(187,136,92),
	ROCLAIRE_MID_LIGHT(211,217,226),
	ROCLAIRE_DESATURATED_LIGHT(235,242,249),
	ROCLAIRE_ACCENT_1(254,252,66),
	ROCLAIRE_ACCENT_2(221,29,29),

	TAKANORI_SATURATED_DARK(122,57,40),
	TAKANORI_MID_DARK(255,107,53),
	TAKANORI_DESATURATED_DARK(252,122,87),
	TAKANORI_MID_LIGHT(255,186,73),

	TELEMACHUS_SATURATED_DARK(73,83,95),
	TELEMACHUS_MID_DARK(83,119,164),
	TELEMACHUS_DESATURATED_DARK(135,152,173),
	TELEMACHUS_DESATURATED_LIGHT(132,179,201),
	TELEMACHUS_ACCENT_1(249,225,76),
	TELEMACHUS_ACCENT_2(240,183,73),

	WANDA_SATURATED_DARK(136,79,132),
	WANDA_MID_DARK(121,95,135),
	WANDA_DESATURATED_DARK(137,123,144),
	WANDA_DESATURATED_LIGHT(199,187,205),
	WANDA_ACCENT_1(239,180,68),

	;

	private final Color color;
	private final Vector3 rgb = new Vector3();
	private final Vector3 hsv = new Vector3();

	HadalColor(int r, int g, int b) {
		this.rgb.set(r, g, b).scl(1 / 255.0f);
		color = new Color(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);

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
