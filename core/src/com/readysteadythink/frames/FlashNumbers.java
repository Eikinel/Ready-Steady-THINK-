package com.readysteadythink.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class FlashNumbers extends BaseFrame {

    //Game variables
    private boolean flash = true;
    private long gameClock = TimeUtils.millis();
    private long startFlash;
    private float flashDuration = 1000f;
    private float waitBetweenGame = 1000f;
    private int SQUARE_MIN = 4;
    private int MAX_SQUARE_PER_LINE = 4;
    private int MAX_SQUARE_PER_COLUMN = 5;
    private boolean check = false;

    //Text
    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Cabin-Bold.ttf"));
    private FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    private BitmapFont fontLarge;
    private BitmapFont fontSmall;
    private BitmapFont fontTiny;
    private GlyphLayout layout = new GlyphLayout();
    private String title = "Flash Numbers";
    private Vector2 titlePos;

    //Shapes
    private ShapeRenderer square = new ShapeRenderer();
    private float squareSize = screenw / 8.f;
    private float borderWidth = screenw / 12.f;
    private int squareNumbers = SQUARE_MIN;
    private List<Integer> squareIndexes = new ArrayList<Integer>();
    private List<Vector2> squarePos = new ArrayList<Vector2>();
    private List<Boolean> squareFlipped = new ArrayList<Boolean>();
    private Vector2 min, max, spaceWidth; //Boundaries for squares


    public FlashNumbers() {
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:, ";

        parameter.size = (int)(screenw / 8);
        fontLarge = generator.generateFont(parameter);
        fontLarge.setColor(Color.BLACK);
        layout.setText(fontLarge, title);
        titlePos = new Vector2(
                screenw / 2 - layout.width / 2,
                screenh - layout.height - screenh * 0.05f);

        parameter.size = (int)(screenw / 12);
        fontSmall = generator.generateFont(parameter);
        fontSmall.setColor(Color.BLACK);

        parameter.size = (int)(screenw / 24);
        fontTiny = generator.generateFont(parameter);
        fontTiny.setColor(Color.BLACK);

        min = new Vector2(titlePos.x, screenh * 0.1f);
        max = new Vector2(
                titlePos.x + layout.width - squareSize,
                titlePos.y - layout.height - squareSize - screenh * 0.1f);
        spaceWidth = new Vector2(
                (max.x - min.x - squareSize * (MAX_SQUARE_PER_LINE - 1)) / (MAX_SQUARE_PER_LINE - 1),
                (max.y - min.y - squareSize * (MAX_SQUARE_PER_COLUMN - 1)) / (MAX_SQUARE_PER_COLUMN - 1));

        InitSquares();
    }

    public IFrame update(float dt) {
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
                    restart(true);
                    break;
                }
                if (previous && !squareFlipped.get(i)) {
                    Gdx.app.log("Debug", "previous index " + Integer.toString(i + 1) + ": " + Boolean.toString(previous) + " / actual: " + Boolean.toString(squareFlipped.get(i)));
                    restart(false);
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
                square.begin(ShapeType.Line);
                Gdx.gl20.glLineWidth(borderWidth);
            } else {
                square.begin(ShapeType.Filled);
            }
            square.setColor(Color.BLACK);
            square.rect(squarePos.get(i).x, squarePos.get(i).y, squareSize, squareSize);
            square.end();
        }

        long timeElapsedSecond = TimeUtils.timeSinceMillis(gameClock) / 1000;
        batch.begin();
        fontLarge.draw(batch, title, titlePos.x, titlePos.y);
        fontTiny.draw(batch,
                "Timer: " + Long.toString(timeElapsedSecond) + " second" + (timeElapsedSecond >= 2 ? "s" : ""),
                screenw * 0.02f, screenh * 0.98f);
        for (int i = 0; i < squareIndexes.size(); i++) {
            layout.setText(fontSmall, Integer.toString(squareIndexes.get(i)));
            fontSmall.draw(batch, Integer.toString(squareIndexes.get(i)),
                    squarePos.get(i).x + squareSize / 2 - layout.width / 2,
                    squarePos.get(i).y + squareSize - layout.height / 2);
        }
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!flash) {
            Vector3 camPos = camera.unproject(new Vector3(screenX, screenY, 0));
            ListIterator<Vector2> it = squarePos.listIterator();
            int i = 0;

            while (it.hasNext()) {
                Vector2 pos = it.next();

                if (camPos.x >= pos.x && camPos.x <= pos.x + squareSize &&
                        camPos.y >= pos.y && camPos.y <= pos.y + squareSize) {
                    Gdx.app.log("Debug", "Touched square n°" + squareIndexes.get(i));
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
        generator.dispose();
        fontLarge.dispose();
        fontSmall.dispose();
        square.dispose();
    }

    public void restart(boolean isWon) {
        Gdx.app.log("Debug", "Game is " + (isWon ? "won" : "lost"));
        if (isWon)
            squareNumbers++;
        else if (!isWon && squareNumbers > SQUARE_MIN)
            squareNumbers--;

        squareIndexes.clear();
        squarePos.clear();
        squareFlipped.clear();
        InitSquares();
        flash = true;
    }

    public void InitSquares() {
        Random rand = new Random();
        List<Vector2> takenCases = new ArrayList<Vector2>();

        startFlash = TimeUtils.millis();

        for(int i = 1; i <= squareNumbers; i++) {
            Vector2 randCase = new Vector2(rand.nextInt(MAX_SQUARE_PER_LINE), rand.nextInt(MAX_SQUARE_PER_COLUMN));

            for (int j = 0; j < takenCases.size(); j++) {
                if (randCase.equals(takenCases.get(j))) {
                    Gdx.app.log("Debug", "Same position encountered, redefinition");
                    randCase = new Vector2(rand.nextInt(MAX_SQUARE_PER_LINE), rand.nextInt(MAX_SQUARE_PER_COLUMN));
                    j = 0;
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
