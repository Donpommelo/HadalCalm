package com.mygdx.hadal.states;

import com.mygdx.hadal.actors.UILevel.uiType;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockLevel;

public class HubState extends PlayState {

	
	public HubState(GameStateManager gsm, Loadout loadout) {
		super(gsm, loadout, UnlockLevel.HUB, false);
	}
	
	@Override
	public void show() {
		super.show();
		getUiLevel().setType(uiType.HUB);
	}

}
