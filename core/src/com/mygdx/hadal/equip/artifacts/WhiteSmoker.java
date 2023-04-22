package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.artifact.WhiteSmokerActivate;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class WhiteSmoker extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float HOVER_COST_REDUCTION = -0.15f;

	private static final float PROJECTILE_SPEED = 20.0f;

	private static final float FIRE_DAMAGE = WhiteSmokerActivate.FIRE_DAMAGE;
	private static final float FIRE_DURATION = WhiteSmokerActivate.FIRE_DURATION;

	public WhiteSmoker() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.HOVER_COST, HOVER_COST_REDUCTION, p),
				new Status(state, p) {

			@Override
			public void whileHover(Vector2 hoverDirection) {
				SyncedAttack.WHITE_SMOKER.initiateSyncedAttackSingle(state, p.getPlayer(), p.getSchmuck().getPixelPosition(),
						new Vector2(hoverDirection).nor().scl(-PROJECTILE_SPEED));
			}
		}).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) FIRE_DURATION),
				String.valueOf((int) FIRE_DAMAGE),
				String.valueOf((int) -(HOVER_COST_REDUCTION * 100))};
	}
}
