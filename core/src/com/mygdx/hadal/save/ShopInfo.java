package com.mygdx.hadal.save;

import java.util.Map;

public class ShopInfo {

	private String type;
	private Map<String, Integer> prices;
	
	public ShopInfo() {}

	public String getType() { return type; }

	public Map<String, Integer> getPrices() { return prices; }
}

