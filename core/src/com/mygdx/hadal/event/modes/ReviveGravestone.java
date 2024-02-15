package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.EventUtils;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.PacketUtil;
import com.mygdx.hadal.utils.TextUtil;
import com.mygdx.hadal.utils.b2d.HadalBody;
import com.mygdx.hadal.utils.b2d.HadalFixture;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;
import static com.mygdx.hadal.users.Transition.MEDIUM_FADE_DELAY;

/**
 */
public class ReviveGravestone extends Event {

	private static final Vector2 GRAVE_SIZE = new Vector2(80, 80);
	private final static float PARTICLE_DURATION = 5.0f;
	private static final float CHECK_RADIUS = 5.0f;

	//this is the player that this event is fixed to and the last player that held it
	private final User user;
	private final float returnMaxTimer;
	private final Event defaultStartPoint;

	private float returnTimer;
	private boolean positionReset;

	//textures that indicate how long until the flag is returned to base
	private final TextureRegion returnMeter, returnBar;
	private float returnPercent, returnDelayed;

	//amount of players currently nearby their dropped flag to speed up its return
	private int numReturning;

	public ReviveGravestone(PlayState state, Vector2 startPos, User user, float returnMaxTimer,	Event defaultStartPoint) {
		super(state, startPos, GRAVE_SIZE);
		this.user = user;
		this.returnMaxTimer = returnMaxTimer;
		this.returnDelayed = returnMaxTimer;
		this.defaultStartPoint = defaultStartPoint;

		this.returnTimer = returnMaxTimer;

		setEventSprite(Sprite.DIATOM_A);
		setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
		setGravity(1.0f);
		setSynced(true);
		setReliableCreate(true);

		//set flag's color according to team alignment
		HadalColor color = user.getTeamFilter().getPalette().getIcon();
		new ParticleEntity(state, this, Particle.BRIGHT_TRAIL, 0, 0, true, SyncType.CREATESYNC)
				.setScale(1.8f).setColor(color);

		//make objective marker track this event
		EventUtils.setObjectiveMarkerTeam(state, this, Sprite.CLEAR_CIRCLE_ALERT, color,
				true, false, false, user.getTeamFilter());

		if (state.isSpectatorMode()) {
			state.getUiObjective().addObjective(this, Sprite.CLEAR_CIRCLE_ALERT, color, true, false, false);
		} else if (HadalGame.usm.getOwnUser().getTeamFilter() == user.getTeamFilter()) {
			state.getUiObjective().addObjective(this, Sprite.CLEAR_CIRCLE_ALERT, color, true, false, false);
		}

		this.returnMeter = Sprite.UI_RELOAD_METER.getFrame();
		this.returnBar = Sprite.UI_RELOAD_BAR.getFrame();

		//we must set this event's layer to make it render underneath players
		setLayer(PlayState.ObjectLayer.HBOX);
	}

	@Override
	public void create() {

		this.eventData = new EventData(this);

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, BodyConstants.BIT_WALL, (short) 0)
				.setGravity(1.0f)
				.setSensor(false)
				.addToWorld(world);

