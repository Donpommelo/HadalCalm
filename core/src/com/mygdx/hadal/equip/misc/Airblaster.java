package com.mygdx.hadal.equip.misc;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageConstant;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Airblaster extends MeleeWeapon {

	private static final float swingCd = 0.5f;
	private static final float windup = 0.0f;
	private static final float baseDamage = 0.0f;
	private static final Vector2 hitboxSize = new Vector2(175, 175);
	private static final Vector2 hitboxSpriteSize = new Vector2(260, 260);
	private static final float knockback = 60.0f;
	public static final float momentum = 40.0f;
	
	private static final float reflectVeloAmp = 1.5f;

	public Airblaster(Schmuck user) {
		super(user, swingCd, windup, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		fire(state, user, user.getPixelPosition(), weaponVelo, faction);
		user.recoil(mouseLocation, momentum * (1 + shooter.getStat(Stats.BOOST_RECOIL)));
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.AIRBLAST.playUniversal(state, startPosition, 0.5f, false);
		
		Hitbox hbox = new Hitbox(state, startPosition, new Vector2(hitboxSize).scl(1 + user.getBodyData().getStat(Stats.BOOST_SIZE)), swingCd, new Vector2(), user.getHitboxfilter(), true, false, user, Sprite.IMPACT);
		hbox.setSpriteSize(new Vector2(hitboxSpriteSize).scl(1 + user.getBodyData().getStat(Stats.BOOST_SIZE)));
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageConstant(state, hbox, user.getBodyData(), baseDamage, new Vector2(startVelocity).nor().scl(knockback * (1 + user.getBodyData().getStat(Stats.BOOST_POW))), DamageTypes.REFLECT));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), startVelocity.nor().scl(hitboxSize.x / 2 / PPM), false));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (fixB.getType().equals(UserDataTypes.HITBOX)){
						if (fixB.getEntity().isAlive()) {
							fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().scl(reflectVeloAmp).setAngle(startVelocity.angle()));
						}
					}
				}
			}
		});
		
		user.getBodyData().statusProcTime(new ProcTime.Airblast(this));
	}
}
