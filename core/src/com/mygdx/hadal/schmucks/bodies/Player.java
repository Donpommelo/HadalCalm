package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;


public class Player extends Schmuck {

	private MoveStates moveState;
	
	//Fixtures and user data
	private Fixture arm, feet;
	private HadalData armData, feetData;
	
	private PolygonShape pShape;
	private FixtureDef fixtureDef;
	
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
		
	public Player(PlayState state, World world, OrthographicCamera camera, RayHandler rays) {
		super(state, world, camera, rays);
		
		this.body = BodyBuilder.createBox(world, 300, 300, playerWidth, playerHeight, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				Constants.PLAYER_HITBOX);
				
		this.fixtureDef = new FixtureDef();
		PolygonShape pShape = new PolygonShape();
		fixtureDef.shape = pShape;
		pShape.setAsBox(playerWidth / PPM / 4, playerHeight / PPM / 8, new Vector2(0, -0.5f), 0);
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = Constants.BIT_SENSOR;
        fixtureDef.filter.maskBits = Constants.BIT_WALL;
        fixtureDef.filter.groupIndex = Constants.PLAYER_HITBOX;
        
		this.feet = this.body.createFixture(fixtureDef);
		
		this.bodyData = new BodyData(world, this);
		this.feetData = new FeetData(world);
		
		feet.setUserData(feetData);
		body.getFixtureList().get(0).setUserData(bodyData);
	}
	
	public void create() {
		
	}
	
	public void controller(float delta) {
		
		moveState = MoveStates.STAND;
		
		boolean grounded = feetData.getNumContacts() > 0;
		
		if (grounded) {
			bodyData.extraJumpsUsed = 0;
		}
		
		if(Gdx.input.isKeyPressed((Input.Keys.W))) {
			if (!grounded && bodyData.extraJumpsUsed == bodyData.numExtraJumps) {
				if (jumpCdCount < 0) {
//					moveState = MoveStates.HOVER;
					if (bodyData.currentFuel >= bodyData.hoverCost) {
						bodyData.currentFuel -= bodyData.hoverCost;
						jumpCdCount = hoverCd;
						jump(bodyData.hoverPow);
					}
				}
			}
		}
		
		if(Gdx.input.isKeyJustPressed((Input.Keys.W))) {
			if (grounded) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					jump(bodyData.jumpPow);
				}
			} else {
				if (bodyData.extraJumpsUsed < bodyData.numExtraJumps) {
					if (jumpCdCount < 0) {
						jumpCdCount = jumpCd;
						bodyData.extraJumpsUsed++;
						jump(bodyData.extraJumpPow);
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
					fastFall(bodyData.fastFallPow);
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
		
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			if (shootCdCount < 0) {
				shootCdCount = bodyData.currentTool.useCd;
				bodyData.currentTool.mouseClicked(state, bodyData, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), world, camera, rays);
			}

		}
		
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			if (airblastCdCount < 0) {
				if (bodyData.currentFuel >= bodyData.airblastCost) {
					bodyData.currentFuel -= bodyData.airblastCost;
					airblastCdCount = airblastCd;
					airblast(Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY());
				}
			}
		}
		
		Vector2 currentVel = body.getLinearVelocity();
		float desiredXVel = 0.0f;
		switch(moveState) {
		case MOVE_LEFT:
			desiredXVel = grounded ? -bodyData.maxGroundXSpeed : -bodyData.maxAirXSpeed;
			break;
		case MOVE_RIGHT:
			desiredXVel = grounded ? bodyData.maxGroundXSpeed : bodyData.maxAirXSpeed;
			break;
		default:
			break;
		}
		
		float accel = 0.0f;
		
		if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
			accel = grounded ? bodyData.groundXAccel : bodyData.airXAccel;
		} else {
			accel = grounded ? bodyData.groundXDeaccel : bodyData.airXDeaccel;
		}
		
		float newX = accel * desiredXVel + (1 - accel) * currentVel.x;
		
		body.setLinearVelocity(newX, currentVel.y);
		
//		float velChange = desiredXVel - currentVel.x;
//		float impulse = body.getMass() * velChange;
//		body.applyLinearImpulse(new Vector2(impulse, 0), body.getWorldCenter(), true);
		
		if (bodyData.currentFuel + bodyData.fuelRegen > bodyData.maxFuel) {
			bodyData.currentFuel = bodyData.maxFuel;
		} else {
			bodyData.currentFuel += bodyData.fuelRegen;
		}
		
		jumpCdCount--;
		fastFallCdCount--;
		airblastCdCount--;
		shootCdCount--;
	}
	
	private void airblast(int x, int y) {
		
		Vector3 bodyScreenPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		float powerDiv = bodyScreenPosition.dst(x, y, 0) / bodyData.airblastPow;
		
		float xImpulse = (bodyScreenPosition.x - x) / powerDiv;
		float yImpulse = (bodyScreenPosition.y - y) / powerDiv;
		
		body.applyLinearImpulse(new Vector2(xImpulse, yImpulse), body.getWorldCenter(), true);
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
	
	
}
