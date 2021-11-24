package com.mygdx.hadal.save;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.save.UnlockManager.UnlockTag;
import com.mygdx.hadal.text.TextFilterUtil;

/**
 * An InfoItem represents the information for a single unlockable item to be loaded/saved to json
 * @author Cikola Cluthurlando
 */
public class InfoItem {

	private String name;
	private String description;
	private String descriptionLong;

	private Array<UnlockTag> tags = new Array<>();
	
	public InfoItem() {}

	public String getName() { return name; }

	public String getDescription() { return description; }
	
	public String getDescriptionLong() { return TextFilterUtil.filterText(descriptionLong); }
	
	public Array<UnlockTag> getTags() { return tags; }

	public void setName(String name) { this.name = name; }

	public void setDescription(String description) { this.description = description; }

	public void setTags(Array<UnlockTag> tags) { this.tags = tags; }
}
