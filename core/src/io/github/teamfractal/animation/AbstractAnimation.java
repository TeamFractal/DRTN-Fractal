package io.github.teamfractal.animation;

import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.teamfractal.screens.AbstractAnimationScreen;

public abstract class AbstractAnimation implements IAnimation {
    protected IAnimationFinish callback;
    protected float time;
    protected AbstractAnimationScreen screen;
    protected boolean animationFinish;
    protected boolean tick(Batch batch) { return true; }

    protected boolean tickDelta(float delta, Batch batch) {
        return tick(batch);
    }

    @Override
    public synchronized boolean tick(float delta, Batch batch) {
        if (animationFinish) return true;

        time += delta;
        animationFinish = tickDelta(delta, batch);
        return animationFinish;
    }

    @Override
    public void setAnimationFinish(IAnimationFinish callback) {
        this.callback = callback;
    }

    @Override
    public void callAnimationFinish() {
        if (callback != null) {
            callback.OnAnimationFinish();
            cancelAnimation();
        }
    }

    @Override
    public synchronized void cancelAnimation() {
        animationFinish = true;
        callback = null;
        time = 0;
    }

    @Override
    public void setupScreen(AbstractAnimationScreen abstractAnimationScreen) {
        screen = abstractAnimationScreen;

        time = 0;
        animationFinish = false;
    }
}
