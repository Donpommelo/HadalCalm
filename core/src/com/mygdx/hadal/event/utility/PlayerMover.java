package com.mygdx.hadal.event.utility;

import java.util.ArrayList;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
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
 * exclude: boolean. de we exclude the player that activated this? 
 * 
 
 * @author Zachary Tu
 *
 */
public class PlayerMover extends Event {

	private boolean all, exclude;
	private boolean moving = false;

	private ArrayList<Player> players;
	
	public PlayerMover(PlayState state, boolean all, boolean exclude) {
		super(state);
		this.all = all;
		this.exclude = exclude;
		
		this.players = new ArrayList<Player>();
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					moving = true;
					
					players.clear();
					
					if (state.isServer()) {
						if (all) {
							
							//go through all players
							for (int f: HadalGame.server.getScores().keySet()) {

								Player playerLeft;
								if (f == 0) {
									playerLeft = state.getPlayer();
								} else {
									playerLeft = HadalGame.server.getPlayers().get(f);
								}
								
								//warp player if they are a different player (or we have exclude turned off)
								if (!exclude || !p.equals(playerLeft)) {
									if (playerLeft != null) {
										players.add(playerLeft);
									}
								}
							}
						} else {
							players.add(p);
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
				
				for (Player p: players) {
					p.setTransform(getConnectedEvent().getPosition(), 0);
					
					//if possible, activate connected event's connected event.
					if (getConnectedEvent().getConnectedEvent() != null) {
						getConnectedEvent().getConnectedEvent().getEventData().preActivate(getEventData(), p);
					}
				}
			}
		}
	}
}
