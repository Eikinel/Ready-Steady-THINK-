package com.readysteadythink.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BaseUIElement implements IUIElement {
    private Vector2 position = new Vector2(0, 0);
    private Vector2 size = new Vector2(0, 0);
    private Sprite sprite = null;
    private String name;

    public BaseUIElement(String name, float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
        setName(name);
    }

    public BaseUIElement(String name, Sprite sprite) {
        setSprite(sprite);
        setName(name);
    }

    public BaseUIElement(String name, Sprite sprite, float x, float y) {
        setSprite(sprite);
        setName(name);
        setPosition(x, y);
    }

    @Override
    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
        updateSprite();
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setSize(float width, float height) {
        size.x = width;
        size.y = height;
        updateSprite();
    }

    @Override
    public Vector2 getSize() {
        return size;
    }

    @Override
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.sprite.setOrigin(0, 0);
        updateSelf();
    }

    private void updateSprite() {
        if (sprite != null) {
            sprite.setPosition(position.x, position.y);
            sprite.setSize(size.x, size.y);
        }
    }

    private void updateSelf() {
        if (sprite != null) {
            position.x = sprite.getX();
            position.y = sprite.getY();
            size.x = sprite.getWidth();
            size.y = sprite.getHeight();
        }
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public boolean isIn(float x, float y) {
        if (sprite != null) {
            x -= sprite.getOriginX();
            y -= sprite.getOriginY();
        }
        return x >= this.position.x && x < this.position.x + this.size.x &&
                y >= this.position.y && y < this.position.y + this.size.y;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void update(float dt) {

    }

    public void draw(SpriteBatch batch, Viewport viewport) {
        if (sprite != null) {
            sprite.draw(batch);
        }
    }
}
