

package io.github.teamfractal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import io.github.teamfractal.animation.AnimationCustomHeader;
import io.github.teamfractal.animation.AnimationPhaseTimeout;
import io.github.teamfractal.animation.chancellor.WildChancellorAppear;
import io.github.teamfractal.animation.IAnimationFinish;
import io.github.teamfractal.entity.*;
import io.github.teamfractal.screens.*;
import io.github.teamfractal.util.*;

import java.util.ArrayList;
import java.util.Random;
/**
 * @author DRTN
 * Team Website with download:
 * https://misterseph.github.io/DuckRelatedFractalProject/
 * <p>
 * This Class contains either modifications or is entirely new in Assessment 3
 * <p>
 * If you are in any doubt a complete changelog can be found here:
 * https://github.com/NotKieran/DRTN-Fractal/compare/Fractal_Initial...development
 * <p>
 * And a more concise report can be found in our Change3 document.
 **/

/**
 * This is the main game start up class.
 * It will set up all the necessary classes.
 */
public class RoboticonQuest extends Game {
	private static Random rnd = new Random();
    private static RoboticonQuest _instance;
	public Skin skin;
    public PlotManager plotManager;

    public TTFont headerFontRegular;
    public TTFont headerFontLight;
    public TTFont smallFontRegular;
    public TTFont smallFontLight;
	public TTFont tinyFontRegular;
	public TTFont tinyFontLight;

    public GameScreen gameScreen;
    public Market market;
    public RoboticonMarketScreen roboticonMarket;
    public GenerationOverlay genOverlay;
    public ResourceMarketScreen resourceMarket;

	private int turnNumber = 1;
	private SpriteBatch batch;
	private MainMenuScreen mainMenuScreen;

    private ArrayList<Player> playerList;
    private int phase;
	private int landBoughtThisTurn;
	private float effectChance;
	private int currentPlayerIndex;

	private AnimationCustomHeader playerHeader;
	private AnimationCustomHeader phase1description;
	private AnimationCustomHeader phase2description;
	private AnimationCustomHeader phase3description;
	private AnimationCustomHeader phase4description;
	private AnimationCustomHeader phase5description;

	private PlotEffectSource plotEffectSource;
	private PlayerEffectSource playerEffectSource;
	public Skin skin2;
	public Skin skin3;
	private TTFont titleFontRegular;
	private TTFont titleFontLight;

	public RoboticonQuest() {
		_instance = this;
		reset(2,0);
	}

	public static RoboticonQuest getInstance() {
		return _instance;
	}

	/**
	 * Getter for the index of the current Player
	 * @param player The player that the index is being retrieved for
	 * @return The index of the specified player
	 */
	public int getPlayerIndex (Player player) {

		return playerList.indexOf(player);
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		Fonts fonts = new Fonts();
        fonts.montserratRegular.setSize(32);
        fonts.montserratLight.setSize(32);
        titleFontRegular = fonts.montserratRegular;
        titleFontLight = fonts.montserratLight;

        fonts = new Fonts();
        fonts.montserratRegular.setSize(24);
        fonts.montserratLight.setSize(24);
        headerFontRegular = fonts.montserratRegular;
        headerFontLight = fonts.montserratLight;

        fonts = new Fonts();
        fonts.montserratRegular.setSize(16);
        fonts.montserratLight.setSize(16);
        smallFontRegular = fonts.montserratRegular;
        smallFontLight = fonts.montserratLight;

		fonts = new Fonts();
		fonts.montserratRegular.setSize(12);
		fonts.montserratLight.setSize(12);
		tinyFontRegular = fonts.montserratRegular;
		tinyFontLight = fonts.montserratLight;
        //Import TrueType fonts for use in drawing textual elements

		setupSkin();

		// Setup other screens.
		mainMenuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);
        roboticonMarket = new RoboticonMarketScreen(this);
        genOverlay = new GenerationOverlay(Color.GRAY, Color.WHITE, 3);
        resourceMarket = new ResourceMarketScreen(this);

		//Setup tile and player effects for later application
		setupEffects();

		//Setup header animations to be played out at certain stages in the game
		setupAnimations();

