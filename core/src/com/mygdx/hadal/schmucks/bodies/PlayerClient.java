package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.client.ClientPredictionFrame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.WorldUtil;

/**
 * A ClientPlayer represents a client's own player.
 * This processes things like client prediction
 * @author Hepepper Hasufferson
 */
public class PlayerClient extends Player {

	//this represents how precisely we lerp towards the server position
	private static final float CONVERGE_MULTIPLIER = 0.02f;
	
	//these are the amounts of latency in seconds under which the prediction strategy will kick in.
	private static final float LATENCY_THRESHOLD_MIN = 0.005f;
	private static final float LATENCY_THRESHOLD_MAX = 0.01f;

	//tolerance variables. if the prediction is incorrect by more than these thresholds, we must adjust our predictions
	private static final float DIST_TOLERANCE = 12.0f;

	public PlayerClient(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData,
						int connID, User user, boolean reset, StartPoint start) {
		super(state, startPos, name, startLoadout, oldData, connID, user, reset, start);
	}

	@Override
	public void create() {
		super.create();
		
		predictedPosition.set(getPosition());
		lastPosition.set(getPosition());
	}
	
	//this is an ordered list of client frames that keep track of the client's velocity and displacement over time
	private final Array<ClientPredictionFrame> frames = new Array<>();
	
	//the client's most recent position
	private final Vector2 lastPosition = new Vector2();
	
	//where we predict the client would be on the server
	private final Vector2 predictedPosition = new Vector2();
	private final Vector2 rubberbandPosition = new Vector2();

	//where we extrapolate the client will be by the time a packet we send reaches the server accounting for latency
	private final Vector2 extrapolatedPosition = new Vector2();
	private final Vector2 extrapolationVelocity = new Vector2();

	//this is the amount of positional history that we keep track of
	private float historyDuration;
	
	//are we currently predicting client location or just doing the normal interpolation (true if latency is high enough)
	private boolean predicting;

	//the game time when we last received a timestamp from the server
	private float lastTimestamp;

