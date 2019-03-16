package com.mygdx.hadal.equip.melee;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallParticles;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class Scrapripper extends MeleeWeapon {

	private final static String name = "Scrap-Ripper";
	private final static float swingCd = 0.25f;
	private final static float windup = 0.2f;
	private final static float backSwing = 0.6f;
	private final static float baseDamage = 30.0f;
	private final static int hitboxSize = 180;
	private final static int swingArc = 120;
	private final static float knockback = 25.0f;
	private final static float momentum = 7.5f;
	
	private final static Sprite weaponSprite = Sprite.MT_SCRAPRIPPER;
	private final static Sprite eventSprite = Sprite.P_SCRAPRIPPER;

	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startAngle, float x, float y, short filter) {
						
			Hitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd, backSwing, startAngle, 
					startAngle.nor().scl(hitboxSize / 4 / PPM), true, filter, user);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.MELEE));
		}
	};
	
	public Scrapripper(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing, weaponSprite, eventSprite);
	}
}
