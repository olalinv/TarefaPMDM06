package com.robottitto.gpmdm06.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class SnakeSegment {

    private float x;
    private float y;
    private Texture texture;

    public SnakeSegment(Texture texture) {
        this.texture = texture;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void updateSegmentPosition(float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public void draw(Batch batch, int snakeX, int snakeY) {
        if (!(getX() == snakeX && getY() == snakeY)) {
            batch.draw(texture, this.getX(), this.getY());
        }
    }

}
