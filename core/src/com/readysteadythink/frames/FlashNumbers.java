package com.readysteadythink.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.readysteadythink.Global;
import com.readysteadythink.ui.BaseUIElement;
import com.readysteadythink.ui.IUIElement;
import com.readysteadythink.ui.TextUtils;
import com.readysteadythink.ui.UIElementManager;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class FlashNumbers extends BaseFrame {

    private IFrame next = null;

    //UI Elements
    private UIElementManager manager = new UIElementManager();
    private IUIElement toMenu;

    //Game variables
    private long gameClock = TimeUtils.millis();
    private int SQUARE_MIN = 4;
    private int MAX_SQUARE_PER_LINE = 4;
    private int MAX_SQUARE_PER_COLUMN = 5;
    private int squareNumbers = SQUARE_MIN;
    private boolean win = false;
    private boolean flash = true;
    private boolean wait = false;
    private long startFlash;
    private long startWait;
    private float flashDuration;
    private float waitDuration = 1000f;
    private boolean check = false;
    private int lastClicked;

    //Sprites
    private Sprite O = new Sprite(new Texture("FlashNumbers/O.png"));
    private Sprite X = new Sprite(new Texture("FlashNumbers/X.png"));
    private Sprite buttonToMenu = new Sprite(new Texture("Shared/to_menu.png"));
    private boolean blink = false;
    private float blinkEnd = waitDuration / 2.5f;
    private float blinkInterval = blinkEnd / 5.f;

    //Text
    private String title = "Flash Numbers";
    private Vector2 titlePos;
    private GlyphLayout layout;
    TextUtils textUtils = new TextUtils();

    //Shapes
    private ShapeRenderer square = new ShapeRenderer();
    private float squareSize = screenw / 8.f;
    private float borderWidth = screenw / 12.f;
    private List<Integer> squareIndexes = new ArrayList<Integer>();
    private List<Vector2> squarePos = new ArrayList<Vector2>();
    private List<Boolean> squareFlipped = new ArrayList<Boolean>();
    private Vector2 min, max, spaceWidth; //Boundaries for squares

    //Sound
    private Sound success = Gdx.audio.newSound(Gdx.files.internal("FlashNumbers/success.mp3"));
    private Sound fail = Gdx.audio.newSound(Gdx.files.internal("FlashNumbers/fail.mp3"));


    public FlashNumbers() {
        X.setScale(squareSize / X.getWidth());
        O.setScale((screenw / O.getWidth()) * 0.75f);
        buttonToMenu.setScale((screenw / buttonToMenu.getWidth()) * 0.1f);
        square.setColor(Color.BLACK);

        layout = new GlyphLayout(textUtils.getFontLarge(), title);
        titlePos = new Vector2(
                screenw / 2 - layout.width / 2,
                screenh - layout.height - screenh * 0.05f);
        O.setPosition(
                screenw / 2.f - O.getWidth() / 2.f,
                screenh / 2.f - O.getHeight() / 2.f - (screenh - titlePos.y) / 2.f
        );

        min = new Vector2(screenw * 0.1f, screenh * 0.15f);
        max = new Vector2(
                screenw * 0.9f - squareSize,
                titlePos.y - layout.height - squareSize - screenh * 0.15f);
        spaceWidth = new Vector2(
                (max.x - min.x - squareSize * (MAX_SQUARE_PER_LINE - 1)) / (MAX_SQUARE_PER_LINE - 1),
                (max.y - min.y - squareSize * (MAX_SQUARE_PER_COLUMN - 1)) / (MAX_SQUARE_PER_COLUMN - 1));

        toMenu = new BaseUIElement(Global.TO_MENU, buttonToMenu,
                screenw - (buttonToMenu.getWidth() * buttonToMenu.getScaleX()) - screenw * 0.02f,
                screenh - (buttonToMenu.getHeight() * buttonToMenu.getScaleY()) - screenh * 0.01f);
        manager.add(toMenu);

        initSquares();
    }

    @Override
    public void init() {
        super.init();
    }

    public IFrame update(float dt) {
        if (next != null) return next;

        if (wait) {
            long timeSinceWait = TimeUtils.timeSinceMillis(startWait);

            if (timeSinceWait >= waitDuration) {
                wait = false;
                restart();
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

        if (flash && TimeUtils.timeSinceMillis(startFlash) >= flashDuration) {
            Gdx.app.log("Debug", "Stop flash at time " + Long.toString(TimeUtils.timeSinceMillis(gameClock)));
            flipAll(false);
            flash = false;
        }

        if (check) {
            check = false;
            int nbTrue = 0;
            boolean previous = false;

            for (int i = squareFlipped.size() - 1; i >= 0; i--) {
                if (squareFlipped.get(i))
                    nbTrue++;
                if (nbTrue == squareFlipped.size()) {
                    win = true;
                    success.play();
                    initWait();
                    break;
                }
                if (previous && !squareFlipped.get(i)) {
                    win = false;
                    X.setPosition(
                            squarePos.get(lastClicked).x - (X.getWidth() * X.getScaleX()) / 1.25f,
                            squarePos.get(lastClicked).y - (X.getHeight() * X.getScaleY()) / 1.25f);
                    fail.play();
                    initWait();
                    break;
                }
                previous = squareFlipped.get(i);
            }
        }

        return this;
    }

    public void draw() {
        super.draw();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (int i = 0; i < squareIndexes.size(); i++) {
            if (squareFlipped.get(i)) {
                Gdx.gl.glLineWidth(borderWidth);
                square.begin(ShapeType.Line);
            } else {
                square.begin(ShapeType.Filled);
            }
            square.rect(squarePos.get(i).x, squarePos.get(i).y, squareSize, squareSize);
            square.end();
        }

        long timeElapsedSecond = TimeUtils.timeSinceMillis(gameClock) / 1000;
        batch.begin();
        manager.draw(batch, viewport);
        textUtils.getFontLarge().draw(batch, title, titlePos.x, titlePos.y);
        textUtils.getFontTiny().draw(batch,
                "Timer: " + Long.toString(timeElapsedSecond) + " second" + (timeElapsedSecond >= 2 ? "s" : ""),
                screenw * 0.02f, screenh * 0.98f);
        for (int i = 0; i < squareIndexes.size(); i++) {
            layout.setText(textUtils.getFontSmall(), Integer.toString(squareIndexes.get(i)));
            textUtils.getFontSmall().draw(batch, Integer.toString(squareIndexes.get(i)),
                    squarePos.get(i).x + squareSize / 2 - layout.width / 2,
                    squarePos.get(i).y + squareSize - layout.height / 2);
        }
        if (wait) {
            if (!win)
                X.draw(batch);
            else if (win && !blink)
                O.draw(batch);
        }
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!flash && !wait) {
            Vector3 camPos = camera.unproject(new Vector3(screenX, screenY, 0));
            String hit = manager.firstHit(camPos.x, camPos.y);
            ListIterator<Vector2> it = squarePos.listIterator();
            int i = 0;

            if (hit != null) {
                Gdx.app.log("Log", hit);
                if (hit == toMenu.getName())
                    next = new MenuMain();
            }

            while (it.hasNext()) {
                Vector2 pos = it.next();

                if (camPos.x >= pos.x && camPos.x <= pos.x + squareSize &&
                        camPos.y >= pos.y && camPos.y <= pos.y + squareSize) {
                    Gdx.app.log("Debug", "Touched square n°" + squareIndexes.get(i));
                    lastClicked = i;
                    squareFlipped.set(i, true);
                    check = true;
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
        square.dispose();
        O.getTexture().dispose();
        X.getTexture().dispose();
    }

    public void restart() {
        Gdx.app.log("Debug", "Game is " + (win ? "won" : "lost"));
        if (win)
            squareNumbers++;
        else if (!win && squareNumbers > SQUARE_MIN)
            squareNumbers--;

        squareIndexes.clear();
        squarePos.clear();
        squareFlipped.clear();
        initSquares();
    }

    public void initWait() {
        flipAll(true);
        blink = false;
        wait = true;
        startWait = TimeUtils.millis();
    }

    public void initSquares() {
        Random rand = new Random();
        List<Vector2> takenCases = new ArrayList<Vector2>();

        flash = true;
        flashDuration = 1000.f + 200.f * (squareNumbers - SQUARE_MIN);
        startFlash = TimeUtils.millis();

        for(int i = 1; i <= squareNumbers; i++) {
            Vector2 randCase = new Vector2(rand.nextInt(MAX_SQUARE_PER_LINE), rand.nextInt(MAX_SQUARE_PER_COLUMN));

            for (int j = 0; j < takenCases.size(); j++) {
                if (randCase.equals(takenCases.get(j))) {
                    Gdx.app.log("Debug", "Same position encountered, redefinition");
                    randCase = new Vector2(rand.nextInt(MAX_SQUARE_PER_LINE), rand.nextInt(MAX_SQUARE_PER_COLUMN));
                    j = -1;
                }
            }
            takenCases.add(randCase);

            squareIndexes.add(i);
            squarePos.add(new Vector2(
                    min.x + randCase.x * squareSize + randCase.x * spaceWidth.x,
                    max.y - randCase.y * squareSize - randCase.y * spaceWidth.y));
            squareFlipped.add(true);
        }
    }

    public void flipAll(boolean flip) {
        for (int i = 0; i < squareFlipped.size(); i++)
            squareFlipped.set(i, flip);
    }
}
