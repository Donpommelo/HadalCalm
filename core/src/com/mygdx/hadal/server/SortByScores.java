package com.mygdx.hadal.server;

import java.util.Comparator;

public class SortByScores implements Comparator<SavedPlayerFields> {

	@Override
	public int compare(SavedPlayerFields a, SavedPlayerFields b) {
		return b.getScore() - a.getScore();
	}
}