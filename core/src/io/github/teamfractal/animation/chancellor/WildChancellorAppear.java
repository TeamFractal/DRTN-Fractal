package io.github.teamfractal.animation.chancellor;

import com.badlogic.gdx.Gdx;
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
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.screens.AbstractAnimationScreen;
import io.github.teamfractal.util.TTFont;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.github.teamfractal.animation.chancellor.WildChancellorAppear.CaptureState.*;

public class WildChancellorAppear extends AbstractAnimation implements IAnimation {
	private static final Random rnd = new Random();
	private static final CaptureData captureData;
	private float endTime;
	private boolean captureSuccess;

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
    private static final float handAnimationTime = 0.2f;
	private static final List<Texture> textureHands;
	private static final Texture textureMasterBall;

    private final Stage stage;
    private final RoboticonQuest game;
    private boolean eventEnd = false;
    private CaptureState state = CaptureState.WaitInput;
    private float shadowHeight;
    private AbstractAnimationScreen.Size size;
    private final CaptureData.AttributeType chancellType;
    private final int chancellorHp = 60;
	private String promptMessage = "";
	private TypeAnimation typeAnimation;


	AnimationTimeout timeoutAnimation = new AnimationTimeout(15);

	/**
	 * Get resource ready in the memory.
	 */
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
	private final Table tableActions = new Table();
	private final Table tableFight = new Table();

