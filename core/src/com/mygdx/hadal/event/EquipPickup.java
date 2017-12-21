package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class EquipPickup extends Event {

	private Equipable equip;
	
	public EquipPickup(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, Equipable equip) {
		super(state, world, camera, rays, width, height, x, y);
		this.equip = equip;
		state.create(this);
	}
	
	public void create() {
		this.eventData = new InteractableEventData(world, this) {
			public void onInteract(Player p) {
				Equipable temp = p.playerData.currentTool;
				p.playerData.pickup(equip);
				equip = temp;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	public void render(SpriteBatch batch) {
		
	}
	
	public String getText() {
		return equip.name + " (PRESS E TO SWAP)";
	}

}
