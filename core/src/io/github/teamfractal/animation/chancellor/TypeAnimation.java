package io.github.teamfractal.animation.chancellor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.teamfractal.animation.AbstractAnimation;
import io.github.teamfractal.animation.IAnimation;
import io.github.teamfractal.exception.InvalidControlCharException;

public class TypeAnimation extends AbstractAnimation implements IAnimation {
	private final WildChancellorAppear appearAnimation;
	double typeInterval = 0.5;
	String text = "";
	int index;
	float x, y;
	BitmapFont font;
	GlyphLayout layout;
	float lastTime;
	float waitTime;

	StringBuilder renderStrBuilder = new StringBuilder(255);

	private boolean sendNotification = false;

	public TypeAnimation(WildChancellorAppear appearAnimation, float x, float y, BitmapFont font) {
		this.appearAnimation = appearAnimation;
		this.x = x;
		this.y = y;
		this.font = font;
		lastTime = 0;
		layout = new GlyphLayout(font, "");
	}

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

	private boolean endStringLoading() {
		if (index < 0) index = 0;
		return index >= text.length();
	}

	/**
	 * Check if we are waiting; if not, update the index.
	 * @return <code>true</code> if we are waiting, otherwise we are not.
	 */
	private boolean waiting() {
		if (waitTime <= 0) return false;

		waitTime -= lastTime;
		if (waitTime <= 0) {
			// subtract neg value = add
			lastTime = -waitTime;
			waitTime = 0;
			return false;
		}

		lastTime = 0;
		return true;
	}

	private void updateString() {
		while(lastTime >= typeInterval) {
			while(waiting()) {
				return ;
			}

			lastTime -= typeInterval;
			if (loadChar()) {
				layout.setText(font, renderStrBuilder.toString());
			}
		}
	}

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
					System.out.println("Update typeInterval to " + typeInterval);
					ignoreWhiteSpace();
					expectChar('}');
					break;

				case 'w':
					// wait
					expectChar('{');
					ignoreWhiteSpace();
					waitTime += readFloat();
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

	private void ignoreWhiteSpace() {
		while(isWhiteSpace(peekChar())) {
			index++;
		}
	}

	private boolean isWhiteSpace(char c) {
		return c == ' '
				|| c == '\t'
				|| c == '\r'
				|| c == '\n';
	}

	private void expectChar(char check) {
		char c = readChar();
		if (c != check) {
			throw new InvalidControlCharException(c, index);
		}
	}

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

	private char peekChar() {
		if (endStringLoading()) return '\0';

		return text.charAt(index);
	}

	private char readChar() {
		char c = peekChar();
		index++;
		return c;
	}

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

	public void setText(String text) {
		time = 0;
		index = 0;
		lastTime = 0;
		waitTime = 0;
		renderStrBuilder = new StringBuilder(255);
		sendNotification = false;
		this.text = text;
	}

	public void setInterval(double interval) {
		this.typeInterval = interval;
	}
}
