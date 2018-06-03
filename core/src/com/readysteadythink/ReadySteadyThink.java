package com.readysteadythink;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import com.readysteadythink.frames.FlashNumbers;
import com.readysteadythink.frames.IFrame;
import com.readysteadythink.frames.MenuMain;

public class ReadySteadyThink extends ApplicationAdapter implements InputProcessor {
	private IFrame currentFrame = null;

	@Override
	public void create () {
		IFrame menu = new MenuMain();

		currentFrame = menu;
		currentFrame.init();

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		if (currentFrame != null) {
			IFrame nextFrame = currentFrame.update(Gdx.graphics.getDeltaTime());
			if (nextFrame == currentFrame) {
				currentFrame.draw();
			} else {
				currentFrame.uninit();
				currentFrame.dispose();
				currentFrame = nextFrame;
				currentFrame.init();
			}
		} else {
			Gdx.app.log("Log", "No frame set !");
		}
	}

	@Override
	public void dispose () {
		if (currentFrame != null) {
			currentFrame.dispose();
		}
	}

	@Override
	public void pause() {
		if (currentFrame != null) {
			currentFrame.pause();
		}
	}

	@Override
	public void resume() {
		if (currentFrame != null) {
			currentFrame.resume();
		}
	}

	@Override
	public void resize(int width, int height) {
		if (currentFrame != null) {
			currentFrame.resize(width, height);
		}
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (currentFrame != null) {
			return currentFrame.mouseMoved(screenX, screenY);
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (currentFrame != null) {
			return currentFrame.touchDown(screenX, screenY, pointer, button);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (currentFrame != null) {
			return currentFrame.touchDragged(screenX, screenY, pointer);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (currentFrame != null) {
			return currentFrame.touchUp(screenX, screenY, pointer, button);
		}
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (currentFrame != null) {
			return currentFrame.keyDown(keycode);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (currentFrame != null) {
			return currentFrame.keyUp(keycode);
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (currentFrame != null) {
			return currentFrame.keyTyped(character);
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (currentFrame != null) {
			return currentFrame.scrolled(amount);
		}
		return false;
	}
}
