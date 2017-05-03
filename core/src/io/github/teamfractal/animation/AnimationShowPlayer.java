

package io.github.teamfractal.animation;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.teamfractal.entity.Player;
import io.github.teamfractal.screens.AbstractAnimationScreen;

public class AnimationShowPlayer extends AbstractAnimation implements IAnimation {
	private float time;
	private static BitmapFont font = new BitmapFont();
	private static GlyphLayout glyphLayout = new GlyphLayout();
	private int playerNumber;

	public AnimationShowPlayer (int playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * Generate and concat resource strings.
	 * @return  Generated resource generation message.
	 */
	private float fn_quad(float t) {
		if (t > 1f) return 1;
		return t * t;
	}

	/**
	 * The quadratic function.
	 * @return       If <code>t</code> is larger than <code>1</code>,
	 *               then it will return <code>1</code>; otherwise
	 *               the square of <code>t</code>.
	 */
	private float fn_opacity () {
		if (time < 1f) {
			// Fade in
			return fn_quad(time);
		} else if (time >= 1f && time < 2f) {
			// Stay visible.
			return 1;
		} else {
			// 2 ~ 3:
			// Reverse of the function to fade out.
			return 1f - fn_quad(time - 2f);
		}
	}

	// Time length for the animation in second.
	private static final float animationLength = 3;

	/**
	 * Render call.
	 * @param batch     The Batch for drawing stuff.
	 * @return          return <code>true</code> if the animation has completed.
	 */
	@Override
	public boolean tick(Batch batch) {
		if (time > animationLength) {
			return true;
		}

		AbstractAnimationScreen.Size size = screen.getScreenSize();

		batch.begin();
		font.setColor(1,1,1, fn_opacity());
		glyphLayout.setText(font, "PLAYER " + playerNumber);
		font.draw(batch, glyphLayout, size.Width / 2 - glyphLayout.width / 2, size.Height - 20 - fn_quad(time) * 30);
		batch.end();
		return false;
	}
}
