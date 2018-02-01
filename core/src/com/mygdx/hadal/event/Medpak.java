package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Medpak extends Event{

	private static final int width = 16;
	private static final int height = 16;
	
	private static final int hpRegained = 25;

	private MedpakSpawner spawner;
	
	private static final String name = "Medpak";

	public Medpak(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y,
			MedpakSpawner medpakSpawner) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.spawner = medpakSpawner;
	}
	
	public void create() {

		this.eventData = new EventData(world, this) {
			public void onTouch(HadalData fixB) {
				if (fixB != null && !consumed) {
					if (fixB.getType().equals(UserDataTypes.BODY)) {
						if (((PlayerBodyData)fixB).currentHp < ((PlayerBodyData)fixB).getMaxHp()) {
							((PlayerBodyData)fixB).regainHp(hpRegained, ((PlayerBodyData)fixB), true, DamageTypes.MEDPAK);
							if (spawner != null) {
								spawner.readyToSpawn = true;
							}
							queueDeletion();
						}
					}
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				Constants.ENEMY_HITBOX, true, eventData);
	}
}
