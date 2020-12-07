package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class CommutersParasol extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float lifespan = 3.0f;
		
	private static final Vector2 size = new Vector2(150, 20);
	private static final Vector2 position = new Vector2(0, 2.5f);
	private static final float procCd = 6.0f;
	
	private static final Sprite sprite = Sprite.ORB_BLUE;
	
	public CommutersParasol() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			
			@Override
			public void timePassing(float delta) {
				while (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					Hitbox hbox = new Hitbox(state, inflicted.getSchmuck().getPixelPosition(), size, lifespan, new Vector2(0, 0), inflicted.getSchmuck().getHitboxfilter(), true, false, inflicted.getSchmuck(), sprite);
					hbox.makeUnreflectable();
					
					hbox.addStrategy(new ControllerDefault(state, hbox, inflicted));
					hbox.addStrategy(new FixedToEntity(state, hbox, inflicted, new Vector2(0, 0), position, false));
					hbox.addStrategy(new HitboxStrategy(state, hbox, inflicted) {
						
						@Override
						public void onHit(HadalData fixB) {
							if (fixB != null) {
								if (fixB.getType().equals(UserDataTypes.HITBOX)) {
									if (fixB.getEntity().isAlive()) {
										Vector2 newVelo = new Vector2(fixB.getEntity().getPosition()).sub(inflicted.getSchmuck().getPosition());
										fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().setAngleDeg(newVelo.angleDeg()));
										
										SoundEffect.SPRING.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.2f, false);
									}
								}
							}
						}
					});
				}
				procCdCount += delta;
			}
		};
		
		return enchantment;
	}
}
