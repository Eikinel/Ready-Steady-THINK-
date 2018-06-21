package com.readysteadythink.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.readysteadythink.ui.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by 50090 on 14/06/2018.
 */

public class QuickMaths extends BaseFrame {
    //Game variables
    private long gameClock = TimeUtils.millis();
    private char operators[] = {'+', '-', '*'};
    private char choosenOperator = operators[0];
    private int choosenNumbers[] = {0, 0};
    private int MAX_NUMBER_NORMAL = 100;
    private int MAX_NUMBER_MULT = 12;
    private long startWait;
    private boolean wait = false;
    private int result;
    private boolean check = false;
    private boolean win;

    //Text
    private String title = "Quick Maths";
    private String operation = "";
    private Vector2 titlePos;
    private Vector2 operationPos;
    private GlyphLayout layout;
    TextUtils textUtils = new TextUtils();

    //Sprites
    private Sprite O = new Sprite(new Texture("FlashNumbers/O.png"));
    private Sprite X = new Sprite(new Texture("FlashNumbers/X.png"));
    private boolean blink = false;
    private float waitDuration = 1000f;
    private float blinkEnd = waitDuration / 2.5f;
    private float blinkInterval = blinkEnd / 5.f;

    //Buttons
    private ShapeRenderer rect = new ShapeRenderer();
    private float borderWidth = screenw / 12.f;
    private int nbButtons = 4;
    private Vector2 rectSize = new Vector2((screenw * 0.66f) / (nbButtons / 2), screenh * 0.1f);
    private Vector2 space = new Vector2(rectSize.x / 3.f, rectSize.y / 2.f);
    private int buttonsPerLine = nbButtons / 2;
    private List<Vector2> buttonPos = new ArrayList<Vector2>();
    private List<Integer> buttonResult = new ArrayList<Integer>();
    private int lastClicked;

    //Sound
    private Sound success = Gdx.audio.newSound(Gdx.files.internal("FlashNumbers/success.mp3"));
    private Sound fail = Gdx.audio.newSound(Gdx.files.internal("FlashNumbers/fail.mp3"));

    public QuickMaths() {
        X.setScale(rectSize.y / X.getWidth());
        O.setScale((screenw / O.getWidth()) * 0.75f);
        rect.setColor(Color.BLACK);

        layout = new GlyphLayout(textUtils.getFontLarge(), title);
        titlePos = new Vector2(
                screenw / 2 - layout.width / 2,
                screenh - layout.height - screenh * 0.05f);

        for (int i = 0; i < nbButtons; i++)
            buttonPos.add(new Vector2(screenw * 0.1f + ((rectSize.x + space.x) * (i % buttonsPerLine)),
                    screenh * 0.1f + ((rectSize.y + space.y) * ((int)Math.floor(i / buttonsPerLine)))));

        nextOperation();
    }

    @Override
    public void init() {
        super.init();
    }

    public IFrame update(float dt) {
        if (wait) {
            long timeSinceWait = TimeUtils.timeSinceMillis(startWait);

            if (timeSinceWait >= waitDuration) {
                wait = false;
                nextOperation();
            }
            else if (timeSinceWait < blinkEnd) {
                if (timeSinceWait % (blinkInterval * 2) < blinkInterval)
                    blink = false;
                else
                    blink = true;
            }
            else
                blink = false;
        }

        if (check) {
            check = false;

            if (buttonResult.get(lastClicked) == result) {
                O.setScale((screenw / O.getTexture().getWidth()) * 0.75f);
                O.setPosition(
                        screenw / 2.f - O.getWidth() / 2.f,
                        screenh / 2.f - O.getHeight() / 2.f - (screenh - titlePos.y) / 2.f);
                success.play();
                win = true;
            }
            else {
                int buttonAnswerIndex = getButtonIndexByValue(result);

                O.setScale(rectSize.y / O.getTexture().getWidth());
                X.setPosition(
                        buttonPos.get(lastClicked).x + rectSize.x / 2 - (X.getWidth() * X.getScaleX()),
                        buttonPos.get(lastClicked).y + rectSize.y / 2 - (X.getHeight() * X.getScaleY()));
                O.setPosition(buttonPos.get(buttonAnswerIndex).x + rectSize.x / 2 - (X.getWidth() * X.getScaleX()),
                        buttonPos.get(buttonAnswerIndex).y + rectSize.y / 2 - (X.getHeight() * X.getScaleY()));
                fail.play();
                win = false;
            }
            initWait();
        }

        return this;
    }

