package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

/**
 * @author Muburough Mapodilla
 */
public class DepthCharge extends ActiveItem {

	private static final float MAX_CHARGE = 10.0f;
	
	private static final float RECOIL = 40.0f;
	
	private static final float DURATION = 1.0f;
	private static final float PROC_CD = 0.25f;
	
	private static final Vector2 EXPLOSION_SIZE = new Vector2(300, 300);
	private static final float EXPLOSION_DAMAGE = 40.0f;
	private static final float EXPLOSION_KNOCKBACK = 20.0f;

	public DepthCharge(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		
		user.getPlayer().pushMomentumMitigation(0, RECOIL);
		
		user.addStatus(new Status(state, DURATION, false, user, user) {
			
			private float procCdCount = PROC_CD;
			private final Vector2 explosionPos = new Vector2(user.getSchmuck().getPixelPosition());
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					
					SoundEffect.EXPLOSION6.playUniversal(state, explosionPos, 0.8f, false);
					
					WeaponUtils.createExplosion(state, explosionPos, EXPLOSION_SIZE.x, user.getPlayer(), EXPLOSION_DAMAGE,
							EXPLOSION_KNOCKBACK, user.getPlayer().getHitboxFilter(), true, DamageSource.DEPTH_CHARGE);
					explosionPos.sub(0, EXPLOSION_SIZE.x / 2);
				}
				procCdCount += delta;
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (DURATION / PROC_CD)),
				String.valueOf((int) EXPLOSION_DAMAGE)};
	}
}
