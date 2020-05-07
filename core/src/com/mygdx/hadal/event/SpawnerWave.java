package com.mygdx.hadal.event;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.WaveType;
import com.mygdx.hadal.schmucks.bodies.enemies.WaveType.WaveTag;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

public class SpawnerWave extends Event {

	private int pointId;
	private int waveNum;
	private int extraField;
	private ArrayList<WaveTag> tags;
	
	public SpawnerWave(PlayState state, Vector2 startPos, Vector2 size, int pointId, int extraField, String tag) {
		super(state, startPos, size);
		this.pointId = pointId;
		this.extraField = extraField;
		
		for (String s: tag.split(",")) {
			tags.add(WaveTag.valueOf(s));
		}
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				WaveType.getWave(tags).spawnWave((SpawnerWave) event, waveNum, extraField);
				waveNum++;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (0), (short) 0, true, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}
	
	public int getPointId() { return pointId; }
}
