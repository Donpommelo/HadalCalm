package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class MuddlingCup extends Artifact {

	private final static String name = "Muddling Cup";
	private final static String descr = "Fire weapon When Airblasting";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float projSpeed = 12.0f;
	
	public MuddlingCup() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new Status(state, name, descr, b) {
			
			private Vector2 projAngle = new Vector2();
			@Override
			public void onAirBlast(Equipable tool) {
				
				inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), projAngle.set(tool.getWeaponVelo()).scl(projSpeed * PPM),
						inflicted.getSchmuck().getPosition().x * PPM, 
						inflicted.getSchmuck().getPosition().y * PPM,
						inflicted.getSchmuck().getHitboxfilter());
				
				
			}
		});
		return enchantment;
	}
}
