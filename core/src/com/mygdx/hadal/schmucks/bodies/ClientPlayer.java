package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.client.ClientPredictionFrame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;

/**
 * A ClientPlayer represents a client's own player.
 * This processes things like client prediction
 * @author Hepepper Hasufferson
 *
 */
public class ClientPlayer extends Player {

	//this represents how precisely we lerp towards the server position
	private static final float CONVERGE_MULTIPLIER = 0.02f;
	
	//these are the amounts of latency in seconds under which the prediction strategy will kick in.
	private static final float LATENCY_THRESHOLD_MIN = 0.1f;
	private static final float LATENCY_THRESHOLD_MAX = 0.4f;
	
	//tolerance variables. if the prediction is incorrect by more than these thresholds, we must adjust our predictions
//	private static final float VELO_TOLERANCE = 200.0f;
	private static final float DIST_TOLERANCE = 12.0f;
	
	public ClientPlayer(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData, int connID, boolean reset, StartPoint start) {
		super(state, startPos, name, startLoadout, oldData, connID, reset, start);
	}

	@Override
	public void create() {
		super.create();
		
		predictedPosition.set(getPosition());
		lastPosition.set(getPosition());
	}
	
	//this is an ordered list of client frames that keep track of the client's velocity and displacement over time
	private final ArrayList<ClientPredictionFrame> frames = new ArrayList<>();
	
	//the client's most recent position
	private final Vector2 lastPosition = new Vector2();
	
	//where we predict the client would be on the server
	private final Vector2 predictedPosition = new Vector2();
	
	//where we extrapolate the client will be by the time a packet we send reaches the server accounting for latency
	private final Vector2 extrapolatedPosition = new Vector2();
	private final Vector2 extrapolationVelocity = new Vector2();

	//this is the amount of positional history that we keep track of
	private float historyDuration;
	
	//are we currently predicting client location or just doing the normal interpolation (true if latency is high enough)
	private boolean predicting;
	
	@Override
	public void onReceiveSync(Object o, float timestamp) {
		super.onReceiveSync(o, timestamp);
		
		if (o instanceof Packets.SyncEntity) {
			Packets.SyncEntity p = (Packets.SyncEntity) o;

			float latency = ((ClientState) state).getLatency();
			float dt = Math.max(0.0f, historyDuration - latency);

			historyDuration -= dt;

			//we remove each frame in our history that is older than our latency
			while (!frames.isEmpty() && dt > 0) {
				ClientPredictionFrame frame = frames.get(0);
				if (dt >= frame.delta) {
					dt -= frame.delta;
					frames.remove(0);
				} else {

					//the last frame is trimmed so the total amount of time in our history is equal to our latency
					float t = 1 - dt / frame.delta;
					frame.delta -= dt;
					frame.positionChange.scl(t);
					break;
				}
			}

			if (!frames.isEmpty()) {
				
				//if our velocity is outside the range of tolerance, edit each frame with the new velocity
//				if (p.velocity.dst2(frames.get(0).velocity) > VELO_TOLERANCE) {
//
//					for (ClientPredictionFrame frame: frames) {
//						frame.velocity = p.velocity;
//						frame.positionChange = p.velocity.scl(frame.delta);
//					}
//				}
				
				//we predict our position is equal to what the server sent us, plus our total displacement in the time it took for that position to reach us
				predictedPosition.set(p.pos);
				
				for (ClientPredictionFrame frame: frames) {
					predictedPosition.add(frame.positionChange);
				}
				
				//if our position is too far away from what the server sends us, just rubberband.
				if (body != null) {
					if (predictedPosition.dst2(getPosition()) > DIST_TOLERANCE && predicting) {

						setTransform(predictedPosition, 0.0f);
						lastPosition.set(predictedPosition);
					}
				}
			}
		}
	}
	
	//most of the code here is just lifted from the Player class to simulate movement actions like jumping, hovering, fastfalling and boosting
	private final Vector2 playerLocation = new Vector2();
	private final Vector2 playerWorldLocation = new Vector2();
	private final Vector2 newPosition = new Vector2();
	private final Vector2 fug = new Vector2();
	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		controllerCount += delta;
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;
			
			if (hoveringAttempt && playerData.getExtraJumpsUsed() >= playerData.getExtraJumps() &&
				((ClientState) state).getUiPlay().getOverrideFuelAmount() >= playerData.getHoverCost()) {
				if (jumpCdCount < 0) {
					hover();
				}
			}
			
