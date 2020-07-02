package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

public enum SoundEffect {

	RUN("sound/run.ogg"),
	DOUBLEJUMP("sound/doublejump.mp3"),
	JUMP("sound/jump.mp3"),
	HOVER("sound/hover.ogg"),
	LOCKANDLOAD("sound/lockandload.mp3"),
	RELOAD("sound/reload.mp3"),

	AIRBLAST("sound/airblast.mp3"),
	BEAM3("sound/beam3.ogg"),
	BEE_BUZZ("sound/bees.ogg"),
	BOOMERANG_WHIZ("sound/boomerang.mp3"),
	BOMB("sound/bomb.mp3"),
	BOW_SHOOT("sound/bowshoot.mp3"),
	BOW_STRETCH("sound/bowstretch.ogg"),
	CANNON("sound/cannon.mp3"),
	CORK("sound/cork.mp3"),
	CRACKER1("sound/cracker1.mp3"),
	CRACKER2("sound/cracker1.mp3"),
	DARKNESS1("sound/darkness1.mp3"),
	DARKNESS2("sound/darkness2.mp3"),
	DRILL("sound/drill.ogg"),
	ELECTRIC_CHAIN("sound/electric_chain.mp3"),
	EXPLOSION1("sound/explosion1.mp3"),
	EXPLOSION6("sound/explosion6.mp3"),
	EXPLOSION9("sound/explosion9.mp3"),
	EXPLOSION_FUN("sound/explosion_fun.mp3"),
	GUN1("sound/gun1.mp3"),
	GUN2("sound/gun2.mp3"),
	FLAMETHROWER("sound/flamethrower.ogg"),
	FIRE9("sound/fire9.mp3"),
	FIRE10("sound/fire10.mp3"),
	ICE_IMPACT("sound/iceimpact.mp3"),
	LASER2("sound/laser2.mp3"),
	LASERHARPOON("sound/laser_harpoon.mp3"),
	LASERSHOT("sound/lasershot.mp3"),
	LAUNCHER("sound/launcher.mp3"),
	LAUNCHER4("sound/launcher4.mp3"),
	MINIGUN_UP("sound/minigunup.mp3"),
	MINIGUN_DOWN("sound/minigundown.mp3"),
	MINIGUN_LOOP("sound/minigunloop.ogg"),
	METAL_IMPACT_1("sound/metalimpact1.mp3"),
	METAL_IMPACT_2("sound/metalimpact2.mp3"),
	NOISEMAKER("sound/noisemaker.mp3"),
	OOZE("sound/ooze.ogg"),
	PISTOL("sound/pistol.mp3"),
	POPTAB("sound/poptab.mp3"),
	ROCKET("sound/rocket.mp3"),
	ROLLING_ROCKET("sound/rolling_rocket.mp3"),
	SHOOT1("sound/shoot1.mp3"),
	SHAKE("sound/shake.mp3"),
	SHOTGUN("sound/shotgun.mp3"),
	SLAP("sound/slap.mp3"),
	SLASH("sound/slash.mp3"),
	SPIKE("sound/spike.mp3"),
	THUNDER("sound/thunder.mp3"),
	WIND2("sound/wind2.mp3"),
	WIND3("sound/wind3loop.mp3"),
	WOOSH("sound/woosh.mp3"),
	ZAP("sound/zap.mp3"),

	WALL_HIT1("sound/Wall-Hit 1.mp3"),
	DAMAGE1("sound/damage1.mp3"),
	DAMAGE3("sound/damage3.mp3"),
	DAMAGE5("sound/damage5.mp3"),
	DAMAGE6("sound/damage6.mp3"),
	DEFLATE("sound/deflate.mp3"),
	KICK1("sound/kick1.mp3"),
	STAB("sound/stabbing.mp3"),
	SQUISH("sound/squish.mp3"),

	EATING("sound/eating.mp3"),
	FALLING("sound/falling1.mp3"),
	MAGIC1_ACTIVE("sound/magic1.mp3"),
	MAGIC2_FUEL("sound/magic2.mp3"),
	MAGIC11("sound/magic11.mp3"),
	MAGIC18_BUFF("sound/magic18.mp3"),
	MAGIC21_HEAL("sound/magic21.ogg"),
	MAGIC25("sound/magic25.ogg"),
	MAGIC27_EVIL("sound/magic27.mp3"),

	COIN3("sound/coin03.mp3"),
	SPRING("sound/spring.mp3"),
	BLOP("sound/blop.mp3"),
	DOORBELL("sound/doorbell.wav"),
	NEGATIVE("sound/negative0.wav"),
	POSITIVE("sound/positive0.wav"),
	PREFIRE("sound/prefire.mp3"),

