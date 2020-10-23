package com.mygdx.hadal.save;

import java.util.LinkedHashMap;

/**
 * A ShopInfo includes information about what a shop is selling + prices.
 * This is used by the Quartermaster Hub Event to display selection
 * @author Lisky Lerzivington
 */
public class ShopInfo {

	//the type of shop this is (selling weapons, artifacts etc?)
	private String type;
	
	//mapping of items to prices sold at this shop
	private LinkedHashMap<String, Integer> prices;
	
	public ShopInfo() {}

	public String getType() { return type; }

	public LinkedHashMap<String, Integer> getPrices() { return prices; }
}

