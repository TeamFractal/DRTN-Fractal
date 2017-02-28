package io.github.teamfractal.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.teamfractal.RoboticonQuest;

public class PlayerSelectActors extends Table {
	
	private int AIPlayerAmount;
	private int playerAmount;
	private Label playerAmountLabel;
	private Label playerLabel;
	private Label AIAmountLabel;
	private Label AIPlayerLabel;
	private TextButton morePlayers;
	private TextButton lessPlayers;
	private TextButton moreAIPlayers;
	private TextButton lessAIPlayers;
	private RoboticonQuest game;
	
	public PlayerSelectActors(RoboticonQuest game){
		this.game = game;
		AIPlayerAmount = 0;
		playerAmount = 0;
		playerLabel = new Label("Players", game.skin2);
		AIPlayerLabel = new Label("AI Players", game.skin2);
		playerAmountLabel = new Label("0", game.skin2);
		AIAmountLabel = new Label("0", game.skin2);
		morePlayers = new TextButton("+", game.skin2);
		lessPlayers = new TextButton("-", game.skin2);
		moreAIPlayers = new TextButton("+", game.skin2);
		lessAIPlayers = new TextButton("-", game.skin2);
		add(playerLabel).height(40).left();
		row();
		add(lessPlayers);
		add(playerAmountLabel).width(30);
		add(morePlayers).width(60);
		row();
		add(AIPlayerLabel).height(40).left();
		row();
		add(lessAIPlayers);
		add(AIAmountLabel).width(30);
		add(moreAIPlayers);
		row();
		bindEvents();
		
	}
	
	private void bindEvents(){
		morePlayers.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				playerAmount += 1;
				updateLabels();
			}
		});
		
		moreAIPlayers.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				AIPlayerAmount += 1;
				updateLabels();
			}
		});
		
		lessPlayers.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				playerAmount -= 1;
				updateLabels();
			}
		});
		
		lessAIPlayers.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				AIPlayerAmount -= 1;
				updateLabels();
			}
		});
	}
	
	private void updateLabels(){
		playerAmountLabel.setText("" + playerAmount);
		AIAmountLabel.setText("" + AIPlayerAmount);
	}

}
