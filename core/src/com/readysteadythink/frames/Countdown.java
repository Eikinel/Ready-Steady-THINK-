package com.readysteadythink.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.readysteadythink.Global;

/**
 * Created by 50090 on 05/06/2018.
 */

public class Countdown extends BaseFrame {

    private float delay = 3.f;
    private float timeSinceStart = 0.f;
    private String nextName;
    private IFrame next = null;

    //Text
    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Shared/Fonts/Cabin-Bold.ttf"));
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private BitmapFont fontLarge;
    private GlyphLayout layout = new GlyphLayout();
    private String countdown;
    private int textSize = (int)(screenw / 4);

    public Countdown(String frameName) {
        nextName = frameName;

        parameter.characters = "0123456789";
        parameter.size = textSize;
        fontLarge = generator.generateFont(parameter);
        fontLarge.setColor(Color.BLACK);
    }

    public Countdown(String frameName, float duration) {
        this(frameName);
        delay = duration;
    }

    @Override
    public void init() {
        super.init();
        Global.setFade("out", delay);
    }

    @Override
    public IFrame update(float dt) {
        timeSinceStart += dt;
        countdown = Integer.toString((int)Math.ceil(delay - timeSinceStart));
        layout.setText(fontLarge, countdown);

        if (timeSinceStart >= delay) {
            if (nextName == Global.FLASH_NUMBERS)
                next = new FlashNumbers();
            else if (nextName == Global.QUICK_MATH)
                next = new QuickMaths();
        }

        if (next != null) return next;
        return this;
    }

    @Override
    public void draw() {
        super.draw();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        fontLarge.draw(batch, countdown,
                screenw / 2 - layout.width / 2,
                screenh / 2 + layout.height / 2);
        batch.end();
    }
}
