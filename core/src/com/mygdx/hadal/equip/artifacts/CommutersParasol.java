package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.ControllerDefault;
import com.mygdx.hadal.schmucks.strategies.FixedToUser;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class CommutersParasol extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float lifespan = 3.0f;
		
	private final static Vector2 size = new Vector2(150, 20);
	private final static Vector2 position = new Vector2(0, 2.5f);
	private final static float procCd = 6.0f;
	
	private final static Sprite sprite = Sprite.ORB_BLUE;
	
	public CommutersParasol() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					Hitbox hbox = new Hitbox(state, inflicted.getSchmuck().getPixelPosition(), size, lifespan, new Vector2(0, 0), inflicted.getSchmuck().getHitboxfilter(), true, false, 
							inflicted.getSchmuck(), sprite);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, inflicted));
					hbox.addStrategy(new FixedToUser(state, hbox, inflicted, new Vector2(0, 0), position, false));
					hbox.addStrategy(new HitboxStrategy(state, hbox, inflicted) {
						
						@Override
						public void onHit(HadalData fixB) {
							if (fixB != null) {
								if (fixB.getType().equals(UserDataTypes.HITBOX)){
									if (((Hitbox)fixB.getEntity()).isAlive()) {
										Vector2 newVelo = new Vector2(fixB.getEntity().getPosition()).sub(inflicted.getSchmuck().getPosition());
										((Hitbox)fixB.getEntity()).setLinearVelocity(((Hitbox)fixB.getEntity()).getLinearVelocity().setAngle(newVelo.angle()));
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