	UISWITCH1("sound/switch5.wav"),
	UISWITCH2("sound/switch7.wav"),
	UISWITCH3("sound/switch10.wav"),
	;
	
	private String soundFileName;
	private Sound sound;
	
	SoundEffect(String soundFileName) {
		this.soundFileName = soundFileName;
	}
	
	/**
	 * This loads a selected sound from its filename
	 */
	public Sound getSound() {
		
		if (sound == null) {
			sound = Gdx.audio.newSound(Gdx.files.internal(soundFileName));
		}
		return sound;
	}
	
	/**
	 * This plays a select sound for the player
	 */
	public long play(GameStateManager gsm, boolean singleton) {
		return play(gsm, 1.0f, singleton);
	}
	
	/**
	 * This plays a single sound for the player and returns the sound id
	 * singleton is for sounds that only have 1 instance playing at a time.
	 * Do not use singleton sounds for multiple sound effects
	 */
	public long play(GameStateManager gsm, float volume, boolean singleton) {
		
		if (singleton) {
			getSound().stop();
		}
		
		long thisSound = getSound().play(volume * gsm.getSetting().getSoundVolume() * gsm.getSetting().getMasterVolume());
		
		return thisSound;
	}
	
	/**
	 * This is used to play sounds that have a source in the world.
	 * The volume and pan of the sound depends on the location relative to the player listening.
	 */
	public long playSourced(PlayState state, Vector2 worldPos, float volume, boolean singleton) {

		long soundId = getSound().play();

		updateSoundLocation(state, worldPos, volume, soundId);

		return soundId;
	}
	
	/**
	 * This plays a sound effect for all players.
	 * This is only run by the host. I think.
	 */
	public long playUniversal(PlayState state, Vector2 worldPos, float volume, boolean singleton) {
		
		//Send a packet to the client and play the sound
		if (state.isServer()) {
			HadalGame.server.sendToAllUDP(new Packets.SyncSoundSingle(this, worldPos, volume, singleton));
		}
		
		if (worldPos == null) {
			return play(state.getGsm(), volume, singleton);
		} else {
			return playSourced(state, worldPos, volume, singleton);
		}
	}
	
	/**
	 * This plays a sound effect for a single player.
	 * This is only run by the host
	 */
	public long playExclusive(PlayState state, Vector2 worldPos, Player player, float volume, boolean singleton) {
		
		if (state.isServer() && player != null) {
			
			//for the host, we simply play the sound. Otherwise, we send a sound packet to the client
			if (player.getConnID() == 0) {
				
				if (worldPos == null) {
					return play(state.getGsm(), volume, singleton);
				} else {
					return playSourced(state, worldPos, volume, singleton);
				}
			} else {
				HadalGame.server.sendPacketToPlayer(player, new Packets.SyncSoundSingle(this, worldPos, volume, singleton));
			}
		}
		
		//this line hopefully doesn't get run. (b/c this should not get run on the client or with no input player)
		return (long) 0;
	}
	
	//maxDist is the largest distance the player can hear sounds from.
	//Further sounds will be quieter.
	private final static float maxDist = 3500.0f;
	/**
	 * updateSoundLocation updates the volume and pan of single instance of a sound.
	 * This is done based on the sound's location relative to the player.
	 * This is used for sounds attached to entities. 
	 */
	public void updateSoundLocation(PlayState state, Vector2 worldPos, float volume, long soundId) {
		Player player = state.getPlayer();
		if (player.getBody() != null) {
			
			float xDist = worldPos.x - player.getPixelPosition().x;
			float yDist = worldPos.y - player.getPixelPosition().y;
			float dist = Math.abs(xDist) + Math.abs(yDist);

			float pan = 0.0f;
			float newVolume = 1.0f;
			
			//sound will be played from right/left headphone depending on relative x-coordinate
			if (xDist > maxDist) {
				pan = 1.0f;
			} else if (xDist < -maxDist) {
				pan = -1.0f;
			} else {
				pan = xDist / maxDist;
			}
			
			//sound volume scales inversely to distance from sound
			if (dist > maxDist) {
				newVolume = 0.0f;
			} else if (dist <= 0) {
				newVolume = 1.0f;
			} else {
				newVolume = (maxDist - dist) / maxDist;
			}
			getSound().setPan(soundId, pan, newVolume * volume * state.getGsm().getSetting().getSoundVolume() * state.getGsm().getSetting().getMasterVolume());
		} else {
			getSound().setVolume(soundId, state.getGsm().getSetting().getSoundVolume() * state.getGsm().getSetting().getMasterVolume());
		}
	}
}
