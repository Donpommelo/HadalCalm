package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

/**
 * Sound Effects each represent a single sound effect in the game. some of these effects can loop.
 * @author Snogganana Smudorf
 */
public enum SoundEffect {

	RUN("sound/run.ogg"),
	DOUBLEJUMP("sound/doublejump.mp3"),
	JUMP("sound/jump.mp3"),
	HOVER("sound/hover.ogg"),
	LOCKANDLOAD("sound/lockandload.mp3"),
	RELOAD("sound/reload.mp3"),

	AIRBLAST("sound/airblast.mp3"),
	AR15("sound/ar15.mp3"),
	ATTACK1("sound/attack1.mp3"),
	BEAM3("sound/beam3.ogg"),
	BEE_BUZZ("sound/bees.ogg"),
	BOOMERANG_WHIZ("sound/boomerang.mp3"),
	BOMB("sound/bomb.mp3"),
	BOTTLE_ROCKET("sound/bottlerocket.mp3"),
	BOW_SHOOT("sound/bowshoot.mp3"),
	BOW_STRETCH("sound/bowstretch.ogg"),
	CANNON("sound/cannon.mp3"),
	CORK("sound/cork.mp3"),
	CRACKER1("sound/cracker1.mp3"),
	CRACKER2("sound/cracker1.mp3"),
	CYBER2("sound/cyber2.mp3"),
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
	SHOOT2("sound/shoot2.mp3"),
	SHAKE("sound/shake.mp3"),
	SHOTGUN("sound/shotgun.mp3"),
	SLAP("sound/slap.mp3"),
	SLASH("sound/slash.mp3"),
	SPIKE("sound/spike.mp3"),
	SPIT("sound/spit.mp3"),
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
	MAGIC0_DAMAGE("sound/magic0.mp3"),
	MAGIC1_ACTIVE("sound/magic1.mp3"),
	MAGIC2_FUEL("sound/magic2.mp3"),
	MAGIC3_BURST("sound/magic3.mp3"),
	MAGIC11_WEIRD("sound/magic11.mp3"),
	MAGIC18_BUFF("sound/magic18.mp3"),
	MAGIC21_HEAL("sound/magic21.ogg"),
	MAGIC25_SPELL("sound/magic25.ogg"),
	MAGIC27_EVIL("sound/magic27.mp3"),

	COIN3("sound/coin03.mp3"),
	SPRING("sound/spring.mp3"),
	BLOP("sound/blop.mp3"),
	DOORBELL("sound/doorbell.wav"),
	NEGATIVE("sound/negative0.wav"),
	PING("sound/button02b.mp3"),
	POSITIVE("sound/positive0.wav"),
	PREFIRE("sound/prefire.mp3"),

	UISWITCH1("sound/switch5.wav"),
	UISWITCH2("sound/switch7.wav"),
	UISWITCH3("sound/switch10.wav"),

	HITSOUND_BLIP("sound/hitsoundBlip.mp3"),
	HITSOUND_COWBELL("sound/hitsoundCowbell.mp3"),
	HITSOUND_DING("sound/hitsoundDing.mp3"),
	HITSOUND_DRUM("sound/hitsoundDrum.mp3"),
	HITSOUND_PIANO("sound/hitsoundPiano.mp3"),
	HITSOUND_SHREK("sound/hitsoundShrek.mp3"),

	;
	
	//this is the filename of the sound
	private final String soundFileName;
	
	//this is the sound associated with the effect
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
	 * This plays a single sound for the player and returns the sound id
	 * singleton is for sounds that only have 1 instance playing at a time. (mostly menus)
	 * Do not use singleton sounds for multiple sound effects
	 */
	public long play(GameStateManager gsm, float volume, boolean singleton) {
		return play(gsm, volume, 1.0f, singleton);
	}
	
	public long play(GameStateManager gsm, float volume, float pitch, boolean singleton) {
		
		if (singleton) {
			getSound().stop();
		}

		return getSound().play(volume * gsm.getSetting().getSoundVolume() * gsm.getSetting().getMasterVolume(), pitch, 0.0f);
	}

	public void playNoModifiers(float volume) {
		getSound().play(volume, 1.0f, 0.0f);
	}

