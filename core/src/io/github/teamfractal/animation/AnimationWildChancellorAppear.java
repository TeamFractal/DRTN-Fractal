package io.github.teamfractal.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.Player;
import io.github.teamfractal.screens.AbstractAnimationScreen;
import io.github.teamfractal.util.Fonts;
import io.github.teamfractal.util.TTFont;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimationWildChancellorAppear implements IAnimation {
	private static final Random rnd = new Random();
    private final InputProcessor processor;
    private final Stage stage;
    private final RoboticonQuest game;
    private boolean eventEnd = false;
    private CaptureState state;

    private static float nextFloat(float max) {
		return rnd.nextFloat() * max;
	}
	private static final ShapeRenderer shape = new ShapeRenderer();
	private static final Color shadowColour = new Color(0f, 0f, 0f, 0.7f);
	private static final int overlayOffsetY = 80;
	private static final Texture textureChancellor;
	private static final BitmapFont fontTitle;
    private static final BitmapFont fontText;
    AnimationTimeout timeoutAnimation = new AnimationTimeout(15);

    static {
        textureChancellor = new Texture(Gdx.files.internal("image/chancellor.png"));
        TTFont f = new TTFont(Gdx.files.internal("font/MontserratRegular.ttf"));

        f.setSize(36);
        fontTitle = f.font();

        f.setSize(14);
        fontText = f.font();
    }

	private class Strip {
		private final AnimationWildChancellorAppear animation;
        private float width;
        private float heightBound;
        private float velocity;
        private float x;
        private float y;
        private int height;

        public Strip(float heightBound, AnimationWildChancellorAppear animation) {
			this.animation = animation;
			this.heightBound = heightBound;
			reset(false);
		}

		public void reset(boolean xFromEnd) {
			velocity = nextFloat(200f) + 70;
			width = nextFloat(7.5f) + 50;
			height = rnd.nextInt(3) + 1;
			y = nextFloat(heightBound - height);

			if (xFromEnd) {
			    x = Gdx.graphics.getWidth();
            } else {
                x = nextFloat(Gdx.graphics.getWidth() + width) + 20;
            }
		}

		public void tick(float delta, float offsetY) {
			x -= velocity * delta;
			if (x + width < 0) {
				reset(true);
			}

			this.draw(offsetY);
		}

		private void draw(float offsetY) {
			shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.setColor(Color.WHITE);
			shape.line(x, offsetY + y, x + width, offsetY + y + height);
			shape.end();
		}
	}

	private final static int heightBound = 90;
	private final List<Strip> strips;
    private final Table table = new Table();
	public AnimationWildChancellorAppear(RoboticonQuest game, AbstractAnimationScreen screen, Skin skin) {
	    this.game = game;
	    processor = Gdx.input.getInputProcessor();
	    stage = new Stage();

		int count = rnd.nextInt(30) + 20;
		strips = new ArrayList<Strip>(count);
		for (int i = 0; i < count; i++) {
			strips.add(new Strip(heightBound, this));
		}

		TextButton btnCapture = new TextButton("Capture ($20)", skin);
		btnCapture.pad(10);

		TextButton btnLeave = new TextButton("Leave", skin);
		btnLeave.pad(10);
		float padLeft = (btnCapture.getPrefWidth() - btnLeave.getPrefWidth()) / 2 + 10;
		btnLeave.pad(10, padLeft, 10, padLeft);

		table.add(btnCapture).padRight(30);
        table.add(btnLeave);
        table.setX((Gdx.graphics.getWidth()) / 2);
        table.setY((Gdx.graphics.getHeight() - table.getPrefHeight()) / 2 );
        stage.addActor(table);

        btnCapture.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                captureChancellor();
            }
        });

        btnLeave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cancelAnimation();
            }
        });

        timeoutAnimation.setAnimationFinish(new IAnimationFinish() {
            @Override
            public void OnAnimationFinish() {
                cancelAnimation();
            }
        });
        screen.addAnimation(timeoutAnimation);
	}

    private void captureChancellor() {
        Player player = game.getPlayer();
        if (player.getMoney() >= 20) {
            player.setMoney(player.getMoney() - 20);
            table.setVisible(false);
            doCaptureAnimation();
        }
    }

    private void doCaptureAnimation() {
        state = CaptureState.Capturing;
        // TODO: prepare capture text feedback
    }

    @Override
	public boolean tick(float delta, AbstractAnimationScreen screen, Batch batch) {
        Gdx.input.setInputProcessor(stage);

		// Draw shadow and background
		AbstractAnimationScreen.Size size = screen.getScreenSize();
		float shadowHeight = (size.Height - heightBound) / 2;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(shadowColour);

		// top
		shape.rect(0, size.Height - shadowHeight + overlayOffsetY,
                size.Width, shadowHeight + overlayOffsetY);

        // bottom
        shape.rect(0, 0, size.Width, overlayOffsetY + shadowHeight);

        // middle
		shape.setColor(Color.CYAN);
		shape.rect(0, shadowHeight + overlayOffsetY, size.Width, heightBound);
		shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        // Draw strips
		for (Strip strip : strips) {
			strip.tick(delta, shadowHeight + overlayOffsetY);
		}

		// Draw Chancellor
        batch.begin();
		fontTitle.draw(batch, "A wild Chancellor has appeared!",
                size.Width / 2,
                (size.Height - fontTitle.getLineHeight()) / 2 + overlayOffsetY + heightBound + 20,
                0, Align.center, false);
		batch.draw(textureChancellor,
                (size.Width - textureChancellor.getWidth()) / 2,
                (size.Height - textureChancellor.getHeight()) / 2 + overlayOffsetY + 10);
		batch.end();

        stage.act(delta);
        stage.draw();

		return eventEnd;
	}

	@Override
	public void setAnimationFinish(IAnimationFinish callback) {

	}

	@Override
	public void callAnimationFinish() {

	}

	@Override
	public void cancelAnimation() {
        eventEnd = true;
	}

	enum CaptureState {
	    WaitInput,
	    Capturing,
        CaptureFailed,
        CaptureSuccess
    }
}
