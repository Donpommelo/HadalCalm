package com.mygdx.hadal.utils;

import java.util.ArrayList;

import com.mygdx.hadal.managers.GameStateManager;

/**
 * why did I make this
 * @author Zachary Tu
 *
 */
public enum NameGenerator {
	
	B_START("b", "b", "start", "start", "start"),
	C_START("c", "c", "start"),
	D_START("d", "d", "start"),
	F_START("f", "f", "start"),
	G_START("g", "g", "start", "start", "start"),
	H_START("h", "h", "start"),
	J_START("j", "j", "start"),
	K_START("k", "k", "start"),
	L_START("l", "l", "start"),
	M_START("m", "m", "start"),
	N_START("n", "n", "start"),
	P_START("p", "p", "start"),
	QU_START("qu", "qu", "start"),
	R_START("r", "r", "start"),
	S_START("s", "s", "start", "start", "start", "start", "start"),
	T_START("t", "t", "start", "start", "start", "start", "start"),
	V_START("v", "v", "start"),
	W_START("w", "w", "start"),
	X_START("x", "x", "start"),
	Y_START("y", "y", "start"),
	Z_START("z", "z", "start"),
	
	C_NEXT("c", "c", "s"),
	H_NEXT("h", "h", "c", "g", "p", "s", "t", "w"),
	K_NEXT("k", "k", "s"),
	L_NEXT("l", "l", "b", "c", "f", "g", "p", "s", "v"),
	M_NEXT("m", "m", "s"),
	N_NEXT("n", "n", "g", "s"),
	P_NEXT("p", "p", "s"),
	Q_NEXT("qu", "qu", "s"),
	R_NEXT("r", "r", "b", "c", "d", "f", "g", "k", "p", "t", "w"),
	T_NEXT("t", "t", "s"),
	V_NEXT("v", "v", "s"),
	W_NEXT("w", "w", "s", "t"),
	
