package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.DestructableBlock;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.EventUtils;
import com.mygdx.hadal.schmucks.entities.ClientIllusion;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Nuberry Nolpgins
 */
public class Terraformer extends ActiveItem {

	private static final float USECD = 0.0f;
	private static final float USEDELAY = 0.0f;
	private static final float MAX_CHARGE = 8.0f;
	
	private static final Vector2 BLOCK_SIZE = new Vector2(64, 192);
	private static final int BLOCK_HP = 250;
	private static final float BLOCK_SPEED = 8.0f;

	public Terraformer(Schmuck user) {
		super(user, USECD, USEDELAY, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		SoundEffect.MAGIC1_ACTIVE.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);
		
		Event block = new DestructableBlock(state, user.getPlayer().getProjectileOrigin(weaponVelo, BLOCK_SIZE.x), BLOCK_SIZE, BLOCK_HP, false) {
			
			@Override
			public void create() {
				super.create();
				body.setLinearVelocity(new Vector2(weaponVelo).nor().scl(BLOCK_SPEED));
				EventUtils.addFeetFixture(this);
			}
		};
		block.setEventSprite(Sprite.UI_MAIN_HEALTH_MISSING);
		block.setScaleAlign(ClientIllusion.alignType.CENTER_STRETCH);
		block.setStandardParticle(Particle.IMPACT);
		block.setGravity(1.0f);
		block.setSynced(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(BLOCK_HP)};
	}
}
