package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class MuddlingCup extends Artifact {

	private final static String name = "Muddling Cup";
	private final static String descr = "Fires Shots When Airblasting";
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
				
				if (inflicted.getCurrentTool() instanceof RangedWeapon && inflicted.getSchmuck() instanceof Player) {
					((RangedWeapon)inflicted.getCurrentTool()).getOnShoot().makeHitbox(inflicted.getSchmuck(), state, tool, 
							tool.getWeaponVelo().scl(projSpeed),
							inflicted.getSchmuck().getBody().getPosition().x * PPM, 
							inflicted.getSchmuck().getBody().getPosition().y * PPM, 
							inflicted.getSchmuck().getHitboxfilter());
				}
			}
		});
		return enchantment;
	}
}
