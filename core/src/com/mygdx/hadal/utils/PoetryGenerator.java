package com.mygdx.hadal.utils;

import java.util.ArrayList;

import com.mygdx.hadal.managers.GameStateManager;

public enum PoetryGenerator {

	INDEP_CLAUSE_START("", "indep_clause_start", "indep_clause_next", "start"),
	INDEP_CLAUSE_END(".", "end", "", "indep_clause_next"),
	
	PP_START("", "pp_start", "pp_end", "indep_clause_start"),
	PP_NEXT(",", "pp_next", "", "pp_end"),
	
	ADV_START("", "adv_start", "adv_end", "indep_clause_start"),
	ADV_NEXT(",", "adv_next", "", "adv_end"),
	
	NP_START("", "np_start", "np_next", "indep_clause_start"),

	NP_V("", "np_v", "_v", "pp_next", "adv_next"),
	NP_VP("", "np_vp", "_vp", "pp_next", "adv_next"),
	NP_ADV("", "np_adv", "_vp", "pp_next", "adv_next"),
	V__PP("", "v_pp", "_pp", "np_next"),
	V__ADV("", "v_adv", "_adv", "np_next"),
	VP__ADV("", "vp_adv", "_adv", "np_next"),
	
	PREPOSITION(" <preposition>", "prep", "", "pp", "_p"),
	
	NOUN_PHRASE("", "np", "", "np_start", "np_v", "np_vp", "np_adv"),
	VERB_PHRASE("", "vp", "np", "_vp", "vp_adv"),
	PREP_PHRASE("", "pp", "np", "pp_start", "_pp"),

	DETERMINER("", "determiner", "", "np"),
	
	THE(" the", "the", "", "determiner"),
	A(" a", "a", "", "determiner"),
	
	NOUN("", "noun", "", "a", "the", "adjective"),
	NOUN_ABSTRACT(" <noun_abstract>", "noun_abstract", "", "noun"),
	NOUN_ANIMAL(" <noun_animal>", "noun_animal", "", "noun"),
	NOUN_CLOTHING(" <noun_clothing>", "noun_clothing", "", "noun"),
	NOUN_FRUIT(" <noun_fruit>", "noun_fruit", "", "noun"),
	NOUN_FURNITURE(" <noun_furniture>", "noun_furniture", "", "noun"),
	NOUN_INSTRUMENT(" <noun_instrument>", "noun_instrument", "", "noun"),
	NOUN_MATERIAL(" <noun_material>", "noun_material", "", "noun"),
	NOUN_OBJECT(" <noun_object>", "noun_object", "", "noun"),
	NOUN_VEGETABLE(" <noun_vegetable>", "noun_vegetable", "", "noun"),

	ADJECTIVE(" <adjective>", "adjective", "", "a", "the"),
	
	VERB(" <verb>", "verb", "", "_v", "vp", "v_pp", "v_adv"),
	ADVERB(" <adverb>", "adverb", "", "adv_start"),
	
	;
	
	//This name and the end of this name fragment
	private String me, endTag, endPhrase;
	
	//how likely is this segment to follow any of its predecessors?
	private int weight;
	
	//A list of name fragments that this name fragment can follow.
	private String[] canFollow;
	
	PoetryGenerator(String me, String endTag, String endPhrase, int weight, String... canFollow) {
		this.me = me;
		this.endTag = endTag;
		this.endPhrase = endPhrase;
		this.weight = weight;
		this.canFollow = canFollow;
	}
	
	PoetryGenerator(String me, String endTag, String endPhrase, String... canFollow) {
		this(me, endTag, endPhrase, 1, canFollow);
	}
	
	/**
	 * Base Case for recursive name generation.
	 * Start with a name fragment with a "start" tag
	 */
	public static String generatePoetry() {
		return TextFilterUtil.filterPoemTags(generateName("start"));
	}
	
	/**
	 * Recursive case. Randomly select a valid next name fragment
	 * @param prev: The name so far
	 * @return: The generated name fragment, plus the rest of the name
	 */
	public static String generateName(String prev) {
		
		ArrayList<PoetryGenerator> possibleNexts = new ArrayList<PoetryGenerator>();
		
		for (PoetryGenerator gen: PoetryGenerator.values()) {
			for (int i = 0; i < gen.canFollow.length; i++) {
				if (gen.canFollow[i].equals(prev)) {
					for (int j = 0; j < gen.weight; j++) {
						possibleNexts.add(gen);
					}
				}
			}
		}
		
		if (possibleNexts.isEmpty()) {
			return "";
		}
		
		int randomIndex = GameStateManager.generator.nextInt(possibleNexts.size());
		PoetryGenerator next = possibleNexts.get(randomIndex);
		
		if (next.endTag.equals("end")) {
			return TextFilterUtil.filterPoemTags(next.me);
		}
		
		String nextWord = TextFilterUtil.filterPoemTags(generateName(next.endTag));
		String thisWord = TextFilterUtil.filterPoemTags(next.me);
		
		if (next.endTag.equals("a")) {
			if (nextWord.length() >= 2) {
				if (nextWord.charAt(1) == 'a' || nextWord.charAt(1) == 'e' || nextWord.charAt(1) == 'i' || nextWord.charAt(1) == 'o' || nextWord.charAt(1) == 'u') {
					thisWord = " an";
				}
			}
		}
		
		if (!next.endPhrase.equals("")) {
			return thisWord + nextWord + TextFilterUtil.filterPoemTags(generateName(next.endPhrase));
		}
		
		return thisWord + nextWord;
	}
}
