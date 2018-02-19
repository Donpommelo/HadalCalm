package com.mygdx.hadal.states;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;

public class HubState extends PlayState {

	public HubState(GameStateManager gsm, Loadout loadout) {
		super(gsm, loadout, "Maps/test_map.tmx", false);

	}

}
