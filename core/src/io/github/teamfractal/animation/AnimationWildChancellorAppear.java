package io.github.teamfractal.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.teamfractal.screens.AbstractAnimationScreen;

import java.util.Random;

public class AnimationWildChancellorAppear implements IAnimation {
	private static Random rnd = new Random();
	private static float nextFloat(float max) {
		return rnd.nextFloat() * max;
	}
	private class Strip {
		private final AnimationWildChancellorAppear animation;
		float width;
		float heightBound;
		public float velocity;
		public float x;
		public float y;

		public Strip(float heightBound, AnimationWildChancellorAppear animation) {
			this.animation = animation;
			this.heightBound = heightBound;
			reset();
		}

		public void reset() {
			velocity = nextFloat(3f);
			width = nextFloat(7.5f);
			x = nextFloat(Gdx.graphics.getWidth() + width);
		}

		public void tick(float delta, Batch batch) {
			x -= velocity * delta;
			if (x - width < 0) {
				reset();
			}
		}
	}

	public AnimationWildChancellorAppear() {

	}

	@Override
	public boolean tick(float delta, AbstractAnimationScreen screen, Batch batch) {
		return false;
	}

	@Override
	public void setAnimationFinish(IAnimationFinish callback) {

	}

	@Override
	public void callAnimationFinish() {

	}

	@Override
	public void cancelAnimation() {

	}
}