			if (fastFalling && predicting) {
				fastFall();
			}
		}
		
		grounded = feetData.getNumContacts() > 0;
		
		if (grounded) {
			playerData.setExtraJumpsUsed(0);
		}
		
		jumpCdCount -= delta;
		fastFallCdCount -= delta;
		airblastCdCount -= delta;

		if (jumpBuffered && jumpCdCount < 0) {
			jumpBuffered = false;
			jump();
		}
		
		if (airblastBuffered && airblastCdCount < 0) {
			airblastBuffered = false;
			airblast();
		}
		
		//for the server's own player, the sprite's arm should exactly match their mouse
		playerLocation.set(getPixelPosition());
		playerWorldLocation.set(getPosition());
		mouseAngle.set(playerLocation.x, playerLocation.y).sub(((ClientState) state).getMousePosition().x, ((ClientState) state).getMousePosition().y);
		attackAngle = (float)(Math.atan2(mouseAngle.y, mouseAngle.x) * 180 / Math.PI);
		
		if (body != null && alive) {
			
			//we add a new prediction frame to our list with our current displacement/velocity
			ClientPredictionFrame frame = new ClientPredictionFrame(delta);
			frame.positionChange.set(playerWorldLocation).sub(lastPosition);
			frame.velocity.set(getLinearVelocity());
			frames.add(frame);
			historyDuration += delta;
			
			//we do our latency check here. if our latency is too high/low, we switch to/away our predicting mode
			float latency = ((ClientState) state).getLatency();

			if (predicting && latency < LATENCY_THRESHOLD_MIN) {
				predicting = false;
			} else if (!predicting && latency > LATENCY_THRESHOLD_MAX) {
				predicting = true;
			}
			
			//when predicting, we extrapolate our position based on our prediction plus our current velocity given the current latency.
			if (predicting) {
				
				extrapolatedPosition.set(predictedPosition).add(extrapolationVelocity.set(getLinearVelocity()).scl((CONVERGE_MULTIPLIER) * latency));
				fug.set(extrapolatedPosition);
				
				float t;
				t = delta / (latency * (1 + CONVERGE_MULTIPLIER));

				newPosition.set(getPosition()).add(extrapolatedPosition.sub(playerWorldLocation).scl(t));
				setTransform(newPosition, 0.0f);
			}
			lastPosition.set(getPosition());
		}
	}
	
	@Override
	public void clientInterpolation() {
		
		//on low-ping mode, we just interpolate our body just like any other entity
		if (!predicting) {
			super.clientInterpolation();
		}
	}
	
	@Override
	public void hover() {
		if (!predicting) { return; }

		if (jumpCdCount < 0) {
			
			//Player will continuously do small upwards bursts that cost fuel.
			jumpCdCount = hoverCd;
			pushMomentumMitigation(0, playerData.getHoverPower());
		}
	}
	
	@Override
	public void jump() {
		if (!predicting) { return; }

		if (grounded) {
			if (jumpCdCount < 0) {
				
				jumpCdCount = jumpCd;
				pushMomentumMitigation(0, playerData.getJumpPower());
			} else {
				jumpBuffered = true;
			}
		} else if (playerData.getExtraJumpsUsed() < playerData.getExtraJumps()) {
			if (jumpCdCount < 0) {
				jumpCdCount = jumpCd;
				playerData.setExtraJumpsUsed(playerData.getExtraJumpsUsed() + 1);
				pushMomentumMitigation(0, playerData.getJumpPower());
			} else {
				jumpBuffered = true;
			}
		}
	}
	
	private final Vector2 mousePos = new Vector2();
	@Override
	public void airblast() {
		if (!predicting) { return; }

		if (airblastCdCount < 0) {
			if (((ClientState) state).getUiPlay().getOverrideFuelAmount() > ((ClientState) state).getUiPlay().getOverrideAirblastCost()) {
				mousePos.set(((ClientState) state).getMousePosition().x,((ClientState) state).getMousePosition().y);
				recoil(mousePos, Airblaster.momentum);
			}
		} else {
			airblastBuffered = true;
		}
	}

	@Override
	protected void applyForce(float delta) {
		if (predicting) {
			super.applyForce(delta);
		}
	}

	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPlayerAll) {
			Packets.SyncPlayerAll p = (Packets.SyncPlayerAll) o;

			getPlayerData().setCurrentSlot(p.currentSlot);
			getPlayerData().setCurrentTool(getPlayerData().getMultitools()[p.currentSlot]);
			setToolSprite(playerData.getCurrentTool().getWeaponSprite().getFrame());
			getPlayerData().getCurrentTool().setReloading(p.reloading, true);
			reloadPercent = p.reloadPercent;
			getPlayerData().getCurrentTool().setCharging(p.charging);
			chargePercent = p.chargePercent;
			getPlayerData().setOverrideOutOfAmmo(p.outOfAmmo);
			invisible = p.invisible;
			
			//notably, we omit the syncing of our passability, as that causes weird interactions with dropthrough platforms
		} else {
			super.onClientSync(o);
		}
	}
}
