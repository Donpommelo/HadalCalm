package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.userdata.InteractableEventData;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.b2d.HadalBody;

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
	
	//the last scroll percent of the menu (to auto scroll to when reopening)
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
				
				if (state.getUIManager().getUiHub().isActive()) {
					back();
				} else {
					enter();
					setScroll();
				}
			}
		};

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, BodyConstants.BIT_PLAYER, (short) 0)
				.setBodyType(BodyDef.BodyType.StaticBody)
				.addToWorld(world);
	}
	
	/**
	 * This keeps track of whether the player is in front of the event or not.
	 * Event closes if non-spectator player moves away
	 */
	@Override
	public void controller(float delta) {
		if (open && closeOnLeave) {
			if (HadalGame.usm.getOwnPlayer() != null) {
				if (getPosition().dst2(HadalGame.usm.getOwnPlayer().getPosition()) > MAX_DISTANCE * MAX_DISTANCE
						&& !state.getSpectatorManager().isSpectatorMode()) {
					leave();
					open = false;
				}
			}
		}
	}

	@Override
	public void clientController(float delta) {
		controller(delta);
	}

	/**
	 * This is run when the player interacts with the event. Pull up an extra menu with options specified by the child.
	 */
	public void enter() {
		state.getUIManager().getUiHub().setType(type);
		state.getUIManager().getUiHub().setTitle(title);
		state.getUIManager().getUiHub().enter(this);
		open = true;
	}

	public void back() {
		leave();
	}

	/**
	 * Player exits the event. Makes the ui slide out
	 */
	public void leave() {
		state.getUIManager().getUiHub().leave();
		open = false;
		lastScroll = state.getUIManager().getUiHub().getOptions().getScrollX();
	}

	/**
	 * Upon opening, set scroll to last scroll amount
	 */
	private void setScroll() {
		state.getUIManager().getUiHub().getOptions().layout();
		state.getUIManager().getUiHub().getOptions().setScrollX(lastScroll);
	}

	/**
	 * This is overridden by each hub event. This adds the options to the hub ui
	 */
	public void addOptions(String search, int slots, UnlockTag tag) {
		lastTag = tag;
		lastSearch = search;
		lastSlot = slots;
	}

	@Override
	public void loadDefaultProperties() {
		setEventSprite(Sprite.PYRAMID);
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
