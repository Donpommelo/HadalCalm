package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.save.UnlockCharacter;

public enum ColorPalette {

    BASE(HadalColor.WHITE, HadalColor.BASE_SATURATED_DARK, HadalColor.BASE_MID_DARK, HadalColor.BASE_DESATURATED_DARK,
            HadalColor.BASE_SATURATED_LIGHT, HadalColor.BASE_MID_LIGHT, HadalColor.BASE_DESATURATED_LIGHT,
            HadalColor.BASE_ACCENT_1, HadalColor.BASE_ACCENT_2),

    MAXIMILLIAN(HadalColor.MAXIMILLIAN_MID_DARK, HadalColor.MAXIMILLIAN_SATURATED_DARK, HadalColor.MAXIMILLIAN_MID_DARK,
            HadalColor.MAXIMILLIAN_DESATURATED_DARK, HadalColor.NOTHING, HadalColor.NOTHING, HadalColor.MAXIMILLIAN_DESATURATED_LIGHT,
            HadalColor.MAXIMILLIAN_ACCENT_1, HadalColor.MAXIMILLIAN_ACCENT_2),

    MOREAU(HadalColor.MOREAU_SATURATED_DARK, HadalColor.MOREAU_SATURATED_DARK, HadalColor.MOREAU_MID_DARK,
            HadalColor.MOREAU_DESATURATED_DARK, HadalColor.NOTHING, HadalColor.NOTHING, HadalColor.MOREAU_DESATURATED_LIGHT,
            HadalColor.MOREAU_ACCENT_1, HadalColor.NOTHING),

    ROCLAIRE(HadalColor.ROCLAIRE_MID_LIGHT, HadalColor.ROCLAIRE_SATURATED_DARK, HadalColor.ROCLAIRE_MID_DARK,
            HadalColor.ROCLAIRE_DESATURATED_DARK, HadalColor.NOTHING, HadalColor.ROCLAIRE_MID_LIGHT, HadalColor.ROCLAIRE_DESATURATED_LIGHT,
            HadalColor.ROCLAIRE_ACCENT_1, HadalColor.ROCLAIRE_ACCENT_2),

    TAKANORI(HadalColor.TAKANORI_MID_LIGHT, HadalColor.TAKANORI_SATURATED_DARK, HadalColor.TAKANORI_MID_DARK,
            HadalColor.TAKANORI_DESATURATED_DARK, HadalColor.NOTHING, HadalColor.TAKANORI_MID_LIGHT, HadalColor.NOTHING,
            HadalColor.TAKANORI_MID_LIGHT, HadalColor.NOTHING),

    TELEMACHUS(HadalColor.TELEMACHUS_DESATURATED_LIGHT, HadalColor.TELEMACHUS_SATURATED_DARK, HadalColor.TELEMACHUS_MID_DARK,
            HadalColor.TELEMACHUS_DESATURATED_DARK, HadalColor.NOTHING, HadalColor.NOTHING, HadalColor.TELEMACHUS_DESATURATED_LIGHT,
            HadalColor.TELEMACHUS_ACCENT_1, HadalColor.TELEMACHUS_ACCENT_2),

    WANDA(HadalColor.WANDA_MID_DARK, HadalColor.WANDA_SATURATED_DARK, HadalColor.WANDA_MID_DARK, HadalColor.WANDA_DESATURATED_DARK,
            HadalColor.NOTHING, HadalColor.NOTHING, HadalColor.WANDA_DESATURATED_LIGHT, HadalColor.WANDA_ACCENT_1,
            HadalColor.NOTHING),


    BANANA(HadalColor.BANANA, HadalColor.SUNGLOW, HadalColor.CORN, HadalColor.BANANA, HadalColor.YELLOW, HadalColor.JASMINE,
            HadalColor.BLONDE, HadalColor.SIENNA, HadalColor.KOBICHA),

    CELADON(HadalColor.CELADON, HadalColor.FERN, HadalColor.CELADON, HadalColor.HONEYDEW, HadalColor.MALACHITE, HadalColor.GRANNY_SMITH,
            HadalColor.NYANZA, HadalColor.TEA, HadalColor.MANTIS),

    CHARTREUSE(HadalColor.CHARTREUSE, HadalColor.FRENCH_LIME, HadalColor.INCHWORM, HadalColor.MINDARO, HadalColor.CHARTREUSE,
            HadalColor.MINT, HadalColor.TEA, HadalColor.LEMON, HadalColor.GREEN),

    COQUELICOT(HadalColor.COQUELICOT, HadalColor.VERMILION, HadalColor.COQUELICOT, HadalColor.FLAME, HadalColor.PORTLAND_ORANGE,
            HadalColor.CORAL, HadalColor.TANGERINE, HadalColor.SUNGLOW, HadalColor.AMBER),