	A_VOWEL("a", "a", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	E_VOWEL("e", "e", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	I_VOWEL("i", "i", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	O_VOWEL("o", "o", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	U_VOWEL("u", "u", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	
	
	DGE("dge", "dge", "a", "e", "i", "o", "u"),
	GG("gg", "gg", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	
	FT("ft", "f_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	
	LB("lb", "l_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	LC("lc", "l_", "a", "e", "i", "o", "u"),
	LD("ld", "l_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	LF("lf", "l_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	LG("lg", "l_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	LM("lm", "l_", "a", "e", "i", "o", "u"),
	LN("ln", "l_", "a", "e", "i", "o", "u"),
	LP("lp", "l_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	LT("lt", "l_", "a", "e", "i", "o", "u"),
	LV("lv", "l_", "a", "e", "i", "o", "u"),
	LZ("lz", "l_", "a", "e", "i", "o", "u"),
	
	MB("mb", "m_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	MP("mp", "m_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	
	ND("nd", "n_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	NG("ng", "n_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	NK("nk", "n_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	NT("nt", "n_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	
	RB("rb", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RD("rd", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RF("rf", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RG("rg", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RK("rk", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RL("rl", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RM("rm", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RN("rn", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RP("rp", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	RT("rt", "r_", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	
	BBLE("bble", "le", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	CKLE("ckle", "le", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	DDLE("ddle", "le", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	FFLE("ffle", "le", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	GGLE("ggle", "le", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	PPLE("pple", "le", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	SSLE("ssle", "le", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),
	TTLE("ttle", "le", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u"),

	_NOTHING_("", "end", "gg", "dge", "f_", "l_", "m_", "n_", "r_", "le"),
	ART("art", "end", "gg", "f_", "l_", "m_", "n_", "r_"),
	SWORTH("sworth", "end", "a", "e", "i", "o", "u", "gg", "dge", "l_", "m_", "n_", "r_", "le"),
	BERG("berg", "end", "a", "e", "i", "o", "u", "f_", "l_", "m_", "n_", "r_", "le"),
	BERT("bert", "end", "a", "e", "i", "o", "u", "f_", "l_", "m_", "n_", "r_", "le"),
	BERRY("berry", "end", "gg", "dge", "f_", "l_", "m_", "n_", "r_"),
	BLATT("blatt", "end", "a", "e", "i", "o", "u", "f_", "l_", "m_", "n_", "r_", "le"),
	BOTTOMS("bottoms", "end", "a", "e", "i", "o", "u", "f_", "l_", "m_", "n_", "r_", "le"),
	DALE("dale", "end", "a", "e", "i", "o", "u", "gg", "f_", "l_", "m_", "n_", "r_", "le"),
	NUT("nut", "end", "gg", "dge", "f_", "l_", "m_", "n_", "r_"),
	SBY("sby", "end", "a", "e", "i", "o", "u", "gg", "l_",  "m_", "n_", "r_", "le"),
	SMITH("smith", "end", "a", "e", "i", "o", "u", "f_", "l_", "m_", "n_", "r_", "le"),
	SON("son", "end", "f_", "l_", "m_", "n_", "r_"),
	TON("ton", "end", "l_", "m_", "n_", "r_"),
	INGTON("ington", "end", "f_", "l_", "m_", "n_", "r_"),

	IE("ie", "end", "f_", "l_", "n_", "m_", "r_"),
	O("o", "end", "gg", "n_", "m_", "r_"),
	Y("y", "end", "gg", "n_", "m_", "r_", "le"),
	R_END("r", "end", "le"),
	GINS("gins", "end", "l_", "r_", "le"),
	KINS("kins", "end", "gg", "l_", "r_", "le"),
	
	ACAMOLE("acamole", "end", "u"),
	ADRUNNER("adrunner", "end", "o"),
	AMOFMUSHROOM("amofmushroom", "end", "e"),
	ANUTBUTTER("anutbutter", "end", "e"),
	ATMEAL("atmeal", "end", "o"),
	AZEBUCKET("azebucket", "end", "e"),
	
	BAGANOUSH("baganoush", "end", "a", "o", "u"),
	BALANTE("balante", "end", "a", "e", "i", "o", "u"),
	BAMA("bama", "end", "o"),
	BANERO("banero", "end", "a"),
	BBADON("bbadon", "end", "a", "e", "i", "o", "u"),
	BDUL("bdul", "end", "a", "e", "i", "o", "u"),
	BBERER("bberer", "end", "a", "e", "i", "o", "u"),
	BBERISH("bberish", "end", "a", "e", "i", "o", "u"),
	BBERY("bbery", "end", "a", "e", "i", "o", "u"),
	BBITY("bbity", "end", "a", "e", "i", "o", "u"),
	BANFOO("banfoo", "end", "a", "e", "i", "o", "u"),
	BASCO("basco", "end", "a"),
	BASTIAN("bastian", "end", "a", "e", "i", "o", "u"),
	BEDIAH("bediah", "end", "a", "e", "i", "o", "u"),
	BIDEEN("bideen", "end", "a", "e", "i", "o", "u"),
	BISCUS("biscus", "end", "a", "e", "i", "o", "u"),
	BISH("bish", "end", "a", "e", "i", "o", "u"),
	BITHA("bitha", "end", "a", "e", "i", "o", "u"),
	BRAMANIAM("bramaniam", "end", "u"),
	BRAXAS("braxas", "end", "a"),
	BRINA("brina", "end", "a", "e", "i", "o", "u"),
	BRUARY("bruary", "end", "a", "e", "i", "o", "u"),
	
	CANDCHEESE("candcheese", "end", "a"),
	CAINE("caine", "end", "o"),
	CARDO("cardo", "end", "a", "e", "i", "o", "u"),
	CASTE("caste", "end", "a"),
	CATINI("catini", "end", "u"),
	CCIATELLO("cciatello", "end", "a", "e", "i", "o", "u"),
	CCINI("ccini", "end", "a", "e", "i", "o", "u"),
	CHARD("chard", "end", "a", "e", "i", "o", "u"),
	CHARY("chary", "end", "a", "e", "i", "o", "u"),
	CHELLE("chelle", "end", "a", "e", "i", "o", "u"),
	CHNOLD("chnold", "end", "a", "e", "i", "o", "u"),
	CHYSOISSE("chysoisse", "end", "i"),
	CIEWEITZ("cieweitz", "end", "a", "e", "i", "o", "u"),
	CKADEE("ckadee", "end", "a", "e", "i", "o", "u"),
	CKER("cker", "end", "a", "e", "i", "o", "u"),
	CKERAL("ckeral", "end", "a"),
	CKHEART("ckheart", "end", "a", "e", "i", "o", "u"),
	CKINGBIRD("ckingbird", "end", "a", "e", "i", "o", "u"),
	CKWHEAT("ckwheat", "end", "u"),
	CNICBASKET("cnicbasket", "end", "a", "e", "i", "o", "u"),
	CONUT("conut", "end", "o"),
	CORY("cory", "end", "i"),
	CORICE("corice", "end", "i"),
	CUMBER("cumber", "end", "u"),
	CYCLE("cycle", "end", "i"),
	
	DALUPE("dalupe", "end", "a"),
	DARDUS("dardus", "end", "a", "e", "i", "o", "u"),
	DDEUS("ddeus", "end", "a", "e", "i", "o", "u"),
	DDING("dding", "end", "a", "e", "i", "o", "u"),
	DELAIRE("delaire", "end", "a", "e", "i", "o", "u"),
	DEON("deon", "end", "a", "e", "i", "o", "u"),
	DERICK("derick", "end", "a", "e", "i", "o", "u"),
	DICULOUS("diculous", "end", "a", "e", "i", "o", "u"),
	DIMIR("dimir", "end", "a", "e", "i", "o", "u"),
	DISON("dison", "end", "a"),
	DITYA("ditya", "end", "a"),
	DOLPH("dolph", "end", "a", "o", "u"),
	DRACH("drach", "end", "a", "o", "u"),
	DUARDO("duardo", "end", "a", "o", "u"),
	DYPOO("dypoo", "end", "o", "u"),
	DZILLA("dzilla", "end", "o"),
	
	EBIRD("ubird", "end", "u"),
	EFEATER("efeater", "end", "e"),
	EFUS("efus", "end", "e"),
	EP("ep", "end", "e"),
	EPAK("epak", "end", "e"),
	EPLE("eple", "end", "e"),
	ERIO("erio", "end", "e"),
	ESESTEAK("esesteak", "end", "e"),
	EZLEBUB("ezlebub", "end", "e"),
	EZY("ezy", "end", "e"),

	FAEL("fael", "end", "a", "e", "i", "o", "u"),
	FKA("fka", "end", "a", "e", "i", "o", "u"),
	FFALO("ffalo", "end", "a", "e", "i", "o", "u"),
	FFERSON("fferson", "end", "a", "e", "i", "o", "u"),
	FFERTY("fferty", "end", "a", "e", "i", "o", "u"),
	
	GARFREE("garfree", "end", "u"),
	GARTH("garth", "end", "a", "e", "i", "o", "u"),
	GATRON("gatron", "end", "a", "e", "i", "o", "u"),
	GDALENA("gdalena", "end", "a", "e", "i", "o", "u"),
	GDANOFF("gdanoff", "end", "a", "e", "i", "o", "u"),
	GEY("gey", "end", "o"),
	GGDROP("ggdrop", "end", "e"),
	GGER("gger", "end", "a", "e", "i", "o", "u"),
	GGINBOTHAM("gginbotham", "end", "a", "e", "i", "o", "u"),
	GGPLANT("ggplant", "end", "e"),
	GHERTY("gherty", "end", "a", "e", "i", "o", "u"),
	GHETTI("ghetti", "end", "a"),
	GINALD("ginald", "end", "a", "e", "i", "o", "u"),
	GLODYTE("glodyte", "end", "a","o", "u"),
	GMAC("gmac", "end", "e", "i"),
	GMA("gmac", "end", "a", "e", "i", "o", "u"),
	GNIFICANT("gnificant", "end", "i"),
	GOR("gor", "end", "a", "e", "i", "o", "u"),
	GORY("gory", "end", "a", "e", "i", "o", "u"),
	GPIE("gpie", "end", "a"),
	GUANA("guana", "end", "a", "e", "i", "o", "u"),
	GUEL("guel", "end", "a", "e", "i", "o", "u"),

	HAMMAD("hammad", "end", "a", "o", "u"),
	HEMOTH("hemoth", "end", "e"),
	
	
	ID("id", "end", "o"),
	INSLEY("insley", "end", "a"),
	IQUIRI("iquiri", "end", "a"),
	ISEDPORK("isedpork", "end", "a"),
	ITBASKET("itbasket", "end", "u"),
	ITCAKE("itcake", "end", "u"),

	JANDRO("jandro", "end", "a", "e", "i", "o", "u"),
	JEED("jandro", "end", "a", "u"),
	JITO("jito", "end", "o"),
	JUBE("jube", "end", "u"),

	KACHU("kachu", "end", "i"),
	KEDPOTATO("kedpotato", "end", "a"),
	KEEM("kedpotato", "end", "a"),
	
	LABASH("labash", "end", "a"),
	LAFEL("lafel", "end", "a"),
	LAMARI("lamari", "end", "a"),
	LANDA("landa", "end", "a", "e", "i", "o", "u"),
	LANTRO("lantro", "end", "i"),
	LAPENO("lapeno", "end", "a"),
	LARIO("lario", "end", "a", "e", "i", "o", "u"),
	LAZAR("lazar", "end", "a", "e"),
	LBATROSS("lbatross", "end", "a", "e", "i", "o", "u"),
	LBERO("lboro", "end", "a", "e", "i", "o", "u"),
	LCHER("lcher", "end", "a", "e", "i", "o", "u"),
	LDEMAR("ldemar", "end", "a", "e", "i", "o", "u"),
	LEMACHUS("lemachus", "end", "a", "e", "i", "o", "u"),
	LEXANDER("lexander", "end", "a", "e", "i", "o", "u"),
	LIEZER("liezer", "e"),
	LFREDO("lfredo", "end", "a", "e", "i", "o", "u"),
	LFRAM("lfram", "end", "a", "e", "i", "o", "u"),
	LICAN("lican", "end", "a", "e", "i", "o", "u"),
	LICIOUS("licious", "end", "a", "e", "i", "o", "u"),
	LIAS("lias", "end", "a", "e", "i", "o", "u"),
	LINDA("linda", "end", "a", "e", "i", "o", "u"),
	LLACE("llace", "end", "a", "e", "i", "o", "u"),
	LLBLADDER("llbladder", "end", "a"),
	LLDONE("lldone", "end", "e"),
	LLIAM("lliam", "end", "a", "e", "i", "o", "u"),
	LLIGAN("lligan", "end", "u"),
	LLINAIRE("llinaire", "end", "a", "e", "i", "o", "u"),
	LLSPICE("llspice", "end", "a"),
	LOMON("lomon", "end", "o"),
	LSAVERDE("lsaverde", "end", "a"),
	LYDE("lyde", "end", "a", "e", "i", "o", "u"),
	
	NALD("nald", "end", "a", "e", "i", "o", "u"),
	NALDO("naldo", "end", "a", "e", "i", "o", "u"),
	NANA("nana", "end", "a"),
	NATHAN("nathan", "end", "a", "e", "i", "o", "u"),
	NCHILADA("nchilada", "end", "e"),
	NCHOVY("nchovy", "end", "a"),
	NCISCO("ncisco", "end", "a", "e", "i", "o", "u"),
	NCUBUS("ncubus", "end", "a", "e", "i", "o", "u"),
	NDARIN("ndarin", "end", "a"),
	NDERBENDER("nderbender", "end", "e", "i"),
	NDERSON("nderson", "end", "a", "e", "i", "o", "u"),
	NDRASEKAR("ndrasekar", "end", "a"),
	NDREA("ndrea", "end", "a", "e", "i", "o", "u"),
	NDWICH("ndwich", "end", "a", "e", "i", "o", "u"),
	NDLER("ndler", "end", "a", "e", "i", "o", "u"),
	NDYCANE("ndycane", "end", "a"),
	NEAPPLE("neapple", "end", "i"),
	NESHRAM("neshram", "end", "a", "e", "i"),
	NEFLU("neflu", "end", "i"),
	NEK("nek", "end", "a", "e", "i", "o", "u"),
	NELLA("nella", "end", "a", "e", "i", "o", "u"),
	NESTRONE("nestrone", "end", "i"),
	NEYMAKER("neymaker", "end", "a", "e", "i", "o", "u"),
	NGFISHER("ngfisher", "end", "a", "e", "i", "o", "u"),
	NGFELLOW("ngfellow", "end", "a", "e", "i", "o", "u"),
	NGA("nga", "end", "a", "e", "i", "o", "u"),
	NGERBREAD("ngerbread", "end", "i"),
	NGERINE("ngerine", "end", "a"),
	NGO("ngo", "end", "a", "e", "i", "o", "u"),
	NGOSTEEN("ngosteen", "end", "a"),
	NGUINI("nguini", "end", "i"),
	NICOTTI("nicotti", "end", "a"),
	NILLA("nilla", "end", "a"),
	NIPER("niper", "end", "u"),
	NITZEL("nitzel", "end", "a", "e", "i", "o", "u"),
	NJAMIN("njamin", "end", "a", "e", "i", "o", "u"),
	NKLIN("nklin", "end", "a", "e", "i", "o", "u"),
	NKO("nko", "end", "a", "e", "i", "o", "u"),
	NKY("nky", "end", "a", "e", "i", "o", "u"),
	NKYKONG("nkykong", "end", "a", "o", "u"),
	NNAMON("nnamon", "end", "i"),
	NNIFER("nnifer", "end", "a", "e", "i", "o", "u"),
	NNIGAN("nnigan", "end", "a", "e", "i", "o", "u"),
	NNIVA("nniva", "end", "a", "e", "i", "o", "u"),
	NOCEROS("noceros", "end", "i"),
	NONDORF("nondorf", "end", "a"),
	NSTON("nston", "end", "a", "e", "i", "o", "u"),
	NTACLAUS("ntaclaus", "end", "a"),
	NTONETTE("ntonette", "end", "a", "e", "i", "o", "u"),
	NUMATI("numati", "end", "a"),
	NZALES("nzales", "end", "a", "e", "i", "o", "u"),
	NZO("nzo", "end", "a", "e", "i", "o", "u"),
	
	MANDCHEESE("mandcheese", "end", "a"),
	MANUEL("manuel", "end", "e"),
	MATO("mato", "end", "o"),
	MBLY("mbly", "end", "a", "e", "i", "o", "u"),
	MBURGER("mburger", "end", "a", "e", "i", "o", "u"),
	MBUTAN("mbutan", "end", "a"),
	MELIA("melia", "end", "a"),
	MEMBERT("membert", "end", "a"),
	MENTINE("mentine", "end", "e"),
	MICHANGA("michanga", "end", "i"),
	MILAH("milah", "end", "a"),
	MILTON("milton", "end", "a"),
	MILY("mily", "end", "e"),
	MINGO("mingo", "end", "a", "e", "i", "o", "u"),
	MINGWAY("mingway", "end", "a", "e", "i", "o", "u"),
	MITRI("mitri", "end", "i"),
	MONADE("monade", "end", "e"),
	MONGRASS("mongrass", "end", "e"),
	MPANELLE("mpanelle", "end", "a"),
	MPERNICKEL("mpernickel", "end", "a", "e", "i", "o", "u"),
	MPKIN("mpkin", "end", "u"),
	MPH("mph", "end", "a", "e", "i", "o", "u"),
	MPHODORUS("mphodorus", "end", "a", "e", "i", "o", "u"),
	MPUS("mpus", "end", "a", "e", "i", "o", "u"),
	MONTE("monte", "end", "a", "e", "i", "o", "u"),
	MQUAT("mquat", "end", "u"),

	OLEY("oley", "end", "o"),
	OFUS("ofus", "end", "o"),
	OK("ok", "end", "o"),
	OLE("ole", "end", "o"),
	ONEY("oney", "end", "o"),
	OWULF("owulf", "end", "e"),

	PAYA("paya", "end", "a"),
	PHELIA("phelia", "end", "a", "e", "i", "o", "u"),
	PHIA("phia", "end", "o"),
	PHETH("pheth", "end", "a"),
	PHOMET("phomet", "end", "a"),
	PPERGRINDER("ppergrinder", "end", "e"),
	PPERJACK("pperjack", "end", "e"),
	PPERMINT("ppermint", "end", "e"),
	PPERONCINO("pperoncino", "end", "e"),
	PPET("ppet", "end", "a", "e", "i", "o", "u"),
	PPINGS("ppings", "end", "a", "e", "i", "o", "u"),
	PPLEJACK("pplejack", "end", "a"),
	PRIKA("prika", "end", "a"),
	PTON("pton", "end", "a", "e", "i", "o", "u"),

	QUEFORT("quefort", "end", "a", "o"),

	RABEAU("rabeau", "end", "a", "e", "i", "o", "u"),
	RARDO("rardo", "end", "a", "e", "i", "o", "u"),
	RATIO("ratio", "end", "a", "e", "i", "o", "u"),
	RAWAY("raway", "end", "a"),
	RBECUE("rbecue", "end", "a", "o", "u"),
	RCESTER("rcester", "end", "a", "e", "i", "o", "u"),
	RDAMOM("rdamom", "end", "a"),
	RDECAI("rdecai", "end", "o"),
	RDITA("rdita", "end", "o"),
	RDOCK("rdock", "end", "a", "e", "i", "o", "u"),
	REAU("reau", "end", "a", "e", "i", "o", "u"),
	RFALLE("rfalle", "end", "a"),
	REDITH("redith", "end", "a", "e", "i", "o", "u"),
	REGANO("regano", "end", "o"),
	RFIELD("rfield", "end", "a", "e", "i", "o", "u"),
	RGARET("rgaret", "end", "a", "e", "i", "o", "u"),
	RGARITA("rgarita", "end", "a", "e", "i", "o", "u"),
	RGARINE("rgarine", "end", "a"),
	RGEON("rgeon", "end", "a", "e", "i", "o", "u"),
	RGENOST("rgenost", "end", "e"),
	RGONZOLA("rgonzola", "end", "o"),
	RGUNDY("rgundy", "end", "u"),
	RJORAM("rjoram", "end", "a"),
	RIJUANAS("rijuanas", "a"),
	RIWINKLE("riwinkle", "end", "e"),
	RLOS("rlos", "end", "a", "e", "i", "o", "u"),
	RLOTTE("rlotte", "end", "a", "e", "i", "o", "u"),
	RLOV("rlov", "end", "a", "e", "i", "o", "u"),
	RLSBERG("rlsberg", "end", "a", "e", "i", "o", "u"),
	RMALADE("rmalade", "end", "a", "e", "i", "o", "u"),
	RMELO("rmelo", "end", "a", "e", "i", "o", "u"),
	RMESAN("rmesan", "end", "a"),
	RMICELLI("rmicelli", "end", "e"),
	RNABUS("rnabus", "end", "a", "e", "i", "o", "u"),
	RNAISE("rnaise", "end", "a", "e", "i", "o", "u"),
	RNANDO("rnando", "end", "a", "e", "i", "o", "u"),
	RNARD("rnard", "end", "a", "e", "i", "o", "u"),
	RNFLAKES("rnflakes", "end", "o"),
	RNITAS("rnitas", "end", "a"),
	RONICA("ronica", "end", "a", "e", "i", "o", "u"),
	RPO("rpo", "end", "a", "e", "i", "o", "u"),
	RRACUDA("rracuda", "end", "a"),
	RRAULT("rrault", "end", "a", "e", "i", "o", "u"),
	RRITO("rrito", "end", "u"),
	RROWAY("rroway", "end", "a", "e", "i", "o", "u"),
	RSCHT("rscht", "end", "o"),
	RSERADISH("rseradish", "end", "o"),
	RSHMALLOW("arshmallow", "end", "a"),
	RSIMMON("rsimmon", "end", "e"),
	RSLEY("rsley", "end", "a"),
	RSULA("rsula", "end", "a", "e", "i", "o", "u"),
	RTELLINI("rtellini", "end", "o"),
	RTHOLOMEW("rtholomew", "end", "a", "e", "i", "o", "u"),
	RTICUS("rticus", "end", "a", "o", "u"),
	RTIER("rtier", "end", "a", "e", "i", "o", "u"),
	RTILLA("rtilla", "end", "o"),
	RTOLI("rtoli", "end", "a", "e", "i", "o", "u"),
	RTOUR("rtour", "end", "a", "e", "i", "o", "u"),
	RTRAND("rtrand", "end", "a", "e", "i", "o", "u"),
	RTRIDGE("rtridge", "end", "a"),
	RZIPAN("rzipan", "end", "a"),
	RZVLAK("rzvlak", "end", "a", "e", "i", "o", "u"),

	SAAC("saac", "end", "i"),
	SABELLA("sabella", "end", "a", "e", "i", "o", "u"),
	SADORA("sadora", "end", "a", "e", "i", "o", "u"),
	SAGNA("sagna", "end", "a"),
	SARIO("sario", "end", "o"),
	SCARGOT("scargot", "end", "e"),
	SCOE("scoe", "end", "a", "e", "i", "o", "u"),
	SEIDON("seidon", "end", "o"),
	SEMARY("semary", "end", "o"),
	SEVELT("sevelt", "end", "a", "e", "i", "o", "u"),
	SHINI("shini", "end", "a", "e", "i", "o", "u"),
	SHIRE("shire", "end", "a", "e", "i", "o", "u"),
	SHMAEL("shmael", "end", "a", "e", "i", "o", "u"),
	SHNU("shnu", "end", "i"),
	SHUA("shua", "end", "o"),
	SHWACKER("shwacker", "end", "u"),
	SPUS("spus", "end", "a", "e", "i", "o", "u"),
	SRAEL("srael", "end", "e", "i"),
	SSAFRAS("ssafras", "end", "a"),
	SSAIN("ssain", "end", "a", "u"),
	SSARIAN("ssarian", "end", "a", "o", "u"),
	SSICA("ssica", "end", "e"),
	SSILI("ssili", "end", "u"),
	SSINI("ssini", "end", "a", "e", "i", "o", "u"),
	STADA("stada", "end", "o"),
	STAFA("stafa", "end", "o","u"),
	STARD("stard", "end", "a", "e", "i", "o", "u"),
	STAROSSA("starossa", "end", "e"),
	STOPHER("stopher", "end", "a", "e", "i", "o", "u"),
	STULE("stule", "end", "a", "e", "i", "o", "u"),

	TALINI("talini", "end", "a", "e", "i", "o", "u"),
	TATO("tato", "end", "a", "o"),
	TATRON("tatron", "end", "a", "e", "i", "o", "u"),
	TCHOCOLATE("tchocolate", "end", "o"),
	TCOIN("tcoin", "end", "i"),
	TERMELON("termelon", "end", "a"),
	THERFORD("therford", "end", "a", "e", "i", "o", "u"),
	THIUM("thium", "end", "a", "e", "i", "o", "u"),
	THRO("thro", "end", "a", "e", "i", "o", "u"),
	THESHEBA("thesheba", "end", "a", "e", "i", "o", "u"),
	TICIA("ticia", "end", "a", "e", "i", "o", "u"),
	TMEG("tmeg", "end", "u"),
	TNEY("tney", "end", "i", "u"),
	TNIP("tnip", "end", "a"),
	TONIO("tonio", "end", "a", "e", "i", "o", "u"),
	TTICELLI("tticelli", "end", "a", "e", "i", "o", "u"),
	TTICINI("tticini", "end", "a", "e", "i", "o", "u"),
	TTONCHOPS("ttonchops", "end", "o", "u"),
	TTERCUPS("ttercups", "end", "o", "u"),

	UILLABAISSE("uillabiasse", "end", "o"),
	UILLON("uillon", "end", "o"),
	ULASH("ulash", "end", "o"),
	URDOUGH("urdough", "end", "o"),
	
	VACADO("vacado", "end", "a"),
	VARIUS("varius", "end", "a", "e", "i", "o", "u"),
	VATAPPI("vatappi", "end", "a"),
	VECRAFT("vecraft", "end", "o"),
	VELIER("velier", "end", "a", "e", "i", "o", "u"),
	VENDER("vender", "end", "a"),
	VERGREEN("vergreen", "end", "e"),
	VERLORD("verlord", "end", "o"),
	VINGTON("vington", "end", "a", "e", "i", "o", "u"),
	VINSKY("vinsky", "end", "a", "e", "i", "o", "u"),
	VIOLI("violi", "end", "a"),
	VITICUS("viticus", "end", "e"),

	WDER("wder", "end", "o"),
	WENHOEK("wenhoek", "end", "a", "e", "i", "o", "u"),
	WIFRUIT("wifruit", "end", "i"),
	WYER("wyer", "end", "a", "o"),
	
	XANNE("xanne", "end", "a", "e", "i", "o", "u"),
	XIMUS("ximus", "end", "a", "e", "i", "o", "u"),
	XLEY("xley", "end", "u"),
	
	YARDEE("yardee", "end", "o"),
	YENNE("yenne", "end", "a"),
	YONNAISE("yonnaise", "end", "a"),

	ZARUS("zarus", "end", "a", "e", "i", "o", "u"),
	ZAZEL("zazel", "end", "a"),
	ZEBEL("zebel", "end", "a", "e", "i", "o", "u"),
	ZEKIEL("zekiel", "end", "a", "e", "i", "o", "u"),
	ZERAC("zerac", "end", "a"),
	ZPACHO("zpacho", "end", "a"),
	ZZAPIE("zzapie", "end", "i"),
	ZZERELLA("zzerella", "end", "o"),

	;
	
	//This name and the end of this name fragment
	private String me, endTag;
	
	//A list of name fragments that this name fragment can follow.
	private String[] canFollow;
	
	NameGenerator(String me, String endTag, String... canFollow) {
		this.me = me;
		this.endTag = endTag;
		this.canFollow = canFollow;
	}
	
	/**
	 * This generates a first and last name.
	 * @param alliteration: Should the 2 names have the same first letter?
	 * @return
	 */
	public static String generateFirstLast(boolean alliteration) {
		
		ArrayList<NameGenerator> possibleNexts = new ArrayList<NameGenerator>();

		for (NameGenerator gen: NameGenerator.values()) {
			for (int i = 0; i < gen.canFollow.length; i++) {
				if (gen.canFollow[i].equals("start")) {
					possibleNexts.add(gen);
				}
			}
		}
		
		int randomIndex = GameStateManager.generator.nextInt(possibleNexts.size());
		NameGenerator next1 = possibleNexts.get(randomIndex);
		NameGenerator next2 = alliteration ? possibleNexts.get(randomIndex) : possibleNexts.get(GameStateManager.generator.nextInt(possibleNexts.size()));
		
		String firstName = next1.me + generateName(next1.endTag);
		String lastName = next2.me + generateName(next2.endTag);
		
		firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
		lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
		return firstName + " " + lastName;
	}
	
	/**
	 * Base Case for recursive name generation.
	 * Start with a name fragment with a "start" tag
	 */
	public static String generateName() {
		return generateName("start");
	}
	
	/**
	 * Recursive case. Randomly select a valid next name fragment
	 * @param prev: The name so far
	 * @return: The generated name fragment, plus the rest of the name
	 */
	public static String generateName(String prev) {
		
		ArrayList<NameGenerator> possibleNexts = new ArrayList<NameGenerator>();
		
		for (NameGenerator gen: NameGenerator.values()) {
			for (int i = 0; i < gen.canFollow.length; i++) {
				if (gen.canFollow[i].equals(prev)) {
					possibleNexts.add(gen);
				}
			}
		}
		
		if (possibleNexts.isEmpty()) {
			return "";
		}
		
		int randomIndex = GameStateManager.generator.nextInt(possibleNexts.size());
		NameGenerator next = possibleNexts.get(randomIndex);
		
		if (next.getEndTag().equals("end")) {
			return next.me;
		} else {
			return next.me + generateName(next.endTag);
		}
	}

	public String getMe() {
		return me;
	}

	public void setMe(String me) {
		this.me = me;
	}

	public String getEndTag() {
		return endTag;
	}

	public void setEndTag(String endTag) {
		this.endTag = endTag;
	}

	public String[] getCanFollow() {
		return canFollow;
	}

	public void setCanFollow(String[] canFollow) {
		this.canFollow = canFollow;
	}
}
