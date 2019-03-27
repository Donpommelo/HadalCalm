package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatusProcTime;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * A Schmuck is an entity that can use equipment like the player or an enemy.
 * They also have some innate stats.
 * @author Zachary Tu
 *
 */
public class Schmuck extends HadalEntity {

	//The current movestate of this schmuck
	protected SchmuckMoveStates moveState;
	
	//Fixtures and user data
	protected Fixture feet, rightSensor, leftSensor;
	protected FeetData feetData, rightData, leftData;
	
	//These track whether the schmuck has specific artifacts equipped.
	protected boolean scaling, stomping;

	//user data.
	protected BodyData bodyData;
	
	//Is this schmuck currently standing on a solid surface?
	protected boolean grounded;
	
	//Counters that keep track of delay between action initiation + action execution and action execution + next action
	protected float shootCdCount = 0;
	protected float shootDelayCount = 0;
	
	//Keeps track of a schmuck's sprite flashing after receiving damage.
	protected float flashingCount = 0;
	
	//The last used tool. This is used to process equipment with a delay between using and executing.
	protected Equipable usedTool;
	
	//This counter keeps track of elapsed time so the entity behaves the same regardless of engine tick time.
	protected float controllerCount = 0;
	
	//This particle is triggered upon receiving damage
	public ParticleEntity impact;

	//This is the filter of this unit and hitboxes it spawns
	protected short hitboxfilter;

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
	public Schmuck(PlayState state, float w, float h, float startX, float startY, short hitboxFilter) {
		super(state, w, h, startX, startY);
		this.grounded = false;
		this.hitboxfilter = hitboxFilter;
		impact = new ParticleEntity(state, this, Particle.IMPACT, 1.0f, 0.0f, false, particleSyncType.TICKSYNC);
	}

	/**
	 * When this schmuck is added to the world, give it a foot to keep track of whether it is grounded or not.
	 * IMPORTANT: this method does not create the entity's body! 
	 * Subclasses must create the schmuck's body before calling super.create()! Otherwise body + bodyData will be null.
	 */
	@Override
	public void create() {
		this.feetData = new FeetData(UserDataTypes.FEET, this); 
		
		this.feet = this.body.createFixture(FixtureBuilder.createFixtureDef(width - 2, height / 8, 
				new Vector2(1 / 2 / PPM,  - height / 2 / PPM), true, 0, 0, 0, 0,
				Constants.BIT_SENSOR, (short)(Constants.BIT_WALL | Constants.BIT_ENEMY | Constants.BIT_PLAYER | Constants.BIT_DROPTHROUGHWALL), hitboxfilter));
		
		feet.setUserData(feetData);
		
		this.leftData = new FeetData(UserDataTypes.SIDES, this); 
		
		this.leftSensor = this.body.createFixture(FixtureBuilder.createFixtureDef(width / 8, height, 
				new Vector2(-width / 2 / PPM,  0), true, 0, 0, 0, 0,
				Constants.BIT_PLAYER, (short)(Constants.BIT_WALL), hitboxfilter));
		
		leftSensor.setUserData(leftData);
		
		this.rightData = new FeetData(UserDataTypes.SIDES, this); 
		
		this.rightSensor = this.body.createFixture(FixtureBuilder.createFixtureDef(width / 8, height, 
				new Vector2(width / 2 / PPM,  0), true, 0, 0, 0, 0,
				Constants.BIT_PLAYER, Constants.BIT_WALL, hitboxfilter));
		
		rightSensor.setUserData(rightData);
		
		//Creates a pad at bottom of player with friction. I hate drop-through platforms.
		this.body.createFixture(FixtureBuilder.createFixtureDef(width - 2, height, 
				new Vector2(1 / 2 / PPM,  -1 / PPM), false, 0, 0, 0, 1,
				Constants.BIT_PLAYER, Constants.BIT_WALL, hitboxfilter));
		this.hadalData = bodyData;
	}

