package com.mygdx.hadal.event;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * This event, when interacted with, will give the player a new weapon.
 * If the player's slots are full, this will replace currently held weapon.
 * @author Zachary Tu
 *
 */
public class PickupArtifact extends Event {

	private UnlockArtifact artifact;
	
	private static final String name = "Artifact Pickup";

	private boolean on;
	
	public PickupArtifact(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, String pool, boolean startOn) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.on = startOn;
		artifact = UnlockArtifact.valueOf(getRandArtfFromPool(pool));
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(world, this) {
			
			@Override
			public void onInteract(Player p) {
				if (isAlive() && on) {
					
					p.getPlayerData().addArtifact(artifact);
					
					queueDeletion();
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().onActivate(this);
					}
				}
			}
			
			@Override
			public void onActivate(EventData activator) {
				on = !on;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	public static String getRandArtfFromPool(String pool) {
		
		if (pool.equals("")) {
			return UnlockArtifact.values()[new Random().nextInt(UnlockArtifact.values().length)].name();
		}
		
		ArrayList<String> artifacts = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			artifacts.add(id);
		}
		return artifacts.get(new Random().nextInt(artifacts.size()));
	}

	@Override
	public String getText() {
		if (on) {
			return artifact.getName() + " (E TO TAKE)";
		} else {
			return artifact.getName() + ": LOCKED";
		}
	}

}
