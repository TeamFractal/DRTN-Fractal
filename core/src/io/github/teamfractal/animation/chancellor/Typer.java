package io.github.teamfractal.animation.chancellor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.teamfractal.animation.AbstractAnimation;
import io.github.teamfractal.animation.IAnimation;

public class Typer extends AbstractAnimation implements IAnimation {
	private final WildChancellorAppear appearAnimation;
	double typeInterval = 0.05;
	String text = "";
	int index;
	float x, y;
	BitmapFont font;
	GlyphLayout layout;
	private boolean sendNotification = false;

	public Typer(WildChancellorAppear appearAnimation, float x, float y, BitmapFont font) {
		this.appearAnimation = appearAnimation;
		this.x = x;
		this.y = y;
		this.font = font;
		layout = new GlyphLayout(font, "");
	}

	@Override
	protected boolean tick(Batch batch) {
		index = (int)(time / typeInterval);
		String render = index >= text.length() ? text : text.substring(0, index);

		if (!sendNotification && index >= text.length()) {
			sendNotification = true;
			appearAnimation.typeFinish();
		}

		layout.setText(font, render);

		batch.begin();
		font.setColor(Color.WHITE);
		font.draw(batch, layout, x, y + layout.height);
		batch.end();
		return false;
	}

	public void setText(String text) {
		time = 0;
		index = 0;
		sendNotification = false;
		this.text = text;
	}

	public void setInterval(double interval) {
		this.typeInterval = interval;
	}
}
