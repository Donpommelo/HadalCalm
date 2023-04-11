package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ObligeLaMort extends Artifact {

	private static final int slotCost = 1;

	private static final float pushSpeed = 3.2f;
	private static final float pushSpeedAir = 1.8f;
	private static final float pushSlow = 3.5f;
	private static final float pushSlowAir = 1.5f;
	private static final float maxSpeed = 20.0f;

	private static final float procCd = 1 / 60.0f;

	public ObligeLaMort() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				if (procCdCount >= procCd) {
					procCdCount -= procCd;

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
				if (Math.abs(p.getPlayer().getLinearVelocity().x) < maxSpeed) {
					if (speedUp) {
						float speed = grounded ? pushSpeed  : pushSpeedAir;
						push.set(direction ? speed : -speed, 0.0f);
					} else {
						float speed = grounded ? pushSlow  : pushSlowAir;
						push.set(direction ? -speed : speed, 0.0f);
					}
					p.getPlayer().applyLinearImpulse(push);
				}
			}
		};
	}
}
