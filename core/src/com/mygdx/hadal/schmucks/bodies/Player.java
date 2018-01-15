package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.equip.misc.MomentumStopper;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.ai.PlayerTrail;
import com.mygdx.hadal.managers.AssetList;
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
//	private final static int playerWidth = 21;
//	private final static int playerHeight = 42;
	
	private final static float playerDensity = 0.2f;
	
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
	
	private TextureAtlas atlasBody, atlasTool;
	private TextureRegion bodySprite, bodyBackSprite, armSprite, headSprite, gemSprite, gemInactiveSprite, toolSprite;
	
	public static final int hbWidth = 216;
	public static final int hbHeight = 516;
		
	public static final int headConnectX = -26;
	public static final int headConnectY = 330;
	
	public static final int armConnectX = -304;
	public static final int armConnectY = 218;
	
	public static final int armRotateX = 330;
	public static final int armRotateY = 50;
		
	public static final float scale = 0.15f;
	
	public int armWidth, armHeight, headWidth, headHeight, bodyWidth, bodyHeight, bodyBackWidth, bodyBackHeight,
	toolHeight, toolWidth, gemHeight, gemWidth;
	
	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param x: player starting x position.
	 * @param y: player starting x position.
	 */
	public Player(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y, String playerSprite) {
		super(state, world, camera, rays, hbWidth * scale, hbHeight * scale, x, y);
		mStop = new MomentumStopper(this);
		airblast = new Airblaster(this);
		momentums = new Queue<Vector2>();
		
		atlasBody = (TextureAtlas) HadalGame.assetManager.get(playerSprite);
		atlasTool = (TextureAtlas) HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString());
		bodySprite = atlasBody.findRegion("body_stand");		
		bodyBackSprite = atlasBody.findRegion("body_background");
		armSprite = atlasBody.findRegion("arm");
		headSprite = atlasBody.findRegion("head");
		gemSprite = atlasBody.findRegion("gem_active");
		gemInactiveSprite = atlasBody.findRegion("gem_inactive");
		toolSprite = atlasTool.findRegion("default");
		
		this.armWidth = armSprite.getRegionWidth();
		this.armHeight = armSprite.getRegionHeight();
		this.headWidth = headSprite.getRegionWidth();
		this.headHeight = headSprite.getRegionHeight();
		this.bodyWidth = bodySprite.getRegionWidth();
		this.bodyHeight = bodySprite.getRegionHeight();
		this.bodyBackWidth = bodyBackSprite.getRegionWidth();
		this.bodyBackHeight = bodyBackSprite.getRegionHeight();
		this.toolHeight = toolSprite.getRegionHeight();
		this.toolWidth = toolSprite.getRegionWidth();
		this.gemHeight = gemSprite.getRegionHeight();
		this.gemWidth = gemSprite.getRegionWidth();
	}
	
	/**
	 * Create the player's body and initialize player's user data.
	 */
	public void create() {
		this.playerData = new PlayerBodyData(world, this, state.loadout);
		this.bodyData = playerData;
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, playerDensity, 0, false, true, Constants.BIT_PLAYER, 
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
			if (!grounded && playerData.extraJumpsUsed >= playerData.numExtraJumps + (int) playerData.getBonusJumpNum()) {
				if (jumpCdCount < 0) {
					
					//Player will continuously do small upwards bursts that cost fuel.
					if (playerData.currentFuel >= playerData.hoverCost * (1 + playerData.getBonusHoverCost())) {
						playerData.fuelSpend(playerData.hoverCost * (1 + playerData.getBonusHoverCost()));
						jumpCdCount = hoverCd;
						push(0, playerData.hoverPow * (1 + playerData.getBonusHoverPower()));
					}
				}
			}
		}
		
		//Pressing 'W' = jump.
		if(Gdx.input.isKeyJustPressed((Input.Keys.W))) {
			if (grounded) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					push(0, playerData.jumpPow * (1 + playerData.getBonusJumpPower()));
				}
			} else {
				if (playerData.extraJumpsUsed < playerData.numExtraJumps + (int) playerData.getBonusJumpNum()) {
					if (jumpCdCount < 0) {
						jumpCdCount = jumpCd;
						playerData.extraJumpsUsed++;
						push(0, playerData.jumpPow * (1 + playerData.getBonusJumpPower()));
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
					momentumCdCount = momentumCd * (1 + playerData.getBonusMomentumCd());
					useToolStart(delta, mStop, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), false);
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
				if (playerData.currentFuel >= playerData.airblastCost * (1 + playerData.getBonusAirblastCost())) {
					playerData.fuelSpend(playerData.airblastCost * (1 + playerData.getBonusAirblastCost()));
					airblastCdCount = airblastCd;
					
					useToolStart(delta, airblast, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), false);

				}
			}
		}
		
		//TODO: mouse wheel to scroll through equip?
		
		//process fuel regen
		playerData.fuelGain(playerData.getFuelRegen() * delta);
		
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
		batch.setProjectionMatrix(state.sprite.combined);

		Vector3 bodyScreenPosition = new Vector3(
				body.getPosition().x,
				body.getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		float angle = (float)(Math.atan2(
				bodyScreenPosition.y - (Gdx.graphics.getHeight() - Gdx.input.getY()) ,
				bodyScreenPosition.x - Gdx.input.getX()) * 180 / Math.PI);
				
		if (Math.abs(angle) > 90 && !armSprite.isFlipX()) {
			armSprite.flip(true, false);
			bodySprite.flip(true, false);
			bodyBackSprite.flip(true, false);
			headSprite.flip(true, false);
			toolSprite.flip(true, false);
			gemSprite.flip(true, false);
			gemInactiveSprite.flip(true, false);
		}
		
		if (Math.abs(angle) < 90 && armSprite.isFlipX()) {
			armSprite.flip(true, false);
			bodySprite.flip(true, false);
			bodyBackSprite.flip(true, false);
			headSprite.flip(true, false);
			toolSprite.flip(true, false);
			gemSprite.flip(true, false);
			gemInactiveSprite.flip(true, false);
		}
		
		float armConnectXReal = armConnectX;
		float headConnectXReal = headConnectX;
		float armRotateXReal = armRotateX;
		
		if (armSprite.isFlipX()) {
			armConnectXReal = bodyWidth - armWidth - armConnectX;
			headConnectXReal = bodyWidth - headWidth - headConnectX;
			armRotateXReal = armWidth - armRotateX;
			angle = angle + 180;
		}
		
		batch.draw(toolSprite, 
				body.getPosition().x * PPM - hbWidth * scale / 2 + armConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + armConnectY * scale, 
				armRotateXReal * scale , armRotateY * scale,
				toolWidth * scale, toolHeight * scale, 1, 1, angle);
		
		batch.draw(bodyBackSprite, 
				body.getPosition().x * PPM - hbWidth * scale / 2, 
				body.getPosition().y * PPM - hbHeight * scale / 2, 
				0, 0,
				bodyBackWidth * scale, bodyBackHeight * scale, 1, 1, 0);
		
		batch.draw(armSprite, 
				body.getPosition().x * PPM - hbWidth * scale / 2 + armConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + armConnectY * scale, 
				armRotateXReal * scale, armRotateY * scale,
				armWidth * scale, armHeight * scale, 1, 1, angle);
		
		batch.draw(momentumCdCount < 0 ? gemSprite : gemInactiveSprite, 
				body.getPosition().x * PPM - hbWidth * scale / 2 , 
				body.getPosition().y * PPM - hbHeight * scale / 2, 
				0, 0,
				gemWidth * scale, gemHeight * scale, 1, 1, 0);
		
		batch.draw(bodySprite, 
				body.getPosition().x * PPM - hbWidth * scale / 2, 
				body.getPosition().y * PPM - hbHeight * scale / 2, 
				0, 0,
				bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		
		batch.draw(headSprite, 
				body.getPosition().x * PPM - hbWidth * scale / 2 + headConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + headConnectY * scale, 
				0, 0,
				headWidth * scale, headHeight * scale, 1, 1, 0);
	}
	
	public void dispose() {
		super.dispose();
	}
	
	public PlayerBodyData getPlayerData() {
		return playerData;
	}
	
	public void setToolSprite(TextureRegion sprite) {
		toolSprite = sprite;
	}

	public TextureRegion getToolSprite() {
		return toolSprite;
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
