package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This status makes the player fire their weapon at the mouse for a brief period of time.
 * This is used by "spray" type weapons that, when fired, spray a stream of hboxes for a brief period of time
 * @author Wragatha Wulgham
 */
public class FiringWeapon extends Status {

	//these keep track of interval until next hbox is spawned
	private final float procCd;
	private float procCdCount;

	//size of projectile (used to determine the projectile spawn origin)
	private final float projSize;
	private final Vector2 projOrigin = new Vector2();
	private final Vector2 projVelo = new Vector2();
	
	//tool used to fire this status
	private final Equippable tool;

	//The number of shots that will be fired and the shots fired so far
	private final int shotNum;
	private int shotsFired;

	public FiringWeapon(PlayState state, float i, BodyData p, BodyData v, float projSize, float procCd, int shotNum, Equippable tool) {
		super(state, i, false, p, v);
		this.projSize = projSize;
		this.procCd = procCd;
		this.shotNum = shotNum;
		this.tool = tool;
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);

		//when it activates, this status sets the tool's weaponVelo field and then fires it 
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			
			if (inflicted.getSchmuck() instanceof Player player) {

				//if switching away from the spraying weapon, the spray ends
				if (!player.getEquipHelper().getCurrentTool().equals(tool)) {
					return;
				}

				shotsFired++;

				//keep track of shots fired for spray weapons whose properties change as shots are fired (cola, slodge)
				player.getSpecialWeaponHelper().setSprayWeaponShotNumber(shotsFired);

				if (shotsFired <= shotNum) {
					projVelo.set(player.getMouseHelper().getPixelPosition()).sub(player.getPixelPosition());

					projOrigin.set(player.getProjectileOrigin(projVelo, projSize));

					inflicted.statusProcTime(new ProcTime.Shoot(player.getEquipHelper().getCurrentTool()));
					player.getEquipHelper().getCurrentTool().fire(state, player, projOrigin, projVelo, player.getHitboxFilter());
				}
			}
		}
		procCdCount += delta;
	}
}
