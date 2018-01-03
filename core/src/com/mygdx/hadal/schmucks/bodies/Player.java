package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.equip.misc.MomentumStopper;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.ai.PlayerTrail;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.SteeringUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * The player is the entity that the player controls.
 * @author Zachary Tu
 *
 */
public class Player extends Schmuck implements Location<Vector2>{
	

	//player stats
	private final static int playerWidth = 16;
	private final static int playerHeight = 32;
	
	//counters for various cooldowns.
	private float hoverCd = 0.08f;
	private float jumpCd = 0.25f;
	private float jumpCdCount = 0;
	
	private float fastFallCd = 0.25f;
	private float fastFallCdCount = 0;
	
	private float airblastCd = 0.25f;
	private float airblastCdCount = 0;
	
	protected float interactCd = 0.15f;
	protected float interactCdCount = 0;
	
	protected float momentumCd = 10.0f;
	protected float momentumCdCount = 0;
	
	protected float trailCd = 0.25f;
	protected float trailCdCount = 0;
	protected PlayerTrail lastTrail;
	
	//is the player currently in the process of holding their currently used tool?
	private boolean charging = false;
	
	//user data
	public PlayerBodyData playerData;
	
	//The event that the player last collided with. Used for active events that the player interacts with by pressing 'E'
	public Event currentEvent;
	
	//Equipment that the player has built in to their toolset.
	public MomentumStopper mStop;
	public Airblaster airblast;
	
