package com.robottitto.gpmdm06.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.robottitto.gpmdm06.model.SnakeSegment;
import com.robottitto.gpmdm06.util.Config;
import com.robottitto.gpmdm06.util.Direction;
import com.robottitto.gpmdm06.util.Message;
import com.robottitto.gpmdm06.util.Status;

public class Screen extends ScreenAdapter {

    private SpriteBatch spriteBatch;
    private Texture snakeHead;
    private Texture snakeBody;
    private Texture apple;
    private Array<SnakeSegment> snakeSegments = new Array<com.robottitto.gpmdm06.model.SnakeSegment>();
    private int appleXCoord;
    private int appleYCoord;
    private int snakeXCoord = 0;
    private int snakeYCoord = 0;
    private int snakeXPrevCoord = 0;
    private int snakeYPrevCoord = 0;
    private boolean appleIsVisible = false;
    private boolean snakeHasCrashed = false;
    private Status status = Status.STARTED;
    private int direction = Direction.RIGHT;
    private float timer = Config.MOVEMENT_TIME;
    private GlyphLayout layout;
    private BitmapFont bitmapFont;

    @Override
    public void show() {
        super.show();
        layout = new GlyphLayout();
        bitmapFont = new BitmapFont();
        spriteBatch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal("head.png"));
        snakeBody = new Texture(Gdx.files.internal("body.png"));
        apple = new Texture(Gdx.files.internal("apple.png"));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        switch (status) {
            case STARTED:
                registerEventListeners();
                updateSnake(delta);
                generateAnApple();
                onEatingAnApple();
                break;
            case FINISHED:
                pressStart();
                break;
        }
        clearScreen();
        draw();
    }

    private void moveSnake() {
        snakeXPrevCoord = snakeXCoord;
        snakeYPrevCoord = snakeYCoord;
        switch (direction) {
            case Direction.LEFT:
                snakeXCoord -= Config.CELL_RADIO;
                return;
            case Direction.RIGHT:
                snakeXCoord += Config.CELL_RADIO;
                return;
            case Direction.UP:
                snakeYCoord += Config.CELL_RADIO;
                return;
            case Direction.DOWN:
                snakeYCoord -= Config.CELL_RADIO;
                return;
        }
    }

    private void registerEventListeners() {
        boolean leftKeyPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rightKeyPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean upKeyPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean downKeyPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        if (leftKeyPressed) {
            changeDirection(Direction.LEFT);
        }
        if (rightKeyPressed) {
            changeDirection(Direction.RIGHT);
        }
        if (upKeyPressed) {
            changeDirection(Direction.UP);
        }
        if (downKeyPressed) {
            changeDirection(Direction.DOWN);
        }
    }

    private void changeDirection(int direction) {
        if (this.direction != Direction.getOppositeDirection(direction)) {
            this.direction = direction;
        }
    }

    private void draw() {
        spriteBatch.begin();
        spriteBatch.draw(snakeHead, snakeXCoord, snakeYCoord);
        for (SnakeSegment snakeSegment : snakeSegments) {
            snakeSegment.draw(spriteBatch, snakeXCoord, snakeYCoord);
        }
        if (appleIsVisible) {
            spriteBatch.draw(apple, appleXCoord, appleYCoord);
        }
        if (status == Status.FINISHED) {
            layout.setText(bitmapFont, Message.GAME_OVER);
            bitmapFont.setColor(255, 255, 255, 0.85F);
            bitmapFont.draw(spriteBatch, Message.GAME_OVER, (Gdx.graphics.getWidth() - layout.width) / 2, (Gdx.graphics.getHeight() - layout.height) / 2);
        }
        spriteBatch.end();
    }

    private void generateAnApple() {
        if (!appleIsVisible) {
            do {
                appleXCoord = MathUtils.random(Gdx.graphics.getWidth() / Config.CELL_RADIO - 1) * Config.CELL_RADIO;
                appleYCoord = MathUtils.random(Gdx.graphics.getHeight() / Config.CELL_RADIO - 1) * Config.CELL_RADIO;
                appleIsVisible = true;
            } while (appleXCoord == snakeXCoord && appleYCoord == snakeYCoord);
        }
    }

    private void onEatingAnApple() {
        if (appleIsVisible && appleXCoord == snakeXCoord && appleYCoord == snakeYCoord) {
            SnakeSegment snakeSegment = new SnakeSegment(this.snakeBody);
            snakeSegment.updateSegmentPosition(snakeXCoord, snakeYCoord);
            snakeSegments.insert(0, snakeSegment);
            appleIsVisible = false;
        }
    }

    private void updateSnakeSegment() {
        if (snakeSegments.size > 0) {
            SnakeSegment snakeSegment = snakeSegments.removeIndex(0);
            snakeSegment.updateSegmentPosition(snakeXPrevCoord, snakeYPrevCoord);
            snakeSegments.add(snakeSegment);
        }
    }

    private void updateSnake(float delta) {
        if (!snakeHasCrashed) {
            timer -= delta;
            if (timer <= 0) {
                timer = Config.MOVEMENT_TIME;
                moveSnake();
                checkOutOfBounds();
                updateSnakeSegment();
                checkSelfCollision();
            }
        }
    }

    private void pressStart() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            restartGame();
        }
    }

    private void restartGame() {
        status = Status.STARTED;
        direction = Direction.RIGHT;
        snakeXCoord = 0;
        snakeYCoord = 0;
        snakeXPrevCoord = 0;
        snakeYPrevCoord = 0;
        appleIsVisible = false;
        snakeSegments.clear();
        timer = Config.MOVEMENT_TIME;
    }

    private void checkOutOfBounds() {
        if (snakeXCoord < 0 || snakeXCoord >= Gdx.graphics.getWidth() || snakeYCoord < 0 || snakeYCoord >= Gdx.graphics.getHeight())
            status = Status.FINISHED;
    }

    private void checkSelfCollision() {
        for (SnakeSegment snakeSegment : snakeSegments) {
            if (snakeSegment.getX() == snakeXCoord && snakeSegment.getY() == snakeYCoord)
                status = Status.FINISHED;
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

}
