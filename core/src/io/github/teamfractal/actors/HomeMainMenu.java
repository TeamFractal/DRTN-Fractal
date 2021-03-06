
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
import io.github.teamfractal.screens.PlayerSelectScreen;


public class HomeMainMenu extends Table {
    private static Texture titleTexture = new Texture(Gdx.files.internal("roboticon_images/Duck-Related Roboticon Quest (Small).png"));
    private RoboticonQuest game;
	private TextButton btnNewGame;
	private TextButton btnExit;

	/**
	 * Initialise the Home Menu.
	 * @param game    The game object.
	 */
	public HomeMainMenu(RoboticonQuest game) {
		this.game = game;

		// Create UI Components
		final Image imgTitle = new Image();
		imgTitle.setDrawable(new TextureRegionDrawable(new TextureRegion(titleTexture)));
		

		btnNewGame = new TextButton("Begin New Game", game.skin);


		btnExit = new TextButton("Exit", game.skin);

		// Adjust properties.
		btnNewGame.pad(10);
		btnExit.pad(10);

		// Bind events.
		bindEvents();

		// Add UI Components to table.
		add(imgTitle).pad(5).colspan(3);
		row();

		add(btnNewGame);
		add(btnExit);

	}

	/**
	 * Bind button events.
	 */
	private void bindEvents() {
		btnNewGame.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				game.setScreen(new PlayerSelectScreen(game));
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
