

package io.github.teamfractal.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.teamfractal.screens.AbstractAnimationScreen;

public class AnimationCustomHeader extends AbstractAnimation implements IAnimation {
    /**
     * Measures the length of time over which the animation should play out
     */
    private float length;

    /**
     * The text to be displayed in the animation
     */
    private String text;

    /**
     * The font of the text to be displayed in the animation
     */
    private BitmapFont font;

    /**
     * Builds a textual animation that, when played, fades in from the top; remains stationary for a few seconds,
     * and then fades out to disappear until it's played again
     *
     * @param text The text to be displayed in the animation
     * @param font The font of the text to be displayed in the animation
     * @param length The length of time over which the animation should play out
     */
    public AnimationCustomHeader(String text, BitmapFont font, int length) {
        this.text = text;
        this.font = font;
        this.length = length;
    }

    /**
     * Function that maps time [t] to [t^2] when [t less than 1]...
     * ...[t] to 1 when [1 less than t less than (length - 1)]...
     * ...and [t] to [(length - t)^2] when [(length - 1) less than t less than length]
     *
     * Used to calculate opacity levels for the animation's fade-in and fade-out sequences
     *
     * @return float The alpha (opacity) level of the animation
     */
    private float calculateOpacity() {
        if (time < 1) {
            return (float) Math.pow(time, 2);
        } else if (time < length - 1) {
            return 1;
        } else {
            return (float) Math.pow(length - time, 2);
        }
    }

    /**
     * Sets the text to be displayed in the animation
     *
     * @param text The text to be displayed in the animation
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the length of the animation (in seconds)
     *
     * @param length The length of time over which the animation should play out
     */
    public void setLength(float length) {
        this.length = length;
    }

    /**
     * Executes once per frame to drive the animation forward
     *
     * @param batch The rendering pipeline operated to draw the animation's next frame
     */
    @Override
    public boolean tick(Batch batch) {
        //Update the amount of time recorded since the animation's beginning

        if (time > length) {
            return true;
        }
        //Cut this method short and prevent the animation from being drawn if the amount of time since it was
        //last played back exceeds the length of the animation itself

        batch.begin();
        //Activate a rendering pipeline to draw the animation for the next frame

        font.setColor(1, 1, 1, calculateOpacity());
        GlyphLayout GL = new GlyphLayout(font, text);
        //Set up the font for the animation's text based on the amount of time since it was started

        if (time < 1) {
            font.draw(batch, GL, Gdx.graphics.getWidth() / 2 - GL.width / 2, Gdx.graphics.getHeight() - 5 - (((float) Math.pow(time, 2)) * 30));
        } else {
            font.draw(batch, GL, Gdx.graphics.getWidth() / 2 - GL.width / 2, Gdx.graphics.getHeight() - 35);
        }
        //Draws the animation such that it slides in from the top at the beginning and then freezes in place

        batch.end();
        //Stop the rendering pipeline now that it has drawn the animation for the next frame

        return false;
    }
}
