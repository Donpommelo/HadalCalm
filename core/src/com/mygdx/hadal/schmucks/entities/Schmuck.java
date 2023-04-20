package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;

/**
 * A Schmuck is an entity that can use equipment like the player or an enemy.
 * They also have some innate stats.
 * @author Creetcorn Cleddner
 *
 */
public class Schmuck extends HadalEntity {

	//the name of this schmuck
	protected String name;
	
	//The current MoveState of this schmuck
	protected MoveState moveState;
	
	//user data.
	private BodyData bodyData;
	
	//the enemy's base hp.
    protected float baseHp;
    
	//This particle is triggered upon receiving damage
	public ParticleEntity impact;

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

		impact = new ParticleEntity(state, this, Particle.IMPACT, 1.0f, 0.0f, false, SyncType.NOSYNC);
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
	public void render(SpriteBatch batch) {}

	/**
	 * This is calledregularly to send a packet to the corresponding client schmuck.
	 * This packet updates MoveState, hp and entity parameters.
	 */
	@Override
	public void onServerSync() {
		state.getSyncPackets().add(new PacketsSync.SyncSchmuck(entityID, getPosition(), getLinearVelocity(),
				entityAge, state.getTimer(), moveState, getBodyData().getCurrentHp()));
	}
	
	/**
	 * The client schmuck receives the packet sent above and updates the provided fields.
	 */
	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncSchmuck p) {
			if (!this.equals(state.getPlayer())) {
				moveState = p.moveState;
			}
			getBodyData().setCurrentHp(p.currentHp);
		}
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

	public boolean isOrigin() {
		if (state.isServer()) {
			return !(this instanceof PlayerClientOnHost);
		} else {
			return this instanceof PlayerSelfOnClient;
		}
	}

	@Override
	public void increaseAnimationTime(float i) { animationTime += i; }
	
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
}
