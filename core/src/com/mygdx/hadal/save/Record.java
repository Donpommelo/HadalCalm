package com.mygdx.hadal.save;

import java.util.Map;

public class Record {

	private int scrap;
	private int scrip;
	
	private Map<String, Integer> hiScores;
	
	public Record() {
		
	}
	
	public int getScrap() {
		return scrap;
	}
	public void setScrap(int scrap) {
		this.scrap = scrap;
	}
	public int getScrip() {
		return scrip;
	}
	public void setScrip(int scrip) {
		this.scrip = scrip;
	}

	public Map<String, Integer> getHiScores() {
		return hiScores;
	}

	public void setHiScores(Map<String, Integer> hiScores) {
		this.hiScores = hiScores;
	}

}
