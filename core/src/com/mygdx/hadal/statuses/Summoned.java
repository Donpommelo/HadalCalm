package com.mygdx.hadal.statuses;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Temporary designates a schmuck as a summon that awards a summoner the score/kills it gets
 * @author Lornard Landwich
 */
public class Summoned extends Status {

	//this is the player that summoned this unit (and will get credit for its kills)
	private final Player summoner;
	
	public Summoned(PlayState state, BodyData i, Player summoner) {
		super(state, i);
		this.summoner = summoner;
		this.setServerOnly(true);
	}
	
	@Override
	public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
		summoner.getHitsoundHelper().playHitSound(vic, damage);
		return damage;	
	}
	
	@Override
	public void onKill(BodyData vic, DamageSource source) {
		if (vic.getSchmuck() instanceof Player) {
			state.getMode().processPlayerDeath(state, summoner, null, DamageSource.MISC);
		}
	}
}