    CRIMSON(HadalColor.CRIMSON, HadalColor.RUBY, HadalColor.CRIMSON, HadalColor.FIERY_ROSE, HadalColor.AMARANTH, HadalColor.SALMON,
            HadalColor.PINK, HadalColor.CORN, HadalColor.AMBER),

    EGGPLANT(HadalColor.EGGPLANT, HadalColor.SPANISH_VIOLET, HadalColor.EGGPLANT, HadalColor.DARK_LIVER, HadalColor.ROYAL_PURPLE,
            HadalColor.MOUNTAIN_MAJESTY, HadalColor.THISTLE, HadalColor.EMERALD, HadalColor.SPANISH_VIRIDIAN),

    GOLD(HadalColor.GOLD, HadalColor.HARVEST_GOLD, HadalColor.GOLD, HadalColor.METALLIC_GOLD, HadalColor.JONQUIL, HadalColor.FLAX,
            HadalColor.CHAMPAGNE, HadalColor.GOLDEN_YELLOW, HadalColor.GOLDENROD),

    GREY(HadalColor.GREY, HadalColor.DARK_GREY, HadalColor.GREY, HadalColor.SILVER_CHALICE, HadalColor.PLATINUM, HadalColor.PLATINUM,
            HadalColor.LIGHT_GRAY, HadalColor.NOTHING, HadalColor.ONYX),

    PLUM(HadalColor.PLUM, HadalColor.PALATINATE, HadalColor.DARK_BYZANTIUM, HadalColor.MOUNTBATTEN_PINK, HadalColor.PLUM,
            HadalColor.RAZZMIC_BERRY, HadalColor.LILAC_LUSTER, HadalColor.APRICOT, HadalColor.GRULLO),

    MAUVE(HadalColor.MAUVE, HadalColor.AMETHYST, HadalColor.LAVENDER, HadalColor.THISTLE, HadalColor.HELIOTROPE, HadalColor.MAUVE,
            HadalColor.PINK_LACE, HadalColor.TEA, HadalColor.SILVER_CHALICE),

    ORANGE(HadalColor.ORANGE, HadalColor.PUMPKIN, HadalColor.SAFFRON, HadalColor.APRICOT, HadalColor.ORANGE, HadalColor.AMBER, HadalColor.PEACH,
            HadalColor.HONEY, HadalColor.MANGO),

    SKY_BLUE(HadalColor.SKY_BLUE, HadalColor.CYAN_PROCESS, HadalColor.SKY_BLUE, HadalColor.COLUMBIA_BLUE, HadalColor.WHITE,
            HadalColor.GAINSBORO, HadalColor.PLATINUM, HadalColor.MISTY_ROSE, HadalColor.MARIGOLD),

    TAN(HadalColor.TAN, HadalColor.BISTRE, HadalColor.SHADOW, HadalColor.GRULLO, HadalColor.BRONZE, HadalColor.TAN, HadalColor.BONE,
            HadalColor.HONEY, HadalColor.BRONZE),

    TURQUOISE(HadalColor.TURQUOISE, HadalColor.TIFFANY_BLUE, HadalColor.TURQUOISE, HadalColor.MID_BLUE_GREEN, HadalColor.TURQUOISE_BLUE,
            HadalColor.CELESTE, HadalColor.POWDER_BLUE, HadalColor.SUNGLOW, HadalColor.STEEL_BLUE),

    VIOLET(HadalColor.VIOLET, HadalColor.BYZANTINE, HadalColor.FUCHSIA, HadalColor.PLUM, HadalColor.ORCHID, HadalColor.VIOLET, HadalColor.THISTLE,
            HadalColor.CANARY, HadalColor.BURLYWOOD),

    KAMABOKO(HadalColor.HOT_PINK, HadalColor.HOT_PINK, HadalColor.PERSIAN_PINK, HadalColor.COTTON_CANDY, HadalColor.SILVER, HadalColor.PLATINUM,
            HadalColor.GAINSBORO, HadalColor.CORN, HadalColor.HONEY),

    LEMON_LIME(HadalColor.LEMON, HadalColor.FRENCH_LIME, HadalColor.INCHWORM, HadalColor.MINDARO, HadalColor.MAXIMUM_YELLOW, HadalColor.ICTERINE,
            HadalColor.CANARY, HadalColor.LEMON, HadalColor.CORAL),

