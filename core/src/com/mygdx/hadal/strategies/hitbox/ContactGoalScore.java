package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.modes.FootballGoal;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes the hbox "score" when it comes in contact with a "goal" event.
 * hboxes die when they score
 * @author Twarvuldemar Thersula
 */
public class ContactGoalScore extends HitboxStrategy {

	private final static float PARTICLE_DURATION = 5.0f;

	//the player that last hit the "ball" to be given score credit
	private Player lastHit;

	public ContactGoalScore(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB, Body body) {
		if (fixB != null) {
			if (fixB.getEntity() instanceof FootballGoal goal) {
				if (state.isServer()) {
					ParticleEntity particle = new ParticleEntity(state, goal, Particle.DIATOM_IMPACT_LARGE, 0, PARTICLE_DURATION,
							true, SyncType.CREATESYNC);
					if (goal.getTeamIndex() < AlignmentFilter.currentTeams.length) {
						particle.setColor(AlignmentFilter.currentTeams[goal.getTeamIndex()].getPalette().getIcon());
					}
					goal.getEventData().preActivate(null, lastHit);

					hbox.die();
				}
			}
		}
	}

	@Override
	public void receiveDamage(BodyData perp, float baseDamage, Vector2 knockback, DamageTag... tags) {
		if (perp instanceof PlayerBodyData playerData) {
			lastHit = playerData.getPlayer();
		}
	}
}
