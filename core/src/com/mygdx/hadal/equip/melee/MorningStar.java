package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;

public class MorningStar extends MeleeWeapon {

	private static final float SWING_CD = 0.4f;
	private static final float WINDUP = 0.1f;
	
	private static final Vector2 PROJECTILE_SIZE = new Vector2(75, 75);
	private static final Vector2 CHAIN_SIZE = new Vector2(20, 20);

	private static final float BASE_DAMAGE = 50.0f;
	private static final float KNOCKBACK = 60.0f;
	private static final float FLASH_LIFESPAN = 1.0f;

	private static final float SWING_FORCE = 7500.0f;
	private static final float RANGE = 60.0f;
	private static final float CHAIN_LENGTH = 1.2f;

	private static final int CHAIN_NUM = 4;
	private static final float HOME_POWER = 120.0f;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.P_DEFAULT;

	private static final Sprite CHAIN_SPRITE = Sprite.ORB_ORANGE;
	private static final Sprite PROJ_SPRITE = Sprite.FLAIL;

	//this is the hitbox that this weapon extends
	private Hitbox base, star;

	//is the hitbox active?
	private boolean active;

	public MorningStar(Schmuck user) {
		super(user, SWING_CD, WINDUP, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	private final Vector2 projOffset = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		
		
		//when clicked, we create the flail weapon and move it in the direction of the mouse click
		if (!active) {
			active = true;
			projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(RANGE);
			Hitbox[] hboxes = SyncedAttack.MORNING_STAR.initiateSyncedAttackMulti(state, user, new Vector2(), new Vector2[2], new Vector2[2]);
			base = hboxes[0];
			star = hboxes[1];
		}
		if (star != null) {
			star.applyForceToCenter(projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(SWING_FORCE));
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void unequip(PlayState state) {
		active = false;
		deactivate();
	}

	public static Hitbox[] createMorningStar(PlayState state, Schmuck user) {
		Hitbox[] links = new Hitbox[CHAIN_NUM];
		Hitbox[] hboxes = new Hitbox[2];

		//the base is connected to the player and links to the rest of the flail weapon
		Hitbox base = new Hitbox(state, user.getPixelPosition(), CHAIN_SIZE, 0, new Vector2(), user.getHitboxfilter(),
				true, false, user, CHAIN_SPRITE);
		base.setDensity(1.0f);
		base.makeUnreflectable();
		base.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		base.setSyncedDeleteNoDelay(true);

		base.addStrategy(new HitboxStrategy(state, base, user.getBodyData()) {

			private boolean linked;
			@Override
			public void controller(float delta) {

				if (!linked) {
					if (user.getBody() != null && base.getBody() != null) {
						linked = true;
						RevoluteJointDef joint1 = new RevoluteJointDef();
						joint1.bodyA = user.getBody();
						joint1.bodyB = base.getBody();
						joint1.collideConnected = false;

						joint1.localAnchorA.set(0, 0);
						joint1.localAnchorB.set(CHAIN_LENGTH, 0);

						state.getWorld().createJoint(joint1);
					}
				}
			}

			@Override
			public void die() {
				for (int i = 0; i < CHAIN_NUM; i++) {
					if (links[i] != null) {
						links[i].setLifeSpan(2.0f);
						links[i].addStrategy(new ControllerDefault(state, links[i], user.getBodyData()));
						links[i].addStrategy(new FlashNearDeath(state, links[i], user.getBodyData(), FLASH_LIFESPAN));
					}
				}
				hboxes[1].setLifeSpan(2.0f);
				hboxes[1].addStrategy(new ControllerDefault(state, hboxes[1], user.getBodyData()));
				hboxes[1].addStrategy(new FlashNearDeath(state, hboxes[1], user.getBodyData(), FLASH_LIFESPAN));
				hbox.queueDeletion();
			}
		});

		//create several linked hboxes
		for (int i = 0; i < CHAIN_NUM; i++) {
			final int currentI = i;
			links[i] = new Hitbox(state, user.getPixelPosition(), CHAIN_SIZE, 0, new Vector2(),user.getHitboxfilter(),
					true, false, user, CHAIN_SPRITE);
			links[i].setDensity(1.0f);
			links[i].setSyncDefault(false);
			links[i].makeUnreflectable();
			links[i].setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

			links[i].addStrategy(new HitboxStrategy(state, links[i], user.getBodyData()) {

				private boolean linked;
				@Override
				public void controller(float delta) {
					if (!linked) {
						if (currentI == 0) {
							if (base.getBody() != null && hbox.getBody() != null) {
								linked = true;
								RevoluteJointDef joint1 = new RevoluteJointDef();
								joint1.bodyA = base.getBody();
								joint1.bodyB = hbox.getBody();
								joint1.collideConnected = false;

								joint1.localAnchorA.set(-CHAIN_LENGTH, 0);
								joint1.localAnchorB.set(CHAIN_LENGTH, 0);

								state.getWorld().createJoint(joint1);
							}
						} else {
							if (links[currentI - 1].getBody() != null && hbox.getBody() != null) {
								linked = true;

								RevoluteJointDef joint1 = new RevoluteJointDef();
								joint1.bodyA = links[currentI - 1].getBody();
								joint1.bodyB = hbox.getBody();
								joint1.collideConnected = false;
								joint1.localAnchorA.set(-CHAIN_LENGTH, 0);
								joint1.localAnchorB.set(CHAIN_LENGTH, 0);

								state.getWorld().createJoint(joint1);
							}
						}
					}
				}

				@Override
				public void die() {
					hbox.queueDeletion();
				}
			});

			if (!state.isServer()) {
				((ClientState) state).addEntity(links[i].getEntityID(), links[i], false, ClientState.ObjectLayer.HBOX);
			}
		}

		//the star hbox damages people and has weight
		Hitbox star = new RangedHitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, 0, new Vector2(),
				user.getHitboxfilter(), false, true, user, PROJ_SPRITE);

		star.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		star.setGravity(1.0f);
		star.setDensity(0.1f);
		star.makeUnreflectable();

		star.addStrategy(new DamageStandard(state, star, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.MORNING_STAR,
				DamageTag.WHACKING, DamageTag.MELEE).setRepeatable(true));
		star.addStrategy(new ContactWallSound(state, star, user.getBodyData(), SoundEffect.WALL_HIT1, 0.25f));
		star.addStrategy(new ContactUnitSound(state, star, user.getBodyData(), SoundEffect.SLAP, 0.25f, true).setPitch(0.5f));
		star.addStrategy(new HomingMouse(state, star, user.getBodyData(), HOME_POWER));

		star.addStrategy(new HitboxStrategy(state, star, user.getBodyData()) {

			private boolean linked;
			@Override
			public void controller(float delta) {

				if (!linked) {
					if (links[CHAIN_NUM - 1].getBody() != null && hbox.getBody() != null) {
						linked = true;

						RevoluteJointDef joint1 = new RevoluteJointDef();
						joint1.bodyA = links[CHAIN_NUM - 1].getBody();
						joint1.bodyB = hbox.getBody();
						joint1.collideConnected = false;
						joint1.localAnchorA.set(-CHAIN_LENGTH, 0);
						joint1.localAnchorB.set(CHAIN_LENGTH, 0);

						state.getWorld().createJoint(joint1);
					}
				}
			}

			@Override
			public void die() {
				hbox.queueDeletion();
			}
		});
		hboxes[0] = base;
		hboxes[1] = star;
		return hboxes;
	}

	/**
	 * upon deactivation, we delete the base hbox and make the others have a temporary lifespan
	 * this is so that the user can fling the flail by switching to another weapon
	 */
	private void deactivate() {

		active = false;
		
		if (base != null) {
			base.die();
		}
	}

	@Override
	public float getBotRangeMax() { return 5 * CHAIN_LENGTH + 2; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE)};
	}
}
