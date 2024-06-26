package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.map.SettingTeamMode.TeamMode;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.users.User;

/**
 * A PlayerAlignmentChanger changes a player's hitbox filter. This changes what "team" they are on.
 * <p>
 * Triggered Behavior: This triggers changing the player's alignment
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * pvp: boolean. If true, we change their filter to their default pvp filter
 * filter: short. if pvp is false, this is the filter we set the player to.
 
 * @author Heftbert Huthrop
 */
public class PlayerAlignmentChanger extends Event {

	private final boolean pvp;
	private final short filter;

	public PlayerAlignmentChanger(PlayState state, boolean pvp, float filter) {
		super(state);
		this.pvp = pvp;
		this.filter = (short) filter;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {

				if (p != null) {
					User user = p.getUser();

					short newIndex;
					if (pvp && user != null) {
						p.setPvpOverride(true);
						if (state.getMode().isTeamDesignated() || TeamMode.TEAM_MANUAL.equals(state.getMode().getTeamMode())) {
							if (AlignmentFilter.NONE.equals(user.getLoadoutManager().getActiveLoadout().team)) {
								newIndex = user.getHitboxFilter().getFilter();
							} else {
								newIndex = user.getTeamFilter().getFilter();
							}
						} else {
							newIndex = user.getHitboxFilter().getFilter();
						}
					} else {
						p.setPvpOverride(false);
						newIndex = filter;
					}

					if (p.getMainFixture() != null) {
						Filter filter = p.getMainFixture().getFilterData();
						filter.groupIndex = newIndex;

						p.getMainFixture().setFilterData(filter);
						p.setHitboxFilter(newIndex);
					}
				}
			}
		};
	}

	@Override
	public void loadDefaultProperties() {
		setClientSyncType(eventSyncTypes.ECHO_ACTIVATE);
		setServerSyncType(eventSyncTypes.ECHO_ACTIVATE_EXCLUDE);
	}
}
