package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Player extends Schmuck {
	
	//Fixtures and user data
	
	private final static int playerWidth = 16;
	private final static int playerHeight = 32;
	
	private int hoverCd = 5;
	private int jumpCd = 15;
	private int jumpCdCount = 0;
	
	private int fastFallCd = 15;
	private int fastFallCdCount = 0;
	
	private int airblastCd = 15;
	private int airblastCdCount = 0;
	
	private boolean charging = false;
	
	public PlayerBodyData playerData;
	
	public Player(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, playerWidth, playerHeight, x, y);
		state.create(this);
	}
	
	public void create() {
		this.playerData = new PlayerBodyData(world, this);
		this.bodyData = playerData;
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				Constants.PLAYER_HITBOX, false, playerData);
        
		super.create();
	}
	
	public void controller(float delta) {
		
		moveState = MoveStates.STAND;
		
		grounded = feetData.getNumContacts() > 0;
		
		if (grounded) {
			playerData.extraJumpsUsed = 0;
		}
		
		if(Gdx.input.isKeyPressed((Input.Keys.W))) {
			if (!grounded && playerData.extraJumpsUsed == playerData.numExtraJumps) {
				if (jumpCdCount < 0) {
					if (playerData.currentFuel >= playerData.hoverCost) {
						playerData.fuelSpend(playerData.hoverCost);
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
		
		if(Gdx.input.isKeyPressed((Input.Keys.E))) {
			//TODO: interact with object
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.SPACE))) {
			//TODO: save momentum
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
			charging = true;
			useToolStart(playerData.currentTool, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY());
		} else {
			if (charging) {
				useToolRelease(playerData.currentTool, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY());
			}
			charging = false;
		}
		
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			if (airblastCdCount < 0) {
				if (playerData.currentFuel >= playerData.airblastCost) {
					playerData.fuelSpend(playerData.airblastCost);
					airblastCdCount = airblastCd;
					
					recoil(Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), playerData.airblastPow);
				}
			}
		}
		
		
		playerData.fuelGain(playerData.fuelRegen);
		
		if (playerData.currentTool.reloading) {
			playerData.currentTool.reload();
		}
		
		jumpCdCount--;
		fastFallCdCount--;
		airblastCdCount--;
		
		super.controller(delta);		
		
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
