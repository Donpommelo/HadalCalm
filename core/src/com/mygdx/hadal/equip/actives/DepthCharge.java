package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class DepthCharge extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 12.0f;
	
	private final static float recoil = 40.0f;
	
	private final static float duration = 1.0f;
	private static final float procCd = .25f;
	
	private final static Vector2 explosionSize = new Vector2(300, 300);
	private static final float explosionDamage = 40.0f;
	private static final float explosionKnockback = 20.0f;

	
	public DepthCharge(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		
		user.getPlayer().pushMomentumMitigation(0, recoil);
		
		user.addStatus(new Status(state, duration, false, user, user) {
			
			private float procCdCount = procCd;
			private Vector2 explosionPos = new Vector2(user.getSchmuck().getPixelPosition());
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					SoundEffect.EXPLOSION6.playUniversal(state, explosionPos, 0.8f, false);
					
					WeaponUtils.createExplosion(state, explosionPos, explosionSize.x, user.getPlayer(), explosionDamage, explosionKnockback, user.getPlayer().getHitboxfilter());
					explosionPos.sub(0, explosionSize.x / 2);
				}
				procCdCount += delta;
			}
		});
	}
}
