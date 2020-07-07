package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.DestructableBlock;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Terraformer extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 10.0f;
	
	private final static Vector2 blockSize = new Vector2(96, 96);
	private final static int blockHp = 200;
	private final static float blockSpeed = 10.0f;

	public Terraformer(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, final PlayerBodyData user) {	
		SoundEffect.MAGIC1_ACTIVE.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);
		
		Event block = new DestructableBlock(state, user.getPlayer().getProjectileOrigin(weaponVelo, blockSize.x), blockSize, blockHp, false) {
			
			@Override
			public void create() {
				super.create();
				body.setLinearVelocity(new Vector2(weaponVelo).nor().scl(blockSpeed));
			}
		};
		block.setEventSprite(Sprite.UI_MAIN_HEALTH_MISSING);
		block.setScaleAlign("CENTER_STRETCH");
		block.setStandardParticle(Particle.IMPACT);
		block.setGravity(1.0f);
		block.setSynced(true);
	}
}
