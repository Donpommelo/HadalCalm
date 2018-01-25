package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * A Schmuck is an entity that can use equipment like the player or an enemy.
 * They also have some innate stats.
 * @author Zachary Tu
 *
 */
public class Schmuck extends HadalEntity {

	//The current movestate of this schmuck
	public MoveStates moveState;
	
	//Fixtures and user data
	protected Fixture feet;
	protected FeetData feetData;
	
	//user data.
	protected BodyData bodyData;
	
	//Is this schmuck currently standing on a solid surface?
	protected boolean grounded;
	
	//Counters that keep track of delay between action initiation + action execution and action execution + next action
	public float shootCdCount = 0;
	public float shootDelayCount = 0;
	
	//The last used tool. This is used to process equipment with a delay between using and executing.
	public Equipable usedTool;
	
	//This counter keeps track of elapsed time so the entity behaves the same regardless of engine tick time.
	public float controllerCount = 0;
	
	// This counter keeps track of frames for animations. This is incremented every controller tick for now.
	public float animationTime = 0;
	public void increaseAnimationTime(float i) { animationTime += i; }
	public float getAnimationTime() { return animationTime; }

	public float hitboxfilter;

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
	public Schmuck(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float w, float h,
			float startX, float startY, float hitboxFilter) {
		super(state, world, camera, rays, w, h, startX, startY);
		this.grounded = false;
		this.hitboxfilter = hitboxFilter;
	}

	/**
	 * When this schmuck is added to the world, give it a foot to keep track of whether it is grounded or not.
	 * IMPORTANT: this method does not create the entity's body! 
	 * Subclasses must create the schmuck's body before calling super.create()! Otherwise body + bodyData will be null.
	 */
	@Override
	public void create() {
		this.feetData = new FeetData(world, UserDataTypes.FEET, this);        
		this.feet = this.body.createFixture(FixtureBuilder.createFixtureDef(width, height / 8, new Vector2(0,  - height / 2 / PPM), true, 0,
				Constants.BIT_SENSOR, (short)(Constants.BIT_WALL | Constants.BIT_ENEMY), Constants.PLAYER_HITBOX));
		
		feet.setUserData(feetData);
		
		this.hadalData = bodyData;
	}

	/**
	 * The basic behaviour of a schmuck depends on its moveState.
	 * This method contains some physics that constrains schmucks in addition to box2d stuff.
	 */
	@Override
	public void controller(float delta) {
		
		//This line ensures that this runs every 1/60 second regardless of computer speed.
		controllerCount+=delta;
		if (controllerCount >= 1/60f) {
			controllerCount -= 1/60f;
						
			Vector2 currentVel = body.getLinearVelocity();
			float desiredXVel = 0.0f;
			float desiredYVel = 0.0f;
			
			//set desired velocity depending on move states. TODO: add movestates for schmucks not affected by gravity.
			switch(moveState) {
			case MOVE_LEFT:
				desiredXVel = grounded ? 
						-bodyData.maxGroundXSpeed * (1 + bodyData.getBonusGroundSpeed()) :
						-bodyData.maxAirXSpeed * (1 + bodyData.getBonusAirSpeed());
				break;
			case MOVE_RIGHT:
				desiredXVel = grounded ? 
						bodyData.maxGroundXSpeed * (1 + bodyData.getBonusGroundSpeed()) : 
						bodyData.maxAirXSpeed * (1 + bodyData.getBonusAirSpeed());
				break;
			default:
				break;
			}
			
			float accelX = 0.0f;
			float accelY = 0.0f;
			
			//Process acceleration based on bodyData stats.
			if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
				accelX = grounded ? 
						bodyData.groundXAccel * (1 + bodyData.getBonusGroundAccel()): 
						bodyData.airXAccel * (1 + bodyData.getBonusAirAccel());
			} else {
				accelX = grounded ? 
						bodyData.groundXDeaccel * (1 + bodyData.getBonusGroundDrag()) : 
						bodyData.airXDeaccel * (1 + bodyData.getBonusAirDrag());
			}
			
			float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
			
			if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
				accelY = grounded ? 
						bodyData.groundYAccel * (1 + bodyData.getBonusGroundDrag()): 
						bodyData.airYAccel * (1 + bodyData.getBonusAirDrag());
			} else {
				accelY = grounded ? 
						bodyData.groundYDeaccel * (1 + bodyData.getBonusGroundDrag()):
						bodyData.airYDeaccel * (1 + bodyData.getBonusAirDrag());
			}
			
			float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;
			
			Vector2 force = new Vector2(newX - currentVel.x, newY - currentVel.y).scl(body.getMass());
			body.applyLinearImpulse(force, body.getWorldCenter(), true);
		}
		
		//Apply base hp regen
		bodyData.regainHp(bodyData.getHpRegen() * delta);
		
		//process cooldowns
		shootCdCount-=delta;
		shootDelayCount-=delta;
		
		//If the delay on using a tool just ended, use the tool.
		if (shootDelayCount <= 0 && usedTool != null) {
			useToolEnd();
		}
		
		//Process statuses
		bodyData.statusProcTime(3, bodyData, delta, null);
		
	}

	/**
	 * Draw the schmuck
	 */
	@Override
	public void render(SpriteBatch batch) {
		
	}

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

			//account for the tool's use delay.
			shootDelayCount = tool.useDelay;
			
			//Register the tool targeting the input coordinates.
			tool.mouseClicked(delta, state, bodyData, hitbox, x, y, world, camera, rays);
			
			//set the tool that will be executed after delay to input tool.
			usedTool = tool;
		}
	}
	
	/**
	 * This method is called after a tool is used following the tool's delay.
	 */
	public void useToolEnd() {
			
		//the schmuck will not register another tool usage for the tool's cd
		shootCdCount = usedTool.getUseCd() * (1 - bodyData.getToolCdReduc());
		
		//execute the tool.
		usedTool.execute(state, bodyData, world, camera, rays);
		
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
		tool.release(state, bodyData, world, camera, rays);
	}	
	
	public BodyData getBodyData() {
		return bodyData;
	}
	
/*	public float getFaction() {
		
	}*/
}
