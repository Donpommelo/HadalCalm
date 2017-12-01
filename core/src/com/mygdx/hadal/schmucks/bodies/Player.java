package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.schmucks.userdata.PlayerSchmuck;
import com.mygdx.hadal.schmucks.userdata.FeetSchmuck;
import com.mygdx.hadal.schmucks.userdata.HadalSchmuck;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;


public class Player{

	private Body body;
	private PointLight light;
	private MoveStates moveState;
	
	private Fixture arm, feet;
	private HadalSchmuck armData, feetData;
	private PlayerSchmuck bodyData;
	
	private PolygonShape pShape;
	private FixtureDef fixtureDef;
	
	private int playerWidth = 16;
	private int playerHeight = 32;
	
	private int hoverCd = 5;
	private int jumpCd = 15;
	private int jumpCdCount = 0;
	
	private int fastFallCd = 15;
	private int fastFallCdCount = 0;
		
	public Player(World world, RayHandler rays) {
		this.body = BodyBuilder.createBox(world, 300, 300, playerWidth, playerHeight, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR), (short) 0.1f);
				
		this.fixtureDef = new FixtureDef();
		PolygonShape pShape = new PolygonShape();
		fixtureDef.shape = pShape;
		pShape.setAsBox(playerWidth / PPM / 4, playerHeight / PPM / 8, new Vector2(0, -0.5f), 0);
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = Constants.BIT_SENSOR;
        fixtureDef.filter.maskBits = Constants.BIT_WALL;
        
		this.feet = this.body.createFixture(fixtureDef);
		
		
		this.bodyData = new PlayerSchmuck(world);
		this.feetData = new FeetSchmuck(world);
		
		feet.setUserData(feetData);
		body.setUserData(bodyData);
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
		
		jumpCdCount--;
		fastFallCdCount--;
		
		if (bodyData.currentFuel + bodyData.fuelRegen > bodyData.maxFuel) {
			bodyData.currentFuel = bodyData.maxFuel;
		} else {
			bodyData.currentFuel += bodyData.fuelRegen;
		}
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
		
	}
	
	public Vector2 getPosition() {
        return body.getPosition();
    }
	
	public PlayerSchmuck getPlayerData() {
		return bodyData;
	}
}
