package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class LuminousEsca extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BONUS_LIGHT = 0.25f;
	private static final float RADIUS = 10.0f;
	private static final float PROC_CD = 0.5f;
	private static final float BLIND_DURATION = 0.5f;

	public LuminousEsca() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
			new StatChangeStatus(state, Stats.LIGHT_RADIUS, BONUS_LIGHT, p),
			new Status(state, p) {

				private float procCdCount = PROC_CD;
				private final Vector2 entityLocation = new Vector2();
				@Override
				public void timePassing(float delta) {
					if (procCdCount < PROC_CD) {
						procCdCount += delta;
					}
					if (procCdCount >= PROC_CD) {
						procCdCount -= PROC_CD;
						entityLocation.set(p.getSchmuck().getPosition());
						state.getWorld().QueryAABB(fixture -> {
									if (fixture.getUserData() instanceof BodyData bodyData) {
										if (bodyData.getSchmuck().getHitboxFilter() != p.getSchmuck().getHitboxFilter()) {
											bodyData.addStatus(new Blinded(state, BLIND_DURATION, p, bodyData, false));
										}
									}
									return true;
								},
						entityLocation.x - RADIUS, entityLocation.y - RADIUS,
						entityLocation.x + RADIUS, entityLocation.y + RADIUS);
					}
				}
			});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) Blinded.BLIND_FADE_THRESHOLD_1)};
	}
}
