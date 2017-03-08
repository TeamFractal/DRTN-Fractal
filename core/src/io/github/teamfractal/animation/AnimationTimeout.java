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
import io.github.teamfractal.screens.AbstractAnimationScreen;

public class AnimationTimeout extends AbstractAnimation implements IAnimation {
    private final float timeout;
    private static final BitmapFont font;
    private static final ShapeRenderer rect = new ShapeRenderer();
    private static final GlyphLayout glyphLayout = new GlyphLayout();

    static {
        rect.setAutoShapeType(true);
        font = RoboticonQuest.getInstance().tinyFontLight.font();
    }

    /**
     * Initialise the animation.
     * @param timeout        Timeout length, in seconds.
     */
    public AnimationTimeout(float timeout) {
        this.timeout = timeout;
    }

    /**
     * Count down bar colour changes from green to red overtime.
     */
    private void barColour() {
        float r = time / timeout;
        rect.setColor(r, 1 - r, 0, 0.7f);
    }


    /**
     * Draw animation on screen.
     *
     * @param batch     The Batch for drawing stuff.
     * @return          return <code>true</code> if the animation has completed.
     */
    @Override
    public boolean tick(Batch batch) {
        AbstractAnimationScreen.Size size = screen.getScreenSize();

        if (time >= timeout) return true;

        int timeLeft = (int)(timeout - time) + 1;
        String countdown = String.valueOf(timeLeft);

        synchronized (rect) {
            rect.setProjectionMatrix(batch.getProjectionMatrix());
            rect.begin(ShapeRenderer.ShapeType.Filled);
            barColour();
            rect.rect(0, 0, (1 - time / timeout) * size.Width, 3);
            rect.end();
        }

        synchronized (font) {
            batch.begin();
            glyphLayout.setText(font, countdown);
            font.setColor(1, 1, 1, 1);
            font.draw(batch, glyphLayout, 10, 25);
            batch.end();
        }

        return false;
    }
}
