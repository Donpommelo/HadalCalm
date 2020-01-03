package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This is a boss in the game
 * @author Zachary Tu
 *
 */
public class Boss2 extends BossFloating {
				
    private static final float aiAttackCd = 3.0f;
	
	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hbWidth = 161;
	private static final int hbHeight = 250;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 4500;
	private static final int moveSpeed = 20;
	private static final int spinSpeed = 40;
	
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
	private Body link1, link2;
	
	public Boss2(PlayState state, Vector2 startPos, enemyType type, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), type, filter, hp, moveSpeed, spinSpeed, aiAttackCd, spawner, sprite);
	}

	@Override
	public void create() {
		super.create();
		
		link1 = BodyBuilder.createBox(world, startPos, hboxSize, 0, 10, 0, false, false, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, true, bodyData);
		
		link2 = BodyBuilder.createBox(world, startPos, hboxSize, 0, 10, 0, false, false, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, true, bodyData);
		
		RevoluteJointDef joint1 = new RevoluteJointDef();
		joint1.bodyA = body;
		joint1.bodyB = link1;
		joint1.collideConnected = false;
		joint1.localAnchorA.set(0, -width / 2 / 32);
		joint1.localAnchorB.set(0, width / 2 / 32);
		joint1.enableLimit = true;
		joint1.lowerAngle = -0.5f;
		joint1.upperAngle = 0.5f;
		
		world.createJoint(joint1);
		
		RevoluteJointDef joint2 = new RevoluteJointDef();
		joint2.bodyA = link1;
		joint2.bodyB = link2;
		joint2.collideConnected = false;
		joint2.localAnchorA.set(0, -width / 2 / 32);
		joint2.localAnchorB.set(0, width / 2 / 32);
		joint2.enableLimit = true;
		joint2.lowerAngle = -0.5f;
		joint2.upperAngle = 0.5f;
		
		world.createJoint(joint2);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		boolean flip = false;
		double realAngle = getOrientation() % (Math.PI * 2);
		if ((realAngle > Math.PI && realAngle < 2 * Math.PI) || (realAngle < 0 && realAngle > -Math.PI)) {
			flip = true;
		}
		
		batch.draw((TextureRegion) floatingSprite.getKeyFrame(animationTime, true), 
				link1.getPosition().x * PPM - hboxSize.y / 2, 
				(flip ? size.y : 0) + link1.getPosition().y * PPM - hboxSize.x / 2, 
				hboxSize.y / 2, 
				(flip ? -1 : 1) * hboxSize.x / 2,
				size.x, (flip ? -1 : 1) * size.y, 1, 1, 
				(float) Math.toDegrees(link1.getAngle()) - 90);
		
		batch.draw((TextureRegion) floatingSprite.getKeyFrame(animationTime, true), 
				link2.getPosition().x * PPM - hboxSize.y / 2, 
				(flip ? size.y : 0) + link2.getPosition().y * PPM - hboxSize.x / 2, 
				hboxSize.y / 2, 
				(flip ? -1 : 1) * hboxSize.x / 2,
				size.x, (flip ? -1 : 1) * size.y, 1, 1, 
				(float) Math.toDegrees(link2.getAngle()) - 90);

		if (shaderCount > 0) {
			batch.setShader(null);
		}
	}
}
