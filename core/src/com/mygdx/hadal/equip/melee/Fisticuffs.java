package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;

public class Fisticuffs extends MeleeWeapon {

	private final static float swingCd = 0.08f;
	private final static float windup = 0.0f;
	private final static float baseDamage = 25.0f;

	private final static Vector2 projectileSize = new Vector2(75, 75);
	private final static float lifespan = 0.1f;
	private final static float knockback = 25.0f;
	private final static Sprite projSprite = Sprite.IMPACT;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private final static float range = 50.0f;
	
	public Fisticuffs(Schmuck user) {
		super(user, swingCd, windup, weaponSprite, eventSprite);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.WOOSH.playUniversal(state, startPosition, 1.0f, false);

		Vector2 projOffset = new Vector2(((Player)user).getMouse().getPixelPosition()).sub(user.getPixelPosition()).nor().scl(range).add(user.getPixelPosition());
		
		Hitbox hbox = new Hitbox(state, projOffset, projectileSize, lifespan, new Vector2(0, 0), filter, true, true, user, projSprite);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStatic(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.MELEE));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f));
	}
}
