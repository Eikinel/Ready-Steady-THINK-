package com.readysteadythink.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by 50090 on 14/06/2018.
 */

public class TextUtils {
    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Shared/Fonts/Cabin-Bold.ttf"));
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private BitmapFont fontExtraLarge;
    private BitmapFont fontLarge;
    private BitmapFont fontSmall;
    private BitmapFont fontTiny;

    public TextUtils() {
        float screenw = Gdx.graphics.getWidth();

        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:, +-*/";

        parameter.size = (int)(screenw / 6);
        fontExtraLarge = generator.generateFont(parameter);
        fontExtraLarge.setColor(Color.BLACK);

        parameter.size = (int)(screenw / 8);
        fontLarge = generator.generateFont(parameter);
        fontLarge.setColor(Color.BLACK);

        parameter.size = (int)(screenw / 12);
        fontSmall = generator.generateFont(parameter);
        fontSmall.setColor(Color.BLACK);

        parameter.size = (int)(screenw / 24);
        fontTiny = generator.generateFont(parameter);
        fontTiny.setColor(Color.BLACK);
    }

    public BitmapFont getFontExtraLarge() {
        return fontExtraLarge;
    }

    public BitmapFont getFontLarge() {
        return fontLarge;
    }

    public BitmapFont getFontSmall() {
        return fontSmall;
    }

    public BitmapFont getFontTiny() {
        return fontTiny;
    }


    public void dispose() {
        generator.dispose();
        fontExtraLarge.dispose();
        fontLarge.dispose();
        fontSmall.dispose();
        fontTiny.dispose();
    }
}