    MAGMA(HadalColor.SCARLET, HadalColor.SCARLET, HadalColor.JET, HadalColor.ONYX, HadalColor.VERMILION, HadalColor.RUFOUS,
            HadalColor.DARK_LIVER, HadalColor.AMBER, HadalColor.COQUELICOT),

    BLACK_AND_YELLOW(HadalColor.CITRINE, HadalColor.SMOKY_BLACK, HadalColor.JET, HadalColor.CITRINE, HadalColor.LEMON,
            HadalColor.MAXIMUM_YELLOW, HadalColor.ICTERINE, HadalColor.CANARY, HadalColor.ONYX),

    HALLOWEEN(HadalColor.ORANGE, HadalColor.SMOKY_BLACK, HadalColor.JET, HadalColor.CITRINE, HadalColor.PUMPKIN,
            HadalColor.SAFFRON, HadalColor.APRICOT, HadalColor.HONEY, HadalColor.MANGO)

    ;

    private final HadalColor Icon, SaturatedDark, MidDark, DesaturatedDark, SaturatedLight, MidLight, DesaturatedLight,
    Accent1, Accent2;

    ColorPalette(HadalColor icon, HadalColor saturatedDark, HadalColor midDark, HadalColor desaturatedDark, HadalColor saturatedLight,
                 HadalColor midLight, HadalColor desaturatedLight, HadalColor accent1, HadalColor accent2) {
        Icon = icon;
        SaturatedDark = saturatedDark;
        MidDark = midDark;
        DesaturatedDark = desaturatedDark;
        SaturatedLight = saturatedLight;
        MidLight = midLight;
        DesaturatedLight = desaturatedLight;
        Accent1 = accent1;
        Accent2 = accent2;
    }

    /**
     * This returns the shader program with variables set to replace each base color with palette colors
     */
    public ShaderProgram getShader(UnlockCharacter character) {
        ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/pass.vert").readString(),
                Gdx.files.internal("shaders/colorreplace.frag").readString());

        shader.bind();
        shader.setUniformf("oldcolor1", BASE.SaturatedDark.getHSV());
        shader.setUniformf("oldcolor2", BASE.MidDark.getHSV());
        shader.setUniformf("oldcolor3", BASE.DesaturatedDark.getHSV());
        shader.setUniformf("oldcolor4", BASE.SaturatedLight.getHSV());
        shader.setUniformf("oldcolor5", BASE.MidLight.getHSV());
        shader.setUniformf("oldcolor6", BASE.DesaturatedLight.getHSV());
        shader.setUniformf("oldcolor7", BASE.Accent1.getHSV());
        shader.setUniformf("oldcolor8", BASE.Accent2.getHSV());

        if (HadalColor.NOTHING.equals(SaturatedDark)) {
            shader.setUniformf("newcolor1", character.getPalette().SaturatedDark.getHSV());
        } else {
            shader.setUniformf("newcolor1", SaturatedDark.getHSV());
        }
        if (HadalColor.NOTHING.equals(MidDark)) {
            shader.setUniformf("newcolor2", character.getPalette().MidDark.getHSV());
        } else {
            shader.setUniformf("newcolor2", MidDark.getHSV());
        }
        if (HadalColor.NOTHING.equals(DesaturatedDark)) {
            shader.setUniformf("newcolor3", character.getPalette().DesaturatedDark.getHSV());
        } else {
            shader.setUniformf("newcolor3", DesaturatedDark.getHSV());
        }
        if (HadalColor.NOTHING.equals(SaturatedLight)) {
            shader.setUniformf("newcolor4", character.getPalette().SaturatedLight.getHSV());
        } else {
            shader.setUniformf("newcolor4", SaturatedLight.getHSV());
        }
        if (HadalColor.NOTHING.equals(MidLight)) {
            shader.setUniformf("newcolor5", character.getPalette().MidLight.getHSV());
        } else {
            shader.setUniformf("newcolor5", MidLight.getHSV());
        }
        if (HadalColor.NOTHING.equals(DesaturatedLight)) {
            shader.setUniformf("newcolor6", character.getPalette().DesaturatedLight.getHSV());
        } else {
            shader.setUniformf("newcolor6", DesaturatedLight.getHSV());
        }
        if (HadalColor.NOTHING.equals(Accent1)) {
            shader.setUniformf("newcolor7", character.getPalette().Accent1.getHSV());
        } else {
            shader.setUniformf("newcolor7", Accent1.getHSV());
        }
        if (HadalColor.NOTHING.equals(Accent2)) {
            shader.setUniformf("newcolor8", character.getPalette().Accent2.getHSV());
        } else {
            shader.setUniformf("newcolor8", Accent2.getHSV());
        }
        return shader;
    }

    public HadalColor getIcon() { return Icon; }
}
