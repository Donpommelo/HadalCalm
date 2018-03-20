package com.mygdx.hadal.dialogue;

import com.mygdx.hadal.event.userdata.EventData;

public class Dialogue {

	private String name, text;
	private boolean end;
	
	private EventData radio, trigger;
	
	public Dialogue(String name, String text, boolean end, EventData radio, EventData trigger) {
		this.name = name;
		this.text = text;
		this.end = end;
		this.radio = radio;
		this.trigger = trigger;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public EventData getRadio() {
		return radio;
	}

	public void setRadio(EventData radio) {
		this.radio = radio;
	}

	public EventData getTrigger() {
		return trigger;
	}

	public void setTrigger(EventData trigger) {
		this.trigger = trigger;
	}

}
