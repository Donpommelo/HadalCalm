package com.mygdx.hadal.dialog;

import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.utils.TextFilterUtil;

/**
 * A DialogInfo is information about a single Dialog.
 * These are read from json
 * @author Zachary Tu
 */
public class DialogInfo {

	//These strings are to be displayed in the box
	private String name, text, sprite, displayedText;
	
	//This indicates whether this dialogue is the end of the conversation it is a part of
	private boolean end;
	
	//This indicates the desired behaviour when a new dialog is added over this one. (True: override. False : placed after)
	private boolean override;
	
	//This indicates whether to use a smaller window to display the text.
	private boolean small;
	
	//This is the duration in seconds that the dialogue will be active. This can be set to 0 for dialogues that need to be actively skipped
	private float duration;
	
	public DialogInfo() {}

	public DialogInfo(String name, String text, String sprite, boolean end, boolean override, boolean small, float dura) {
		this.name = name;
		this.text = text;
		this.sprite = sprite;
		this.end = end;
		this.override = override;
		this.small = small;
		this.duration = dura;
	}
	
	//This is run before adding a dialog to the dialog box or message window.
	//This filters and formats the text
	public void setDisplayedText(GameStateManager gsm) {
		if (name.equals("")) {
			displayedText = TextFilterUtil.filterGameText(gsm, text);
		} else {
			displayedText = TextFilterUtil.filterGameText(gsm, name + ": " + text);
		}
	}

	public String getSprite() {	return sprite; }

	public boolean isEnd() { return end; }

	public boolean isOverride() { return override; }

	public boolean isSmall() { return small; }

	public float getDuration() { return duration; }
	
	public String getDisplayedText() { return displayedText; }
}
