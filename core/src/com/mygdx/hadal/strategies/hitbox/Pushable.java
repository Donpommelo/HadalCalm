package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * Pushable hboxes can be affected by the knockback of other hitboxes that is normally applied alongside damage
 * @author Glapricot Gowick
 */
public class Pushable extends HitboxStrategy {

	//multiplier to knockback incurred to make hboxes like naval mines extra pushable
	private final float pushMultiplier;

	public Pushable(PlayState state, Hitbox proj, BodyData user, float pushMultiplier) {
		super(state, proj, user);
		this.pushMultiplier = pushMultiplier;
	}
	
	@Override
	public void receiveDamage(BodyData perp, float baseDamage, Vector2 knockback, DamageTypes... tags) {
		hbox.push(knockback.scl(pushMultiplier));
	}
}
