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

	private static String name = "Firing Weapon";
	private static String descr = "Firing";
	
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
	
	//tool used to fire this status
	private Equipable tool;
	
	public FiringWeapon(PlayState state, float i, BodyData p, BodyData v, float projVelo, float minVelo, float veloDeprec, float projSize, float procCd, Equipable tool) {
		super(state, i, name, descr, false, p, v);
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
				
			inflicted.getCurrentTool().setWeaponVelo(((Player)inflicted.getSchmuck()).getMouse().getPixelPosition().sub(inflicted.getSchmuck().getPixelPosition()).nor().scl(currentVelo));
			
			projOrigin = inflicted.getSchmuck().getProjectileOrigin(inflicted.getCurrentTool().getWeaponVelo(), projSize);
			
			inflicted.statusProcTime(StatusProcTime.ON_SHOOT, null, 0, null, inflicted.getCurrentTool(), null);
			
			inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), projOrigin, inflicted.getCurrentTool().getWeaponVelo(), inflicted.getSchmuck().getHitboxfilter());
		}
		
		procCdCount += delta;
	}
}
