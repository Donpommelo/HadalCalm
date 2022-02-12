package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class CommutersParasol extends Artifact {

	private static final int slotCost = 2;
	
	private static final float lifespan = 3.0f;
	private static final Vector2 size = new Vector2(150, 20);
	private static final Vector2 position = new Vector2(0, 2.5f);
	private static final float procCd = 6.0f;
	
	private static final Sprite sprite = Sprite.ORB_BLUE;
	
	public CommutersParasol() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				while (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					Hitbox hbox = new Hitbox(state, p.getSchmuck().getPixelPosition(), size, lifespan, new Vector2(0, 0),
							p.getSchmuck().getHitboxfilter(), true, false, p.getSchmuck(), sprite);
					hbox.makeUnreflectable();
					
					hbox.addStrategy(new ControllerDefault(state, hbox, p));
					hbox.addStrategy(new FixedToEntity(state, hbox, p, new Vector2(), position));
					hbox.addStrategy(new HitboxStrategy(state, hbox, p) {
						
						@Override
						public void onHit(HadalData fixB) {
							if (fixB != null) {
								if (fixB.getType().equals(UserDataType.HITBOX)) {
									if (fixB.getEntity().isAlive()) {
										Vector2 newVelo = new Vector2(fixB.getEntity().getPosition()).sub(p.getSchmuck().getPosition());
										fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().setAngleDeg(newVelo.angleDeg()));
										
										SoundEffect.SPRING.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.2f, false);
									}
								}
							}
						}
					});
				}
				procCdCount += delta;
			}
		};
	}
}
