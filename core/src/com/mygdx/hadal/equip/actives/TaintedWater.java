package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class TaintedWater extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 13.0f;
	
	private static final float duration = 2.0f;
	private static final float procCd = .2f;
	
	private static final Vector2 poisonSize = new Vector2(101, 250);
	private static final float poisonDamage = 1.8f;
	private static final float poisonDuration = 2.5f;
	
	public TaintedWater(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		SoundEffect.MAGIC27_EVIL.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		final boolean right = weaponVelo.x > 0;

		user.addStatus(new Status(state, duration, false, user, user) {
			
			private float procCdCount = procCd;
			private final Vector2 poisonPos = new Vector2(user.getSchmuck().getPixelPosition()).add(poisonSize.x / 2 * (right ? 1 : -1), 0);
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					new Poison(state, poisonPos.add(poisonSize.x * (right ? 1 : -1), 0), poisonSize, poisonDamage, poisonDuration, user.getSchmuck(), true, user.getSchmuck().getHitboxfilter());
				}
				procCdCount += delta;
			}
		});
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
