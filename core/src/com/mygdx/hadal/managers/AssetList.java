package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum AssetList {

	TITLE_CARD("HADAL_PANIC_3.jpg", Texture.class),
	MENU_CARD("MENU_CARD.png", Texture.class),
	LOADOUT_CARD("LOADOUT_CARD.png", Texture.class),
	GAME_OVER_CARD("GAME_OVER_CARD.png", Texture.class),
	VICTORY_CARD("VICTORY_CARD.png", Texture.class),
	BUTLER_FONT("fonts/butler.fnt", null),
	LEARNING_FONT("fonts/learning_curve.fnt", null),
	FIXEDSYS_FONT("fonts/fixedsys.fnt", null),
	
	// Player and enemy sprites.
	PROJ_1("sprites/projectiles.png", Texture.class),
	PROJ_1_ATL("sprites/projectiles.atlas", TextureAtlas.class),
	
	TURRET_1("sprites/turret.png", Texture.class),
	TURRET_ATL("sprites/turret.atlas", TextureAtlas.class),
	
	FISH_1("sprites/fish.png", Texture.class),
	FISH_ATL("sprites/fish.atlas", TextureAtlas.class),
	
	PLAYER_MOREAU("sprites/player/moreau.png", Texture.class),
	PLAYER_MOREAU_ATL("sprites/player/moreau.atlas", TextureAtlas.class),
	
	PLAYER_TAKA("sprites/player/takanori.png", Texture.class),
	PLAYER_TAKA_ATL("sprites/player/takanori.atlas", TextureAtlas.class),
	
	PLAYER_TELE("sprites/player/telemachus.png", Texture.class),
	PLAYER_TELE_ATL("sprites/player/telemachus.atlas", TextureAtlas.class),
	
	MULTITOOL_1("sprites/player/multitool.png", Texture.class),
	MULTITOOL_ATL("sprites/player/multitool.atlas", TextureAtlas.class),
	
	// Particle effects.
	PARTICLE_ATLAS("sprites/particle/particles.atlas", TextureAtlas.class),
	BUBBLE_IMPACT("sprites/particle/bubble_impact.particle", null),
	BUBBLE_TRAIL("sprites/particle/bubble_trail.particle", null),
	SMOKE_PUFF("sprites/particle/smoke_puff.particle", null),
	SPARK_TRAIL("sprites/particle/spark_trail.particle", null),
	
	UIPATCHIMG("ui/window.png", Texture.class),
	UIPATCHATL("ui/window.atlas", TextureAtlas.class),
	UISKINIMG("ui/uiskin.png", Texture.class),
	UISKINATL("ui/uiskin.atlas", TextureAtlas.class),
	UI1("ui/UI.png", Texture.class),
	UI2("ui/UI2.png", Texture.class),
	UIATLAS("ui/UI.atlas", TextureAtlas.class);
	
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
