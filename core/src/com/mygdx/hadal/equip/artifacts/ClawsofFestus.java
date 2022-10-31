package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

public class ClawsofFestus extends Artifact {

	private static final int slotCost = 1;
	
	public ClawsofFestus() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean created;
			private Fixture leftSensor, rightSensor;
			private FeetData leftData, rightData;
			@Override
			public void onRemove() {
				created = false;
				if (null != inflicted.getSchmuck().getBody()) {
					inflicted.getSchmuck().getBody().destroyFixture(leftSensor);
					inflicted.getSchmuck().getBody().destroyFixture(rightSensor);
				}
			}

			@Override
			public void timePassing(float delta) {
				if (inflicted.getSchmuck() instanceof Player player) {
					if (null != player.getBody()) {
						if (!created) {
							created = true;

							leftData = new FeetData(UserDataType.FEET, player);

							leftSensor = FixtureBuilder.createFixtureDef(player.getBody(),
									new Vector2(-player.getSize().x / 2, 0.5f),
									new Vector2(player.getSize().x / 8, player.getSize().y - 2),
									true, 0, 0, 0, 0,
									Constants.BIT_SENSOR, Constants.BIT_WALL, player.getHitboxfilter());

							leftSensor.setUserData(leftData);

							rightData = new FeetData(UserDataType.FEET, player);

							rightSensor = FixtureBuilder.createFixtureDef(player.getBody(),
									new Vector2(player.getSize().x / 2,  0.5f),
									new Vector2(player.getSize().x / 8, player.getSize().y - 2),
									true, 0, 0, 0, 0,
									Constants.BIT_SENSOR, Constants.BIT_WALL, player.getHitboxfilter());

							rightSensor.setUserData(rightData);
						}

						boolean touchingWall = 0 < leftData.getNumContacts() || 0 < rightData.getNumContacts();
						player.setGroundedOverride(touchingWall);
					}
				}
			}
		};
	}
}
