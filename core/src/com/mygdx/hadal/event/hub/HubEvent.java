package com.mygdx.hadal.event.hub;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * The HubEvent is one of the events in the hub of the game that produces an extra ui for the player to manage
 * stuff between rounds.
 * @author Zachary Tu
 */
public class HubEvent extends Event {

	//is the even menu currently open
	protected boolean open;
	
	//the string that is displayed in the menu
	protected String title;
	
	//this is the type of hub
	protected hubTypes type;
	
	//should we check unlock status for options? should we close the menu when the player moves far away from the event?
	protected boolean checkUnlock, closeOnLeave;
	
	//options dsplayed here must have these tags.
	protected ArrayList<UnlockTag> tags;
	
	//the distance the player can move away before the menu disappears
	private static float maxDistance = 5.0f;
	
	public HubEvent(final PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave, hubTypes type) {
		super(state, startPos, size);
		this.open = false;
		this.title = title;
		this.checkUnlock = checkUnlock;
		this.closeOnLeave = closeOnLeave;
		this.type = type;
		this.tags = new ArrayList<UnlockTag>();
		for (String s: tag.split(",")) {
			tags.add(UnlockTag.valueOf(s));
		}
	}
	
	@Override
	public void create() {
		this.eventData =  new InteractableEventData(this) {
			
			@Override
			public void onInteract(Player p) {
				preActivate(null, p);
			}
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (state.getUiHub().isActive()) {
					leave();
				} else {
					enter();
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER),	(short) 0, true, eventData);
	}
	
	/**
	 * This keeps track of whether the player is in front of the event or not.
	 */
	@Override
	public void controller(float delta) {
		if (open && closeOnLeave) {
			if (getPosition().dst(state.getPlayer().getPosition()) > maxDistance) {
				leave();
				open = false;
			}
		}
	}
	
	@Override
	public void clientController(float delta) {
		if (open && closeOnLeave) {
			if (getPosition().dst(state.getPlayer().getPosition()) > maxDistance) {
				leave();
				open = false;
			}
		}
	}

	/**
	 * This is run when the player interacts with the event. Pull up an extra menu with options specified by the child.
	 */
	public void enter() {
		state.getUiHub().setType(type);
		state.getUiHub().setTitle(title);
		state.getUiHub().enter();
		open = true;
	}
	
	/**
	 * Player exits the event. Makes the ui slide out
	 */
	public void leave() {
		state.getUiHub().leave();
		open = false;
	}
	
	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.PYRAMID);
		setSyncType(eventSyncTypes.USER);
		setSynced(true);
	}
}
