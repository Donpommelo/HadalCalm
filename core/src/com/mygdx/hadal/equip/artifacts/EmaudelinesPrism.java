package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class EmaudelinesPrism extends Artifact {

	private final static String name = "Emaudeline's Prism";
	private final static String descr = "Split Shot";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public EmaudelinesPrism() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 27, -2.0f, b),
				new StatChangeStatus(state, 26, -0.25f, b),
				new Status(state, name, descr, b) {
			
			@Override
			public void onShoot(Equipable tool) {
				if (tool instanceof RangedWeapon) {
					
					((RangedWeapon)tool).getOnShoot().makeHitbox(inflicted.getSchmuck(), state, tool, 
							new Vector2(tool.getWeaponVelo()).setAngle(tool.getWeaponVelo().angle() + 20),
							inflicted.getSchmuck().getBody().getPosition().x * PPM, 
							inflicted.getSchmuck().getBody().getPosition().y * PPM, 
							inflicted.getSchmuck().getHitboxfilter());
					((RangedWeapon)tool).getOnShoot().makeHitbox(inflicted.getSchmuck(), state, tool, 
							new Vector2(tool.getWeaponVelo()).setAngle(tool.getWeaponVelo().angle() - 20),
							inflicted.getSchmuck().getBody().getPosition().x * PPM, 
							inflicted.getSchmuck().getBody().getPosition().y * PPM, 
							inflicted.getSchmuck().getHitboxfilter());
				}
			}
		});
		return enchantment;
	}
}
