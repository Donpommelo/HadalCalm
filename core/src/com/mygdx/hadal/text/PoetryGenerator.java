package com.mygdx.hadal.text;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * a frightful resentment invests like a lens.
 * otherwise, a selfish thimble writhes.
 * witness! the misshapen crux of cartilage.
 * @author Jaardvark Jilfibberish
 */
public enum PoetryGenerator {

	VERSE1_START("", "verse1_start", "verse1_next", "start1"),
	VERSE1_END(".", "end", "", "verse1_next"),
	
	VERSE2_START("", "verse2_start", "verse2_next", "start2"),
	VERSE2_END(".", "end", "", "verse2_next"),
	
	VERSE3_START("", "verse3_start", "verse3_next", "start3"),
	VERSE3_END(".", "end", "", "verse3_next"),
	
	PP_START("", "pp_start", "pp_end", 1, 1, "verse1_start"),
	PP_NEXT(",", "pp_next", "", "pp_end"),
	
	ADV_START("", "adv_start", "adv_end", "verse1_start"),
	ADV_NEXT(",", "adv_next", "", "adv_end"),
	
	NP_START("", "np_start", "np_next", "verse1_start"),

	SC_START("", "sc_start", "sc_end1", 1, 1, "verse1_start"),
	SC_NEXT1("", "sc_next1", "sc_end2", "sc_end1"),
	SC_NEXT2(",", "sc_next2", "", "sc_end2"),
	
	CONJ_ADV_START("", "conj_adv_start", "conj_adv_end", "verse2_start"),
	CONJ_ADV_NEXT(",", "conj_adv_next", "", "conj_adv_end"),
	
	ENDER_START("", "ender_start", "ender_end", "verse3_start"),
	ENDER_NEXT("!", "ender_next", "", "ender_end"),

	NP_VP_NO_ADV("", "np_vp", "_vp_no_adv", "adv_next"),
	NP_VP("", "np_vp", "_vp", "pp_next", "conj_adv_next", "_np_vp", "sc_next1", "sc_next2"),
	
	NP_VP_ADV("", "np_vp_no_adv_adv", "_vp_adv", "pp_next"),
	VP_ADV("", "vp_no_adv_adv", "_adv", "np_next", "_vp_adv"),
	
	V_NP("", "v_np", "_np", 1, 1, "vp", "vp_no_adv"),
	VP_PP("", "vp_pp", "_pp", 1, 1, "np_next"),
	VP_CONJ_NP_VP("", "vp_conj_np_vp", "_conj_np_vp", 1, 1, "np_next"),
	VP_SC_NP_VP("", "vp_sc_np_vp", "_sc_np_vp", 1, 1, "np_next"),
	CONJ_NP_VP("", "conj_np_vp", "_np_vp", "_conj_np_vp"),
	SC_NP_VP("", "sc_np_vp", "_np_vp", "_sc_np_vp"),
	
	PREPOSITION(" <preposition>", "prep", "", "pp", "_p"),
	
	NOUN_PHRASE("", "np", "", "_np", "np_start", "np_v", "np_vp", "np_vp_no_adv_adv"),
	VERB_PHRASE("", "vp", "", "_vp", "np_next", "vp_start", "vp_conj_np_vp", "vp_sc_np_vp", "vp_pp"),
	VERB_PHRASE_NO_ADVERB("", "vp_no_adv", "", "_vp_no_adv", "vp_no_adv_adv"),
	PREP_PHRASE("", "pp", "np", "pp_start", "_pp"),

	DETERMINER("", "determiner", "", "np"),
	THE(" the", "the", "", "determiner"),
	A(" a", "a", "", "determiner"),
	
	DETERMINER_NO_NOUN("", "det_no_noun", "", "det_adj_n_extra"),
	THE_NO_NOUN(" the", "the_no_noun", "", "det_no_noun"),
	A_NO_NOUN(" a", "a_no_noun ", "", "det_no_noun"),
	
	NOUN("", "n", "", "a", "the", "_n", "n_of_material", "n_extra"),
	NOUN_ABSTRACT(" <noun_abstract>", "n_abstract", "", 3, 0, "n"),
	NOUN_ANIMAL(" <noun_animal>", "n_animal", "", 3, 0, "n"),
	NOUN_CLOTHING(" <noun_clothing>", "n_clothing", "", "n"),
	NOUN_FRUIT(" <noun_fruit>", "n_fruit", "", 2, 0, "n"),
	NOUN_FURNITURE(" <noun_furniture>", "n_furniture", "", "n"),
	NOUN_INSTRUMENT(" <noun_instrument>", "n_instrument", "", "n"),
	NOUN_MATERIAL(" <noun_material>", "n_material", "", 3, 0, "n", "_material"),
	NOUN_OBJECT(" <noun_object>", "n_object", "", 3, 0, "n"),
	NOUN_PEOPLE(" <noun_people>", "n_people", "", 3, 0, "n", "extra"),
	NOUN_VEGETABLE(" <noun_vegetable>", "n_vegetable", "", "n"),

	ADJECTIVE_NOUN("", "adj_n", "_n", 3, 1, "a", "the"),
	ADJECTIVE(" <adjective>", "adj", "", "adj_n", "adj_n_extra"),
	
	COLOR_NOUN("", "color_n", "_n", 1, 1, "a", "the"),
	COLOR(" <color>", "color", "", "color_n"),
	
