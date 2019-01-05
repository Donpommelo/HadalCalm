package com.mygdx.hadal.states;

import com.mygdx.hadal.actors.UITag.uiType;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockLevel;

public class HubState extends PlayState {

	
	public HubState(GameStateManager gsm, Loadout loadout) {
		super(gsm, loadout, UnlockLevel.HUB, false, null);
	}
	
	@Override
	public void show() {
		super.show();
		getUiExtra().changeTypes(0, uiType.SCRAP, uiType.SCRIP);
	}

}
