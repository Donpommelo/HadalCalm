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

	H_NEXT("h", "h", "g", "p", "s", "t", "w"),
	L_NEXT("l", "l", "b", "c", "f", "g", "p", "s", "v"),
	M_NEXT("m", "m", "s"),
	N_NEXT("n", "n", "s"),
	Q_NEXT("qu", "qu", "s"),
	R_NEXT("r", "r", "b", "c", "d", "f", "g", "p"),
	T_NEXT("t", "t", "s"),
	W_NEXT("w", "w", "t"),
	
	A_VOWEL("a", "a", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	E_VOWEL("e", "e", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	I_VOWEL("i", "i", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	O_VOWEL("o", "o", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	U_VOWEL("u", "u", "b", "c", "d" , "f" , "g", "h", "j", "k", "l", "m", "n", "p", "qu", "r", "s", "t", "v", "w", "x", "y", "z"),
	
	GG("gg", "gg", "a", "e", "i", "o", "u"),
	DGE("dge", "dge", "a", "e", "i", "o", "u"),
	RB("rb", "rb", "a", "e", "i", "o", "u"),
	RD("rd", "rd", "a", "e", "i", "o", "u"),
	RG("rg", "rg", "a", "e", "i", "o", "u"),
	RK("rk", "rk", "a", "e", "i", "o", "u"),
	RL("rl", "rl", "a", "e", "i", "o", "u"),
	RM("rm", "rm", "a", "e", "i", "o", "u"),
	RN("rn", "rn", "a", "e", "i", "o", "u"),
	RP("rp", "rp", "a", "e", "i", "o", "u"),
	RT("rt", "rt", "a", "e", "i", "o", "u"),
	CKLE("ckle", "le", "a", "e", "i", "o", "u"),
	GGLE("ggle", "le", "a", "e", "i", "o", "u"),
	FFLE("ffle", "le", "a", "e", "i", "o", "u"),
	DDLE("ddle", "le", "a", "e", "i", "o", "u"),
	PPLE("pple", "le", "a", "e", "i", "o", "u"),

	_NOTHING_("", "end", "gg", "dge", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	ART("art", "end", "gg", "rb", "rd", "rg", "rk", "rm", "rn", "rp", "rt"),
	SWORTH("sworth", "end", "a", "e", "i", "o", "u", "gg", "dge", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	BERG("berg", "end", "a", "e", "i", "o", "u", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	BERT("bert", "end", "a", "e", "i", "o", "u", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	BERRY("berry", "end", "gg", "dge", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt"),
	BLATT("blatt", "end", "a", "e", "i", "o", "u", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	BOTTOMS("bottoms", "end", "a", "e", "i", "o", "u", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	DALE("dale", "end", "a", "e", "i", "o", "u", "gg", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	NUT("nut", "end", "gg", "dge", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt"),
	SBY("sby", "end", "a", "e", "i", "o", "u", "gg", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	SMITH("smith", "end", "a", "e", "i", "o", "u", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt", "le"),
	SON("son", "end", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt"),
	TON("ton", "end", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt"),
	INGTON("ington", "end", "rb", "rd", "rg", "rk", "rl", "rm", "rn", "rp", "rt"),

	O("o", "end", "gg", "rb", "rd", "rg", "rk", "rm", "rn", "rp", "rt"),
	Y("y", "end", "gg", "rb", "rd", "rg", "rk", "rm", "rn", "rp", "rt", "le"),
	R_END("r", "end", "le"),
	GINS("gins", "end", "rb", "rd", "rg", "rk", "rm", "rn", "rp", "le"),
	KINS("kins", "end", "gg", "rb", "rd", "rg", "rk", "rm", "rn", "rp", "le"),
	
	ACAMOLE("acamole", "end", "u"),
	ADRUNNER("adrunner", "end", "o"),
	ANUTBUTTER("anutbutter", "end", "e"),
	ATMEAL("atmeal", "end", "o"),
	AZEBUCKET("azebucket", "end", "e"),
	
	BALANTE("balante", "end", "a", "e", "i", "o", "u"),
	BAMA("bama", "end", "o"),
	BBADON("bbadon", "end", "a", "e", "i", "o", "u"),
	BDUL("bdul", "end", "a", "e", "i", "o", "u"),
	BBERER("bberer", "end", "a", "e", "i", "o", "u"),
	BBERISH("bberish", "end", "a", "e", "i", "o", "u"),
	BBERY("bbery", "end", "a", "e", "i", "o", "u"),
	BANFOO("banfoo", "end", "a", "e", "i", "o", "u"),
	BASTIAN("bastian", "end", "a", "e", "i", "o", "u"),
	BEDIAH("bediah", "end", "a", "e", "i", "o", "u"),
	BISCUS("biscus", "end", "a", "e", "i", "o", "u"),
	BISH("bish", "end", "a", "e", "i", "o", "u"),
	BITHA("bitha", "end", "a", "e", "i", "o", "u"),
	BRAXAS("braxas", "end", "a"),
	BRINA("brina", "end", "a", "e", "i", "o", "u"),
	BRUARY("bruary", "end", "a", "e", "i", "o", "u"),
	
	CANDCHEESE("candcheese", "end", "a"),
	CAINE("caine", "end", "o"),
	CARDO("cardo", "end", "a", "e", "i", "o", "u"),
	CASTE("caste", "end", "a"),
	CCIATELLO("cciatello", "end", "a", "e", "i", "o", "u"),
	CCINI("ccini", "end", "a", "e", "i", "o", "u"),
	CHARD("chard", "end", "a", "e", "i", "o", "u"),
	CHARY("chary", "end", "a", "e", "i", "o", "u"),
	CHELLE("chelle", "end", "a", "e", "i", "o", "u"),
	CHNOLD("chnold", "end", "a", "e", "i", "o", "u"),
	CIEWEITZ("cieweitz", "end", "a", "e", "i", "o", "u"),
	CKADEE("ckadee", "end", "a", "e", "i", "o", "u"),
	CKER("cker", "end", "a", "e", "i", "o", "u"),
	CKERAL("ckeral", "end", "a"),
	CKHEART("ckheart", "end", "a", "e", "i", "o", "u"),
	CKINGBIRD("ckingbird", "end", "a", "e", "i", "o", "u"),
	CNICBASKET("cnicbasket", "end", "a", "e", "i", "o", "u"),
	CUMBER("cumber", "end", "u"),
	CYCLE("cycle", "end", "i"),
	
	DARDUS("dardus", "end", "a", "e", "i", "o", "u"),
	DDEUS("ddeus", "end", "a", "e", "i", "o", "u"),
	DDING("dding", "end", "a", "e", "i", "o", "u"),
	DELAIRE("delaire", "end", "a", "e", "i", "o", "u"),
	DEON("deon", "end", "a", "e", "i", "o", "u"),
	DERICK("derick", "end", "a", "e", "i", "o", "u"),
	DICULOUS("diculous", "end", "a", "e", "i", "o", "u"),
	DIMIR("dimir", "end", "a", "e", "i", "o", "u"),
	DOLPH("dolph", "end", "a", "o", "u"),
	DRACH("drach", "end", "a", "o", "u"),
	DUARDO("duardo", "end", "a", "o", "u"),
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
	GGER("gger", "end", "a", "e", "i", "o", "u"),
	GGINBOTHAM("gginbotham", "end", "a", "e", "i", "o", "u"),
	GGPLANT("ggplant", "end", "e"),
	GHERTY("gherty", "end", "a", "e", "i", "o", "u"),
	GINALD("ginald", "end", "a", "e", "i", "o", "u"),
	GLODYTE("glodyte", "end", "a","o", "u"),
	GMAC("gmac", "end", "e", "i"),
	GMA("gmac", "end", "a", "e", "i", "o", "u"),
	GOR("gor", "end", "a", "e", "i", "o", "u"),
	GORY("gory", "end", "a", "e", "i", "o", "u"),
	GUANA("guana", "end", "a", "e", "i", "o", "u"),
	GUEL("guel", "end", "a", "e", "i", "o", "u"),

	HAMMAD("hammad", "end", "a", "o", "u"),
	HEMOTH("hemoth", "end", "e"),
	
	ID("id", "end", "o"),
	INSLEY("insley", "end", "a"),
	IQUIRI("iquiri", "end", "a"),
	ISEDPORK("isedpork", "end", "a"),

	JANDRO("jandro", "end", "a", "e", "i", "o", "u"),
	JEED("jandro", "end", "a", "u"),
	JITO("jito", "end", "o"),

	KACHU("kachu", "end", "i"),
	KEDPOTATO("kedpotato", "end", "a"),
	KEEM("kedpotato", "end", "a"),
	
	LAFEL("lafel", "end", "a"),
	LANDA("landa", "end", "a", "e", "i", "o", "u"),
	LARIO("lario", "end", "a", "e", "i", "o", "u"),
	LBATROSS("lbatross", "end", "a", "e", "i", "o", "u"),
	LCHER("lcher", "end", "a", "e", "i", "o", "u"),
	LDEMAR("ldemar", "end", "a", "e", "i", "o", "u"),
	LEMACHUS("lemachus", "end", "a", "e", "i", "o", "u"),
	LEXANDER("lexander", "end", "a", "e", "i", "o", "u"),
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
	LLINAIRE("llinaire", "end", "a", "e", "i", "o", "u"),
	LLSPICE("llspice", "end", "a"),
	LOMON("lomon", "end", "o"),
	LYDE("lyde", "end", "a", "e", "i", "o", "u"),
	
	NALD("nald", "end", "a", "e", "i", "o", "u"),
	NALDO("naldo", "end", "a", "e", "i", "o", "u"),
	NANA("nana", "end", "a"),
	NATHAN("nathan", "end", "a", "e", "i", "o", "u"),
	NCHOVY("nchovy", "end", "a"),
	NCISCO("ncisco", "end", "a", "e", "i", "o", "u"),
	NCUBUS("ncubus", "end", "a", "e", "i", "o", "u"),
	NDARIN("ndarin", "end", "a"),
	NDERSON("nderson", "end", "a", "e", "i", "o", "u"),
	NDREA("ndrea", "end", "a", "e", "i", "o", "u"),
	NDWICH("ndwich", "end", "a", "e", "i", "o", "u"),
	NDLER("ndler", "end", "a", "e", "i", "o", "u"),
	NEFLU("neflu", "end", "i"),
	NEK("nek", "end", "a", "e", "i", "o", "u"),
	NELLA("nella", "end", "a", "e", "i", "o", "u"),
	NEYMAKER("neymaker", "end", "a", "e", "i", "o", "u"),
	NGFISHER("ngfisher", "end", "a", "e", "i", "o", "u"),
	NITZEL("nitzel", "end", "a", "e", "i", "o", "u"),
	NEAPPLE("neapple", "end", "i"),
	NESHRAM("neshram", "end", "a", "e", "i"),
	NGFELLOW("ngfellow", "end", "a", "e", "i", "o", "u"),
	NGA("nga", "end", "a", "e", "i", "o", "u"),
	NGO("ngo", "end", "a", "e", "i", "o", "u"),
	NJAMIN("njamin", "end", "a", "e", "i", "o", "u"),
	NKLIN("nklin", "end", "a", "e", "i", "o", "u"),
	NKO("nko", "end", "a", "e", "i", "o", "u"),
	NKY("nky", "end", "a", "e", "i", "o", "u"),
	NKYKONG("nkykong", "end", "a", "o", "u"),
	NNAMON("nnamon", "end", "i"),
	NNIFER("nnifer", "end", "a", "e", "i", "o", "u"),
	NNIVA("nniva", "end", "a", "e", "i", "o", "u"),
	NOCEROS("noceros", "end", "i"),
	NONDORF("nondorf", "end", "a"),
	NSTON("nston", "end", "a", "e", "i", "o", "u"),
	NTACLAUS("ntaclaus", "end", "a"),
	NTONETTE("ntonette", "end", "a", "e", "i", "o", "u"),
	NZALES("nzales", "end", "a", "e", "i", "o", "u"),
	NZO("nzo", "end", "a", "e", "i", "o", "u"),
	
	MANDCHEESE("mandcheese", "end", "a"),
	MATO("mato", "end", "o"),
	MBLY("mbly", "end", "a", "e", "i", "o", "u"),
	MBURGER("mburger", "end", "a", "e", "i", "o", "u"),
	MINGO("mingo", "end", "a", "e", "i", "o", "u"),
	MINGWAY("mingway", "end", "a", "e", "i", "o", "u"),
	MONADE("monade", "end", "e"),
	MPERNICKEL("mpernickel", "end", "a", "e", "i", "o", "u"),
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

	PHELIA("phelia", "end", "a", "e", "i", "o", "u"),
	PHOMET("phomet", "end", "a"),
	PPERONCINO("pperoncino", "end", "e"),
	PPET("ppet", "end", "a", "e", "i", "o", "u"),
	PPINGS("ppings", "end", "a", "e", "i", "o", "u"),
	PPLEJACK("pplejack", "end", "a"),
	PRIKA("prika", "end", "a"),

	QUEFORT("quefort", "end", "a", "o"),

	RABEAU("rabeau", "end", "a", "e", "i", "o", "u"),
	RARDO("rardo", "end", "a", "e", "i", "o", "u"),
	RATIO("ratio", "end", "a", "e", "i", "o", "u"),
	RCESTER("rcester", "end", "a", "e", "i", "o", "u"),
	RDAMOM("rdamom", "end", "a"),
	RDOCK("rdock", "end", "a", "e", "i", "o", "u"),
	REAU("reau", "end", "a", "e", "i", "o", "u"),
	REDITH("redith", "end", "a", "e", "i", "o", "u"),
	RFIELD("rfield", "end", "a", "e", "i", "o", "u"),
	RGARET("rgaret", "end", "a", "e", "i", "o", "u"),
	RGARITA("rgarita", "end", "a", "e", "i", "o", "u"),
	RGEON("rgeon", "end", "a", "e", "i", "o", "u"),
	RGONZOLA("rgonzola", "end", "o"),
	RJORAM("rjoram", "end", "a"),
	RLOS("rlos", "end", "a", "e", "i", "o", "u"),
	RLOTTE("rlotte", "end", "a", "e", "i", "o", "u"),
	RLOV("rlov", "end", "a", "e", "i", "o", "u"),
	RLSBERG("rlsberg", "end", "a", "e", "i", "o", "u"),
	RMALADE("rmalade", "end", "a", "e", "i", "o", "u"),
	RMELO("rmelo", "end", "a", "e", "i", "o", "u"),
	RMESAN("rmesan", "end", "a"),
	RNABUS("rnabus", "end", "a", "e", "i", "o", "u"),
	RNAISE("rnaise", "end", "a", "e", "i", "o", "u"),
	RNANDO("rnando", "end", "a", "e", "i", "o", "u"),
	RNARD("rnard", "end", "a", "e", "i", "o", "u"),
	RNFLAKES("rnflakes", "end", "o"),
	RONICA("ronica", "end", "a", "e", "i", "o", "u"),
	RPO("rpo", "end", "a", "e", "i", "o", "u"),
	RRACUDA("rracuda", "end", "a"),
	RRAULT("rrault", "end", "a", "e", "i", "o", "u"),
	RRITO("rrito", "end", "u"),
	RROWAY("rroway", "end", "a", "e", "i", "o", "u"),
	RSERADISH("rseradish", "end", "o"),
	RSHMALLOW("arshmallow", "end", "a"),
	RSULA("rsula", "end", "a", "e", "i", "o", "u"),
	RTHOLOMEW("rtholomew", "end", "a", "e", "i", "o", "u"),
	RTIER("rtier", "end", "a", "e", "i", "o", "u"),
	RTOLI("rtoli", "end", "a", "e", "i", "o", "u"),
	RTOUR("rtour", "end", "a", "e", "i", "o", "u"),
	RTRAND("rtrand", "end", "a", "e", "i", "o", "u"),
	RZVLAK("rzvlak", "end", "a", "e", "i", "o", "u"),

	SABELLA("sabella", "end", "a", "e", "i", "o", "u"),
	SADORA("sadora", "end", "a", "e", "i", "o", "u"),
	SCARGOT("scargot", "end", "e"),
	SCOE("scoe", "end", "a", "e", "i", "o", "u"),
	SEIDON("seidon", "end", "o"),
	SEVELT("sevelt", "end", "a", "e", "i", "o", "u"),
	SHINI("shini", "end", "a", "e", "i", "o", "u"),
	SHIRE("shire", "end", "a", "e", "i", "o", "u"),
	SHMAEL("shmael", "end", "a", "e", "i", "o", "u"),
	SHWACKER("shwacker", "end", "u"),
	SPUS("spus", "end", "a", "e", "i", "o", "u"),
	SSINI("ssini", "end", "a", "e", "i", "o", "u"),
	STARD("stard", "end", "a", "e", "i", "o", "u"),
	STOPHER("stopher", "end", "a", "e", "i", "o", "u"),
	STULE("stule", "end", "a", "e", "i", "o", "u"),

	TATO("tato", "end", "a", "o"),
	TATRON("tatron", "end", "a", "e", "i", "o", "u"),
	TCHOCOLATE("tchocolate", "end", "o"),
	TERMELON("termelon", "end", "a"),
	THERFORD("therford", "end", "a", "e", "i", "o", "u"),
	THIUM("thium", "end", "a", "e", "i", "o", "u"),
	THRO("thro", "end", "a", "e", "i", "o", "u"),
	THESHEBA("thesheba", "end", "a", "e", "i", "o", "u"),
	TICIA("ticia", "end", "a", "e", "i", "o", "u"),
	TONIO("tonio", "end", "a", "e", "i", "o", "u"),
	TTICELLI("tticelli", "end", "a", "e", "i", "o", "u"),
	TTICINI("tticini", "end", "a", "e", "i", "o", "u"),
	TTONCHOPS("ttonchops", "end", "o", "u"),

	VACADO("vacado", "end", "a"),
	VARIUS("varius", "end", "a", "e", "i", "o", "u"),
	VECRAFT("vecraft", "end", "o"),
	VELIER("velier", "end", "a", "e", "i", "o", "u"),
	VENDER("vender", "end", "a"),
	VERLORD("verlord", "end", "o"),
	VINGTON("vington", "end", "a", "e", "i", "o", "u"),
	VINSKY("vinsky", "end", "a", "e", "i", "o", "u"),
	VITICUS("viticus", "end", "e"),

	WYER("wyer", "end", "a", "o"),
	WENHOEK("wenhoek", "end", "a", "e", "i", "o", "u"),
	WIFRUIT("wifruit", "end", "i"),

	XANNE("xanne", "end", "a", "e", "i", "o", "u"),
	
	YARDEE("yardee", "end", "o"),
	YONNAISE("yonnaise", "end", "a"),

	ZARUS("zarus", "end", "a", "e", "i", "o", "u"),
	ZAZEL("zazel", "end", "a"),
	ZEBEL("zebel", "end", "a", "e", "i", "o", "u"),
	ZEKIEL("zekiel", "end", "a", "e", "i", "o", "u"),
	ZERAC("zerac", "end", "a"),
	ZZAPIE("zzapie", "end", "i"),
	ZZERELLA("zzerella", "end", "a", "e", "i", "o", "u"),

	;
	
	private String me, endTag;
	private String[] canFollow;
	
	NameGenerator(String me, String endTag, String... canFollow) {
		this.me = me;
		this.endTag = endTag;
		this.canFollow = canFollow;
	}
	
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
	
	public static String generateName() {
		return generateName("start");
	}
	
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
