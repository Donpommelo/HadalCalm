package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import box2dLight.RayHandler;

public class Player extends Schmuck {

	private MoveStates moveState;
	
	//Fixtures and user data
	private Fixture feet;
	private HadalData feetData;
	
	private int playerWidth = 16;
	private int playerHeight = 32;
	
	private int hoverCd = 5;
	private int jumpCd = 15;
	private int jumpCdCount = 0;
	
	private int fastFallCd = 15;
	private int fastFallCdCount = 0;
	
	private int airblastCd = 15;
	private int airblastCdCount = 0;
	
	private int shootCdCount = 0;
	
	public PlayerBodyData playerData;
	
	public Player(PlayState state, World world, OrthographicCamera camera, RayHandler rays) {
		super(state, world, camera, rays);
		state.create(this);
	}
	
	public void create() {
		this.playerData = new PlayerBodyData(world, this);
		this.feetData = new FeetData(world);
		
		this.body = BodyBuilder.createBox(world, 300, 300, playerWidth, playerHeight, 1, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				Constants.PLAYER_HITBOX, playerData);
        
		this.feet = this.body.createFixture(FixtureBuilder.createFixtureDef(playerWidth, playerHeight / 8, new Vector2(0, -0.5f), true, 0,
				Constants.BIT_SENSOR, Constants.BIT_WALL, Constants.PLAYER_HITBOX));
		
		feet.setUserData(feetData);
	}
	
	public void controller(float delta) {
		
		moveState = MoveStates.STAND;
		
		boolean grounded = feetData.getNumContacts() > 0;
		
		if (grounded) {
			playerData.extraJumpsUsed = 0;
		}
		
		if(Gdx.input.isKeyPressed((Input.Keys.W))) {
			if (!grounded && playerData.extraJumpsUsed == playerData.numExtraJumps) {
				if (jumpCdCount < 0) {
//					moveState = MoveStates.HOVER;
					if (playerData.currentFuel >= playerData.hoverCost) {
						playerData.currentFuel -= playerData.hoverCost;
						jumpCdCount = hoverCd;
						jump(playerData.hoverPow);
					}
				}
			}
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.W))) {
			if (grounded) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					jump(playerData.jumpPow);
				}
			} else {
				if (playerData.extraJumpsUsed < playerData.numExtraJumps) {
					if (jumpCdCount < 0) {
						jumpCdCount = jumpCd;
						playerData.extraJumpsUsed++;
						jump(playerData.extraJumpPow);
					}
				}
			}
        }		
		
		if(Gdx.input.isKeyPressed((Input.Keys.S))) {
			if (grounded) {
				moveState = MoveStates.CROUCH;
			}
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.S))) {
			if (!grounded) {
				if (fastFallCdCount < 0) {
					fastFallCdCount = fastFallCd;
					fastFall(playerData.fastFallPow);
				}
			}
		}
		
		if(Gdx.input.isKeyPressed((Input.Keys.A))) {
			moveState = MoveStates.MOVE_LEFT;
        }

		if(Gdx.input.isKeyPressed((Input.Keys.D))) {
			moveState = MoveStates.MOVE_RIGHT;
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.SPACE))) {
			//save momentum
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.R))) {
			playerData.currentTool.reloading = true;
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.Q))) {
			playerData.switchToLast();
		}
		
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
		
		
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			if (shootCdCount < 0) {
				shootCdCount = playerData.currentTool.useCd;
				playerData.currentTool.mouseClicked(state, playerData, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), world, camera, rays);
			}
		}
		
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			if (airblastCdCount < 0) {
				if (playerData.currentFuel >= playerData.airblastCost) {
					playerData.currentFuel -= playerData.airblastCost;
					airblastCdCount = airblastCd;
					recoil(Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), playerData.airblastPow);
				}
			}
		}
		
		Vector2 currentVel = body.getLinearVelocity();
		float desiredXVel = 0.0f;
		float desiredYVel = 0.0f;
		switch(moveState) {
		case MOVE_LEFT:
			desiredXVel = grounded ? -playerData.maxGroundXSpeed : -playerData.maxAirXSpeed;
			break;
		case MOVE_RIGHT:
			desiredXVel = grounded ? playerData.maxGroundXSpeed : playerData.maxAirXSpeed;
			break;
		default:
			break;
		}
		
		float accelX = 0.0f;
		float accelY = 0.0f;
		
		if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
			accelX = grounded ? playerData.groundXAccel : playerData.airXAccel;
		} else {
			accelX = grounded ? playerData.groundXDeaccel : playerData.airXDeaccel;
		}
		
		float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
		
		if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
			accelY = grounded ? playerData.groundYAccel : playerData.airYAccel;
		} else {
			accelY = grounded ? playerData.groundYDeaccel : playerData.airYDeaccel;
		}
		
		float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;
		
		body.setLinearVelocity(newX, newY);
		

		
		if (playerData.currentFuel + playerData.fuelRegen > playerData.maxFuel) {
			playerData.currentFuel = playerData.maxFuel;
		} else {
			playerData.currentFuel += playerData.fuelRegen;
		}
		
		if (playerData.currentTool.reloading) {
			playerData.currentTool.reload();
		}
		
		jumpCdCount--;
		fastFallCdCount--;
		airblastCdCount--;
		shootCdCount--;
		
	}
	
	

	public void jump(float impulse) {
		body.applyLinearImpulse(new Vector2(0, impulse), body.getWorldCenter(), true);
	}
	
	public void fastFall(float impulse) {
		body.applyLinearImpulse(new Vector2(0, -impulse), body.getWorldCenter(), true);
	}
	
	public void render(SpriteBatch batch) {
		
	}
	
	public void dispose() {
		super.dispose();
	}
	
	public PlayerBodyData getPlayerData() {
		return playerData;
	}
}
