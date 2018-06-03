package com.readysteadythink;

import com.badlogic.gdx.audio.Music;

public class Global {
    static float musicVolume = 1.f;
    static Music music = null;

    public static void changeMusic(Music newMusic) {
        if (music != null) {
            music.stop();
            music.dispose();
        }
        music = newMusic;
        music.setVolume(musicVolume);
        music.setLooping(true);
        music.play();
    }
}
