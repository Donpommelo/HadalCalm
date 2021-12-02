package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.*;

public class Nurdler extends Artifact {

	private static final int slotCost = 1;
	
	private static final float procCd = 0.2f;
	
	private static final Vector2 projectileSize = new Vector2(18, 18);
	private static final float lifespan = 0.5f;
	private static final Sprite projSprite = Sprite.ORB_BLUE;
	
	private static final float baseDamage = 15.0f;
	private static final float knockback = 2.5f;
	private static final int spread = 30;
	
	private static final float projSpeed = 30.0f;
	
	public Nurdler() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			private final Vector2 startVelo = new Vector2();
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public void whileAttacking(float delta, Equippable tool) {
				if (tool.isReloading()) { return; }

				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					Hitbox hbox = new RangedHitbox(state, p.getSchmuck().getPixelPosition(), projectileSize, lifespan,
							startVelo.set(tool.getWeaponVelo()).nor().scl(projSpeed), p.getSchmuck().getHitboxfilter(),
							true, true, p.getSchmuck(), projSprite);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, p));
					hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, p));
					hbox.addStrategy(new ContactWallDie(state, hbox, p));
					hbox.addStrategy(new DamageStandard(state, hbox, p, baseDamage, knockback, DamageTypes.RANGED));
					hbox.addStrategy(new Spread(state, hbox, p, spread));
				}
			}
		};
	}
}
