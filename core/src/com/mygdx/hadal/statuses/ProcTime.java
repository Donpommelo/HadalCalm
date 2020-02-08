package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;

/**
 * 
 * @author Zachary Tu
 *
 */
public class ProcTime {

	public static class StatCalc {
		public StatCalc() {}
	}
	
	public static class InflictDamage {
		public float damage;
		public BodyData vic;
		public DamageTypes[] tags;
		
		public InflictDamage(float damage, BodyData vic, DamageTypes...  tags) {
			this.damage = damage;
			this.vic = vic;
			this.tags = tags;
		}
	}

	public static class ReceiveDamage {
		public float damage;
		public BodyData perp;
		public DamageTypes[] tags;
		
		public ReceiveDamage(float damage, BodyData perp, DamageTypes...  tags) {
			this.damage = damage;
			this.perp = perp;
			this.tags = tags;
		}
	}
	
	public static class ReceiveHeal {
		public float heal;
		public BodyData perp;
		public DamageTypes[] tags;
		
		public ReceiveHeal(float heal, BodyData perp, DamageTypes...  tags) {
			this.heal = heal;
			this.perp = perp;
			this.tags = tags;
		}
	}
	
	public static class TimePass {
		public float time;		
		
		public TimePass(float time) {
			this.time = time;
		}
	}

	public static class Kill {
		BodyData vic;
		
		public Kill(BodyData vic) {
			this.vic = vic;
		}
	}

	public static class Death {
		BodyData perp;
		
		public Death(BodyData perp) {
			this.perp = perp;
		}
	}
	
	public static class WhileAttack {
		public float time;
		public Equipable tool;
		
		public WhileAttack(float time, Equipable tool) {
			this.time = time;
			this.tool = tool;
		}
	}
	
	public static class Shoot {
		public Equipable tool;
		
		public Shoot(Equipable tool) {
			this.tool = tool;
		}
	}
	
	public static class Reload {
		public Equipable tool;
		
		public Reload(Equipable tool) {
			this.tool = tool;
		}
	}
	
	public static class CreateHitbox {
		public Hitbox hbox;
		
		public CreateHitbox(Hitbox hbox) {
			this.hbox = hbox;
		}
	}
	
	public static class PlayerCreate {
		
		public PlayerCreate() {}
	}
	
	public static class Airblast {
		public Equipable tool;
		
		public Airblast(Equipable tool) {
			this.tool = tool;
		}
	}
	
	public static class BeforeActiveUse {
		public ActiveItem tool;
		
		public BeforeActiveUse(ActiveItem tool) {
			this.tool = tool;
		}
	}
	
	public static class AfterActiveUse {
		public ActiveItem tool;
		
		public AfterActiveUse(ActiveItem tool) {
			this.tool = tool;
		}
	}
	
	public static class AfterBossSpawn {
		public Enemy boss;
		
		public AfterBossSpawn(Enemy boss) {
			this.boss = boss;
		}
	}
}
