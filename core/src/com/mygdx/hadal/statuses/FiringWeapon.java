package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This status makes the player fire their weapon at the mouse for a brief period of time.
 * This is used by "spray" type weapons that, when fired, spray a stream of hboxes for a brief period of time
 * @author Zachary Tu
 *
 */
public class FiringWeapon extends Status {

	//these keep track of interval until next hbox is spawned
	private float procCdCount;
	private float procCd;
	
	//The velocity of newly created hboxes
	private float currentVelo;
	
	//Themin value of velocity of hboxes and the rate that the velocity decreases to this min value
	private float minVelo;
	private float veloDeprec;
	
	//size of projectile (used to determine the projectile spawn origin)
	private float projSize;
	private Vector2 projOrigin = new Vector2();
	private Vector2 projVelo = new Vector2();
	
	//tool used to fire this status
	private Equipable tool;
	
	public FiringWeapon(PlayState state, float i, BodyData p, BodyData v, float projVelo, float minVelo, float veloDeprec, float projSize, float procCd, Equipable tool) {
		super(state, i, false, p, v);
		this.minVelo = minVelo;
		this.veloDeprec = veloDeprec;
		this.projSize = projSize;
		this.procCd = procCd;
		
		this.currentVelo = projVelo;
		this.tool = tool;
	}
	
	@Override
	public void timePassing(float delta) {
		
		super.timePassing(delta);
		
		//if switching away from the spraying weapon, the spray ends
		if (!inflicted.getCurrentTool().equals(tool)) {
			return;
		}
		
		//when it activates, this status sets the tool's weaponVelo field and then fires it 
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			
			if (currentVelo > minVelo) {
				currentVelo -= veloDeprec;
			}
			projVelo.set(((Player) inflicted.getSchmuck()).getMouse().getPixelPosition()).sub(inflicted.getSchmuck().getPixelPosition());
			inflicted.getCurrentTool().setWeaponVelo(projVelo.nor().scl(currentVelo));
			
			projOrigin = inflicted.getSchmuck().getProjectileOrigin(inflicted.getCurrentTool().getWeaponVelo(), projSize);
			
			inflicted.statusProcTime(new ProcTime.Shoot(inflicted.getCurrentTool()));
			
			inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), projOrigin, inflicted.getCurrentTool().getWeaponVelo(), inflicted.getSchmuck().getHitboxfilter());
		}
		
		procCdCount += delta;
	}
}
