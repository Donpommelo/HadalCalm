package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.Static;

/**
 * @author Larlando Locwump
 */
public class SamsonOption extends ActiveItem {

	private static final float MAX_CHARGE = 5.0f;
	
	private static final Vector2 PROJECTILE_SIZE = new Vector2(400, 400);
	private static final float DURATION = 1.5f;
	private static final float PROC_CD = 0.12f;
	private static final int EXPLOSION_RADIUS = 200;
	private static final float EXPLOSION_DAMAGE = 50.0f;
	private static final float EXPLOSION_KNOCKBACK = 30.0f;
	
	public SamsonOption(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), PROJECTILE_SIZE, DURATION, new Vector2(), (short) 0, false, false, user.getPlayer(), Sprite.NOTHING);
		hbox.makeUnreflectable();
		hbox.setSyncDefault(false);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new Static(state, hbox, user));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private float procCdCount = PROC_CD;
			private final Vector2 explosionPosition = new Vector2();
			@Override
			public void controller(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					float randX = (MathUtils.random() * hbox.getSize().x) - (hbox.getSize().x / 2) + hbox.getPixelPosition().x;
					float randY = (MathUtils.random() * hbox.getSize().y) - (hbox.getSize().y / 2) + hbox.getPixelPosition().y;
					
					SoundEffect.EXPLOSION6.playUniversal(state, explosionPosition.set(randX, randY), 0.5f, false);
					WeaponUtils.createExplosion(state, explosionPosition, EXPLOSION_RADIUS, user.getSchmuck(), EXPLOSION_DAMAGE, EXPLOSION_KNOCKBACK,
						user.getSchmuck().getHitboxfilter(), true, DamageSource.SAMSON_OPTION);
				}
			}
		});
		
		user.receiveDamage(9999, new Vector2(), user, false, null, DamageSource.SAMSON_OPTION);
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(DURATION),
				String.valueOf(PROC_CD),
				String.valueOf((int) EXPLOSION_DAMAGE)};
	}
}
