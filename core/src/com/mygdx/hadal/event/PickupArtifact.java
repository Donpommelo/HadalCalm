package com.mygdx.hadal.event;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.event.utility.TriggerAlt;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
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

	private String pool;
	
	public PickupArtifact(PlayState state, Vector2 startPos, String pool) {
		super(state, name, startPos, new Vector2(Event.defaultPickupEventSize, Event.defaultPickupEventSize));
		this.pool = pool;
		
		//Set this pickup to a random equip in the input pool
		setArtifact(UnlockArtifact.valueOf(getRandArtfFromPool(pool)));
	}
	
	@Override
	public void create() {
		this.eventData = new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				preActivate(null, p);
			}
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (activator != null) {
					if (activator.getEvent() instanceof TriggerAlt) {
						String msg = ((TriggerAlt)activator.getEvent()).getMessage();
						if (msg.equals("roll")) {
							setArtifact(UnlockArtifact.valueOf(getRandArtfFromPool(pool)));
						} else {
							setArtifact(UnlockArtifact.valueOf(getRandArtfFromPool(msg)));
						}
					}
					return;
				}
				
				p.getPlayerData().addArtifact(artifact, true);
				setArtifact(UnlockArtifact.NOTHING);
			}
			
			@Override
			public void preActivate(EventData activator, Player p) {
				onActivate(activator, p);
				HadalGame.server.sendToAllTCP(new Packets.SyncPickup(entityID.toString(), artifact.toString(), null));
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if (!artifact.equals(UnlockArtifact.NOTHING)) {
			super.render(batch);
		}
		
		HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
		float y = getPixelPosition().y + size.y / 2;
		HadalGame.SYSTEM_FONT_SPRITE.draw(batch, artifact.getName(), getPixelPosition().x - size.x / 2, y);
	}
	
	@Override
	public Object onServerCreate() {
		return new Packets.CreatePickup(entityID.toString(), getPixelPosition(), PickupType.ARTIFACT, artifact.toString(), null);
	}
	
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPickup) {
			Packets.SyncPickup p = (Packets.SyncPickup) o;
			setArtifact(UnlockArtifact.valueOf(p.newPickup));
		} else {
			super.onClientSync(o);
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
	
	public void setArtifact(UnlockArtifact artifact) {
		this.artifact = artifact;
		
		if (artifact.equals(UnlockArtifact.NOTHING)) {
			if (standardParticle != null) {
				standardParticle.turnOff();
			}
		} else {
			if (standardParticle != null) {
				standardParticle.turnOn();
			}
		}
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.CUBE);
	}
}
