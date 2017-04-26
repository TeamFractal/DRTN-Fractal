package io.github.teamfractal.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.animation.IAnimation;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public abstract class AbstractAnimationScreen {
	protected abstract RoboticonQuest getGame();

	/**
	 * Animation queue.
	 */
	private final ArrayList<IAnimation> animations = new ArrayList<IAnimation>();

	/**
	 * While rendering, animation queue should not be modified.
	 */
	private final ArrayList<IAnimation> queueAnimations = new ArrayList<IAnimation>();

	/**
	 * Add a new animation to current Screen.
	 * @param animation    The animation to be added.
	 */
	public void addAnimation(IAnimation animation) {
		if (!animations.contains(animation) && !queueAnimations.contains(animation)) {
			synchronized (queueAnimations) {
				queueAnimations.add(animation);
				animation.setupScreen(this);
			}
		}
	}

	/**
	 * Request to render animation.
	 * @param delta   Time delta from last render call.
	 */
	void renderAnimation(float delta) {
		Batch batch = getGame().getBatch();

		synchronized (animations) {
			synchronized (queueAnimations) {
				animations.addAll(queueAnimations);
				queueAnimations.clear();
			}

			try {
				Iterator<IAnimation> iterator = animations.iterator();

				while (iterator.hasNext()) {
					IAnimation animation = iterator.next();
					if (animation.tick(delta, batch)) {
						iterator.remove();
						animation.callAnimationFinish();
					}
				}
			} catch (ConcurrentModificationException ex) {
				// ignore
			}
		}
	}

	/**
	 * The screen size.
	 * @return  Size of the screen.
	 */
	abstract public Size getScreenSize();

	/**
	 * Remove all animation present on the screen.
	 */
	public void clearAnimation() {
		synchronized (animations) {
			synchronized (queueAnimations) {
				animations.clear();
				queueAnimations.clear();
			}
		}
	}

	/**
	 * A size structure with Width and Height property.
	 */
	public class Size {
		public float Width;
		public float Height;
	}
}
