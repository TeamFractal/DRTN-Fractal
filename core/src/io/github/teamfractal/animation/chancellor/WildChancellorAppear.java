package io.github.teamfractal.animation.chancellor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.animation.AbstractAnimation;
import io.github.teamfractal.animation.AnimationTimeout;
import io.github.teamfractal.animation.IAnimation;
import io.github.teamfractal.animation.IAnimationFinish;
import io.github.teamfractal.entity.CaptureData;
import io.github.teamfractal.entity.Player;
import io.github.teamfractal.screens.AbstractAnimationScreen;
import io.github.teamfractal.util.TTFont;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WildChancellorAppear extends AbstractAnimation implements IAnimation {
	private static final Random rnd = new Random();
	private static final CaptureData captureData;
	private static float nextFloat(float max) {
		return rnd.nextFloat() * max;
	}
	private static final ShapeRenderer shape = new ShapeRenderer();
	private static final Color shadowColour = new Color(0f, 0f, 0f, 0.7f);
	private static final int overlayOffsetY = 80;
	private static final Texture textureChancellor;
	private static final BitmapFont fontTitle;
    private static final BitmapFont fontText;
    private static final int handCount = 4;
    private static final float handAnimationTime = 0.3f;
	private static final float handAnimationTotalTime = handAnimationTime * (handCount - 0.5f);
	private static final List<Texture> textureHands;
	private static final Texture textureMasterBall;

    private final InputProcessor processor;
    private final Stage stage;
    private final RoboticonQuest game;
    private boolean eventEnd = false;
    private CaptureState state = CaptureState.WaitInput;
    private float shadowHeight;
    private AbstractAnimationScreen.Size size;
    private final CaptureData.AttributeType chancellType;

    AnimationTimeout timeoutAnimation = new AnimationTimeout(15);

	static {
        textureChancellor = new Texture(Gdx.files.internal("image/chancellor.png"));
        TTFont f = new TTFont(Gdx.files.internal("font/MontserratRegular.ttf"));

        f.setSize(36);
        fontTitle = f.font();

        f.setSize(14);
        fontText = f.font();

	    textureHands = new ArrayList<Texture>(handCount);
	    for(int i = 1; i <= handCount; i++)
	    	textureHands.add(new Texture(Gdx.files.internal("capture/hand" + i + ".png")));
	    textureMasterBall = new Texture(Gdx.files.internal("capture/master-ball.png"));

	    captureData = CaptureData.getData();
    }

	private final static int heightBound = 90;
	private final List<WhiteStrip> strips;
    private final Table table = new Table();
	public WildChancellorAppear(RoboticonQuest game, Skin skin) {
	    this.game = game;
	    processor = Gdx.input.getInputProcessor();
	    stage = new Stage();

        chancellType = CaptureData.randomType();
		int count = rnd.nextInt(30) + 20;
		strips = new ArrayList<WhiteStrip>(count);
		for (int i = 0; i < count; i++) {
			strips.add(new WhiteStrip(heightBound, this));
		}

		float padLeft;

		TextButton btnMasterBall = new TextButton("Master Ball ($20)", skin);

		TextButton btnFight = new TextButton("Fight ($10)", skin);
		padLeft = (btnMasterBall.getPrefWidth() - btnFight.getPrefWidth()) / 2 + 20;
		btnFight.pad(20, padLeft, 20, padLeft);


		TextButton btnLeave = new TextButton("Run Away", skin);
		padLeft = (btnMasterBall.getPrefWidth() - btnLeave.getPrefWidth()) / 2 + 20;
		btnLeave.pad(20, padLeft, 20, padLeft);

		btnMasterBall.pad(20);

		table.add(btnFight).padRight(30);
		table.add(btnMasterBall).padRight(30);
        table.add(btnLeave);
        table.setX((Gdx.graphics.getWidth()) / 2);
        table.setY((Gdx.graphics.getHeight() - table.getPrefHeight()) / 2);
        stage.addActor(table);

        btnMasterBall.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
	            timeoutAnimation.setAnimationFinish(null);
                captureChancellor();
	            timeoutAnimation.cancelAnimation();
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
	}

	float captureTime;


	@Override
	public void setupScreen(AbstractAnimationScreen abstractAnimationScreen) {
		super.setupScreen(abstractAnimationScreen);

		size = screen.getScreenSize();
		shadowHeight = (size.Height - heightBound) / 2;

		float offsetY = shadowHeight + overlayOffsetY;

		for (WhiteStrip strip : strips) {
			// Draw shadow and background
			strip.setOffsetY(offsetY);
		}

		screen.addAnimation(timeoutAnimation);
	}

    private void captureChancellor() {
	    captureTime = time;
        Player player = game.getPlayer();
        if (player.getMoney() >= 20) {
            player.setMoney(player.getMoney() - 20);
            table.setVisible(false);
            doCaptureAnimation();
        }
    }

    private void doCaptureAnimation() {
        state = CaptureState.BeginCapture;
        // TODO: prepare capture text feedback
    }

    @Override
	public boolean tickDelta(float delta, Batch batch) {
        Gdx.input.setInputProcessor(stage);

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
	    for (WhiteStrip strip : strips) {
		    strip.tick(delta, batch);
	    }

        batch.begin();
	    // Draw event name
		fontTitle.draw(batch, "A wild Chancellor has appeared!",
                size.Width / 2,
                (size.Height - fontTitle.getLineHeight()) / 2 + overlayOffsetY + heightBound + 20,
                0, Align.center, false);

        // Draw Chancellor
		batch.draw(textureChancellor,
                (size.Width - textureChancellor.getWidth()) / 2,
                (size.Height - textureChancellor.getHeight()) / 2 + overlayOffsetY + 10);

        // Draw Chancellor type
        fontText.setColor(Color.BLACK);
        fontText.draw(batch, chancellType.toString(),
                size.Width / 2 + textureChancellor.getWidth() / 2 + 20,
                size.Height / 2 + overlayOffsetY + textureChancellor.getHeight() - 20,
                0, Align.left, false);

		batch.end();

        stage.act(delta);
        stage.draw();

        switch (state) {
	        case WaitInput:
		        break;

	        case BeginCapture:
	        	renderCaptureAnimation(batch);
		        break;

	        case MasterBallThrown:
		        break;

	        case CaptureInProgress:
		        break;

	        case CaptureFailed:
		        break;

	        case CaptureSuccess:
		        break;

	        case CaptureTimeout:
		        break;
        }

	    return eventEnd;
	}

	private void renderCaptureAnimation(Batch batch) {
		float t = time - captureTime;

		int handIndex = (int)Math.floor(t / handAnimationTime);
		if (handIndex >= handCount)
			handIndex = handCount - 1;

		batch.begin();
		batch.draw(textureHands.get(handIndex), -40 * Math.max(0, t - handAnimationTotalTime), 0);

		// Ball move from (70, 185).
		float x = t * 60f;
		batch.draw(textureMasterBall, 70 + x, (float)Math.sqrt(x) * t + 185);

		// fontText.draw(batch, "t: " + t, 20, 20);
		fontText.draw(batch, game.getPlayerName() + " has thrown master ball!", 20, 20);

		if (t > 6.3f) {
			state = CaptureState.MasterBallThrown;
		}

		batch.end();
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
		timeoutAnimation.cancelAnimation();
		timeoutAnimation.setAnimationFinish(null);
	}

	enum CaptureState {
	    WaitInput,
		BeginCapture,
		MasterBallThrown,
		CaptureInProgress,
        CaptureFailed,
        CaptureSuccess,
		CaptureTimeout,

        WaitFightAction,
        DoFightAction,
        FightResult
    }
}