	NOUN_OF_MATERIAL("", "n_of_material", "_of_material", 1, 1, "a", "the"),
	OF_MATERIAL(" of", "of_material", "_material", "_of_material", "extra"),
	
	VERB(" <verb>", "v", "", "_v", "vp", "vp_no_adv", "v_np", "v_adv", "v_and_v"),
	ADVERB(" <adverb>", "adv", "", "adv_start", "adv_v", "_adv"),
	ADVERB_VERB("", "adv_v", "_v", 1, 1, "vp"),
	VERB_AND_VERB("", "v_and_v", "_and_v", 1, 1, "vp", "vp_no_adv"),
	AND_VERB(" and", "and_v", "_v", "_and_v"),
	COORD_CONJ(", <coordinating_conjunction>", "coord_conj", "", "conj_np_vp"),
	SUBORD_CONJ(" <subordinating_conjunction>", "sub_conj", "", "sc_start", "sc_np_vp"),
	CONJ_ADV(" <conjunctive_adverb>", "conj_adv", "", "conj_adv_start"),
	
	VERSE_ENDER(" <verse_enders>", "verse_ender", "", "ender_start"),
	DETERMINER_ADJECTIVE_NOUN_EXTRA("", "det_adj_n_extra", "_adj_n_extra", "ender_next"),
	ADJECTIVE_NOUN_EXTRA("", "adj_n_extra", "_n_extra", "_adj_n_extra"),
	_NOUN_EXTRA("", "n_extra", "_extra", "_n_extra"),
	EXTRA("", "extra", "", "_extra"),
	;
	
	private static final int maxLengthExtras = 2;
	
	//This name and the end of this poem fragment
	private final String me, endTag, endPhrase;
	
	//how likely is this segment to follow any of its predecessors?
	private final int weight;
	
	//this counts the amount of "extra" modifiers (like adjectives/adverbs) This is limited to avoid overly long lines.
	private final int lengthExtra;
	
	//A list of poem fragments that this poem fragment can follow.
	private final String[] canFollow;
	
	PoetryGenerator(String me, String endTag, String endPhrase, int weight, int lengthExtra, String... canFollow) {
		this.me = me;
		this.endTag = endTag;
		this.endPhrase = endPhrase;
		this.weight = weight;
		this.lengthExtra = lengthExtra;
		this.canFollow = canFollow;
	}
	
	PoetryGenerator(String me, String endTag, String endPhrase, String... canFollow) {
		this(me, endTag, endPhrase, 1, 0, canFollow);
	}
	
	/**
	 * Base Case for recursive name generation.
	 * Start with a name fragment with a "start" tag
	 */
	public static String generatePoetry() {
		
		currentLengthExtra = 0;
		String verse1 = TextFilterUtil.filterPoemTags(generateName("start1"));
		currentLengthExtra = 0;
		String verse2 = TextFilterUtil.filterPoemTags(generateName("start2"));
		currentLengthExtra = 0;
		String verse3 = TextFilterUtil.filterPoemTags(generateName("start3"));

		return  verse1 + "\n" + verse2 + "\n" + verse3;
	}
	
	//this keeps track of how many "extra length" poem segments have been added.
	//If we have too many "extra length" segments, we stop adding more to avoid overly long poems
	private static int currentLengthExtra;
	/**
	 * Recursive case. Randomly select a valid next poem fragment
	 * @param prev: The poem so far
	 * @return The generated poem fragment, plus the rest of the poem
	 */
	public static String generateName(String prev) {

		Array<PoetryGenerator> possibleNexts = new Array<>();
		
		//identify all possible next poetry fragments
		for (PoetryGenerator gen: PoetryGenerator.values()) {
			for (int i = 0; i < gen.canFollow.length; i++) {
				if (gen.canFollow[i].equals(prev)) {
					for (int j = 0; j < gen.weight; j++) {
						
						//if poem has too many "extra length" fragments, we cannot add any more.
						if (currentLengthExtra + gen.lengthExtra <= maxLengthExtras) {
							possibleNexts.add(gen);
						}
					}
				}
			}
		}
		
		if (possibleNexts.isEmpty()) { return ""; }
		
		//pick a random possible next fragment and add it.
		int randomIndex = MathUtils.random(possibleNexts.size - 1);
		PoetryGenerator next = possibleNexts.get(randomIndex);
		currentLengthExtra += next.lengthExtra;
		
		if (next.endTag.equals("end")) {
			return TextFilterUtil.filterPoemTags(next.me);
		}
		
		//filter poem fragments which contain extra text tags
		String nextWord = TextFilterUtil.filterPoemTags(generateName(next.endTag));
		String thisWord = TextFilterUtil.filterPoemTags(next.me);
		
		//deal with a/an and vowels
		if (next.endTag.equals("a")) {
			if (nextWord.length() >= 2) {
				if (nextWord.charAt(1) == 'a' || nextWord.charAt(1) == 'e' || nextWord.charAt(1) == 'i' || nextWord.charAt(1) == 'o' || nextWord.charAt(1) == 'u') {
					thisWord = " an";
				}
			}
		}

		//append proper end phase (if existent)
		if (!next.endPhrase.equals("")) {
			return thisWord + nextWord + TextFilterUtil.filterPoemTags(generateName(next.endPhrase));
		}
		
		return thisWord + nextWord;
	}
}
