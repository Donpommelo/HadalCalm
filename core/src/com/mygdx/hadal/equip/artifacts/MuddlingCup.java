package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class MuddlingCup extends Artifact {

	private final static String name = "Muddling Cup";
	private final static String descr = "Fires Weapon When Airblasting";
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
			
			@Override
			public void onAirBlast(Equipable tool) {
				inflicted.getCurrentTool().fire(state, inflicted.getSchmuck(), tool.getWeaponVelo().scl(projSpeed),
						inflicted.getSchmuck().getPosition().x * PPM, 
						inflicted.getSchmuck().getPosition().y * PPM,
						inflicted.getSchmuck().getHitboxfilter());
			}
		});
		return enchantment;
	}
}
