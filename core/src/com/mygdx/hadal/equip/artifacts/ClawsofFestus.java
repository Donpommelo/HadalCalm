package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.b2d.HadalFixture;

public class ClawsofFestus extends Artifact {

	private static final int SLOT_COST = 1;
	
	public ClawsofFestus() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean created, deleted;
			private Fixture leftSensor, rightSensor;
			private FeetData leftData, rightData;
			@Override
			public void onRemove() {
				created = false;
				deleted = true;
			}

			@Override
			public void timePassing(float delta) {
				if (inflicted.getSchmuck() instanceof Player player) {
					if (null != player.getBody()) {
						if (!created) {
							created = true;

							leftData = new FeetData(UserDataType.FEET, player);
							leftSensor = new HadalFixture(
									new Vector2(-player.getSize().x / 2, 0.5f),
									new Vector2(player.getSize().x / 8, player.getSize().y - 2),
									BodyConstants.BIT_SENSOR, BodyConstants.BIT_WALL, player.getHitboxFilter())
									.addToBody(player.getBody());
							leftSensor.setUserData(leftData);

							rightData = new FeetData(UserDataType.FEET, player);
							rightSensor = new HadalFixture(
									new Vector2(player.getSize().x / 2,  0.5f),
									new Vector2(player.getSize().x / 8, player.getSize().y - 2),
									BodyConstants.BIT_SENSOR, BodyConstants.BIT_WALL, player.getHitboxFilter())
									.addToBody(player.getBody());
							rightSensor.setUserData(rightData);
						}

						boolean touchingWall = 0 < leftData.getNumContacts() || 0 < rightData.getNumContacts();
						player.getGroundedHelper().setGroundedOverride(touchingWall);

						if (deleted) {
							if (null != leftSensor && null != rightSensor) {
								player.getBody().destroyFixture(leftSensor);
								player.getBody().destroyFixture(rightSensor);
							}
						}
					}
				}
			}
		};
	}
}
