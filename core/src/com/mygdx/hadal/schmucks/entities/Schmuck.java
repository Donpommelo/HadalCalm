package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.helpers.DamageEffectHelper;
import com.mygdx.hadal.schmucks.entities.helpers.SpecialHpHelper;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.utils.PacketUtil;

/**
 * A Schmuck is an entity that has innate stats, can have statuses and receive/deal damage.
 * @author Creetcorn Cleddner
 *
 */
public class Schmuck extends HadalEntity {

	//the name of this schmuck
	protected String name;
	
	//The current MoveState of this schmuck
	protected MoveState moveState = MoveState.DEFAULT;
	
	//user data.
	private BodyData bodyData;
	
	//the enemy's base hp.
    protected float baseHp;
    
	//This particle is triggered upon receiving damage
	public final ParticleEntity impact;

	//these helpers control special visual effects
	private final DamageEffectHelper damageEffectHelper;
	private final SpecialHpHelper specialHpHelper;

	//This is the filter of this unit and hitboxes it spawns
	protected short hitboxFilter;

	/**
	 * This constructor is called when a Schmuck is made.
	 * @param state: Current playState
	 * @param startPos: world position of this entity's starting location
	 * @param size: body size
	 * @param name: name of the schmuck to be displayed in ui and for attributed kills
	 * @param hitboxFilter: who can this entity collide with?
	 * @param baseHp: The amount of damage this schmuck can take before dying
	 */
	public Schmuck(PlayState state, Vector2 startPos, Vector2 size, String name, short hitboxFilter, float baseHp) {
		super(state, startPos, size);
		this.name = name;
		this.hitboxFilter = hitboxFilter;
		this.baseHp = baseHp;

		this.damageEffectHelper = new DamageEffectHelper(state, this);
		this.specialHpHelper = new SpecialHpHelper(this);

		impact = EffectEntityManager.getParticle(state, new ParticleCreate(Particle.IMPACT, this)
				.setStartOn(false));
	}

	@Override
	public void create() {
		this.bodyData = new BodyData(this, baseHp);
	}

	/**
	 * The basic behaviour of a schmuck depends on its moveState.
	 * This method contains some physics that constrains schmucks in addition to box2d stuff.
	 */
	@Override
	public void controller(float delta) {

		damageEffectHelper.controller(delta);
		specialHpHelper.controller(delta);

		//Apply base hp regen
		getBodyData().regainHp(getBodyData().getStat(Stats.HP_REGEN) * delta, getBodyData(), true, DamageTag.REGEN);
		
		//Process statuses
		getBodyData().statusProcTime(new ProcTime.TimePass(delta));
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		getBodyData().statusProcTime(new ProcTime.TimePass(delta));
	}

	/**
	 * Draw the schmuck
	 */
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {}

	/**
	 * This is called regularly to send a packet to the corresponding client schmuck.
	 * This packet updates MoveState, hp and entity parameters.
	 */
	@Override
	public void onServerSync() {
		state.getSyncPackets().add(new PacketsSync.SyncSchmuck(entityID, getPosition(), getLinearVelocity(),
				state.getTimer(), moveState,
				PacketUtil.percentToByte(getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP))));
	}
	
	/**
	 * The client schmuck receives the packet sent above and updates the provided fields.
	 */
	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncSchmuck p) {
			if (!this.equals(HadalGame.usm.getOwnPlayer())) {
				moveState = p.moveState;
			}
			getBodyData().setCurrentHp(PacketUtil.byteToPercent(p.hpPercent) * getBodyData().getStat(Stats.MAX_HP));
		} else if (o instanceof Packets.DeleteSchmuck p) {
			HadalEntity entity = state.findEntity(p.perpID);
			if (entity instanceof Schmuck perp) {
				getBodyData().die(perp.getBodyData(), p.source, p.tags);
			} else {
				getBodyData().die(state.getWorldDummy().getBodyData(), p.source, p.tags);
			}
		}
	}

	//this is the type of death we have. Send to client so they can process the death on their end.
	private int perpID;
	private DamageSource damageSource = DamageSource.MISC;
	private DamageTag[] damageTags = new DamageTag[] {};
	@Override
	public Object onServerDelete() {
		return new Packets.DeleteSchmuck(entityID, perpID, state.getTimer(), damageSource, damageTags);
	}

	private final Vector2 impulse = new Vector2();
	public void recoil(Vector2 recoil, float power) {
		if (!alive) { return; }
		applyLinearImpulse(impulse.set(recoil).nor().scl(-power * (1 + getBodyData().getStat(Stats.RANGED_RECOIL))));
	}
	
	/**
	 * This returns the location that a spawned projectile should be created.
	 * (for the player, we override to make it spawn near the tip of the gun)
	 * @param startVelo: the starting speed and direction of the bullet
	 * @param projSize: the size of the bullet
	 * @return the vector2 position of where the bullet should be spawned relative to the schmuck
	 */
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {	return getPixelPosition(); }

	/**
	 *
	 * @return is this schmuck the character being controlled by the player?
	 */
	public boolean isOrigin() {
		if (state.isServer()) {
			return !(this instanceof PlayerClientOnHost);
		} else {
			return this instanceof PlayerSelfOnClient;
		}
	}

	@Override
	public HadalData getHadalData() { return bodyData; }
	
	public BodyData getBodyData() {	return bodyData; }
	
	public MoveState getMoveState() { return moveState; }
	
	public void setMoveState(MoveState moveState) { this.moveState = moveState; }

	public short getHitboxFilter() { return hitboxFilter; }

	public void setHitboxFilter(short hitboxFilter) { this.hitboxFilter = hitboxFilter; }

	public float getBaseHp() { return baseHp; }

	public void setBaseHp(int baseHp) { this.baseHp = baseHp; }

	public String getName() { return name; }

	public DamageEffectHelper getDamageEffectHelper() {	return damageEffectHelper; }

	public SpecialHpHelper getSpecialHpHelper() { return specialHpHelper; }

	public void setPerpID(int perpID) { this.perpID = perpID; }

	public void setDamageSource(DamageSource damageSource) { this.damageSource = damageSource; }

	public void setDamageTags(DamageTag[] damageTags) { this.damageTags = damageTags; }
}
