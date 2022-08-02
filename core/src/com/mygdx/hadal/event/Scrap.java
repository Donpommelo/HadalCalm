package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * A scrap event is a single currency unit that the player picks up if they touch it.
 * This does not have a blueprint and is not parsed from tiled.
 * 
 * If a client picks up scrap, it will be counted towards the host's money.
 * @author Matannia Muchnold
 */
public class Scrap extends Event {

	private static final Vector2 BASE_SIZE = new Vector2(32, 32);
	
	//spread is for giving the initial scrap a random velocity
	private static final int SPREAD = 90;
	private static final float VELO_AMP = 7.5f;
	private static final float LIFESPAN = 9.0f;

	//short delay before scrap can be picked up
	private static final float PRIME_CD = 0.5f;

	private final Vector2 startVelo = new Vector2(0, 1);

	//does picking up this event increment the player's score?
	private final boolean score;

	public Scrap(PlayState state, Vector2 startPos, boolean score) {
		super(state, startPos, BASE_SIZE, LIFESPAN);
		this.score = score;

		setEventSprite(Sprite.NASU);
		setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
		setGravity(1.0f);
		addAmbientParticle(Particle.SPARKLE);
		setSynced(true);
	}

	private final Vector2 newVelocity = new Vector2();
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (isAlive() && fixB instanceof PlayerBodyData playerData && delay <= 0) {
					event.queueDeletion();

					//in single player, scrap gives the player 1 unit of currency
					if (GameStateManager.currentMode == Mode.SINGLE) {
						state.getGsm().getRecord().incrementScrap(1);
					} else if (score) {

						//in eggplant mode, we increase the players score by 1
						state.getMode().processPlayerScoreChange(state, playerData.getPlayer(), 1);
					}

					state.getUiExtra().syncUIText(UITag.uiType.SCRAP);
					new ParticleEntity(state, fixB.getEntity(), Particle.SPARKLE, 1.0f, 1.0f, true, SyncType.CREATESYNC);
					
					//activate effects that activate upon picking up scrap
					playerData.statusProcTime(new ProcTime.ScrapPickup());
					SoundEffect.COIN3.playExclusive(state, getPixelPosition(), playerData.getPlayer(), 1.0f, false);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, gravity, 1.0f, 0, false, true,
				Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_SENSOR), (short) 0, true, eventData);
		FixtureBuilder.createFixtureDef(body, new Vector2(), size, false, 0, 0, 0.0f, 1.0f,
				Constants.BIT_SENSOR, (short) (Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL), (short) 0);
		
		float newDegrees = startVelo.angleDeg() + MathUtils.random(-SPREAD, SPREAD + 1);
		newVelocity.set(startVelo);
		setLinearVelocity(newVelocity.nor().scl(VELO_AMP).setAngleDeg(newDegrees));
	}
	
	private float delay = PRIME_CD;
	@Override
	public void controller(float delta) {
		super.controller(delta);
		if (delay >= 0) {
			delay -= delta;
		}
	}
}
