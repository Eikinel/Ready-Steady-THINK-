package com.readysteadythink.frames;

public interface IFrame {
    IFrame update(float dt);

    void draw();

    void pause();

    void resume();

    void resize(int width, int height);

    void dispose();

    boolean mouseMoved(int screenX, int screenY);
    boolean touchDown(int screenX, int screenY, int pointer, int button);
    boolean touchDragged(int screenX, int screenY, int pointer);
    boolean touchUp(int screenX, int screenY, int pointer, int button);

    boolean keyDown(int keycode);
    boolean keyUp(int keycode);
    boolean keyTyped(char character);
    boolean scrolled(int amount);

    void init();
    void uninit();
}
