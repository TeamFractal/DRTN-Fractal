/**
 * @author DRTN
 * Team Website with download:
 * https://misterseph.github.io/DuckRelatedFractalProject/
 *
 * This Class contains either modifications or is entirely new in Assessment 3
 *
 * If you are in any doubt a complete changelog can be found here:
 * https://github.com/NotKieran/DRTN-Fractal/compare/Fractal_Initial...development
 *
 * And a more concise report can be found in our Change3 document.
 **/

package io.github.teamfractal.animation;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.Player;
import io.github.teamfractal.screens.AbstractAnimationScreen;

public class AnimationPhaseTimeout extends AnimationTimeout implements IAnimation {
	private final Player player;
	private final RoboticonQuest game;
	private final int currentPhase;

	/**
	 * Initialise the animation.
	 * @param player         Current player.
	 * @param game           The game object.
	 * @param currentPhase   Current phase number.
	 * @param timeout        Timeout length, in seconds.
	 */
	public AnimationPhaseTimeout(Player player, RoboticonQuest game, int currentPhase, float timeout) {
		super(timeout);
		this.player = player;
		this.game = game;
		this.currentPhase = currentPhase;

		super.setAnimationFinish(new IAnimationFinish() {
			@Override
			public void OnAnimationFinish() {
				AnimationPhaseTimeout.this.game.nextPhase();
			}
		});
	}

	/**
	 * Check if the animation should continue or not.
	 * @return  <code>true</code> if the animation should continue.
	 */
	protected boolean continueAnimation() {
		return super.continueAnimation()
				&& game.getPhase() == currentPhase
				&& game.getPlayer() == player;
	}
}
