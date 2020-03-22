package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.utils.Stats;

/**
 * A Schmuck is an entity that can use equipment like the player or an enemy.
 * They also have some innate stats.
 * @author Zachary Tu
 *
 */
public class Schmuck extends HadalEntity {

	//the name of this schmuck
	protected String name;
	
	//The current movestate of this schmuck
	protected MoveState moveState;
	
	//user data.
	private BodyData bodyData;
	
	//the enemy's base hp.
    protected float baseHp;
    
	//Is this schmuck currently standing on a solid surface?
	protected boolean grounded;
	
	//Counters that keep track of delay between action initiation + action execution and action execution + next action
	protected float shootCdCount = 0;
	protected float shootDelayCount = 0;
	
	//The last used tool. This is used to process equipment with a delay between using and executing.
	protected Equipable usedTool;
	
	//This counter keeps track of elapsed time so the entity behaves the same regardless of engine tick time.
	protected float controllerCount = 0;
	
	//This particle is triggered upon receiving damage
	public ParticleEntity impact;

	//This is the filter of this unit and hitboxes it spawns
	protected short hitboxfilter;

	private final static float airAnimationSlow = 0.4f;
	
	/**
	 * This constructor is called when a Schmuck is made.
	 * @param state: Current playState
	 * @param world: Box2d world
	 * @param camera: Game camera
	 * @param rays: game rayhandler
	 * @param w: width
	 * @param h: height
	 * @param startX: starting x position
	 * @param startY: starting y position
	 */
	public Schmuck(PlayState state, Vector2 startPos, Vector2 size, String name, short hitboxFilter, float baseHp) {
		super(state, startPos, size);
		this.name = name;
		this.grounded = false;
		this.hitboxfilter = hitboxFilter;
		this.baseHp = baseHp;
		
		impact = new ParticleEntity(state, this, Particle.IMPACT, 1.0f, 0.0f, false, particleSyncType.TICKSYNC);
	}

	/**
	 */
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
		getBodyData().regainHp(getBodyData().getStat(Stats.HP_REGEN) * delta, getBodyData(), true, DamageTypes.REGEN);
		
		//process cooldowns on firing
		shootCdCount -= delta;
		shootDelayCount -= delta;
		
		//If the delay on using a tool just ended, use the tool.
		if (shootDelayCount <= 0 && usedTool != null) {
			useToolEnd();
		}
		
		//Process statuses
		getBodyData().statusProcTime(new ProcTime.TimePass(delta));
	}

	/**
	 * Draw the schmuck
	 */
	@Override
	public void render(SpriteBatch batch) {}

	/**
	 * This method is called when a schmuck wants to use a tool.
	 * @param delta: Time passed since last usage. This is used for Charge tools that keep track of time charged.
	 * @param tool: Equipment that the schmuck wants to use
	 * @param hitbox: aka filter. Who will be affected by this equipment? Player or enemy or neutral?
	 * @param mouseLocation: screen coordinate that represents where the tool is being directed.
	 * @param wait: Should this tool wait for base cooldowns. No for special tools like built-in airblast/momentum freezing/some enemy attacks
	 */
	public void useToolStart(float delta, Equipable tool, short hitbox, Vector2 mouseLocation, boolean wait) {
		
		getBodyData().statusProcTime(new ProcTime.WhileAttack(delta, tool));
		
		//Only register the attempt if the user is not waiting on a tool's delay or cooldown. (or if tool ignores wait)
		if ((shootCdCount < 0 && shootDelayCount < 0) || !wait) {

			//Register the tool targeting the input coordinates.
			tool.mouseClicked(delta, state, getBodyData(), hitbox, mouseLocation);
			
			//set the tool that will be executed after delay to input tool.
			usedTool = tool;
			
			//account for the tool's use delay.
			shootDelayCount = tool.getUseDelay();
		}
	}
	
	/**
	 * This method is called after a tool is used following the tool's delay.
	 */
	public void useToolEnd() {
			
		//the schmuck will not register another tool usage for the tool's cd
		shootCdCount = usedTool.getUseCd() * (1 - getBodyData().getStat(Stats.TOOL_SPD));
		
		//execute the tool.
		usedTool.execute(state, getBodyData());
		
		//clear the used tool field.
		usedTool = null;
	}
	
	/**
	 * This method is called after the user releases the button for a tool. Mostly used by charge weapons that execute when releasing
	 * instead of after pressing.
	 * @param tool: tool to release
	 * @param hitbox: aka filter. Who will be affected by this equipment? Player or enemy or neutral?
	 * @param mouseLocation: screen coordinate that represents where the tool is being directed.
	 */
	public void useToolRelease(Equipable tool, short hitbox, Vector2 mouseLocation) {
		tool.release(state, getBodyData());
	}	
	
	/**
	 * This is called every engine tick. The server schmuck sends a packet to the corresponding client schmuck.
	 * This packet updates movestate.
	 */
	@Override
	public void onServerSync() {
		super.onServerSync();
		HadalGame.server.sendToAllUDP(new Packets.SyncSchmuck(entityID.toString(), moveState, getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP)));
	}
	
	/**
	 * The client schmuck receives the packet sent above and updates the provided fields.
	 */
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncSchmuck) {
			Packets.SyncSchmuck p = (Packets.SyncSchmuck) o;
			moveState = p.moveState;
			getBodyData().setOverrideHpPercent(p.hpPercent);
		} else {
			super.onClientSync(o);
		}
	}
	
	/**
	 * This returns the location that a spawned projectile should be created. (for the player, we override to make it spawn near the tip of the gun)
	 * @param startVelo
	 * @param projSize
	 * @return
	 */
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {	return getPixelPosition(); }
	
	@Override
	public void increaseAnimationTime(float i) { 
		if (grounded) {
			animationTime += i; 
		} else {
			animationTime += i * airAnimationSlow; 
		}
	}
	
	@Override
	public HadalData getHadalData() { return bodyData; }
	
	public BodyData getBodyData() {	return bodyData; }
	
	public MoveState getMoveState() { return moveState; }
	
	public void setMoveState(MoveState moveState) { this.moveState = moveState; }
	
	public float getShootCdCount() { return shootCdCount; }
	
	public void setShootCdCount(float shootCdCount) { this.shootCdCount = shootCdCount; }

	public float getShootDelayCount() { return shootDelayCount; }
	
	public short getHitboxfilter() { return hitboxfilter; }
	
	public float getBaseHp() { return baseHp; }

	public boolean isGrounded() { return grounded; }

	public String getName() { return name; }
}
