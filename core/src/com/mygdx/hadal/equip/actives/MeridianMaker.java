package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Currents;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * @author Hatonio Hadoof
 */
public class MeridianMaker extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 8.0f;
	
	private static final float baseDamage = 45.0f;
	private static final float knockback = 0.0f;
	private static final Vector2 projectileSize = new Vector2(40, 40);
	private static final float lifespan = 6.0f;
	
	private static final float projectileSpeed = 30.0f;
	
	private static final Sprite projSprite = Sprite.NOTHING;

	private static final int currentRadius = 100;
	private static final float currentForce = 1.0f;
	
	public MeridianMaker(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC11_WEIRD.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
		
		final Vector2 currentVec = new Vector2(weaponVelo).nor().scl(currentForce);
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getProjectileOrigin(weaponVelo, projectileSize.x), projectileSize, lifespan, 
				new Vector2(weaponVelo).nor().scl(projectileSpeed), user.getPlayer().getHitboxfilter(), false, false, user.getPlayer(), projSprite);
		
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandard(state, hbox, user, baseDamage, knockback, DamageSource.MERIDIAN_MAKER, DamageTag.MAGIC));
		hbox.addStrategy(new ContactWallDie(state, hbox, user));
		hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.BRIGHT, 0.0f, 1.0f).setParticleColor(HadalColor.SKY_BLUE).setParticleSize(20));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private final Vector2 lastPosition = new Vector2(hbox.getStartPos()).scl(PPM);
			
			@Override
			public void controller(float delta) {
				if (lastPosition.dst2(hbox.getPixelPosition()) > currentRadius * currentRadius) {
					new Currents(state, lastPosition.set(hbox.getPixelPosition()), new Vector2(currentRadius, currentRadius),
							currentVec, lifespan);
				}
			}
		}); 
	}
}
