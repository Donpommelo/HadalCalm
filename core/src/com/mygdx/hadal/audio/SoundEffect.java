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

	BLOP("sound/blop.mp3"),
	DOORBELL("sound/doorbell.wav"),
	GUN("sound/gun.mp3"),
	BEE_BUZZ("sound/bees.mp3"),
	BOOMERANG_WHIZ("sound/boomerang.mp3"),
	FLAMETHROWER("sound/flamethrower.mp3"),
	LASER2("sound/laser2.mp3"),
	METAL_IMPACT("sound/metalimpact.mp3"),
	PISTOL("sound/pistol.mp3"),
	SHOOT1("sound/shoot1.mp3"),
	SHOTGUN("sound/shotgun.mp3"),
	SPIKE("sound/spike.mp3"),
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
	public long play(GameStateManager gsm) {
		return play(gsm, 1.0f);
	}
	
	public long play(GameStateManager gsm, float volume) {
		return getSound().play(volume * gsm.getSetting().getSoundVolume() * gsm.getSetting().getMasterVolume());
	}
	
	private final static float maxDist = 3000.0f;
	private Vector2 soundPosition = new Vector2();
	public long playSourced(PlayState state, Vector2 worldPos, float volume) {

		long soundId = getSound().play();

		updateSoundLocation(state, worldPos, volume, soundId);

		return soundId;
	}
	
	/**
	 * This plays a sound effect for all players.
	 */
	public long playUniversal(PlayState state, Vector2 worldPos, float volume) {
		
		if (state.isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncSoundSingle(this, worldPos, volume));
		}
		
		if (worldPos == null) {
			return play(state.getGsm(), volume);
		} else {
			return playSourced(state, worldPos, volume);
		}
	}
	
	/**
	 * This plays a sound effect for a single player.
	 */
	public long playExclusive(PlayState state, Vector2 worldPos, Player player, float volume) {
		
		if (state.isServer() && player != null) {
			
			if (player.getConnID() == 0) {
				if (worldPos == null) {
					return play(state.getGsm(), volume);
				} else {
					return playSourced(state, worldPos, volume);
				}
			} else {
				HadalGame.server.sendPacketToPlayer(player, new Packets.SyncSoundSingle(this, worldPos, volume));
			}
		}
		
		//this line hopefully doesn't get run. (b/c this should not get run on the client or with no input player)
		return play(state.getGsm(), volume);
	}
	
	public void updateSoundLocation(PlayState state, Vector2 worldPos, float volume, long soundId) {
		Player player = state.getPlayer();
		if (player.getBody() != null) {
			
			soundPosition.set(worldPos).sub(player.getPixelPosition());
			float dist = soundPosition.len2();
			float xDist = worldPos.x - player.getPixelPosition().x;
			
			float pan = 0.0f;
			float newVolume = 1.0f;
			if (xDist > maxDist) {
				pan = 1.0f;
			} else if (xDist < -maxDist) {
				pan = -1.0f;
			} else {
				pan = xDist / maxDist;
			}
			
			if (dist > maxDist * maxDist) {
				newVolume = 0.0f;
			} else if (dist <= 0) {
				newVolume = 1.0f;
			} else {
				newVolume = (maxDist * maxDist - dist) / (maxDist * maxDist);
			}
			getSound().setPan(soundId, pan, newVolume * volume * state.getGsm().getSetting().getSoundVolume() * state.getGsm().getSetting().getMasterVolume());
		} else {
			getSound().setVolume(soundId, state.getGsm().getSetting().getSoundVolume() * state.getGsm().getSetting().getMasterVolume());
		}
	}
}
