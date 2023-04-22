package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;

/**
 * A PlayerMover is an event that transports the player elsewhere when they it is activated.
 * The event they are transported to does not have to be a portal.
 * 
 * Triggered Behavior: This triggers moving the player
 * Triggering Behavior: This event's connected event serves as the point that schmucks will be teleported to
 * 
 * Fields:
 * all: boolean. do we move all players or not?
 * exclude: boolean. do we exclude the player that activated this? 
 * respawn: boolean. do we update the player's default respawn point (for revives)
 *
 * @author Slalfingo Spehaz
 */
public class PlayerMover extends Event {

	private final boolean all, exclude, updateRespawn;
	
	//are we in the middle of moving the player?
	private boolean moving = false;

	private final Array<Player> players;
	
	public PlayerMover(PlayState state, boolean all, boolean exclude, boolean updateRespawn) {
		super(state);
		this.all = all;
		this.exclude = exclude;
		this.updateRespawn = updateRespawn;
		this.players = new Array<>();
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					moving = true;
					
					players.clear();
					
						if (all) {
							if (state.isServer()) {
								//go through all players
								for (User user : HadalGame.server.getUsers().values()) {
									Player playerLeft = user.getPlayer();
									if (playerLeft != null) {
										//warp player if they are a different player (or we have exclude turned off)
										if (!exclude || !p.equals(playerLeft)) {
											players.add(playerLeft);
										}
										if (updateRespawn) {
											playerLeft.setStart(this.getEvent().getConnectedEvent());
										}
									}
								}
							}
						} else {
							players.add(p);
							if (updateRespawn) {
								p.setStart(this.getEvent().getConnectedEvent());
							}
						}
					if (getConnectedEvent().getStandardParticle() != null && !players.isEmpty()) {
						getConnectedEvent().getStandardParticle().onForBurst(1.0f);
					}
				}
			}
		};
	}
	
	@Override
	public void controller(float delta) {
		if (moving) {
			if (getConnectedEvent().getBody() != null) {
				moving = false;
				
				for (Player p : players) {
					p.setTransform(getConnectedEvent().getPosition(), 0);
					
					//if possible, activate connected event's connected event.
					if (getConnectedEvent().getConnectedEvent() != null) {
						getConnectedEvent().getConnectedEvent().getEventData().preActivate(getEventData(), p);
					}
				}
			}
		}
	}

	@Override
	public void clientController(float delta) {
		controller(delta);
	}
}
