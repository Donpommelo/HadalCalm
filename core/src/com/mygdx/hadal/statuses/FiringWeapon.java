package com.mygdx.hadal.statuses;


import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class FiringWeapon extends Status {

	private static String name = "Firing Weapon";
	private static String descr = "Firing";
	
	private float procCdCount;
	private float procCd;
	
	private float currentVelo;
	private float minVelo;
	private float veloDeprec;
	private int projSize;
	private Vector2 projOrigin = new Vector2();
	
	public FiringWeapon(PlayState state, float i, BodyData p, BodyData v, float projVelo, float minVelo, float veloDeprec, int projSize, float procCd) {
		super(state, i, name, descr, false, p, v);
		this.minVelo = minVelo;
		this.veloDeprec = veloDeprec;
		this.projSize = projSize;
		this.procCd = procCd;
		
		this.currentVelo = projVelo;
	}
	
	@Override
	public void timePassing(float delta) {
		
		super.timePassing(delta);
		
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			
			if (currentVelo > minVelo) {
				currentVelo -= veloDeprec;
			}
				
			inflicted.getCurrentTool().setWeaponVelo(((Player)inflicted.getSchmuck()).getMouse().getPosition().sub(inflicted.getSchmuck().getPosition()).nor().scl(currentVelo));
			
			projOrigin = inflicted.getSchmuck().getProjectileOrigin(inflicted.getCurrentTool().getWeaponVelo(), projSize);
			
			inflicted.statusProcTime(StatusProcTime.ON_SHOOT, null, 0, null, inflicted.getCurrentTool(), null);
			
			inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), inflicted.getCurrentTool().getWeaponVelo(), projOrigin.x, projOrigin.y, inflicted.getSchmuck().getHitboxfilter());
		}
		
		procCdCount += delta;
	}
}
