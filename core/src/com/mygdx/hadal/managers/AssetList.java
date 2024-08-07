package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * The AssetList maps varioous assets to strings to be loaded and managed by the asseetmanager
 * @author Rusteban Reldcoin
 */
public enum AssetList {

	TITLE_CARD("Title.png", Texture.class),
	GREY("grey.png", Texture.class),
	TITLE_BACKGROUND("hadal_title_1280.jpg", Texture.class),
	RESULTS_CARD("same.png", Texture.class),
	FIXEDSYS_FONT("fonts/fixedsys.fnt", null),
	VERDANA_FONT("fonts/verdana.fnt", null),

	//Player Sprites
	PLAYER_MAXIMILLIAN_ATL("sprites/player/maximillian.atlas", TextureAtlas.class),
	PLAYER_MOREAU_ATL("sprites/player/moreau.atlas", TextureAtlas.class),
	PLAYER_MOREAU_FESTIVE_ATL("sprites/player/moreau_festive.atlas", TextureAtlas.class),
	PLAYER_MOREAU_PARTY_ATL("sprites/player/moreau_party.atlas", TextureAtlas.class),
	PLAYER_ROCLAIRE_ATL("sprites/player/roclaire.atlas", TextureAtlas.class),
	PLAYER_TAKANORI_ATL("sprites/player/takanori.atlas", TextureAtlas.class),
	PLAYER_TELEMACHUS_ATL("sprites/player/telemachus.atlas", TextureAtlas.class),
	PLAYER_WANDA_ATL("sprites/player/wanda.atlas", TextureAtlas.class),
	PLAYER_EXTRA_ATL("sprites/player/characterExtra.atlas", TextureAtlas.class),

	PLAYER_MAXIMILLIAN("sprites/player/maximillian.png", Texture.class),
	PLAYER_MOREAU("sprites/player/moreau.png", Texture.class),
	PLAYER_ROCLAIRE("sprites/player/roclaire.png", Texture.class),
	PLAYER_TAKANORI("sprites/player/takanori.png", Texture.class),
	PLAYER_TELEMACHUS("sprites/player/telemachus.png", Texture.class),
	PLAYER_WANDA("sprites/player/wanda.png", Texture.class),

	// Object Sprites
	PROJ_1_ATL("sprites/projectile/projectiles.atlas", TextureAtlas.class),
	NOTIFICATION_ATL("sprites/notifications.atlas", TextureAtlas.class),
	BOOM_1_ATL("sprites/boom.atlas", TextureAtlas.class),
	MULTITOOL_ATL("sprites/player/multitool.atlas", TextureAtlas.class),
	EVENT_ATL("sprites/events/event.atlas", TextureAtlas.class),
	POOL_ATL("sprites/projectile/pool.atlas", TextureAtlas.class),
	SPIRIT_BOMB_ATL("sprites/projectile/spirit_bomb.atlas", TextureAtlas.class),
	FROG_ATL("sprites/projectile/frog.atlas", TextureAtlas.class),

	// Particle effects.
	PARTICLE_ATL("particles/particles.atlas", TextureAtlas.class),

	//Enemy Sprites
	TURRET_ATL("sprites/turret.atlas", TextureAtlas.class),
	KAMABOKO_ATL("sprites/king_kamaboko.atlas", TextureAtlas.class),
	KAMABOKO_CRAWL_ATL("sprites/kamaboko_crawl.atlas", TextureAtlas.class),
	KAMABOKO_SWIM_ATL("sprites/kamaboko_swim.atlas", TextureAtlas.class),
	DRONE_ATL("sprites/drone.atlas", TextureAtlas.class),
	NEPTUNE_KING_ATL("sprites/neptuneking.atlas", TextureAtlas.class),
	KRILL_ATL("sprites/krill.atlas", TextureAtlas.class),
	SCISSORFISH_ATL("sprites/enemy/scissorfish.atlas", TextureAtlas.class),
	SPITTLEFISH_ATL("sprites/enemy/spittlefish.atlas", TextureAtlas.class),
	TORPEDOFISH_ATL("sprites/enemy/torpedofish.atlas", TextureAtlas.class),
	BOMBFISH_ATL("sprites/enemy/bombfish.atlas", TextureAtlas.class),
	SNIPERFISH_ATL("sprites/enemy/sniperfish.atlas", TextureAtlas.class),
	SNIPER_RETICLE_ATL("sprites/projectile/sniperreticle.atlas", TextureAtlas.class),

	//UI
	UIPATCH_ATL("ui/window.atlas", TextureAtlas.class),
	UISKIN_ATL("ui/uiskin.atlas", TextureAtlas.class),
	UI_ATL("ui/UI.atlas", TextureAtlas.class),
	MUSIC_ATL("ui/music.atlas", TextureAtlas.class),
	EMOTE_ATL("sprites/emote/emote.atlas", TextureAtlas.class),
	TELEMACHUS_POINT("ui/telemachus_point.atlas", TextureAtlas.class),
	BOSSGAUGE_ATL("ui/gauge.atlas", TextureAtlas.class),
	PELICAN_ATL("sprites/busts/portrait_pelican.atlas", TextureAtlas.class),
	
	BACKGROUND1("under_da_sea.jpg", Texture.class),
	BACKGROUND2("under_da_sea_no_rocks.jpg", Texture.class),
	BLACK("black.png", Texture.class),
	WHITE("white.png", Texture.class),

	ARTIFACT_BIG_ATL("sprites/artifact/artifactbig.atlas", TextureAtlas.class),
	ARTIFACT_SMALL_ATL("sprites/artifact/artifactsmall.atlas", TextureAtlas.class),
	ARTIFACT_WHITE_ATL("sprites/artifact/artifactwhite.atlas", TextureAtlas.class),
	COSMETICS_ATL("sprites/cosmetics.atlas", TextureAtlas.class),

	//misc stuff from totlc
	IMPACT_ATL("particles/totlc/impact.atlas", TextureAtlas.class),

	//map thumbnails
	MAP_ICONS("maps/maps.atlas", TextureAtlas.class),

	//mode icons
	MODE_ICONS("ui/modes.atlas", TextureAtlas.class),

	;

	//Enum constructor and methods.
	private final String pathname;
    private final Class<?> type;
    
    AssetList(String s, Class<?> c) {
        this.pathname = s;
        this.type = c;
    }

    @Override
    public String toString() { return this.pathname; }

    public Class<?> getType() { return type; }
}