	/**
	 * The basic behaviour of a schmuck depends on its moveState.
	 * This method contains some physics that constrains schmucks in addition to box2d stuff.
	 */
	@Override
	public void controller(float delta) {
		
		//Animate sprites
		increaseAnimationTime(delta);

		//Apply base hp regen
		bodyData.regainHp(bodyData.getHpRegen() * delta, bodyData, true, DamageTypes.REGEN);
		
		//process cooldowns on firing
		shootCdCount-=delta;
		shootDelayCount-=delta;
		flashingCount-=delta;
		
		//If the delay on using a tool just ended, use the tool.
		if (shootDelayCount <= 0 && usedTool != null) {
			useToolEnd();
		}
		
		//Process statuses
		bodyData.statusProcTime(StatusProcTime.TIME_PASS, null, delta, null, bodyData.getCurrentTool(), null);
	}
	
	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		flashingCount-=delta;
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
	 * @param x: x screen coordinate that represents where the tool is being directed.
	 * @param y: y screen coordinate that represents where the tool is being directed.
	 * @param wait: Should this tool wait for base cooldowns. No for special tools like built-in airblast/momentum freezing/some enemy attacks
	 */
	public void useToolStart(float delta, Equipable tool, short hitbox, int x, int y, boolean wait) {
		
		//Only register the attempt if the user is not waiting on a tool's delay or cooldown. (or if tool ignores wait)
		if ((shootCdCount < 0 && shootDelayCount < 0) || !wait) {

			//Register the tool targeting the input coordinates.
			tool.mouseClicked(delta, state, bodyData, hitbox, x, y);
			
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
		shootCdCount = usedTool.getUseCd() * (1 - bodyData.getToolCdReduc());
		
		//execute the tool.
		usedTool.execute(state, bodyData);
		
		//clear the used tool field.
		usedTool = null;
	}
	
	/**
	 * This method is called after the user releases the button for a tool. Mostly used by charge weapons that execute when releasing
	 * instead of after pressing.
	 * @param tool: tool to release
	 * @param hitbox: aka filter. Who will be affected by this equipment? Player or enemy or neutral?
	 * @param x: x screen coordinate that represents where the tool is being directed.
	 * @param y: y screen coordinate that represents where the tool is being directed.
	 */
	public void useToolRelease(Equipable tool, short hitbox, int x, int y) {
		tool.release(state, bodyData);
	}	
	
	/**
	 * This is called every engine tick. The server schmuck sends a packet to the corresponding client schmuck.
	 * This packet updates movestate, hp, fuel and flashingness
	 */
	@Override
	public void onServerSync() {
		super.onServerSync();
		HadalGame.server.server.sendToAllUDP(new Packets.SyncSchmuck(entityID.toString(), moveState,
				getBodyData().getCurrentHp(), getBodyData().getCurrentFuel(), flashingCount));
	}
	
	/**
	 * The client schmuck receives the packet sent above and updates the provided fields.
	 */
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncSchmuck) {
			Packets.SyncSchmuck p = (Packets.SyncSchmuck) o;
			moveState = p.moveState;
			getBodyData().setCurrentHp(p.currentHp);
			getBodyData().setCurrentFuel(p.currentFuel);
			flashingCount = p.flashDuration;
		} else {
			super.onClientSync(o);
		}
	}
	
	@Override
	public HadalData getHadalData() {
		return bodyData;
	}
	
	public BodyData getBodyData() {
		return bodyData;
	}
	
	public float getAttackAngle() {
		return 0;
	}
	
	public boolean isScaling() {
		return scaling;
	}

	public void setScaling(boolean scaling) {
		this.scaling = scaling;
	}

	public boolean isStomping() {
		return stomping;
	}

	public void setStomping(boolean stomping) {
		this.stomping = stomping;
	}

	public SchmuckMoveStates getMoveState() {
		return moveState;
	}
	public void setMoveState(SchmuckMoveStates moveState) {
		this.moveState = moveState;
	}
	
	public float getShootCdCount() {
		return shootCdCount;
	}
	
	public void setShootCdCount(float shootCdCount) {
		this.shootCdCount = shootCdCount;
	}

	public float getShootDelayCount() {
		return shootDelayCount;
	}
	
	public float getFlashingCount() {
		return flashingCount;
	}
	
	public void setFlashingCount(float flashduration) {
		this.flashingCount = flashduration;
	}
	
	public short getHitboxfilter() {
		return hitboxfilter;
	}

	public boolean isGrounded() {
		return grounded;
	}
}
