package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * The HubEvent is one of the events in the hub of the game that produces an extra ui for the player to manage
 * stuff between rounds.
 * @author Jironica Jirsula
 */
public class HubEvent extends Event {

	//the distance the player can move away before the menu disappears
	private static final float MAX_DISTANCE = 5.0f;

	//is the even menu currently open
	protected boolean open;
	
	//the string that is displayed in the menu
	protected final String title;
	
	//this is the type of hub
	protected final hubTypes type;
	
	//should we check unlock status for options? should we close the menu when the player moves far away from the event?
	protected final boolean checkUnlock, closeOnLeave;
	
	//options displayed here must have these tags.
	protected final Array<UnlockTag> tags;
	protected UnlockTag tag;
	protected UnlockTag lastTag = UnlockTag.ALL;
	protected String lastSearch = "";
	protected int lastSlot;
	
	//the last scroll percent of the
	private float lastScroll;

	public HubEvent(final PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave, hubTypes type) {
		super(state, startPos, size);
		this.open = false;
		this.title = UIText.getByName(title).text();
		this.checkUnlock = checkUnlock;
		this.closeOnLeave = closeOnLeave;
		this.type = type;
		this.tags = new Array<>();
		for (String s : tag.split(",")) {
			tags.add(UnlockTag.valueOf(s));
			this.tag = UnlockTag.valueOf(s);
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
					back();
				} else {
					enter();
					setScroll();
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, Constants.BIT_PLAYER,	(short) 0, true, eventData);
	}
	
	/**
	 * This keeps track of whether the player is in front of the event or not.
	 */
	@Override
	public void controller(float delta) {
		if (open && closeOnLeave) {
			if (getPosition().dst2(state.getPlayer().getPosition()) > MAX_DISTANCE * MAX_DISTANCE && !state.isSpectatorMode()) {
				leave();
				open = false;
			}
		}
	}
	
	@Override
	public void clientController(float delta) {
		if (open && closeOnLeave) {
			if (getPosition().dst2(state.getPlayer().getPosition()) > MAX_DISTANCE * MAX_DISTANCE) {
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
		state.getUiHub().enter(this);
		open = true;
	}

	public void back() {
		leave();
	}

	/**
	 * Player exits the event. Makes the ui slide out
	 */
	public void leave() {
		state.getUiHub().leave();
		open = false;
		lastScroll = state.getUiHub().getOptions().getScrollX();
	}

	private void setScroll() {
		state.getUiHub().getOptions().layout();
		state.getUiHub().getOptions().setScrollX(lastScroll);
	}

	public void addOptions(String search, int slots, UnlockTag tag) {
		lastTag = tag;
		lastSearch = search;
		lastSlot = slots;
	}

	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.PYRAMID);
		setSyncType(eventSyncTypes.USER);
		setSynced(true);
	}

	public String getLastSearch() { return lastSearch; }

	public int getLastSlot() { return lastSlot; }

	public UnlockTag getLastTag() { return lastTag; }

	public boolean isSearchable() { return false; }

	public boolean isTaggable() { return false; }

	public boolean isCostable() { return false; }

	public boolean isTabbable() { return false; }

	public String[] getSearchTags() { return new String[0]; }
}
