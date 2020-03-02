package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum AssetList {

	TITLE_CARD("HADAL_PANIC_3.jpg", Texture.class),
	FIXEDSYS_FONT("fonts/fixedsys.fnt", null),
	
	// Player and enemy sprites.
	PROJ_1_ATL("sprites/projectiles.atlas", TextureAtlas.class),
	BOOM_1_ATL("sprites/boom.atlas", TextureAtlas.class),
	TURRET_ATL("sprites/turret.atlas", TextureAtlas.class),
	FISH_ATL("sprites/fish.atlas", TextureAtlas.class),
	KAMABOKO_ATL("sprites/king_kamaboko.atlas", TextureAtlas.class),
	KAMABOKO_CRAWL_ATL("sprites/kamaboko_crawl.atlas", TextureAtlas.class),
	KAMABOKO_SWIM_ATL("sprites/kamaboko_swim.atlas", TextureAtlas.class),
	PLAYER_MOREAU_ATL("sprites/player/moreau.atlas", TextureAtlas.class),
	PLAYER_MOREAU_FESTIVE_ATL("sprites/player/moreau_festive.atlas", TextureAtlas.class),
	PLAYER_TAKA_ATL("sprites/player/takanori.atlas", TextureAtlas.class),
	PLAYER_TELE_ATL("sprites/player/telemachus.atlas", TextureAtlas.class),
	MULTITOOL_ATL("sprites/player/multitool.atlas", TextureAtlas.class),
	EVENT_ATL("sprites/events/event.atlas", TextureAtlas.class),
	
	// Particle effects.
	PARTICLE_ATLAS("particles/particles.atlas", TextureAtlas.class),
	
	UIPATCHATL("ui/window.atlas", TextureAtlas.class),
	UISKINATL("ui/uiskin.atlas", TextureAtlas.class),
	UI_ATL("ui/UI.atlas", TextureAtlas.class),
	TELEMACHUS_POINT("ui/telemachus_point.atlas", TextureAtlas.class),
	HEART_EMPTY("ui/heart_meter.png", Texture.class),
	HEART_FULL("ui/heart_gauge.png", Texture.class),
	
	BOSSGAUGEATLAS("ui/gauge.atlas", TextureAtlas.class),

	PELICANATLAS("sprites/busts/portrait_pelican.atlas", TextureAtlas.class),
	
	BACKGROUND1("under_da_sea.jpg", Texture.class),
	BACKGROUND2("under_da_sea_no_rocks.jpg", Texture.class),
    BLACK("black.png", Texture.class),
	
	//misc stuff from totlc
	EXCLAMATION_ATLAS("particles/totlc/exclamation_mark.atlas", TextureAtlas.class),
	IMPACT_ATLAS("particles/totlc/impact.atlas", TextureAtlas.class),
	STAR_SHOT_ATLAS("particles/totlc/star_shot.atlas", TextureAtlas.class),
	
	;
	//Enum constructor and methods.
	private String pathname;
    private Class<?> type;
    
    AssetList(String s, Class<?> c) {
        this.pathname = s;
        this.type = c;
    }

    @Override
    public String toString() { return this.pathname; }

    public Class<?> getType() { return type; }
}
