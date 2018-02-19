package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;
import static com.mygdx.hadal.utils.Constants.PPM;



import box2dLight.RayHandler;

/**
 * Melee weapons are weapons that create MeleeHitboxes that are attached to the user.
 * @author Zachary Tu
 *
 */
public class MeleeWeapon extends Equipable {

	public float momentum;
	
	public HitboxFactory onSwing;

	public Vector2 velo;
	public int x, y;
	public short faction;
	
	/**
	 * 
	 * @param user: Schmuck that is using this tool.
	 * @param name: Name of the weapon
	 * @param swingcd: The delay after using this tool before you can use a tool again.
	 * @param windup: The delay between pressing the button for this tool and it activating. 
	 * @param momentum: reverse recoil. Using a melee wepon will inch the user forwards by a force of this magnitude.
	 * @param onSwing: This is a factory that creates a melee hitbox.
	 */
	public MeleeWeapon(Schmuck user, String name, float swingcd, float windup, float momentum,
			HitboxFactory onSwing) {
		super(user, name, swingcd, windup);
		this.momentum = momentum;
		this.onSwing = onSwing;
	}
	
	public MeleeWeapon(Schmuck user, String name, float swingcd, float windup, float momentum,
			HitboxFactory onSwing, String spriteId) {
		super(user, name, swingcd, windup, spriteId);
		this.momentum = momentum;
		this.onSwing = onSwing;
	}

	/**
	 * This method is called when a schmuck targets a point with this weapon.
	 * The weapon is not fired yet. Instead, a vector keeping track of the target is set.
	 */
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y, World world,
			OrthographicCamera camera, RayHandler rays) {

		Vector3 bodyScreenPosition = new Vector3(
				shooter.getSchmuck().getBody().getPosition().x, 
				shooter.getSchmuck().getBody().getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		float powerDiv = bodyScreenPosition.dst(x, y, 0);
		
		float xImpulse = -(bodyScreenPosition.x - x) / powerDiv;
		float yImpulse = -(bodyScreenPosition.y - y) / powerDiv;

		this.velo = new Vector2(xImpulse, yImpulse);
		this.faction = faction;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * This is run after the weapon's swingDelay to actually swing.
	 * Here, the stored velo, recoil, filter are used to generate a melee hitbox
	 */
	@Override
	public void execute(PlayState state, BodyData shooter, World world, OrthographicCamera camera, RayHandler rays) {
		onSwing.makeHitbox(user, state, velo, 
				shooter.getSchmuck().getBody().getPosition().x * PPM, 
				shooter.getSchmuck().getBody().getPosition().y * PPM, 
				faction, world, camera, rays);
		
		user.recoil(x, y, -momentum * (1 + shooter.getMeleeMomentum()));

	}
	
	/**
	 * Default behaviour for releasing mouse is nothing.
	 * Override this in charge weapons or other weapons that care about mouse release.
	 */
	@Override
	public void release(PlayState state, BodyData bodyData, World world, OrthographicCamera camera, RayHandler rays) {}

	/**
	 * Default behaviour for reloading is nothing.
	 * Override this for special weapon arts or whatever.
	 */
	@Override
	public void reload(float delta) {}

	/**
	 * returns the weapon name
	 */
	@Override
	public String getText() {
		return "";
	}
	
	@Override
	public float getUseCd() {
		return useCd * (1 - user.getBodyData().getMeleeSwingRate());
	}
	
	@Override
	public float getUseDelay() {
		return useDelay * (1 - user.getBodyData().getMeleeSwingDelay());
	}
}
