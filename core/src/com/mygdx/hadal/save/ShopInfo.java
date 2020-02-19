package com.mygdx.hadal.save;

import java.util.Map;

/**
 * A ShopInfo includes information about what a shop is selling + prices.
 * This is used by the Quartermaster Hub Event to display selection
 * @author Zachary Tu
 *
 */
public class ShopInfo {

	private String type;
	private Map<String, Integer> prices;
	
	public ShopInfo() {}

	public String getType() { return type; }

	public Map<String, Integer> getPrices() { return prices; }
}

