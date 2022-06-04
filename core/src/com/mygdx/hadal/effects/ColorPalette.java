package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mygdx.hadal.save.UnlockCharacter;

public enum ColorPalette {

    BASE(HadalColor.BLACK, HadalColor.BASE_SATURATED_DARK, HadalColor.BASE_MID_DARK, HadalColor.BASE_DESATURATED_DARK,
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

        if (SaturatedDark.equals(HadalColor.NOTHING)) {
            shader.setUniformf("newcolor1", character.getPalette().SaturatedDark.getHSV());
        } else {
            shader.setUniformf("newcolor1", SaturatedDark.getHSV());
        }
        if (MidDark.equals(HadalColor.NOTHING)) {
            shader.setUniformf("newcolor2", character.getPalette().MidDark.getHSV());
        } else {
            shader.setUniformf("newcolor2", MidDark.getHSV());
        }
        if (DesaturatedDark.equals(HadalColor.NOTHING)) {
            shader.setUniformf("newcolor3", character.getPalette().DesaturatedDark.getHSV());
        } else {
            shader.setUniformf("newcolor3", DesaturatedDark.getHSV());
        }
        if (SaturatedLight.equals(HadalColor.NOTHING)) {
            shader.setUniformf("newcolor4", character.getPalette().SaturatedLight.getHSV());
        } else {
            shader.setUniformf("newcolor4", SaturatedLight.getHSV());
        }
        if (MidLight.equals(HadalColor.NOTHING)) {
            shader.setUniformf("newcolor5", character.getPalette().MidLight.getHSV());
        } else {
            shader.setUniformf("newcolor5", MidLight.getHSV());
        }
        if (DesaturatedLight.equals(HadalColor.NOTHING)) {
            shader.setUniformf("newcolor6", character.getPalette().DesaturatedLight.getHSV());
        } else {
            shader.setUniformf("newcolor6", DesaturatedLight.getHSV());
        }
        if (Accent1.equals(HadalColor.NOTHING)) {
            shader.setUniformf("newcolor7", character.getPalette().Accent1.getHSV());
        } else {
            shader.setUniformf("newcolor7", Accent1.getHSV());
        }
        if (Accent2.equals(HadalColor.NOTHING)) {
            shader.setUniformf("newcolor8", character.getPalette().Accent2.getHSV());
        } else {
            shader.setUniformf("newcolor8", Accent2.getHSV());
        }
        return shader;
    }

    public HadalColor getIcon() { return Icon; }
}