	@Override
	public void onReceiveSync(Object o, float timestamp) {
		super.onReceiveSync(o, timestamp);
		
		if (o instanceof PacketsSync.SyncEntity p) {

			//ignore packets sent out of order
			if (p.timestamp < lastTimestamp) { return; }

			lastTimestamp = p.timestamp;
			float latency = ((ClientState) state).getLatency();
			float dt = Math.max(0.0f, historyDuration - latency);

			historyDuration -= dt;

			//we remove each frame in our history that is older than our latency
			while (!frames.isEmpty() && dt > 0) {
				ClientPredictionFrame frame = frames.get(0);
				if (dt >= frame.delta) {
					dt -= frame.delta;
					frames.removeIndex(0);
				} else {

					//the last frame is trimmed so the total amount of time in our history is equal to our latency
					float t = 1 - dt / frame.delta;
					frame.delta -= dt;
					frame.positionChange.scl(t);
					break;
				}
			}

			if (!frames.isEmpty()) {

				//we predict our position is equal to what the server sent us, plus our total displacement in the time it took for that position to reach us
				predictedPosition.set(p.pos);
				rubberbandPosition.setZero();

				for (ClientPredictionFrame frame: frames) {
					rubberbandPosition.add(frame.positionChange);
				}

				predictedPosition.add(rubberbandPosition);

				//if our position is too far away from what the server sends us, just rubberband.
				if (body != null && predicting) {
					if (predictedPosition.dst2(getPosition()) > DIST_TOLERANCE) {

						shortestFraction = 1.0f;
						if (WorldUtil.preRaycastCheck(p.pos, predictedPosition)) {
							state.getWorld().rayCast((fixture, point, normal, fraction) -> {

								if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
									if (fraction < shortestFraction) {
										shortestFraction = fraction;
										return fraction;
									}
								}
								return -1.0f;
							}, p.pos, predictedPosition);
						}

						if (shortestFraction != 1.0f && !rubberbandPosition.isZero()) {
							float dist = rubberbandPosition.len() * shortestFraction - 1;
							predictedPosition.set(p.pos).add(rubberbandPosition.nor().scl(dist));
						}

						setTransform(predictedPosition, 0.0f);
						lastPosition.set(predictedPosition);
					}
				}
			}
		}
	}
	
	//most of the code here is just lifted from the Player class to simulate movement actions like jumping, hovering, fastfalling and boosting
	private final Vector2 playerLocation = new Vector2();
	private final Vector2 playerVelocity = new Vector2();
	private final Vector2 playerWorldLocation = new Vector2();
	private final Vector2 newPredictedPosition = new Vector2();
	private final Vector2 newPosition = new Vector2();
	private float predictionCount;
	private float shortestFraction;
	private static final float predictionInterval = 1 / 60.0f;
	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		controllerCount += delta;
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;
			
			if (hoveringAttempt && playerData.getExtraJumpsUsed() >= playerData.getExtraJumps() &&
				playerData.getCurrentFuel() >= playerData.getHoverCost()) {
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
		playerVelocity.set(getLinearVelocity());
		playerWorldLocation.set(getPosition());

		if (body != null && alive) {
			
			//we add a new prediction frame to our list with our current displacement/velocity
			ClientPredictionFrame frame = new ClientPredictionFrame(delta);
			frame.positionChange.set(playerWorldLocation).sub(lastPosition);
			frame.velocity.set(playerVelocity);
			frames.add(frame);
			historyDuration += delta;

			//we adjust predicted position to ensure it is up-to-date
			predictedPosition.add(frame.positionChange);

			//we do our latency check here. if our latency is too high/low, we switch to/away our predicting mode
			float latency = ((ClientState) state).getLatency();

			if (predicting && latency < LATENCY_THRESHOLD_MIN) {
				predicting = false;
			} else if (!predicting && latency > LATENCY_THRESHOLD_MAX) {
				predicting = true;
			}

			predictionCount += delta;
			while (predictionCount >= predictionInterval) {
				predictionCount -= predictionInterval;

				//when predicting, we extrapolate our position based on our prediction plus our current velocity given the current latency.
				if (predicting) {
					float time = CONVERGE_MULTIPLIER * latency;
					extrapolatedPosition.set(predictedPosition).add(extrapolationVelocity.set(playerVelocity).scl(time));

					float t = predictionInterval / (latency * (1 + CONVERGE_MULTIPLIER));

					newPredictedPosition.set(playerWorldLocation).add(extrapolatedPosition.sub(playerWorldLocation).scl(t));

					shortestFraction = 1.0f;
					if (WorldUtil.preRaycastCheck(playerWorldLocation, newPredictedPosition)) {
						state.getWorld().rayCast((fixture, point, normal, fraction) -> {

							if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
								if (fraction < shortestFraction) {
									shortestFraction = fraction;
									return fraction;
								}
							}
							return -1.0f;
						}, playerWorldLocation, newPredictedPosition);
					}

					//scale extrapolation by shortest fraction to avoid extrapolating through a wall
					if (shortestFraction != 1.0f && !extrapolatedPosition.isZero()) {
						float dist = extrapolatedPosition.len() * shortestFraction - 1;
						newPredictedPosition.set(playerWorldLocation).add(extrapolatedPosition.nor().scl(dist));
					}
					setTransform(newPredictedPosition, 0.0f);
				}
			}
			lastPosition.set(getPosition());
		}

		newPosition.set(getPixelPosition());
		mouseAngle.set(newPosition.x, newPosition.y)
				.sub(((ClientState) state).getMousePosition().x, ((ClientState) state).getMousePosition().y);
		attackAngle = MathUtils.atan2(mouseAngle.y, mouseAngle.x) * MathUtils.radDeg;
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
			if (playerData.getCurrentFuel() >= playerData.getAirblastCost()) {
				mousePos.set(((ClientState) state).getMousePosition().x, ((ClientState) state).getMousePosition().y);
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
}
