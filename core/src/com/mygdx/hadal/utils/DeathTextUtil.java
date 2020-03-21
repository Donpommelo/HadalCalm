package com.mygdx.hadal.utils;

import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.statuses.DamageTypes;

public class DeathTextUtil {

	public static String getDeathText(Schmuck perp, Player vic, DamageTypes... tags) {
		
		if (tags.length > 0) {
			switch (tags[0]) {
			case DISCONNECT:
				return vic.getName() + " disconnected!";
			case LIVES_OUT:
				return vic.getName() + " ran out of lives!";
			default:
				break;
			}
		}

		if (perp.equals(vic)) {
			return getSuicideText(vic.getName(), tags);
		}
		
		if (perp instanceof Player) {
			return getPVPKillText(((Player)perp).getName(), vic.getName(), tags);
		} else if (perp instanceof Enemy){
			return getEnemyKillText(vic.getName(), tags);
		} else {
			return getHazardKillText(vic.getName(), tags);
		}
	}
	
	public static String getSuicideText(String name, DamageTypes... tags) {
		switch (tags[0]) {
		case EXPLOSIVE:
			return name + " blew themself to bits!";
		case POISON:
			return name + " succumbed to poison!";
		default:
			return name + " commited suicide!";
		}
	}
	
	public static String getEnemyKillText(String name, DamageTypes... tags) {
		switch (tags[0]) {
		case EXPLOSIVE:
			return name + " was blown up by a monster!";
		case MELEE:
			return name + " was mauled by a monster!";
		case RANGED:
			return name + " was shot by a monster!";
		default:
			return name + " was killed by a monster!";
		}
	}
	
	public static String getHazardKillText(String name, DamageTypes... tags) {
		switch (tags[0]) {
		case BLASTZONE:
			return name + " fell off the map!";
		case DECAPITATION:
			return name + " was decapitated!";
		case EXPLOSIVE:
			return name + " blew up!";
		case POISON:
			return name + " succumbed to poison!";
		default:
			return name + " died!";
		}
	}
	
	public static String getPVPKillText(String perp, String vic, DamageTypes... tags) {
		switch (tags[0]) {
		case BEES:
			return perp + " killed " + vic + " with bees!";
		case BULLET:
			return perp + " filled " + vic + " with lead!";
		case CRUSHING:
			return perp + " crushed " + vic + "!";
		case DECAPITATION:
			return perp + " decapitated " + vic + "!";
		case ELECTRICITY:
			return perp + " electrocuted " + vic + "!";
		case EXPLOSIVE:
			return perp + " blew " + vic + " to bits!";
		case FIRE:
			return perp + " incinerated " + vic + "!";
		case IMPALEMENT:
			return perp + " impaled " + vic + "!";
		case LASER:
			return perp + " zapped " + vic + " to death!";
		case MAGIC:
			return perp + " killed " + vic + " with magic!";
		case MELEE:
			return perp + " beat " + vic + " to death!";
		case POISON:
			return vic + " succumbed to " + perp + "'s poison!";
		case RANGED:
			return perp + " shot " + vic + " to death!";
		case SHRAPNEL:
			return perp + " killed " + vic + " with shrapnel!";
		case SLODGE:
			return perp + " slodged " + vic + "!";
		case SNIPE:
			return perp + " sniped " + vic + "!";
		case SOUND:
			return perp + " burst " + vic + "'s eardrums!";
		case WATER:
			return perp + " drowned " + vic + "!";
		default:
			return perp + " killed " + vic + "!";
		}
	}
}
