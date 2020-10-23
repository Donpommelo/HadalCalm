package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;

/**
 * A ProcTime is a package of info needed by a specific effect activation type.
 * Each static class matches a proc time that a Status can activate
 * @author Vlujentonette Veham
 */
public abstract class ProcTime {

	/**
	 * This is run when the proc time activates and returns a modified version of itself if a value is passed along
	 */
	public abstract ProcTime statusProcTime(Status status);
	
	public static class StatCalc extends ProcTime {
		public StatCalc() {}

		@Override
		public ProcTime statusProcTime(Status status) {
			status.statChanges();
			return this;
		}
	}
	
	public static class InflictDamage extends ProcTime {
		public float damage;
		public BodyData vic;
		public DamageTypes[] tags;
		
		public InflictDamage(float damage, BodyData vic, DamageTypes...  tags) {
			this.damage = damage;
			this.vic = vic;
			this.tags = tags;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			damage = status.onDealDamage(damage, vic, tags);
			return this;
		}
	}

	public static class ReceiveDamage extends ProcTime {
		public float damage;
		public BodyData perp;
		public DamageTypes[] tags;
		
		public ReceiveDamage(float damage, BodyData perp, DamageTypes...  tags) {
			this.damage = damage;
			this.perp = perp;
			this.tags = tags;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			damage = status.onReceiveDamage(damage, perp, tags);
			return this;
		}
	}
	
	public static class ReceiveHeal extends ProcTime {
		public float heal;
		public BodyData perp;
		public DamageTypes[] tags;
		
		public ReceiveHeal(float heal, BodyData perp, DamageTypes...  tags) {
			this.heal = heal;
			this.perp = perp;
			this.tags = tags;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			heal = status.onHeal(heal, perp, tags);
			return this;
		}
	}
	
	public static class TimePass extends ProcTime {
		public float time;		
		
		public TimePass(float time) {
			this.time = time;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.timePassing(time);
			return this;
		}
	}

	public static class Kill extends ProcTime {
		BodyData vic;
		
		public Kill(BodyData vic) {
			this.vic = vic;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.onKill(vic);
			return this;
		}
	}

	public static class Death extends ProcTime {
		BodyData perp;
		
		public Death(BodyData perp) {
			this.perp = perp;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.onDeath(perp);
			return this;
		}
	}
	
	public static class WhileAttack extends ProcTime {
		public float time;
		public Equippable tool;
		
		public WhileAttack(float time, Equippable tool) {
			this.time = time;
			this.tool = tool;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.whileAttacking(time, tool);
			return this;
		}
	}
	
	public static class Shoot extends ProcTime {
		public Equippable tool;
		
		public Shoot(Equippable tool) {
			this.tool = tool;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.onShoot(tool);
			return this;
		}
	}

	public static class ReloadStart extends ProcTime {
		public Equippable tool;

		public ReloadStart(Equippable tool) {
			this.tool = tool;
		}

		@Override
		public ProcTime statusProcTime(Status status) {
			status.onReloadStart(tool);
			return this;
		}
	}

	public static class ReloadFinish extends ProcTime {
		public Equippable tool;
		
		public ReloadFinish(Equippable tool) {
			this.tool = tool;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.onReloadFinish(tool);
			return this;
		}
	}
	
	public static class CreateHitbox extends ProcTime {
		public Hitbox hbox;
		
		public CreateHitbox(Hitbox hbox) {
			this.hbox = hbox;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.onHitboxCreation(hbox);
			return this;
		}
	}
	
	public static class PlayerCreate extends ProcTime {
		
		public PlayerCreate() {}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.playerCreate();
			return this;
		}
	}
	
	public static class ScrapPickup extends ProcTime {
		
		public ScrapPickup() {}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.scrapPickup();
			return this;
		}
	}

	public static class Airblast extends ProcTime {
		public Equippable tool;
		
		public Airblast(Equippable tool) {
			this.tool = tool;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.onAirBlast(tool);
			return this;
		}
	}

	public static class whileHover extends ProcTime {
		public Vector2 hoverDirection;

		public whileHover(Vector2 hoverDirection) {	this.hoverDirection = hoverDirection; }

		@Override
		public ProcTime statusProcTime(Status status) {
			status.whileHover(hoverDirection);
			return this;
		}
	}
	
	public static class BeforeActiveUse extends ProcTime {
		public ActiveItem tool;
		
		public BeforeActiveUse(ActiveItem tool) {
			this.tool = tool;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.beforeActiveItem(tool);
			return this;
		}
	}
	
	public static class AfterActiveUse extends ProcTime {
		public ActiveItem tool;
		
		public AfterActiveUse(ActiveItem tool) {
			this.tool = tool;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.afterActiveItem(tool);
			return this;
		}
	}
	
	public static class AfterBossSpawn extends ProcTime {
		public Enemy boss;
		
		public AfterBossSpawn(Enemy boss) {
			this.boss = boss;
		}
		
		@Override
		public ProcTime statusProcTime(Status status) {
			status.afterBossSpawn(boss);
			return this;
		}
	}
}
