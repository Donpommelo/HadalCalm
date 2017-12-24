package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class EquipPickup extends Event {

	private Equipable equip;
	
	public static final int numWeapons = 8;
	
	private static final String name = "Equip Pickup";

	public EquipPickup(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, int equipId) {
		super(state, world, camera, rays, name, width, height, x, y);
		switch(equipId) {
		case 0:
			this.equip = new Speargun(null);
			break;
		case 1:
			this.equip = new Scattergun(null);
			break;
		case 2:
			this.equip = new MachineGun(null);
			break;
		case 3:
			this.equip = new IronBallLauncher(null);
			break;
		case 4:
			this.equip = new ChargeBeam(null);
			break;
		case 5:
			this.equip = new Boomerang(null);
			break;
		case 6:
			this.equip = new GrenadeLauncher(null);
			break;
		case 7:
			this.equip = new TorpedoLauncher(null);
			break;
		default:
			this.equip = new Speargun(null);
			break;
		}
		state.create(this);
	}
	
	public void create() {
		this.eventData = new InteractableEventData(world, this) {
			public void onInteract(Player p) {
				Equipable temp = p.playerData.pickup(equip);
				if (temp == null) {
					queueDeletion();
				} else {
					equip = temp;
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(state.hud.combined);
		Vector3 bodyScreenPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
		camera.project(bodyScreenPosition);
		state.font.draw(batch, equip.name, bodyScreenPosition.x - width, bodyScreenPosition.y - height);
	}
	
	public String getText() {
		return equip.name + " (PRESS E TO TAKE)";
	}

}
