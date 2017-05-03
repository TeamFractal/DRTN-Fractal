
package io.github.teamfractal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.Player;
import io.github.teamfractal.screens.EndGameScreen;

import java.util.ArrayList;

/**
 * Created by Jack on 04/02/2017.
 */

public class EndGameActors extends Table {
    private RoboticonQuest game;
    private EndGameScreen screen;
    private Label winner;
    private Label title;
    private Label space;
    
    /**
     -     * Creates the labels that are to appear in the end game screen
     -     * @param game The current game
     -     * @param screen The screen the actors are to be placed on
     -     */
    public EndGameActors(final RoboticonQuest game, EndGameScreen screen){
        this.game = game;
        this.screen = screen;

        this.title = new Label("End of Game", game.skin);
        this.space = new Label("      ", game.skin);
        add(title);
        row();
        row();

        ArrayList<Integer> scores = new ArrayList<Integer>(4);
        ArrayList<Player> players = game.getPlayerList();
        ArrayList<Integer> maxPlayers = new ArrayList<Integer>(4);
        int i = 0;
        for (Player p : players) {
            i++;

            int score = p.calculateScore();
            scores.add(score);
            add(new Label("Player " + i + " Score: " + score, game.skin));
            row();
        }

        int maxScore = scores.get(0);
        i = 0;
        for (Integer score : scores) {
            i++;
            if (score > maxScore) {
                maxScore = score;
                maxPlayers.clear();
                maxPlayers.add(i);
            } else if (score == maxScore) {
                maxPlayers.add(i);
            }
        }

        // Player 1 wins! You are now the Vice-Chancellor of the Colony!
        StringBuilder sb = new StringBuilder();
        i = 0;
        sb.append("Player ");
        for (Integer p : maxPlayers) {
            i++;

            if (i != 1) {
                sb.append(", ");
            }

            sb.append(p);
        }
        sb.append(" wins! You are now the Vice-Chancellor of the Colony!");

        row();
        row();
        this.winner = new Label(sb.toString(), game.skin);
        winner.setAlignment(Align.center);
        add(winner);


    }
}
