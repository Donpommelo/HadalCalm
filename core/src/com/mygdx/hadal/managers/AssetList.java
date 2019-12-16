package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum AssetList {

	TITLE_CARD("HADAL_PANIC_3.jpg", Texture.class),
	GAME_OVER_CARD("GAME_OVER_CARD.png", Texture.class),
	
	BUTLER_FONT("fonts/butler.fnt", null),
	LEARNING_FONT("fonts/learning_curve.fnt", null),
	FIXEDSYS_FONT("fonts/fixedsys.fnt", null),
	
	// Player and enemy sprites.
	PROJ_1("sprites/projectiles.png", Texture.class),
	PROJ_1_ATL("sprites/projectiles.atlas", TextureAtlas.class),
	BOOM_1("sprites/boom.png", Texture.class),
	BOOM_1_ATL("sprites/boom.atlas", TextureAtlas.class),
	
	TURRET_1("sprites/turret.png", Texture.class),
	TURRET_ATL("sprites/turret.atlas", TextureAtlas.class),
	
	FISH_1("sprites/fish.png", Texture.class),
	FISH_ATL("sprites/fish.atlas", TextureAtlas.class),
	
	PLAYER_MOREAU("sprites/player/moreau.png", Texture.class),
	PLAYER_MOREAU_ATL("sprites/player/moreau.atlas", TextureAtlas.class),
	PLAYER_MOREAU_FESTIVE_ATL("sprites/player/moreau_festive.atlas", TextureAtlas.class),
	
	PLAYER_TAKA("sprites/player/takanori.png", Texture.class),
	PLAYER_TAKA_ATL("sprites/player/takanori.atlas", TextureAtlas.class),
	
	PLAYER_TELE("sprites/player/telemachus.png", Texture.class),
	PLAYER_TELE_ATL("sprites/player/telemachus.atlas", TextureAtlas.class),
	
	MULTITOOL_1("sprites/player/multitool.png", Texture.class),
	MULTITOOL_ATL("sprites/player/multitool.atlas", TextureAtlas.class),
	
	EVENT_1("sprites/events/event.png", Texture.class),
	EVENT_2("sprites/events/event2.png", Texture.class),
	EVENT_3("sprites/events/event3.png", Texture.class),
	EVENT_4("sprites/events/event4.png", Texture.class),
	EVENT_5("sprites/events/event5.png", Texture.class),
	EVENT_6("sprites/events/event6.png", Texture.class),
	EVENT_7("sprites/events/event7.png", Texture.class),
	EVENT_ATL("sprites/events/event.atlas", TextureAtlas.class),
	
	// Particle effects.
	PARTICLE_ATLAS("particles/particles.atlas", TextureAtlas.class),
	PARTICLE_TOTLC_ATLAS("particles/totlc/particles_totlc.atlas", TextureAtlas.class),
	PARTYCLE_ATLAS("particles/totlc/partycles.atlas", TextureAtlas.class),
	STAR_PARTICLE("particles/totlc/star_particles.atlas", TextureAtlas.class),
	
	UIPATCHIMG("ui/window.png", Texture.class),
	UIPATCHATL("ui/window.atlas", TextureAtlas.class),
	UISKINIMG("ui/uiskin.png", Texture.class),
	UISKINATL("ui/uiskin.atlas", TextureAtlas.class),
	UI1("ui/UI.png", Texture.class),
	UI2("ui/UI2.png", Texture.class),
	UI_ATL("ui/UI.atlas", TextureAtlas.class),
	ANCHOR("ui/anchor_logo.png", Texture.class),
	ANCHORATLAS("ui/anchor_logo.atlas", TextureAtlas.class),
	ANCHORDARK("ui/anchor_logo_dark.png", Texture.class),
	ANCHORDARKATLAS("ui/anchor_logo_dark.atlas", TextureAtlas.class),
	HEART_EMPTY("ui/heart_meter.png", Texture.class),
	HEART_FULL("ui/heart_gauge.png", Texture.class),
	
	PELICAN("sprites/busts/portrait_pelican.png", Texture.class),
	PELICANATLAS("sprites/busts/portrait_pelican.atlas", TextureAtlas.class),
	
	BACKGROUND1("under_da_sea.jpg", Texture.class),
	BACKGROUND2("under_da_sea_no_rocks.jpg", Texture.class),
    BLACK("black.png", Texture.class),
	
	//misc stuff from totlc
	EXCLAMATION_ATLAS("particles/totlc/exclamation_mark.atlas", TextureAtlas.class),
	EXCLAMATION_MARK("particles/totlc/exclamation_mark.png", Texture.class),
	IMPACT_ATLAS("particles/totlc/impact.atlas", TextureAtlas.class),
	
	STAR_SHOT_ATLAS("particles/totlc/star_shot.atlas", TextureAtlas.class),
	STAR_SHOT("particles/totlc/star_shot.png", Texture.class),
	
	;
	//Enum constructor and methods.
	private String pathname;
    private Class<?> type;
    
    AssetList(String s, Class<?> c) {
        this.pathname = s;
        this.type = c;
    }

    @Override
    public String toString() {
        return this.pathname;
    }

    public Class<?> getType() { 
    	return type; 
    }
}
