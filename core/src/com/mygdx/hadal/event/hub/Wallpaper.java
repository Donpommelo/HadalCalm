package com.mygdx.hadal.event.hub;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UIHub.hubTypes;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.states.PlayState;

/**
 * The Wallpaper (vendor?) is a hub event that allows players to change the shader background.
 * @author Furdhaz Fuvinsky
 */
public class Wallpaper extends HubEvent {

	public static final Shader[] SHADERS = {Shader.NOTHING, Shader.SPLASH, Shader.WAVE, Shader.DRIP, Shader.WIGGLE_STATIC,
		Shader.PLASMA, Shader.WHIRLPOOL, Shader.NORTHERN_LIGHTS, Shader.CLOUD};

	public Wallpaper(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.WALLPAPER);
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (int i = 0; i < SHADERS.length; i++) {
			final int index = i;
			Text itemChoose = new Text(SHADERS[i].name()).setButton(true);
			itemChoose.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {
					state.setShaderBase(SHADERS[index]);
					state.getGsm().getSetting().setCustomShader(index);
				}
			});
			itemChoose.setScale(UIHub.OPTIONS_SCALE);
			hub.getTableOptions().add(itemChoose).height(UIHub.OPTION_HEIGHT).pad(UIHub.OPTION_PAD, 0, UIHub.OPTION_PAD, 0).row();
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.OPTION_HEIGHT).row();
	}
}