    public void draw() {
        super.draw();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        long timeElapsedSecond = TimeUtils.timeSinceMillis(gameClock) / 1000;

        for (int i = 0; i < nbButtons; i++) {
            Gdx.gl.glLineWidth(borderWidth);
            rect.begin(ShapeRenderer.ShapeType.Line);
            rect.rect(buttonPos.get(i).x, buttonPos.get(i).y, rectSize.x, rectSize.y);
            rect.end();
        }

        batch.begin();
        textUtils.getFontLarge().draw(batch, title, titlePos.x, titlePos.y);
        textUtils.getFontTiny().draw(batch,
                "Timer: " + Long.toString(timeElapsedSecond) + " second" + (timeElapsedSecond >= 2 ? "s" : ""),
                screenw * 0.02f, screenh * 0.98f);
        textUtils.getFontExtraLarge().draw(batch, operation, operationPos.x, operationPos.y);

        for (int i = 0; i < nbButtons; i++) {
            layout.setText(textUtils.getFontSmall(), Integer.toString(buttonResult.get(i)));
            textUtils.getFontSmall().draw(batch, Integer.toString(buttonResult.get(i)),
                    buttonPos.get(i).x + (rectSize.x - layout.width) / 2,
                    buttonPos.get(i).y + rectSize.y / 2 + layout.height / 2);
        }

        if (wait) {
            if (!win) {
                X.draw(batch);
                O.draw(batch);
            }
            else if (win && !blink)
                O.draw(batch);
        }

        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!wait) {
            Vector3 camPos = camera.unproject(new Vector3(screenX, screenY, 0));

            ListIterator<Vector2> it = buttonPos.listIterator();
            int i = 0;

            while (it.hasNext()) {
                Vector2 pos = it.next();

                if (camPos.x >= pos.x && camPos.x <= pos.x + rectSize.x &&
                        camPos.y >= pos.y && camPos.y <= pos.y + rectSize.y) {
                    Gdx.app.log("Debug", "Touched button n°" + i + " with value " + buttonResult.get(i));
                    check = true;
                    lastClicked = i;
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    @Override
    public void dispose() {
        textUtils.dispose();
    }

    public void initWait() {
        blink = false;
        wait = true;
        startWait = TimeUtils.millis();
    }

    public void nextOperation() {
        Random rand = new Random();
        boolean isMult = false;

        buttonResult.clear();

        choosenOperator = operators[rand.nextInt(operators.length)];
        if (choosenOperator == '*' || choosenOperator == '/')
            isMult = true;

        for (int i = 0; i < 2; i++) {
            int sign = rand.nextBoolean() ? 1 : -1;
            choosenNumbers[i] = sign * rand.nextInt(isMult ? MAX_NUMBER_MULT : MAX_NUMBER_NORMAL);
        }

        if (choosenOperator == '-' && choosenNumbers[1] < 0) {
            choosenOperator = '+';
            choosenNumbers[1] *= -1;
        }
        else if (choosenOperator == '+' && choosenNumbers[1] < 0) {
            choosenOperator = '-';
            choosenNumbers[1] *= -1;
        }

        operation = choosenNumbers[0] + " " + choosenOperator + " " + choosenNumbers[1];
        layout.setText(textUtils.getFontExtraLarge(), operation);
        operationPos = new Vector2(
                screenw / 2 - layout.width / 2,
                screenh - layout.height - screenh * 0.25f);

        switch (choosenOperator) {
            case '+':
                result = choosenNumbers[0] + choosenNumbers[1];
                break;
            case '-':
                result = choosenNumbers[0] - choosenNumbers[1];
                break;
            case '*':
                result = choosenNumbers[0] * choosenNumbers[1];
                break;
            case '/':
                result = choosenNumbers[0] / choosenNumbers[1];
                break;
            default:
                break;
        }

        buttonResult.add(result);
        for (int i = 1; i < nbButtons; i++) {
            int wrongAnswer;
            while ((wrongAnswer = rand.nextInt(MAX_NUMBER_NORMAL)) == buttonResult.get(0));
            buttonResult.add(wrongAnswer);
        }
        Collections.shuffle(buttonResult);
    }

    private int getButtonIndexByValue(int value) {
        int i = 0;
        ListIterator<Integer> it = buttonResult.listIterator();

        while (it.hasNext()) {
            if (it.next() == result)
                return (i);
            i++;
        }

        return (-1);
    }
}
