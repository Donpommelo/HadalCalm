package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ObligeLaMort extends Artifact {

	private static final int slotCost = 1;

	private static final float pushSpeed = 3.5f;
	private static final float pushSpeedAir = 2.0f;
	private static final float pushSlow = 2.5f;
	private static final float pushSlowAir = 1.0f;
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

					boolean flip = Math.abs(p.getPlayer().getAttackAngle()) > 90;

					if (p.getPlayer().getMoveState().equals(MoveState.MOVE_RIGHT)) {
						applyPush(flip, true, p.getPlayer().isGrounded());
					} else if (p.getPlayer().getMoveState().equals(MoveState.MOVE_LEFT)) {
						applyPush(!flip, false, p.getPlayer().isGrounded());
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
