package com.readysteadythink;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public final class Global {
    static float musicVolume = 1.f;
    static Music music = null;
    static boolean fading = false;
    static String fadeStatus = "none";
    static float fadeDuration = 1.f;

    public static void changeMusic(Music newMusic) {
        if (music != null) {
            Gdx.app.log("Debug", "Music change");
            music.stop();
            music.dispose();
        }

        musicVolume = 1.f;
        fading = false;
        fadeStatus = "none";
        fadeDuration = 1.f;

        music = newMusic;
        music.setVolume(musicVolume);
        music.setLooping(true);
        music.play();

        Gdx.app.log("Debug", "isFading: " + fading);
        Gdx.app.log("Debug", "fadeStatus: " + fadeStatus);
        Gdx.app.log("Debug", "Music volume: " + musicVolume);
        Gdx.app.log("Debug", "Fade duration: " + fadeDuration);
    }

    public static final boolean isFading() {
        return (fading);
    }

    public static final String getFadeStatus() {
        return (fadeStatus);
    }

    public static void setFadeStatus(String value) {
        fadeStatus = value;
        fading = true;
    }

    public static final float getFadeDuration() {
        return (fadeDuration);
    }

    public static void setFadeDuration(float value) {
        fadeDuration = value;
    }

    public static void setFade(String type, float duration) {
        setFadeStatus(type);
        setFadeDuration(duration);
    }

    public static boolean fade(float dt) {
        if (fadeStatus == "in") {
            if (musicVolume < 1.f) {
                musicVolume += dt / fadeDuration;
                music.setVolume(musicVolume);
            }
            else
                fading = false;
        } else if (fadeStatus == "out") {
            if (musicVolume > 0.f) {
                musicVolume -= dt / fadeDuration;
                music.setVolume(musicVolume);
            }
            else
                fading = false;
        }

        if (!fading)
            music.stop();

        return fading;
    }
}
