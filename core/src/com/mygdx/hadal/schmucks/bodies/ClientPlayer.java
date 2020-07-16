package com.mygdx.hadal.schmucks.bodies;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.client.ClientPredictionFrame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class ClientPlayer extends Player {

	private TextureRegion predictionIndicator;
	public ClientPlayer(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData, int connID, boolean reset, StartPoint start) {
		super(state, startPos, name, startLoadout, oldData, connID, reset, start);
		
		predictionIndicator = Sprite.ORB_RED.getFrame();
	}

	private final static float CONVERGE_MULTIPLIER = 0.5f;
	private final static float LATENCY_THRESHOLD = 0.1f;
	private final static float VELO_TOLERANCE = 100.0f;
	private ArrayList<ClientPredictionFrame> frames = new ArrayList<ClientPredictionFrame>();
	private Vector2 lastPosition = new Vector2();
	private Vector2 predictedPosition = new Vector2();
	private Vector2 extrapolatedPosition = new Vector2();
	private Vector2 extrapolationVelocity = new Vector2();

	private float historyDuration;
	
//	public void onReceiveSync(Object o, float timestamp) {
//		super.onReceiveSync(o, timestamp);
//		
//		float latency = ((ClientState) state).getLatency();
//		
//		float dt = Math.max(0.0f, historyDuration - latency);
//		
//		historyDuration -= dt;
//		
//		while (!frames.isEmpty() && dt > 0) {
//			ClientPredictionFrame frame = frames.get(0);
//			if (dt >= frame.delta) {
//				dt -= frame.delta;
//				frames.remove(0);
//			} else {
//				float t = 1 - dt / frame.delta;
//				frame.delta -= dt;
//				frame.position.scl(t);
//				break;
//			}
//		}
//		
//		if (o instanceof Packets.SyncEntity) {
//			Packets.SyncEntity p = (Packets.SyncEntity) o;
//			
//			if (!frames.isEmpty()) {
//				if (p.velocity.dst2(frames.get(0).velocity) > VELO_TOLERANCE) {
//					
//				}
//			}
//			
//			predictedPosition.set(p.pos);
//			
//			for (ClientPredictionFrame frame: frames) {
//				predictedPosition.add(frame.position);
//			}
//		}
//	}
//	
//	private Vector2 newPosition = new Vector2();
//	private Vector2 fug = new Vector2();
//	@Override
//	public void clientController(float delta) {
//		super.clientController(delta);
//		mouseAngle.set(getPixelPosition().y, getPixelPosition().x).sub(((ClientState) state).getMousePosition().y, ((ClientState) state).getMousePosition().x);
//		attackAngle = (float)(Math.atan2(mouseAngle.x, mouseAngle.y) * 180 / Math.PI);
//		
//		ClientPredictionFrame frame = new ClientPredictionFrame(delta);
//		frame.position.set(body.getPosition()).sub(lastPosition);
//		frame.velocity.set(body.getLinearVelocity());
//
//		frames.add(frame);
//		historyDuration += delta;
//		
//		float latency = Math.max(((ClientState) state).getLatency(), 0.01f);
//		
//		if (latency >= LATENCY_THRESHOLD) {
//			extrapolatedPosition.set(predictedPosition).add(extrapolationVelocity.set(body.getLinearVelocity()).scl((1 + CONVERGE_MULTIPLIER) * latency));
//			fug.set(extrapolatedPosition);
//			float t = delta / (latency * (1 + CONVERGE_MULTIPLIER));
//			
//			System.out.println(body.getPosition() + " " + predictedPosition + " " + extrapolatedPosition + " " + t + " " + delta + " " + latency);
//			
//			newPosition.set(body.getPosition()).add(extrapolatedPosition.sub(body.getPosition()).scl(t));
//			
//			System.out.println(newPosition + " " + extrapolationVelocity);
//			
//			setTransform(newPosition, 0.0f);
//			setTransform(newPosition.set(body.getPosition()).lerp(extrapolatedPosition, t), 0.0f);
//			lastPosition.set(newPosition);
//		}
//	}
//	
//	@Override
//	public void clientInterpolation() {
//		
//		if (Math.max(((ClientState) state).getLatency(), 0.01f) < LATENCY_THRESHOLD) {
//			super.clientInterpolation();
//		}
//	}
//	
//	@Override
//	public void render(SpriteBatch batch) {
//		super.render(batch);
//		
//		batch.draw(predictionIndicator, 
//				fug.x * 32 - size.x / 2, 
//				fug.y * 32 - size.y / 2, 
//				size.x / 2, size.y / 2,
//				size.x, size.y, 1, 1, 
//				(float) Math.toDegrees(getAngle()));
//	}
}
