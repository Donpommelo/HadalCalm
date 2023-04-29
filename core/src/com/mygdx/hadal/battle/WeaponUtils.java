package com.mygdx.hadal.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.ExplosionDefault;
import com.mygdx.hadal.strategies.hitbox.Static;

/**
 * This util contains several shortcuts for hitbox-spawning effects for weapons or other items.
 * Includes create explosion, missiles, homing missiles, grenades and bees.
 * @author Lotticelli Lamhock
 */
public class WeaponUtils {

	private static final float SELF_DAMAGE_REDUCTION = 0.5f;
	private static final float EXPLOSION_SPRITE_SCALE = 1.5f;
	private static final Sprite BOOM_SPRITE = Sprite.BOOM;
	public static void createExplosion(PlayState state, Vector2 startPos, float size, Schmuck user, float explosionDamage,
									   float explosionKnockback, short filter, boolean synced, DamageSource source) {
		
		float newSize = size * (1 + user.getBodyData().getStat(Stats.EXPLOSION_SIZE));

		//this prevents players from damaging allies with explosives in the hub
		short actualFilter = filter;

		if (user.getHitboxFilter() == Constants.PLAYER_HITBOX && state.getMode().isHub()) {
			actualFilter = Constants.PLAYER_HITBOX;
		}

		Hitbox hbox = new Hitbox(state, startPos, new Vector2(newSize, newSize), 0.4f, new Vector2(),
			actualFilter, true, false, user, BOOM_SPRITE);
		hbox.setSyncDefault(synced);

		hbox.setSpriteSize(new Vector2(newSize, newSize).scl(EXPLOSION_SPRITE_SCALE));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ExplosionDefault(state, hbox, user.getBodyData(), explosionDamage, explosionKnockback,
				SELF_DAMAGE_REDUCTION, source, DamageTag.EXPLOSIVE));

		if (!state.isServer()) {
			((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
		}
	}

	public static void createMeteors(PlayState state, Schmuck user, Vector2 startPosition, int meteorNumber,
									 float meteorInterval, float baseDamage, float spread) {
		float[] extraFields = new float[3 + meteorNumber];
		extraFields[0] = meteorInterval;
		extraFields[1] = baseDamage;
		extraFields[2] = meteorNumber;

		for (int i = 0; i < meteorNumber; i++) {
			extraFields[i + 3] = (MathUtils.random() -  0.5f) * spread;
		}

		SyncedAttack.METEOR_STRIKE.initiateSyncedAttackSingle(state, user, startPosition, new Vector2(), extraFields);
	}

	public static void createVine(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelo,
								  int vineNum, int splitNum, SyncedAttack syncedAttack) {

		float[] extraFields = new float[3 + vineNum];
		extraFields[0] = vineNum;
		extraFields[1] = splitNum;
		for (int i = 0; i < vineNum; i++) {
			extraFields[i + 2] = MathUtils.random();
		}

		syncedAttack.initiateSyncedAttackSingle(state, user, startPosition, startVelo, extraFields);
	}

	/**
	 * This method returns a player's "color" corresponding to their team color or their character with no team.
	 * This is used to color code player name as well as for streak particle coloring
	 */
	public static Vector3 getPlayerColor(Player player) {

		//return empty vector if player's data has not been created yet.
		if (null != player.getPlayerData()) {
			Loadout loadout = player.getPlayerData().getLoadout();
			if (AlignmentFilter.NONE.equals(loadout.team)) {
				return loadout.character.getPalette().getIcon().getRGB();
			} else if (loadout.team.getPalette().getIcon().getRGB().isZero()) {
				return loadout.character.getPalette().getIcon().getRGB();
			} else {
				return loadout.team.getPalette().getIcon().getRGB();
			}
		} else {
			return new Vector3();
		}
	}

	private static final Vector3 rgb = new Vector3();
	/**
	 * This returns a string corresponding to a player's colored name. (optionally abridged)
	 * Used for kill feed messages and chat window names.
	 */
	public static String getPlayerColorName(Schmuck schmuck, int maxNameLen) {

		if (null == schmuck) { return ""; }

		if (schmuck instanceof Player player) {
			String displayedName = player.getName();

			if (displayedName.length() > maxNameLen) {
				displayedName = displayedName.substring(0, maxNameLen).concat("...");
			}

			//get the player's color and use color markup to add color tags.
			rgb.set(getPlayerColor(player));
			String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
			return "[" + hex + "]" + displayedName + "[]";
		} else {
			return schmuck.getName();
		}
	}

	public static String getColorName(HadalColor color, String name) {
		String hex = "#" + Integer.toHexString(Color.rgb888(color.getColor()));
		return "[" + hex + "]" + name + "[]";
	}
}
