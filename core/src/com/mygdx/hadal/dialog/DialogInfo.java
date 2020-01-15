package com.mygdx.hadal.dialog;

public class DialogInfo {

	//These strings are to be displayed in the box
	private String name, text, sprite;
	
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
	
	public String getName() { return name; }

	public String getText() { return text; }

	public String getSprite() {	return sprite; }

	public boolean isEnd() { return end; }

	public boolean isOverride() { return override; }

	public boolean isSmall() { return small; }

	public float getDuration() { return duration; }
}
