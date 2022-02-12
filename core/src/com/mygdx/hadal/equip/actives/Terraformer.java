package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.DestructableBlock;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * @author Nuberry Nolpgins
 */
public class Terraformer extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 8.0f;
	
	private static final Vector2 blockSize = new Vector2(128, 128);
	private static final int blockHp = 200;
	private static final float blockSpeed = 14.0f;

	public Terraformer(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		SoundEffect.MAGIC1_ACTIVE.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);
		
		Event block = new DestructableBlock(state, user.getPlayer().getProjectileOrigin(weaponVelo, blockSize.x), blockSize, blockHp, false) {
			
			@Override
			public void create() {
				super.create();
				body.setLinearVelocity(new Vector2(weaponVelo).nor().scl(blockSpeed));
				
				FeetData feetData = new FeetData(UserDataType.FEET, this);
				Fixture feet = FixtureBuilder.createFixtureDef(body, new Vector2(0.5f,  - getSize().y / 2), new Vector2(getSize().x, getSize().y / 8), true, 0, 0, 0, 0,
						Constants.BIT_SENSOR, Constants.BIT_DROPTHROUGHWALL, (short) 0);
				feet.setUserData(feetData);
			}
		};
		block.setEventSprite(Sprite.UI_MAIN_HEALTH_MISSING);
		block.setScaleAlign("CENTER_STRETCH");
		block.setStandardParticle(Particle.IMPACT);
		block.setGravity(1.0f);
		block.setSynced(true);
	}
}
