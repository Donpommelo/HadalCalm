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

	private static final int slotCost = 1;
	
	private static final float bonusLight = 0.25f;
	private static final float radius = 10.0f;
	private static final float procCd = 0.5f;
	private static final float blindDuration = 0.5f;

	public LuminousEsca() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
			new StatChangeStatus(state, Stats.LIGHT_RADIUS, bonusLight, p),
			new Status(state, p) {

				private float procCdCount = procCd;
				private final Vector2 entityLocation = new Vector2();
				@Override
				public void timePassing(float delta) {
					if (procCdCount < procCd) {
						procCdCount += delta;
					}
					if (procCdCount >= procCd) {
						procCdCount -= procCd;
						entityLocation.set(p.getSchmuck().getPosition());
						state.getWorld().QueryAABB(fixture -> {
									if (fixture.getUserData() instanceof BodyData bodyData) {
										if (bodyData.getSchmuck().getHitboxfilter() != p.getSchmuck().getHitboxfilter()) {
											bodyData.addStatus(new Blinded(state, blindDuration, p, bodyData, false));
										}
									}
									return true;
								},
						entityLocation.x - radius, entityLocation.y - radius,
						entityLocation.x + radius, entityLocation.y + radius);
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
