package com.readysteadythink.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface IUIElement {
    void setPosition(float x, float y);
    Vector2 getPosition();

    void setSize(float width, float height);
    Vector2 getSize();

    void setSprite(Sprite sprite);
    Sprite getSprite();

    boolean isIn(float x, float y);

    String getName();
    void setName(String name);

    void update(float dt);

    void draw(SpriteBatch batch, Viewport viewport);
}