		setScreen(mainMenuScreen);
	}
	/**
	 * Getter for the batch
	 * @return The batch of the game
	 */
	public Batch getBatch() {
		return batch;
	}

	/**
	 * Setup the default skin for GUI components.
	 */
	private void setupSkin() {
		skin = new Skin();
		skin.add("default", smallFontLight.font());
		skin.addRegions(new TextureAtlas(Gdx.files.internal("skin/skin.atlas")));
		skin.load(Gdx.files.internal("skin/skin.json"));
		
		skin2 = new Skin();
		skin2.add("default", headerFontLight.font());
		skin2.addRegions(new TextureAtlas(Gdx.files.internal("skin/skin.atlas")));
		skin2.load(Gdx.files.internal("skin/skin2.json"));
		
		skin3 = new Skin();
		skin3.add("default",titleFontRegular.font());
		skin3.addRegions(new TextureAtlas(Gdx.files.internal("skin/skin.atlas")));
		skin3.load(Gdx.files.internal("skin/skin2.json"));
		
	}

	/**
	 * Clean up
	 */
	@Override
	public void dispose () {
		mainMenuScreen.dispose();
		gameScreen.dispose();
		batch.dispose();
	}
	/**
	 * Getter for the current phase
	 * @return The current phase of the game
	 */
	public int getPhase(){
		return this.phase;
	}
	/**
	 * Setter for the current phase
	 * @param phase The phase that the current phase is to be set to
	 */
	public void setPhase(int phase) {
		this.phase = phase;
		implementPhase();
	}
	/**
	 * Resets the statistics of all the game's entities
	 * @param AI A boolean describing whether an AI player is playing or not
	 */
	public void reset(int Human, int AI) {
        this.phase = 0;
        plotManager = new PlotManager();
        
        this.playerList = new ArrayList<Player>();
        for (int i = 0; i < Human; i++){
        	this.playerList.add(new Player(this));
        }
        for (int i = 0; i < AI; i++){
        	this.playerList.add(new AIPlayer(this));
        }
        this.currentPlayerIndex = 0;
        this.market = new Market();

    }
	/**
	 * Implements the functionality of the current phase
	 */
    private void implementPhase() {
		gameScreen.clearAnimation();
        System.out.println("RoboticonQuest::nextPhase -> newPhaseState: " + phase);

		switch (phase) {
			// Phase 2: Purchase Roboticon
			case 2:
				phase1description.cancelAnimation();
				gameScreen.addAnimation(phase2description);

                Gdx.input.setInputProcessor(roboticonMarket);

                AnimationPhaseTimeout timeoutAnimation = new AnimationPhaseTimeout(getPlayer(), this, phase, 30);
				gameScreen.addAnimation(timeoutAnimation);

				roboticonMarket.actors().widgetUpdate();

				gameScreen.getActors().setNextButtonVisibility(false);
				this.getPlayer().takeTurn(2);
                break;


			// Phase 3: Roboticon Customisation
			case 3:
				phase2description.cancelAnimation();
				gameScreen.addAnimation(phase3description);
                Gdx.input.setInputProcessor(gameScreen.getStage());

				timeoutAnimation = new AnimationPhaseTimeout(getPlayer(), this, phase, 30);
				gameScreen.addAnimation(timeoutAnimation);
				timeoutAnimation.setAnimationFinish(new IAnimationFinish() {
					@Override
					public void OnAnimationFinish() {
						gameScreen.getActors().hideInstallRoboticon();
					}
				});

				gameScreen.getActors().switchNextButton();
				this.getPlayer().takeTurn(3);
                break;


			// Phase 4: Generate resources for player
			case 4:
				phase3description.cancelAnimation();
				gameScreen.addAnimation(phase4description);
                Gdx.input.setInputProcessor(genOverlay);

                this.getPlayer().generateResources();
				this.market.generateRoboticon();
				this.roboticonMarket.actors().refreshRoboticonShop();
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                    	if (phase == 4)
							nextPhase();
                    }
                }, 3);
                timer.start();

				gameScreen.getActors().switchNextButton();
                break;

			// Phase 5: Open the market

			case 5:
				phase4description.cancelAnimation();
				gameScreen.addAnimation(phase5description);
			    Gdx.input.setInputProcessor(resourceMarket);

			    resourceMarket.actors().widgetUpdate();
			    resourceMarket.gambleStatisticsReset();

				gameScreen.getActors().setNextButtonVisibility(false);
				this.getPlayer().takeTurn(5);
				break;

			// End phase - Clean up and move to next player.
			case 6:
                if (captureChancellor()) {
                	break;
                }

			case 7:
				cleanUpForNextTurn();
				// No "break;" here!
				// Let the game to do phase 1 preparation.

			// Phase 1: Enable of purchase LandPlot
			case 1:
                Gdx.input.setInputProcessor(gameScreen.getStage());
				phase5description.cancelAnimation();
				gameScreen.addAnimation(phase1description);

				setScreen(gameScreen);
				landBoughtThisTurn = 0;

				if (turnNumber > 2) {
					clearEffects();
					setEffects();
				}
				//Only consider imposing effects once each player has claimed at least 1 tile

                System.out.println("Player: " + this.currentPlayerIndex + " Turn: " + this.getTurnNumber());

				if (getPlayer().getMoney() < 10) {
					gameScreen.getActors().setNextButtonVisibility(true);
				} else {
					gameScreen.getActors().setNextButtonVisibility(false);
				}
        		this.getPlayer().takeTurn(1);
				break;
		}

	    if (gameScreen != null) {
		    if (!(getPlayer() instanceof AIPlayer)) {
			    if (phase == 4) {
				    playerHeader.setLength(3);
			    } else {
				    playerHeader.setLength(5);
			    }
			    gameScreen.addAnimation(playerHeader);
		    }

		    gameScreen.getActors().textUpdate();
	    }
	}

	public void fixInputFocus () {
		switch (phase) {
			case 1:
				Gdx.input.setInputProcessor(gameScreen.getStage());
				break;

			case 2:
				Gdx.input.setInputProcessor(roboticonMarket);
				break;

			case 3:
				Gdx.input.setInputProcessor(gameScreen.getStage());
				break;

			case 4:
				Gdx.input.setInputProcessor(genOverlay);
				break;

			case 5:
				Gdx.input.setInputProcessor(resourceMarket);
				break;

			default:
				Gdx.input.setInputProcessor(gameScreen.getStage());
				break;
		}
	}

	private void cleanUpForNextTurn() {
		phase = 1;

		if (checkGameEnded()) {
			setScreen(new EndGameScreen(this));
			return ;
		}

		this.turnNumber += 1;
		this.nextPlayer();
	}

	private boolean captureChancellor() {
    	boolean b = rnd.nextBoolean();

    	if (b || true) {
		    gameScreen.addAnimation(new WildChancellorAppear(this, skin));
	    }

		return b;
	}

	/**
	 * Advances the current phase
	 */
	public void nextPhase() {
        if ((phase == 1) && (landBoughtThisTurn == 0) && (this.getPlayer().getMoney() >= 10)) {
            return;
        }
        phase += 1;
        implementPhase();
	}

	/**
	 * Event callback on player bought a {@link io.github.teamfractal.entity.LandPlot}
	 */
	public void landPurchasedThisTurn() {
		landBoughtThisTurn ++;
	}
	/**
	 * Getter for landBoughtThisTurn
	 -	 * @return Returns true if land hasn't been purchased this turn, false otherwise
	 -	 */
	public boolean canPurchaseLandThisTurn () {
		return (landBoughtThisTurn < 1 && getPlayer().getMoney() >= 10);
	}
	/**
	 * Returns a string describing the current phase
	 * @return A string with the description of the current phase
	 */
	public String getPhaseString () {
		int phase = getPhase();

		switch(phase){
			case 1:
				return "Buy Land Plot";

			case 2:
				return "Purchase Roboticons";

			case 3:
				return "Install Roboticons";

			case 4:
				return "Resource Generation";

			case 5:
				return "Resource Auction";

			default:
				return "Unknown phase";
		}

	}
	/**
	 * Getter for the current player
	 * @return The current player
	 */
	public Player getPlayer(){
        return this.playerList.get(this.currentPlayerIndex);
    }
	/**
	 * Getter for the index of the current player
	 * @return The index of the current player
	 */
    public int getPlayerInt() {
        return this.currentPlayerIndex;
    }

    public String getPlayerName() {
	    return "PLAYER " + (currentPlayerIndex + 1);
    }

	/**
	 * Changes the current player
	 */
	private void nextPlayer() {
		if(this.currentPlayerIndex == this.playerList.size() -1){
			this.currentPlayerIndex = 0;
		}
		else this.currentPlayerIndex ++;

		playerHeader.setText("PLAYER " + (currentPlayerIndex + 1));
		gameScreen.addAnimation(playerHeader);
    }

	/**
	 * Creates and initialises all of the effects
	 */
	private void setupEffects() {
		//Initialise the fractional chance of any given effect being applied at the start of a round
		effectChance = (float) 0.02;

		plotEffectSource = new PlotEffectSource(this);
		playerEffectSource = new PlayerEffectSource(this);

		for (PlotEffect PTE : plotEffectSource) {
			PTE.constructOverlay(gameScreen);
		}

		for (PlayerEffect PLE : playerEffectSource) {
			PLE.constructOverlay(gameScreen);
		}
	}
	/**
	 * Randomly applies the effects
	 */
	private void setEffects() {
		Random RNGesus = new Random();

		for (PlotEffect PTE : plotEffectSource) {
			if (RNGesus.nextFloat() <= effectChance) {
				PTE.executeRunnable();

				if (!(getPlayer() instanceof AIPlayer)) {
					gameScreen.addOverlay(PTE.overlay());
				}
			}
		}

		for (PlayerEffect PLE : playerEffectSource) {
			if (RNGesus.nextFloat() <= effectChance) {
				PLE.executeRunnable();

				if (!(getPlayer() instanceof AIPlayer)) {
					gameScreen.addOverlay(PLE.overlay());
				}
			}
		}

		gameScreen.getActors().textUpdate();
	}
	/**
	 * Clears all imposed PlotEffects
	 */
	private void clearEffects() {
		for (PlotEffect PE : plotEffectSource) {
			PE.revertAll();
		}
	}

	/**
	 * Prepares header animations to indicate when certain phases of the game have been reached
	 */
	private void setupAnimations() {
		playerHeader = new AnimationCustomHeader("PLAYER 1", headerFontRegular.font(), 5);
		phase1description = new AnimationCustomHeader("\nPHASE 1: Claim a Tile", headerFontLight.font(), 5);
		phase2description = new AnimationCustomHeader("\nPHASE 2: Buy and Upgrade Roboticons", headerFontLight.font(), 5);
		phase3description = new AnimationCustomHeader("\nPHASE 3: Deploy Roboticons", headerFontLight.font(), 5);
		phase4description = new AnimationCustomHeader("\nPHASE 4: Generate Resources", headerFontLight.font(), 3);
		phase5description = new AnimationCustomHeader("\nPHASE 5: Buy and Sell Resources", headerFontLight.font(), 5);

		gameScreen.addAnimation(playerHeader);
	}

	/**
	 * Checks whether the game has ended based on whether all of the tiles have been claimed
	 * @return Returns true if ended, false if not
	 */
    private boolean checkGameEnded() {
        boolean ended = true;
		LandPlot[][] plots = plotManager.getLandPlots();
        for (LandPlot[] plot : plots) {
            for (LandPlot aPlot : plot) {
                if (aPlot == null) {
					ended = false;
                }
            }
        }
        return ended;
	}

	/**
	 * Returns the winner of the game, based on which player has the highest score
     * @return String returning the winning player
     */

	public String getWinner(){
        String winner;
        if(playerList.get(0).calculateScore() > playerList.get(1).calculateScore()) {
			winner = "Player 1 wins! You are now the Vice-Chancellor of the Colony!";
		}
		else {
			if (playerList.get(1).calculateScore() > playerList.get(0).calculateScore()) {
				winner = "Player 2 wins! You are now the Vice-Chancellor of the Colony!";
			} else {
				winner = "It's a draw. I'm afraid neither of you will become Vice-Chancellor of the Colony.";
			}
		}
		return winner;
	}

	/**
	 * Getter for the players of the game.
	 * @return The array containing the two players
	 */
	public ArrayList<Player> getPlayerList(){
		return this.playerList;
	}

    public int getTurnNumber() {
        return (int) Math.ceil((double) turnNumber / 2);
    }
}