		//feetdata is set to make the grave selectively pass through dropthrough platforms
		EventUtils.addFeetFixture(this);
		new HadalFixture(new Vector2(), new Vector2(size),
				BodyConstants.BIT_SENSOR, (short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_SENSOR), (short) 0)
				.setFriction(1.0f)
				.addToBody(body)
				.setUserData(eventData);
	}

	private static final float CHECK_INTERVAL = 0.2f;
	private final Vector2 hbLocation = new Vector2();
	private float controllerCount;
	private Player lastReviver;
	@Override
	public void controller(float delta) {

		if (positionReset && defaultStartPoint != null) {
			positionReset = false;
			setTransform(defaultStartPoint.getPosition(), getAngle());
		}

		//return time decrementing scales to number of players nearby
		returnTimer -= delta * numReturningToSpeed(numReturning);

		if (returnTimer > returnMaxTimer) {
			returnTimer = returnMaxTimer;
		}

		if (returnTimer <= 0.0f) {
			new ParticleEntity(state, getPixelPosition(), Particle.DIATOM_IMPACT_LARGE, PARTICLE_DURATION, true, SyncType.CREATESYNC)
					.setColor(user.getTeamFilter().getPalette().getIcon());

			queueDeletion();

			if (HadalGame.usm.getUsers().containsValue(user, true)) {

				String playerName = TextUtil.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH);

				if (lastReviver != null && numReturning > 0) {
					String reviverName = TextUtil.getPlayerColorName(lastReviver, MAX_NAME_LENGTH);
					state.getKillFeed().addNotification(UIText.GRAVE_REVIVER.text(playerName, reviverName), true);
				} else {
					state.getKillFeed().addNotification(UIText.GRAVE_REVIVE.text(playerName), true);
				}

				user.getTransitionManager().setOverrideSpawn(getPixelPosition());
				user.getTransitionManager().setOverrideStart(defaultStartPoint);
				user.getTransitionManager().beginTransition(state,
						new Transition()
								.setNextState(PlayState.TransitionState.RESPAWN)
								.setFadeDelay(MEDIUM_FADE_DELAY));
			}
		}

		//check nearby area for allied players and set return percent
		controllerCount += delta;
		if (controllerCount >= CHECK_INTERVAL) {
			controllerCount = 0.0f;
			hbLocation.set(getPosition());
			numReturning = 0;
			state.getWorld().QueryAABB(fixture -> {
						if (fixture.getUserData() instanceof PlayerBodyData playerData) {
							if (playerData.getPlayer().getHitboxFilter() == user.getPlayer().getHitboxFilter()) {
								lastReviver = playerData.getPlayer();
								numReturning++;
							}
						}
						return true;
					},
					hbLocation.x - CHECK_RADIUS, hbLocation.y - CHECK_RADIUS,
					hbLocation.x + CHECK_RADIUS, hbLocation.y + CHECK_RADIUS);
		}
		returnPercent = (returnMaxTimer - returnTimer) / returnMaxTimer;
	}

	private static final float UI_SCALE = 0.4f;
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		super.render(batch, entityLocation);

		//draw revive meter according to timer
		returnDelayed = Math.min(1.0f, returnDelayed + (returnPercent - returnDelayed) * 0.25f);

		float textX = entityLocation.x - returnMeter.getRegionWidth() * UI_SCALE / 2;
		float textY = entityLocation.y + returnMeter.getRegionHeight() * UI_SCALE + size.y / 2;

		batch.draw(returnBar, textX + 10, textY + 4, returnBar.getRegionWidth() * UI_SCALE * returnDelayed, returnBar.getRegionHeight() * UI_SCALE);
		HadalGame.FONT_SPRITE.draw(batch, user.getStringManager().getNameShort(), textX + 12, textY + returnMeter.getRegionHeight() * UI_SCALE);
		batch.draw(returnMeter, textX, textY, returnMeter.getRegionWidth() * UI_SCALE, returnMeter.getRegionHeight() * UI_SCALE);
	}

	@Override
	public Object onServerCreate(boolean catchup) {
		return new Packets.CreateGrave(entityID, user.getConnID(), getPixelPosition(), returnMaxTimer);
	}

	@Override
	public void onServerSync() {
		state.getSyncPackets().add(new PacketsSync.SyncFlag(entityID, getPosition(), getLinearVelocity(),
				state.getTimer(),
				PacketUtil.percentToByte(returnPercent)));
	}

	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);

		if (o instanceof PacketsSync.SyncFlag p) {
			returnPercent = PacketUtil.byteToPercent(p.returnPercent);
		}
	}

	public void resetPosition() {
		positionReset = true;
	}

	public AlignmentFilter getGraveTeam() { return user.getTeamFilter(); }

	/**
	 * Get speed of flag returning for number of players returning it
	 */
	private float numReturningToSpeed(int numReturning) {
		switch (numReturning) {
			case 0 -> {
				return -0.25f;
			}
			case 1 -> {
				return 1.0f;
			}
			case 2 -> {
				return 2.0f;
			}
			case 3 -> {
				return 4.0f;
			}
			default -> {
				return 8.0f;
			}
		}
	}
}
