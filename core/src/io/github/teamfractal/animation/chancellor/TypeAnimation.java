package io.github.teamfractal.animation.chancellor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.teamfractal.animation.AbstractAnimation;
import io.github.teamfractal.animation.IAnimation;
import io.github.teamfractal.exception.InvalidControlCharException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TypeAnimation extends AbstractAnimation implements IAnimation {
	private final WildChancellorAppear appearAnimation;
	double typeInterval = 0.5;
	String text = "";
	int index;
	float x, y;
	BitmapFont font;
	GlyphLayout layout;
	float lastTime;

	StringBuilder renderStrBuilder = new StringBuilder(255);

	private boolean sendNotification = false;

	/**
	 * Init. class
	 * @param appearAnimation   The parent controller
	 * @param x                 Text display x cord.
	 * @param y                 Text display y cord.
	 * @param font              The font used to draw text
	 */
	public TypeAnimation(WildChancellorAppear appearAnimation, float x, float y, BitmapFont font) {
		this.appearAnimation = appearAnimation;
		this.x = x;
		this.y = y;
		this.font = font;
		lastTime = 0;
		layout = new GlyphLayout(font, "");
	}

	/**
	 * Render text on each frame
	 * @param delta  Time difference
	 * @param batch  <see>ScriptBatch</see> for drawing
	 * @return       <code>true</code> if finished, <code>otherwise</code>
	 */
	@Override
	protected boolean tickDelta(float delta, Batch batch) {
		lastTime += delta;
		if (!endStringLoading()) {
			updateString();
		}

		if (!sendNotification && index >= text.length()) {
			sendNotification = true;
			appearAnimation.typeFinish();
		}

		batch.begin();
		font.setColor(Color.WHITE);
		font.draw(batch, layout, x, y + layout.height);
		batch.end();
		return false;
	}

	/**
	 * Check if string reading is in the end.
	 * @return
	 */
	private boolean endStringLoading() {
		if (index < 0) index = 0;
		return index >= text.length();
	}

	/**
	 * Calculate the text to draw, based on time.
	 */
	private void updateString() {
		while(lastTime >= typeInterval) {
			if (loadChar()) {
				lastTime -= typeInterval;
				layout.setText(font, renderStrBuilder.toString());
			}
		}
	}

	/**
	 * Read next character from string.
	 * If a control character is found, is going to perform some actions.
	 * @return <code>true</code> if the text display buffer has been updated.
	 */
	private boolean loadChar() {
		char c = readChar();

		if (c == '\\') {
			c = readChar();

			switch(c) {
				case 'i':
					// wait
					expectChar('{');
					ignoreWhiteSpace();
					typeInterval = readFloat();
					if (typeInterval < 0.1) {
						typeInterval = 0.1;
					}
					ignoreWhiteSpace();
					expectChar('}');
					break;

				case 'w':
					// wait
					expectChar('{');
					ignoreWhiteSpace();
					lastTime -= readFloat();
					ignoreWhiteSpace();
					expectChar('}');
					break;

				case '\\':
					// escape char
					renderStrBuilder.append(c);
					return true;

				default:
					throw new InvalidControlCharException(c, index);
			}
		} else {
			if (c != '\0') renderStrBuilder.append(c);
			return true;
		}
		return false;
	}

	/**
	 * Skip whitespaces from next string
	 */
	private void ignoreWhiteSpace() {
		while(isWhiteSpace(peekChar())) {
			index++;
		}
	}


	/**
	 * Check if a <code>char</code> is whitespace
	 * @param c   The <code>char</code> to be checked.
	 * @return    <code>true</code> if is, <code>false</code> otherwise.
	 */
	private boolean isWhiteSpace(char c) {
		return c == ' '
				|| c == '\t'
				|| c == '\r'
				|| c == '\n';
	}

	/**
	 * Ensure the next char is the character requested and move the cursor over.
	 * It will throw <see>InvalidControlCharException</see> if does not match.
	 * @param check    Expected character.
	 */
	private void expectChar(char check) {
		char c = readChar();
		if (c != check) {
			throw new InvalidControlCharException(c, index);
		}
	}


	/**
	 * Read in a float.
	 * @return   Float read in.
	 */
	private float readFloat() {
		float v = readInt();

		if (peekChar() == '.') {
			index ++;

			float decimal = 0.1f;

			while (true) {
				char c = peekChar();

				if ('0' <= c && c <= '9') {
					v = v + (c - '0') * decimal;
					decimal /= 10;
					index++;
				} else {
					break;
				}
			}
		}

		return v;
	}

	/**
	 * Preview next character.
	 * @return Next character in the stream.
	 */
	private char peekChar() {
		if (endStringLoading()) return '\0';

		return text.charAt(index);
	}

	/**
	 * Read in next character and move the cursor next to it.
	 * @return The next character read in.
	 */
	private char readChar() {
		char c = peekChar();
		index++;
		return c;
	}

	/**
	 * Read in the next integer in dec format.
	 * @return     Integer read in.
	 */
	private int readInt() {
		int v = 0;

		while (true) {
			char c = peekChar();

			if ('0' <= c && c <= '9') {
				v = v * 10 + (c - '0');
				index++;
			} else {
				break;
			}
		}

		return v;
	}

	/**
	 * Set the target display text.
	 * @param text    The new text
	 */
	public void setText(String text) {
		time = 0;
		index = 0;
		lastTime = 0;
		renderStrBuilder = new StringBuilder(255);
		sendNotification = false;
		this.text = text;
	}

	/**
	 * Set character display interval.
	 * @param interval       New time interval in seconds.
	 */
	void setInterval(double interval) {
		this.typeInterval = interval;
	}
}
