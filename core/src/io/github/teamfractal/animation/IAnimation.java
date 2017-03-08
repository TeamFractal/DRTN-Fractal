package io.github.teamfractal.animation;

import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.teamfractal.screens.AbstractAnimationScreen;

public interface IAnimation {
	/**
	 * Draw animation on screen.
	 *
	 * @param delta     Time change since last call.
	 * @param batch     The Batch for drawing stuff.
	 * @return          return <code>true</code> if the animation has completed.
	 */
	boolean tick(float delta, Batch batch);

	void setAnimationFinish(IAnimationFinish callback);
	void callAnimationFinish();
	void cancelAnimation();

    void setupScreen(AbstractAnimationScreen abstractAnimationScreen);
}