	/**
	 * Initialise the Chancellor class.
	 * @param game    The game object.
	 * @param skin    The UI skin.
	 */
	public WildChancellorAppear(RoboticonQuest game, Skin skin) {
	    this.game = game;
	    stage = new Stage();

        chancellType = CaptureData.randomType();
		int count = rnd.nextInt(30) + 20;
		strips = new ArrayList<WhiteStrip>(count);
		for (int i = 0; i < count; i++) {
			strips.add(new WhiteStrip(heightBound, this));
		}


		// Action buttons
		float padLeft;

		TextButton btnMasterBall = new TextButton("Master Ball ($20)", skin);

		TextButton btnFight = new TextButton("Fight ($10)", skin);
		padLeft = (btnMasterBall.getPrefWidth() - btnFight.getPrefWidth()) / 2 + 20;
		btnFight.pad(20, padLeft, 20, padLeft);


		TextButton btnLeave = new TextButton("Run Away", skin);
		padLeft = (btnMasterBall.getPrefWidth() - btnLeave.getPrefWidth()) / 2 + 20;
		btnLeave.pad(20, padLeft, 20, padLeft);

		btnMasterBall.pad(20);
		
		
		// Fight options
		tableFight.padLeft(30);
		for (final CaptureData.FightAction action: captureData.fightActions) {
			TextButton btnAction = new TextButton(action.name, skin);
			btnAction.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					timeoutAnimation.cancelAnimation();
					WildChancellorAppear.this.doFight(action);
				}
			});
			btnAction.pad(20);
			tableFight.add(btnAction).padRight(30);
		}
		tableFight.setX((Gdx.graphics.getWidth()) / 2);
		tableFight.setY((Gdx.graphics.getHeight() - tableFight.getPrefHeight()) / 2);
		tableFight.setVisible(false);
		stage.addActor(tableFight);
		
		

		tableActions.add(btnFight).padRight(30);
		tableActions.add(btnMasterBall).padRight(30);
        tableActions.add(btnLeave);
        tableActions.setX((Gdx.graphics.getWidth()) / 2);
        tableActions.setY((Gdx.graphics.getHeight() - tableActions.getPrefHeight()) / 2);
        stage.addActor(tableActions);

        btnFight.addListener(new ChangeListener() {
	        @Override
	        public void changed(ChangeEvent event, Actor actor) {
		        tableFight.setVisible(true);
		        tableActions.setVisible(false);
		        state = WaitFightAction;
	        }
        });

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
        typeAnimation = new TypeAnimation(this,20, 20, fontText);
	}

	CaptureData.FightAction lastFightAction;

	/**
	 * Perform one of the fight action and prepare the battle text.
	 * @param action
	 */
	private void doFight(CaptureData.FightAction action) {
		tableFight.setVisible(false);
		lastFightAction = action;

		CaptureData.AttributeRate rate = captureData.getRateFromType(lastFightAction.type);
		CaptureData.AttributeRate.Against againstRate = rate.getAgainstRateFromType(chancellType);
		String comment = captureData.getCommentFromMultiplier(againstRate.multiplier);

		String str = game.getPlayerName() + " have used " + action.name + "!\\w{0.5}";
		if (comment != null) {
			str += "\nIt's " + comment + "\\w{1} ";
		}
		typeAnimation.setInterval(0.1);
		typeAnimation.setText(str);
	}

	/**
	 * Perform the fight action and prepare the battle text.
	 */
	private void doFightResultText () {
		CaptureData.AttributeRate rate = captureData.getRateFromType(lastFightAction.type);
		CaptureData.AttributeRate.Against againstRate = rate.getAgainstRateFromType(chancellType);
		double multiplier = againstRate.multiplier;

		double dmg = (rnd.nextInt(40) + 40) * multiplier;

		// TODO: Cost and price
		if (dmg > chancellorHp) {
			ST successText = new ST(lastFightAction.success);
			successText.add("price", "Energy x30");
			game.getPlayer().setResource(ResourceType.ENERGY, game.getPlayer().getEnergy() + 30);

			typeAnimation.setText(successText.render());
			state = CaptureState.FightSuccessTyping;
		} else {
			CaptureData.FightAction.Fail fail = lastFightAction.fail.get(rnd.nextInt(lastFightAction.fail.size()));
			CaptureData.FightAction.Fail.ActualCost actualCost = fail.calculateCost();
			ST failText = new ST(fail.message);
			String costStr = actualCost.toString();
			if ("".equals(costStr)) {
				costStr = "\nNothing.";
			}
			failText.add("cost", costStr);

			game.getPlayer().applyCost(actualCost);

			typeAnimation.setText(failText.render());
			state = CaptureState.FightFailTyping;
		}
		tableFight.setVisible(false);
		timeoutAnimation.cancelAnimation();
	}

	float eventTime;


	/**
	 * Setup screen.
	 * @param abstractAnimationScreen  Current screen
	 */
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
		screen.addAnimation(typeAnimation);
	}

	/**
	 * Button "Master Ball" callback
	 */
    private void captureChancellor() {
	    eventTime = time;
        Player player = game.getPlayer();
        if (player.getMoney() >= 20) {
            player.setMoney(player.getMoney() - 20);
            tableActions.setVisible(false);
            doCaptureAnimation();
        }
    }

	/**
	 * Set animation start flag.
	 */
	private void doCaptureAnimation() {
        state = CaptureState.BeginCapture;
    }

	/**
	 * Next rendering frame.
	 * @param delta   Time escaped
	 * @param batch   The script batch to draw on.
	 * @return        True: Animation complete; false otherwise.
	 */
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
	    fontText.setColor(Color.WHITE);

		batch.end();

        stage.act(delta);
        stage.draw();

        switch (state) {
	        case BeginCapture:
	        	renderCaptureAnimation(batch);
		        break;

	        case CaptureFinish:
	        case FightResult:
	        	if (time >= endTime) {
					eventEnd = true;
		        }
		        break;

	        case CaptureInProgress:
	        	break;
        }


        // Debug: Draw current state name.
		/*
        batch.begin();
        fontText.setColor(Color.WHITE);
        fontText.draw(batch, "STATE: " + state.toString(), 300, 20);
        batch.end();
		*/

		if (eventEnd) {
			game.nextPhase();
		}

	    return eventEnd;
	}

	private float ballPosX;
	private float ballPosY;

	/**
	 * Render frames during master ball event.
	 * @param batch  The sprite batch to draw on.
	 */
	private void renderCaptureAnimation(Batch batch) {
		float t = time - eventTime;

		int handIndex = (int)Math.floor(t / handAnimationTime);
		if (handIndex >= handCount)
			handIndex = handCount - 1;

		batch.begin();
		// Move hand to left
		batch.draw(textureHands.get(handIndex), -100 * t, 0);

		// Ball move from (70, 185).
		calcBallPos(70, 185, 380, 350, t, 0.8f);
		batch.draw(textureMasterBall, ballPosX, ballPosY);

		// fontText.draw(batch, "t: " + t, 20, 20);
		fontText.draw(batch, game.getPlayerName() + " has thrown master ball!", 20, 20);

		if (t > 1f) {
			state = CaptureState.CaptureInProgress;
			captureSuccess = rnd.nextBoolean();
			typeAnimation.setText(captureData.strBeginMasterBall);
			eventTime = time;
		}

		batch.end();
	}

	/**
	 * Square
	 * @param x   The number to be squared
	 * @return    Squared value.
	 */
	private float sq(float x) {
		return x * x;
	}

	/**
	 * Calculate the position of the master ball based on time and the parabola.
	 *
	 * @param startX      Starting position X
	 * @param startY      Starting position Y
	 * @param endX        Top position X
	 * @param endY        Top position Y
	 * @param currentTime Current time in scale.
	 * @param totalTime   Time taken to move from starting point to top point.
	 */
	private void calcBallPos(float startX, float startY, float endX, float endY, float currentTime, float totalTime) {
		double w = endX - startX;
		double h = endY - startY;
		double x = currentTime / totalTime * w;

		// Calculate param <r>.
		// Formula: y = -((x - w) * r)^2 + h
		// When x = 0, y = 0.
		// 0 = - (-w * r)^2 + h
		// (w * r)^2 = h
		// Take positive solution
		// w * r = sqrt(h)
		// r = sqrt(h) / w
		double r = Math.sqrt(h) / w;
		ballPosY = startY + (float)(h - Math.pow((x - w) * r, 2));
		ballPosX = startX + (float)x;
	}

	/**
	 * Cancel animation.
	 */
	@Override
	public void cancelAnimation() {
        eventEnd = true;
		timeoutAnimation.cancelAnimation();
		typeAnimation.cancelAnimation();
	}

	/**
	 * Typer event complete callback.
	 */
	public void typeFinish() {
		System.out.println("typeFinish: " + state.toString());
		switch (state) {
			case WaitFightAction:
				doFightResultText();
				break;

			case CaptureTextTyping:
				state = CaptureFinish;
				endTime = time + 5;
				break;

			case FightSuccessTyping:
			case FightFailTyping:
				state = FightResult;
				endTime = time + 5;
				break;

			case CaptureInProgress:
				state = CaptureTextTyping;
				typeAnimation.setInterval(0.1);
				if (captureSuccess) {
					typeAnimation.setText(captureData.strCaptureSuccess);
				} else {
					typeAnimation.setText(captureData.strCaptureFail);
				}
		}
	}

	/**
	 * Cancel animation on animation finish.
	 */
	@Override
	public void callAnimationFinish() {
		cancelAnimation();
	}

	/**
	 * All possible capture running state.
	 */
	enum CaptureState {
	    WaitInput,
		BeginCapture,
		CaptureInProgress,
        CaptureFinish,
		CaptureTextTyping,

        WaitFightAction,
		FightSuccessTyping,
		FightFailTyping,
        FightResult
    }
}
