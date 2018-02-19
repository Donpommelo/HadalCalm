package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.Unlock;
import com.mygdx.hadal.equip.Unlock.UnlockType;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Armory extends Event {

	private static final String name = "Armory";

	private boolean activated = false;
	
	public Armory(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y) {
		super(state, world, camera, rays, name, width, height, x, y);
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(world, this) {
			
			@Override
			public void onInteract(Player p) {
				
				Array<Class<? extends Equipable>> items = Unlock.getUnlocks(UnlockType.RANGED);
				
//				state.stage.addActor(actor);
			}
			
			@Override
			public void onActivate(EventData activator) {
				if (!activated) {
					activated = true;
					
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
	
	@Override
	public String getText() {
		return name + " (E TO USE)";
	}
}
