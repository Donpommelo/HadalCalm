package com.mygdx.hadal.server;

import java.util.Comparator;

/**
 * This comparator is used to sort saved player fields by score
 * @author Zachary Tu
 */
public class SortByScores implements Comparator<SavedPlayerFields> {

	@Override
	public int compare(SavedPlayerFields a, SavedPlayerFields b) {
		return b.getScore() - a.getScore();
	}
}