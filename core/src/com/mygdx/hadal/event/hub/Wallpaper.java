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

	public static final Shader[] shaders = {Shader.NOTHING, Shader.SPLASH, Shader.WAVE, Shader.DRIP, Shader.WIGGLE_STATIC,
		Shader.PLASMA, Shader.WHIRLPOOL, Shader.NORTHERN_LIGHTS, Shader.CLOUD};

	public Wallpaper(PlayState state, Vector2 startPos, Vector2 size, String title, String tag, boolean checkUnlock, boolean closeOnLeave) {
		super(state, startPos, size, title, tag, checkUnlock, closeOnLeave, hubTypes.WALLPAPER);
	}

	@Override
	public void enter() {
		super.enter();
		final UIHub hub = state.getUiHub();
		
		for (int i = 0; i < shaders.length; i++) {
			final int index = i;
			Text itemChoose = new Text(shaders[i].name()).setButton(true);
			itemChoose.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent e, float x, float y) {
					state.setShaderBase(shaders[index]);
					state.getGsm().getSetting().setCustomShader(index);
				}
			});
			itemChoose.setScale(UIHub.optionsScale);
			hub.getTableOptions().add(itemChoose).height(UIHub.optionHeight).pad(UIHub.optionPad, 0, UIHub.optionPad, 0).row();
		}
		hub.getTableOptions().add(new Text("")).height(UIHub.optionsHeight).row();
	}
}