	/**
	 * This is used to play sounds that have a source in the world.
	 * The volume and pan of the sound depends on the location relative to the player listening.
	 */
	public long playSourced(PlayState state, Vector2 worldPos, float volume, float pitch) {

		long soundId = getSound().play();

		getSound().setPitch(soundId, pitch);
		
		updateSoundLocation(state, worldPos, volume, soundId);

		return soundId;
	}
	
	/**
	 * This plays a sound effect for all players.
	 * This is only run by the host. I think.
	 */
	public long playUniversal(PlayState state, Vector2 worldPos, float volume, boolean singleton) {
		return playUniversal(state, worldPos, volume, 1.0f, singleton);
	}
	
	public long playUniversal(PlayState state, Vector2 worldPos, float volume, float pitch, boolean singleton) {
		//Send a packet to the client and play the sound
		if (state.isServer()) {
			HadalGame.server.sendToAllUDP(new Packets.SyncSoundSingle(this, worldPos, volume, pitch, singleton));
		}
		
		if (worldPos == null) {
			return play(state.getGsm(), volume, pitch, singleton);
		} else {
			return playSourced(state, worldPos, volume, pitch);
		}
	}
	
	/**
	 * This plays a sound effect for a single player.
	 * This is only run by the host
	 */
	public long playExclusive(PlayState state, Vector2 worldPos, Player player, float volume, boolean singleton) {
		return playExclusive(state, worldPos, player, volume, 1.0f, singleton);
	}
	
	public long playExclusive(PlayState state, Vector2 worldPos, Player player, float volume, float pitch, boolean singleton) {
		if (state.isServer() && player != null) {
			
			//for the host, we simply play the sound. Otherwise, we send a sound packet to the client
			if (player.getConnID() == 0) {
				
				if (worldPos == null) {
					return play(state.getGsm(), volume, pitch, singleton);
				} else {
					return playSourced(state, worldPos, volume, pitch);
				}
			} else {
				HadalGame.server.sendPacketToPlayer(player, new Packets.SyncSoundSingle(this, worldPos, volume, pitch, singleton));
			}
		}
		
		//this line hopefully doesn't get run. (b/c this should not get run on the client or with no input player)
		return 0;
	}
	
	/**
	 * This registers a hitsound for the chosen player.
	 * Large indicates the damage was high or fatal and pitches up the sound.
	 * This is only run on the server.
	 */
	public static void registerHitSound(GameStateManager gsm, Player player, boolean large) {
		
		//play sound right away for host, otherwise send packet
		if (player.getConnID() == 0) {
			playHitSound(gsm, large);
		} else {
			HadalGame.server.sendPacketToPlayer(player, new Packets.SyncHitSound(large));
		}
	}
	
	/**
	 * This actually plays the hitsound.
	 * This is run for player that dealt the damage and is run for both host or client
	 */
	public static void playHitSound(GameStateManager gsm, boolean large) {
		if (gsm.getSetting().getHitsound() != 0) {
			
			float pitch = 1.0f;
			
			if (large) {
				pitch = 1.5f;
			}
			gsm.getSetting().indexToHitsound().play(gsm,
				gsm.getSetting().getHitsoundVolume() * gsm.getSetting().getMasterVolume(), pitch, true);
		}
	}
	
	//maxDist is the largest distance the player can hear sounds from.
	//Further sounds will be quieter.
	private static final float maxDist = 3500.0f;
	/**
	 * updateSoundLocation updates the volume and pan of single instance of a sound.
	 * This is done based on the sound's location relative to the player.
	 * This is used for sounds attached to entities. 
	 */
	private final Vector2 playerPosition = new Vector2();
	public void updateSoundLocation(PlayState state, Vector2 worldPos, float volume, long soundId) {
		Player player = state.getPlayer();
		if (player.getBody() != null) {
			
			playerPosition.set(player.getPixelPosition());
			float xDist = worldPos.x - playerPosition.x;
			float yDist = worldPos.y - playerPosition.y;
			float dist = Math.abs(xDist) + Math.abs(yDist);

			float pan;
			float newVolume;
			
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
