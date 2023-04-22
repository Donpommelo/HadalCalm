package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a number of projectiles when its hbox dies
 * @author Squuddeus Swollygag
 */
public class DieFrag extends HitboxStrategy {
	
	private static final float FRAG_SPEED = 15.0f;

	//this is the number of frags to spawn
	private final int numFrag;

	public DieFrag(PlayState state, Hitbox proj, BodyData user, int numFrag) {
		super(state, proj, user);
		this.numFrag = numFrag;
	}
	
	@Override
	public void die() {
		Vector2 fragPosition = new Vector2(hbox.getPixelPosition());
		Vector2 fragVelo = new Vector2();

		Vector2[] positions = new Vector2[numFrag];
		Vector2[] velocities = new Vector2[numFrag];
		for (int i = 0; i < numFrag; i++) {
			positions[i] = fragPosition;

			float newDegrees = (MathUtils.random(0, 360));
			fragVelo.set(0, FRAG_SPEED).setAngleDeg(newDegrees);
			velocities[i] = new Vector2(fragVelo);
		}
		SyncedAttack.BRITTLING_POWDER.initiateSyncedAttackMulti(state, creator.getSchmuck(), new Vector2(), positions, velocities);
	}
}
