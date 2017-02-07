package io.github.teamfractal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.teamfractal.RoboticonQuest;


public class HomeMainMenu extends Table {
	private RoboticonQuest game;
	private TextButton btnNewGame;
	private TextButton btnNewAIGame;
	private TextButton btnExit;

	private static Texture titleTexture = new Texture(Gdx.files.internal("roboticon_images/Duck-Related Roboticon Quest (Small).png"));

	/**
	 * Initialise the Home Menu.
	 * @param game    The game object.
	 */
	public HomeMainMenu(RoboticonQuest game) {
		this.game = game;

		// Create UI Components
		final Image imgTitle = new Image();
		imgTitle.setDrawable(new TextureRegionDrawable(new TextureRegion(titleTexture)));
		
		btnNewGame = new TextButton("Begin Two-Player Game", game.skin);
		btnNewAIGame = new TextButton("Begin Game Against AI", game.skin);
		btnExit = new TextButton("Exit", game.skin);

		// Adjust properties.
		btnNewGame.pad(10);
		btnNewAIGame.pad(10);
		btnExit.pad(10);

		// Bind events.
		bindEvents();

		// Add UI Components to table.
		add(imgTitle).pad(5).colspan(3);
		row();
		add(btnNewGame);
		add(btnNewAIGame);
		add(btnExit);
	}

	/**
	 * Bind button events.
	 */
	private void bindEvents() {
		btnNewGame.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				game.setScreen(game.gameScreen);
				game.gameScreen.newGame();
			}
		});

		btnNewAIGame.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				//CODE TO BEGIN AI GAME HERE
			}
		});

		btnExit.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
	}
}
