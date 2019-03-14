package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event, when interacted with, will give the player a new artifact
 * 
 * Triggered Behavior: When triggered, this event is toggled on/off to unlock/lock pickup
 * Triggering Behavior: This event will trigger its connected event when picked up.
 * 
 * Fields:
 * pool: String, comma separated list of artifactunlock enum names of all equips that could appear here.
 * 	if this is equal to "", return any weapon in the random pool.
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
	
	//Is the player standing in this event? Will display extra info
	protected boolean open;

	public PickupArtifact(PlayState state, int width, int height, int x, int y, String pool) {
		super(state, name, width, height, x, y);
		this.on = true;
		
		//Set this pickup to a random equip in the input pool
		artifact = UnlockArtifact.valueOf(getRandArtfFromPool(pool));
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				if (isAlive() && on) {
					
					p.getPlayerData().addArtifact(artifact);
					
					queueDeletion();
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().preActivate(this, p);
					}
				}
			}
			
			@Override
			public void onActivate(EventData activator, Player p) {
				on = !on;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (open && eventData.getSchmucks().isEmpty()) {
			open = false;
		}
		if (!open && !eventData.getSchmucks().isEmpty()) {
			open = true;
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		if (open) {
			batch.setProjectionMatrix(state.sprite.combined);
			HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
			float y = body.getPosition().y * PPM + height / 2;
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, artifact.getName(), body.getPosition().x * PPM - width / 2, y);
		}
	}
	
	/**
	 * This method returns the name of a artifact randomly selected from the pool.
	 * @param pool: comma separated list of names of artifact to choose from. if set to "", return any artifact.
	 * @return
	 */
	public static String getRandArtfFromPool(String pool) {
		
		if (pool.equals("")) {
			return UnlockArtifact.getUnlocks(false, UnlockTag.RANDOM_POOL)
					.get(GameStateManager.generator.nextInt(UnlockArtifact.getUnlocks(false, UnlockTag.RANDOM_POOL).size)).name();
		}
		
		ArrayList<String> artifacts = new ArrayList<String>();
		
		for (String id : pool.split(",")) {
			artifacts.add(id);
		}
		return artifacts.get(GameStateManager.generator.nextInt(artifacts.size()));
	}
}
