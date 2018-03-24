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
 * This event, when interacted with, will give the player a new artifact
 * 
 * Triggered Behavior: When triggered, this event is toggled on/off to unlock/lock pickup
 * Triggering Behavior: This event will trigger its connected event when picked up.
 * 
 * Fields:
 * pool: String, comma separated list of artifactunlock enum names of all equips that could appear here.
 * 	if this is equal to "", return any weapon in the random pool.
 * startOn: boolean of whether the event starts on or off. Optiona;. Default: True.
 * 
 * @author Zachary Tu
 *
 */
public class PickupArtifact extends Event {

	//This is the artifact that will be gained upon interacting
	private UnlockArtifact artifact;
	
	private static final String name = "Artifact Pickup";

	//Can this event be interacted with atm?
	private boolean on;
	
	public PickupArtifact(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, String pool, boolean startOn) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.on = startOn;
		
		//Set this pickup to a random equip in the input pool
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
	
	/**
	 * This method returns the name of a artifact randomly selected from the pool.
	 * @param pool: comma separated list of names of artifact to choose from. if set to "", return any artifact.
	 * @return
	 */
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
