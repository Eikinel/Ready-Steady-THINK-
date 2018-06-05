package com.readysteadythink.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.readysteadythink.Global;
import com.readysteadythink.ui.BaseUIElement;
import com.readysteadythink.ui.IUIElement;
import com.readysteadythink.ui.UIElementManager;

/**
 * Created by 50090 on 05/06/2018.
 */

public class SelectGame extends BaseFrame {

    private IFrame next = null;

    //UI Elements
    private UIElementManager manager = new UIElementManager();
    private IUIElement flashNumbers;
    private IUIElement play;
    private float space = screenh * 0.025f;
    private float lineThickness = screenw * 0.01f;

    //Text
    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Shared/Fonts/Cabin-Bold.ttf"));
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private BitmapFont fontLarge;
    private GlyphLayout layout;
    private String title1 = "Select your";
    private String title2 = "game(s)";
    private Vector2 titlePos1;
    private Vector2 titlePos2;

    //Sprite
    private Sprite buttonFlashNumbersSprite = new Sprite(new Texture("Menu/flash_numbers.png"));
    private Sprite buttonPlaySprite = new Sprite(new Texture("Menu/play.png"));

    //Selection
    private IUIElement selection = null;
    private ShapeRenderer selector = new ShapeRenderer();

    public SelectGame() {
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:, ";

        parameter.size = (int)(screenw / 8);
        fontLarge = generator.generateFont(parameter);
        fontLarge.setColor(Color.BLACK);
        layout = new GlyphLayout(fontLarge, title1);
        titlePos1 = new Vector2(
                screenw / 2 - layout.width / 2,
                screenh - layout.height - screenh * 0.05f);
        layout = new GlyphLayout(fontLarge, title2);
        titlePos2 = new Vector2(
                screenw / 2 - layout.width / 2,
                titlePos1.y - layout.height - screenh * 0.05f);

        buttonFlashNumbersSprite.setScale((screenw / buttonFlashNumbersSprite.getWidth()) * 0.75f);
        buttonPlaySprite.setScale((screenw / buttonPlaySprite.getWidth()) * 0.75f);

        Vector2 firstElementPos = new Vector2(
                screenw / 2 - (buttonFlashNumbersSprite.getWidth() * buttonFlashNumbersSprite.getScaleX()) / 2,
                titlePos2.y - layout.height - buttonFlashNumbersSprite.getHeight() * buttonFlashNumbersSprite.getScaleY() - screenh * 0.1f);

        flashNumbers = new BaseUIElement(Global.FLASH_NUMBERS, buttonFlashNumbersSprite, firstElementPos.x, firstElementPos.y);
        play = new BaseUIElement("play", buttonPlaySprite, firstElementPos.x, firstElementPos.y - (buttonPlaySprite.getHeight() * buttonPlaySprite.getScaleY()) - space);

        selector.setColor(Color.GREEN);

        manager.add(flashNumbers);
        manager.add(play);
    }

    @Override
    public void init() {
        super.init();
    }

    public IFrame update(float dt) {
        if (next != null) return next;
        return this;
    }

    @Override
    public void draw() {
        super.draw();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (selection != null) {
            selector.begin(ShapeType.Filled);
            selector.rect(selection.getPosition().x - lineThickness, selection.getPosition().y - lineThickness,
                    selection.getSprite().getWidth() * selection.getSprite().getScaleX() + lineThickness * 2,
                    selection.getSprite().getHeight() * selection.getSprite().getScaleY() + lineThickness * 2);
            selector.end();
        }

        batch.begin();
        manager.draw(batch, viewport);
        fontLarge.draw(batch, title1, titlePos1.x, titlePos1.y);
        fontLarge.draw(batch, title2, titlePos2.x, titlePos2.y);
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 pos = camera.unproject(new Vector3(screenX, screenY, 0));
        String hit = manager.firstHit(pos.x, pos.y);
        if (hit != null) {
            Gdx.app.log("Log", hit);
            if (hit == play.getName())
                next = new Countdown(selection.getName());
            else if (manager.getElementByName(hit) == selection)
                selection = null;
            else
                selection = manager.getElementByName(hit);
        }
        return true;
    }

    @Override
    public void dispose() {
        batch.dispose();
        buttonFlashNumbersSprite.getTexture().dispose();
        buttonPlaySprite.getTexture().dispose();
        generator.dispose();
    }
}
