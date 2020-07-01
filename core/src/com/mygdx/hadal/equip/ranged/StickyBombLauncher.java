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
import com.mygdx.hadal.strategies.hitbox.ContactStick;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DieExplode;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class StickyBombLauncher extends RangedWeapon {

	private final static int clipSize = 6;
	private final static int ammoSize = 30;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.25f;
	private final static int reloadAmount = 0;
	private final static float recoil = 2.0f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static Vector2 stickySize = new Vector2(20, 20);
	private final static float lifespan = 5.0f;
	
	private final static int explosionRadius = 200;
	private final static float explosionDamage = 55.0f;
	private final static float explosionKnockback = 25.0f;	
	
	private final static Sprite projSprite = Sprite.STICKYBOMB;
	private final static Sprite weaponSprite = Sprite.MT_STICKYBOMB;
	private final static Sprite eventSprite = Sprite.P_STICKYBOMB;
	
	//list of hitboxes created
	private Queue<Hitbox> bombsLaid = new Queue<Hitbox>();

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
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		SoundEffect.LAUNCHER.playUniversal(state, startPosition, 1.0f, false);

		Hitbox hboxSticky = new RangedHitbox(state, startPosition, stickySize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		hboxSticky.setGravity(1.0f);

		hboxSticky.addStrategy(new ControllerDefault(state, hboxSticky, user.getBodyData()));
		hboxSticky.addStrategy(new ContactWallSound(state, hboxSticky, user.getBodyData(), SoundEffect.SQUISH, 1.0f));
		hboxSticky.addStrategy(new ContactUnitSound(state, hboxSticky, user.getBodyData(), SoundEffect.SQUISH, 1.0f, false));
		hboxSticky.addStrategy(new ContactStick(state, hboxSticky, user.getBodyData(), true, true));
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), hboxSticky, new Vector2(), new Vector2(), false));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.25f));
		
		bombsLaid.addLast(hboxSticky);
	}
}
