package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ObligeLaMort extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float PUSH_SPEED = 3.2f;
	private static final float PUSH_SPEED_AIR = 1.8f;
	private static final float PUSH_SLOW = 3.5f;
	private static final float PUSH_SLOW_AIR = 1.5f;
	private static final float MAX_SPEED = 20.0f;

	private static final float PROC_CD = 1 / 60.0f;

	public ObligeLaMort() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;

					boolean flip = Math.abs(p.getPlayer().getMouseHelper().getAttackAngle()) > 90;

					if (MoveState.MOVE_RIGHT.equals(p.getPlayer().getMoveState())) {
						applyPush(flip, true, p.getPlayer().getGroundedHelper().isGrounded());
					} else if (MoveState.MOVE_LEFT.equals(p.getPlayer().getMoveState())) {
						applyPush(!flip, false, p.getPlayer().getGroundedHelper().isGrounded());
					}
				}
			}

			private final Vector2 push = new Vector2();
			private void applyPush(boolean speedUp, boolean direction, boolean grounded) {
				if (Math.abs(p.getPlayer().getLinearVelocity().x) < MAX_SPEED) {
					if (speedUp) {
						float speed = grounded ? PUSH_SPEED : PUSH_SPEED_AIR;
						push.set(direction ? speed : -speed, 0.0f);
					} else {
						float speed = grounded ? PUSH_SLOW : PUSH_SLOW_AIR;
						push.set(direction ? -speed : speed, 0.0f);
					}
					p.getPlayer().applyLinearImpulse(push);
				}
			}
		}.setUserOnly(true);
	}
}
