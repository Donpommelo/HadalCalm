package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.enemies.WaveType.WaveTag;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * There are ~6 Wave Spawners in arena maps. When a new wave is spawned, each spawner is activated and a new wave of enemies is spawned
 * Triggered Behavior: When triggered, spawn a wave
 * Triggering Behavior: N/A
 * 
 * Fields:
 * extraField: extra field sometimes used for enemy spawns
 * tag: string tag to further specify which enemies can spawn
 * @author Spibriana Svesabella
 */
public class SpawnerWave extends Event {

	//this is the number of the current wave.
	private final int extraField;
	private final Array<WaveTag> tags = new Array<>();
	
	public SpawnerWave(PlayState state, Vector2 startPos, Vector2 size, int extraField, String tag) {
		super(state, startPos, size);
		this.extraField = extraField;

		for (String s : tag.split(",")) {
			tags.add(WaveTag.valueOf(s));
		}
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) 0, (short) 0, true, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}
	
	public Array<WaveTag> getTags() { return tags; }

	public int getExtraField() { return extraField; }
}
