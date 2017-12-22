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
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import box2dLight.RayHandler;

public class Schmuck extends HadalEntity {

	protected MoveStates moveState;
	
	//Fixtures and user data
	protected Fixture feet;
	protected HadalData feetData;
	
	protected BodyData bodyData;
	protected boolean grounded;
	
	public float shootCdCount = 0;
	public float shootDelayCount = 0;
	public Equipable usedTool;
	
	public Schmuck(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float w, float h,
			float startX, float startY) {
		super(state, world, camera, rays, w, h, startX, startY);
		this.grounded = false;
		state.create(this);
	}

	@Override
	public void create() {
		this.feetData = new HadalData(world, UserDataTypes.FEET, this);        
		this.feet = this.body.createFixture(FixtureBuilder.createFixtureDef(width, height / 8, new Vector2(0, -0.5f), true, 0,
				Constants.BIT_SENSOR, Constants.BIT_WALL, Constants.PLAYER_HITBOX));
		
		feet.setUserData(feetData);
		
		this.hadalData = bodyData;
	}

	@Override
	public void controller(float delta) {
		Vector2 currentVel = body.getLinearVelocity();
		float desiredXVel = 0.0f;
		float desiredYVel = 0.0f;
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
		
		float accelX = 0.0f;
		float accelY = 0.0f;
		
		if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
			accelX = grounded ? bodyData.groundXAccel : bodyData.airXAccel;
		} else {
			accelX = grounded ? bodyData.groundXDeaccel : bodyData.airXDeaccel;
		}
		
		float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
		
		if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
			accelY = grounded ? bodyData.groundYAccel : bodyData.airYAccel;
		} else {
			accelY = grounded ? bodyData.groundYDeaccel : bodyData.airYDeaccel;
		}
		
		float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;
		
		Vector2 force = new Vector2(newX - currentVel.x, newY - currentVel.y).scl(body.getMass());
		body.applyLinearImpulse(force, body.getWorldCenter(), true);

		bodyData.regainHp(bodyData.hpRegen * delta);
		
		shootCdCount-=delta;
		shootDelayCount-=delta;
		
		if (shootDelayCount <= 0 && usedTool != null) {
			useToolEnd();
		}
		
	}

	@Override
	public void render(SpriteBatch batch) {
		
	}

	public void useToolStart(float delta, Equipable tool, short hitbox, int x, int y, boolean wait) {
		if ((shootCdCount < 0 && shootDelayCount < 0) || !wait) {
			if (!tool.charging()) {
				shootDelayCount = tool.useDelay;
			} else {
				tool.charge(delta, state, bodyData, hitbox, y, y, world, camera, rays);
			}
			tool.mouseClicked(state, bodyData, hitbox, x, y, world, camera, rays);
			usedTool = tool;
		}
	}
	
	public void useToolEnd() {
		if (!usedTool.charging()) {
			shootCdCount = usedTool.useCd;
			usedTool.execute(state, bodyData, world, camera, rays);
			usedTool = null;
		}
	}
	
	public void useToolRelease(Equipable tool, short hitbox, int x, int y) {
		tool.release(state, bodyData, world, camera, rays);
	}
	
	
}
