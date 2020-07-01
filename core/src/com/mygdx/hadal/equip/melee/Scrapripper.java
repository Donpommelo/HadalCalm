package com.mygdx.hadal.equip.melee;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;

public class Scrapripper extends MeleeWeapon {

	private final static float shootCd = 0.5f;
	private final static float shootDelay = 0.0f;
	private final static float baseDamage = 60.0f;
	private final static Vector2 hitboxSize = new Vector2(200, 120);
	private final static float knockback = 25.0f;
	private final static float lifespan = 0.25f;
	
	private final static Sprite weaponSprite = Sprite.MT_SCRAPRIPPER;
	private final static Sprite eventSprite = Sprite.P_SCRAPRIPPER;

	public Scrapripper(Schmuck user) {
		super(user, shootCd, shootDelay, weaponSprite, eventSprite);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.WOOSH.playUniversal(state, startPosition, 1.0f, false);

		Hitbox hbox = new Hitbox(state, startPosition, hitboxSize, lifespan, new Vector2(), filter, true, true, user, Sprite.IMPACT);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStatic(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.MELEE));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), startVelocity, startVelocity.nor().scl(hitboxSize.x / 2 / PPM), false));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLASH, 1.0f, true));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EXPLOSION, 0.0f, 0.2f));
	}
}
