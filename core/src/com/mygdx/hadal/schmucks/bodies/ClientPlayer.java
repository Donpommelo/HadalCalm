package com.mygdx.hadal.schmucks.bodies;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.client.ClientPredictionFrame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class ClientPlayer extends Player {

//	private TextureRegion extrapolationIndicator, predictionIndicator;
	public ClientPlayer(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData, int connID, boolean reset, StartPoint start) {
		super(state, startPos, name, startLoadout, oldData, connID, reset, start);
		
//		extrapolationIndicator = Sprite.ORB_RED.getFrame();
//		predictionIndicator = Sprite.ORB_BLUE.getFrame();
	}

	@Override
	public void create() {
		super.create();
		
		predictedPosition.set(body.getPosition());
		lastPosition.set(body.getPosition());
	}
	
	private final static float CONVERGE_MULTIPLIER = 0.05f;
	private final static float LATENCY_THRESHOLD_MIN = 0.08f;
	private final static float LATENCY_THRESHOLD_MAX = 0.1f;
	private final static float VELO_TOLERANCE = 200.0f;
	private ArrayList<ClientPredictionFrame> frames = new ArrayList<ClientPredictionFrame>();
	private Vector2 lastPosition = new Vector2();
	private Vector2 predictedPosition = new Vector2();
	private Vector2 extrapolatedPosition = new Vector2();
	private Vector2 extrapolationVelocity = new Vector2();

	private float historyDuration;
	private boolean predicting;
	
	public void onReceiveSync(Object o, float timestamp) {
		super.onReceiveSync(o, timestamp);
		
		float latency = ((ClientState) state).getLatency();
		float dt = Math.max(0.0f, historyDuration - latency);
		
		historyDuration -= dt;
		
		while (!frames.isEmpty() && dt > 0) {
			ClientPredictionFrame frame = frames.get(0);
			if (dt >= frame.delta) {
				dt -= frame.delta;
				frames.remove(0);
			} else {
				float t = 1 - dt / frame.delta;
				frame.delta -= dt;
				frame.positionChange.scl(t);
				break;
			}
		}
		
		if (o instanceof Packets.SyncEntity) {
			Packets.SyncEntity p = (Packets.SyncEntity) o;
			
			if (!frames.isEmpty()) {
				
				if (p.velocity.dst2(frames.get(0).velocity) > VELO_TOLERANCE) {
					for (ClientPredictionFrame frame: frames) {
						frame.velocity = p.velocity;
						frame.positionChange = p.velocity.scl(frame.delta);
					}
				}
				predictedPosition.set(p.pos);
				
				for (ClientPredictionFrame frame: frames) {
					predictedPosition.add(frame.positionChange);
				}
			}
		}
	}
	
	private Vector2 newPosition = new Vector2();
	private Vector2 fug = new Vector2();
	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		controllerCount += delta;
		
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;
			
			if (hoveringAttempt && playerData.getExtraJumpsUsed() >= playerData.getExtraJumps() && ((ClientState) state).getUiPlay().getOverrideFuelAmount() >= playerData.getHoverCost()) {
				if (jumpCdCount < 0) {
					hover();
					hovering = true;
				}
			} else {
				hovering = false;
			}
			
			if (fastFalling) {
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
		
		mouseAngle.set(getPixelPosition().y, getPixelPosition().x).sub(((ClientState) state).getMousePosition().y, ((ClientState) state).getMousePosition().x);
		attackAngle = (float)(Math.atan2(mouseAngle.x, mouseAngle.y) * 180 / Math.PI);
		
		if (body != null && alive) {
			ClientPredictionFrame frame = new ClientPredictionFrame(delta);
			frame.positionChange.set(getPosition()).sub(lastPosition);
			frame.velocity.set(body.getLinearVelocity());
			frames.add(frame);
			historyDuration += delta;
			
			float latency = ((ClientState) state).getLatency();

			if (predicting && latency < LATENCY_THRESHOLD_MIN) {
				predicting = false;
			} else if (!predicting && latency > LATENCY_THRESHOLD_MAX) {
				predicting = true;
			}
			
			if (predicting) {
				
				extrapolatedPosition.set(predictedPosition).add(extrapolationVelocity.set(body.getLinearVelocity()).scl((CONVERGE_MULTIPLIER) * latency));
				fug.set(extrapolatedPosition);
				
				float t = 0.0f;
				t = delta / (latency * (1 + CONVERGE_MULTIPLIER));

				newPosition.set(body.getPosition()).add(extrapolatedPosition.sub(getPosition()).scl(t));
				setTransform(newPosition, 0.0f);
			}
			lastPosition.set(body.getPosition());
		}
	}
	
	@Override
	public void clientInterpolation() {
		if (!predicting) {
			super.clientInterpolation();
		}
	}
	
	@Override
	public void hover() {
		if (jumpCdCount < 0) {
			
			//Player will continuously do small upwards bursts that cost fuel.
			jumpCdCount = hoverCd;
			pushMomentumMitigation(0, playerData.getHoverPower());
		}
	}
	
	@Override
	public void jump() {
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
	
	private Vector2 mousePos = new Vector2();
	@Override
	public void airblast() {
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
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPlayerAll) {
			Packets.SyncPlayerAll p = (Packets.SyncPlayerAll) o;

			getPlayerData().setCurrentSlot(p.currentSlot);
			getPlayerData().setCurrentTool(getPlayerData().getMultitools()[p.currentSlot]);
			setToolSprite(playerData.getCurrentTool().getWeaponSprite().getFrame());
			getPlayerData().getCurrentTool().setReloading(p.reloading);
			reloadPercent = p.reloadPercent;
			getPlayerData().getCurrentTool().setCharging(p.charging);
			chargePercent = p.chargePercent;
			getPlayerData().setOverrideOutOfAmmo(p.outOfAmmo);
		} else {
			super.onClientSync(o);
		}
	}
	
//	@Override
//	public void render(SpriteBatch batch) {
//		super.render(batch);
//		
//		batch.draw(predictionIndicator, 
//				predictedPosition.x * 32 - size.x / 2, 
//				predictedPosition.y * 32 - size.y / 2, 
//				size.x / 2, size.y / 2,
//				size.x, size.y, 1, 1, 
//				(float) Math.toDegrees(getAngle()));
//		
//		batch.draw(extrapolationIndicator, 
//				fug.x * 32 - size.x / 2, 
//				fug.y * 32 - size.y / 2, 
//				size.x / 2, size.y / 2,
//				size.x, size.y, 1, 1, 
//				(float) Math.toDegrees(getAngle()));
//	}
}