	//Queue of velocities that the player can manipulate
	public Queue<Vector2> momentums;
	
	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param x: player starting x position.
	 * @param y: player starting x position.
	 */
	public Player(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, playerWidth, playerHeight, x, y);
		mStop = new MomentumStopper(this);
		airblast = new Airblaster(this);
		momentums = new Queue<Vector2>();
	}
	
	/**
	 * Create the player's body and initialize player's user data.
	 */
	public void create() {
		this.playerData = new PlayerBodyData(world, this, state.loadout);
		this.bodyData = playerData;
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				Constants.PLAYER_HITBOX, false, playerData);
        
		super.create();
	}
	
	/**
	 * The player's controller currently polls for input.
	 */
	public void controller(float delta) {
		
		//players default position is standing
		moveState = MoveStates.STAND;
		
		//Determine if the player is in the air or on ground.
		grounded = feetData.getNumContacts() > 0;
		
		//player's jumps are refreshed on the ground
		if (grounded) {
			playerData.extraJumpsUsed = 0;
		}
		
		//Holding 'W' = use jetpack if the player is off ground and lacks extra jumps
		if(Gdx.input.isKeyPressed((Input.Keys.W))) {
			if (!grounded && playerData.extraJumpsUsed == playerData.numExtraJumps) {
				if (jumpCdCount < 0) {
					
					//Player will continuously do small upwards bursts that cost fuel.
					if (playerData.currentFuel >= playerData.hoverCost) {
						playerData.fuelSpend(playerData.hoverCost);
						jumpCdCount = hoverCd;
						push(0, playerData.hoverPow);
					}
				}
			}
		}
		
		//Pressing 'W' = jump.
		if(Gdx.input.isKeyJustPressed((Input.Keys.W))) {
			if (grounded) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					push(0, playerData.jumpPow);
				}
			} else {
				if (playerData.extraJumpsUsed < playerData.numExtraJumps) {
					if (jumpCdCount < 0) {
						jumpCdCount = jumpCd;
						playerData.extraJumpsUsed++;
						push(0, playerData.jumpPow);
					}
				}
			}
        }		
		
		//Holding 'S' = crouch. Currently does nothing but TODO: later should reduce knockback.
		if(Gdx.input.isKeyPressed((Input.Keys.S))) {
			if (grounded) {
				moveState = MoveStates.CROUCH;
			}
			if (feetData.terrain != null) {
				feetData.terrain.eventData.onInteract(this);
			}
		}
		
		//Pressing 'S' in the air does a fastfall
		if(Gdx.input.isKeyJustPressed((Input.Keys.S))) {
			if (!grounded) {
				if (fastFallCdCount < 0) {
					fastFallCdCount = fastFallCd;
					push(0, -playerData.fastFallPow);
				}
			}
		}
		
		//Holding 'A', 'D' = move left/right. Schmuck.controller(delta) will handle the physics for these.
		if(Gdx.input.isKeyPressed((Input.Keys.A))) {
			moveState = MoveStates.MOVE_LEFT;
        }

		if(Gdx.input.isKeyPressed((Input.Keys.D))) {
			moveState = MoveStates.MOVE_RIGHT;
		}
		
		//Pressing 'E' = interact with an event
		if(Gdx.input.isKeyJustPressed((Input.Keys.E))) {
			if (currentEvent != null && interactCdCount < 0) {
				interactCdCount = interactCd;
				currentEvent.eventData.onInteract(this);
			}
		}
		
		//Pressing 'Space' = use momentum.
		if(Gdx.input.isKeyJustPressed((Input.Keys.SPACE))) {
			if (momentums.size == 0) {
				if (momentumCdCount < 0) {
					momentumCdCount = momentumCd;
					useToolStart(delta, mStop, (short) 0, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), false);
				}
			} else {
				body.setLinearVelocity(momentums.removeFirst());
			}
		}
		
		//Pressing 'R' = reload current weapon.
		if(Gdx.input.isKeyJustPressed((Input.Keys.R))) {
			playerData.currentTool.reloading = true;
		}
		
		//Pressing 'Q' = switch to last weapon.
		if(Gdx.input.isKeyJustPressed((Input.Keys.Q))) {
			playerData.switchToLast();
		}
		
		//Pressing '1' ... '0' = switch to weapon slot.
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_1))) {
			playerData.switchWeapon(1);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_2))) {
			playerData.switchWeapon(2);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_3))) {
			playerData.switchWeapon(3);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_4))) {
			playerData.switchWeapon(4);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_5))) {
			playerData.switchWeapon(5);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_6))) {
			playerData.switchWeapon(6);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_7))) {
			playerData.switchWeapon(7);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_8))) {
			playerData.switchWeapon(8);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_9))) {
			playerData.switchWeapon(9);
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.NUM_0))) {
			playerData.switchWeapon(10);
		}
		
		//Clicking left mouse = use tool. charging keeps track of whether button is held.
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			charging = true;
			useToolStart(delta, playerData.currentTool, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), true);
		} else {
			if (charging) {
				useToolRelease(playerData.currentTool, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY());
			}
			charging = false;
		}
		
		//Clicking right mouse = airblast
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			if (airblastCdCount < 0) {
				if (playerData.currentFuel >= playerData.airblastCost) {
					playerData.fuelSpend(playerData.airblastCost);
					airblastCdCount = airblastCd;
					
					useToolStart(delta, airblast, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), false);

				}
			}
		}
		
		//TODO: mouse wheel to scroll through equip?
		
		//process fuel regen
		playerData.fuelGain(playerData.fuelRegen * delta);
		
		//If player is reloading, run the reload method of the current equipment.
		if (playerData.currentTool.reloading) {
			playerData.currentTool.reload(delta);
		}
		
		if (trailCdCount < 0) {
			/*trailCdCount += trailCd;
			PlayerTrail newTrail = new PlayerTrail(state, world, camera, rays, (int)(getBody().getPosition().x * PPM), (int)(getBody().getPosition().y * PPM));
			if (lastTrail != null) {
				lastTrail.setTrail(newTrail);
			}
			lastTrail = newTrail;*/
		}
		
		//process cds
		jumpCdCount-=delta;
		fastFallCdCount-=delta;
		airblastCdCount-=delta;
		interactCdCount-=delta;
		momentumCdCount-=delta;
		trailCdCount-=delta;
		
		super.controller(delta);		
		
	}
	
	public void render(SpriteBatch batch) {
		
	}
	
	public void dispose() {
		state.gameOver();
		super.dispose();
	}
	
	public PlayerBodyData getPlayerData() {
		return playerData;
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public void setOrientation(float orientation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return SteeringUtil.vectorToAngle(vector);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		return SteeringUtil.angleToVector(outVector, angle);
	}

	@Override
	public Location<Vector2> newLocation() {
		System.out.println("newLocation was run?");
		return null;//new Location<Vector2>();
	}
}
