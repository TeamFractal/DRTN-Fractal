package io.github.teamfractal.animation.chancellor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.teamfractal.animation.AbstractAnimation;
import io.github.teamfractal.animation.IAnimation;

import java.util.Random;

public class WhiteStrip extends AbstractAnimation implements IAnimation {
    private static final ShapeRenderer shape = new ShapeRenderer();
    private static final Random rnd = new Random();

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    private float offsetY;

    private static float nextFloat(float max) {
        return rnd.nextFloat() * max;
    }

    private final WildChancellorAppear animation;
    private float width;
    private float heightBound;
    private float velocity;
    private float x;
    private float y;
    private int height;

    public WhiteStrip(float heightBound, WildChancellorAppear animation) {
        this.animation = animation;
        this.heightBound = heightBound;
        reset(false);
    }

    public void reset(boolean xFromEnd) {
        velocity = nextFloat(200f) + 70;
        width = nextFloat(7.5f) + 50;
        height = rnd.nextInt(3) + 1;
        y = nextFloat(heightBound - height);

        if (xFromEnd) {
            x = Gdx.graphics.getWidth();
        } else {
            x = nextFloat(Gdx.graphics.getWidth() + width) + 20;
        }
    }

    private void draw() {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.WHITE);
        shape.line(x, offsetY + y, x + width, offsetY + y + height);
        shape.end();
    }

    @Override
    protected boolean tickDelta(float delta, Batch batch) {
        x -= velocity * delta;
        if (x + width < 0) {
            reset(true);
        }

        this.draw();
        return false;
    }
}
