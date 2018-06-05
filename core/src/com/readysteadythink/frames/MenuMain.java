package com.readysteadythink.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.readysteadythink.Global;
import com.readysteadythink.ui.BaseUIElement;
import com.readysteadythink.ui.IUIElement;
import com.readysteadythink.ui.UIElementManager;

public class MenuMain extends BaseFrame {

    private IFrame next = null;

    //UI Elements
    private UIElementManager manager = new UIElementManager();
    private IUIElement title;
    private IUIElement selectGame;
    private IUIElement options;
    private IUIElement exit;

    //Sprites
    private Sprite menuTitleSprite = new Sprite(new Texture("Menu/title.png"));
    private Sprite buttonSelectGameSprite = new Sprite(new Texture("Menu/select.png"));
    private Sprite buttonOptionsSprite = new Sprite(new Texture("Menu/options.png"));
    private Sprite buttonExitSprite = new Sprite(new Texture("Menu/exit.png"));

    public MenuMain() {
        super();

        menuTitleSprite.setScale((screenw / menuTitleSprite.getWidth()) * 0.8f);
        buttonSelectGameSprite.setScale((screenw / buttonSelectGameSprite.getWidth()) * 0.75f);
        buttonOptionsSprite.setScale((screenw / buttonOptionsSprite.getWidth()) * 0.75f);
        buttonExitSprite.setScale((screenw / buttonExitSprite.getWidth()) * 0.75f);

        Vector2 menuTitlePos = new Vector2(
                screenw / 2 - (menuTitleSprite.getWidth() * menuTitleSprite.getScaleX()) / 2,
                screenh - menuTitleSprite.getHeight() * menuTitleSprite.getScaleY() - screenh * 0.1f);
        Vector2 buttonPlayPos = new Vector2(
                screenw / 2 - (buttonSelectGameSprite.getWidth() * buttonSelectGameSprite.getScaleX()) / 2,
                screenh * 0.5f - buttonSelectGameSprite.getHeight() * buttonSelectGameSprite.getScaleY());
        Vector2 buttonOptionsPos = new Vector2(
                buttonPlayPos.x,
                buttonPlayPos.y - buttonOptionsSprite.getHeight() * buttonOptionsSprite.getScaleY() - screenh * 0.05f);
        Vector2 buttonExitPos = new Vector2(
                buttonOptionsPos.x,
                buttonOptionsPos.y - buttonExitSprite.getHeight() * buttonExitSprite.getScaleY() - screenh * 0.05f);

        title = new BaseUIElement("title", menuTitleSprite, menuTitlePos.x, menuTitlePos.y);
        selectGame = new BaseUIElement("select_game", buttonSelectGameSprite, buttonPlayPos.x, buttonPlayPos.y);
        options = new BaseUIElement("options", buttonOptionsSprite, buttonOptionsPos.x, buttonOptionsPos.y);
        exit = new BaseUIElement("exit", buttonExitSprite, buttonExitPos.x, buttonExitPos.y);

        manager.add(title);
        manager.add(selectGame);
        manager.add(options);
        manager.add(exit);
    }

    @Override
    public void init() {
        super.init();
        Global.changeMusic(Gdx.audio.newMusic(Gdx.files.internal("Menu/Chris Rae - Liquid Latin.mp3")));
    }

    @Override
    public IFrame update(float dt) {
        if (next != null) return next;
        return this;
    }

    @Override
    public void draw() {
        super.draw();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        manager.draw(batch, viewport);
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 pos = camera.unproject(new Vector3(screenX, screenY, 0));
        String hit = manager.firstHit(pos.x, pos.y);
        if (hit != null) {
            Gdx.app.log("Log", hit);
            if (hit == selectGame.getName())
                next = new SelectGame();
            else if (hit == exit.getName())
                Gdx.app.exit();
        }
        return true;
    }

    @Override
    public void dispose() {
        batch.dispose();
        menuTitleSprite.getTexture().dispose();
        buttonSelectGameSprite.getTexture().dispose();
        buttonOptionsSprite.getTexture().dispose();
        buttonExitSprite.getTexture().dispose();
    }
}
