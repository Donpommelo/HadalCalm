package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ContactStick;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DieExplode;

public class StickyBombLauncher extends RangedWeapon {

	private final static int clipSize = 6;
	private final static int ammoSize = 30;
	private final static float shootCd = 0.25f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.25f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(25, 25);
	private final static float lifespan = 5.0f;
	
	private final static int explosionRadius = 175;
	private final static float explosionDamage = 50.0f;
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
	public void reload(float delta) {
		
		//upon reload, detonate all laid bombs
		for (Hitbox bomb : bombsLaid) {
			if (bomb.isAlive()) {
				bomb.die();
			}
		}
		bombsLaid.clear();
		
		super.reload(delta);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short)0));
		hbox.addStrategy(new ContactStick(state, hbox, user.getBodyData(), true, true));
		
		bombsLaid.addLast(hbox);
	}
}
