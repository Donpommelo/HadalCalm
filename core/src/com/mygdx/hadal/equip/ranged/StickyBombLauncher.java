package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.*;

public class StickyBombLauncher extends RangedWeapon {

	private static final int clipSize = 6;
	private static final int ammoSize = 36;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.25f;
	private static final int reloadAmount = 0;
	private static final float recoil = 2.0f;
	private static final float projectileSpeed = 40.0f;
	private static final Vector2 projectileSize = new Vector2(50, 50);
	private static final Vector2 stickySize = new Vector2(20, 20);
	private static final float lifespan = 5.0f;
	
	private static final int explosionRadius = 200;
	private static final float explosionDamage = 50.0f;
	private static final float explosionKnockback = 25.0f;	
	
	private static final Sprite projSprite = Sprite.STICKYBOMB;
	private static final Sprite weaponSprite = Sprite.MT_STICKYBOMB;
	private static final Sprite eventSprite = Sprite.P_STICKYBOMB;
	
	//list of hitboxes created
	private final Queue<Hitbox> bombsLaid = new Queue<>();

	public StickyBombLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, false, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public boolean reload(float delta) {
		
		//upon reload, detonate all laid bombs
		for (Hitbox bomb : bombsLaid) {
			if (bomb.isAlive()) {
				bomb.die();
			}
		}
		bombsLaid.clear();
		
		return super.reload(delta);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LAUNCHER.playUniversal(state, startPosition, 0.25f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, stickySize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setSpriteSize(projectileSize);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.25f));
		hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), true, true));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f));
		
		bombsLaid.addLast(hbox);
	}
}
