package io.github.teamfractal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.Roboticon;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.screens.RoboticonMarketScreen;

import java.util.ArrayList;
import java.util.HashMap;

public class RoboticonMarketActors extends Table {
	private static final Texture no_cust_texture;
	private static final Texture energy_texture;
	private static final Texture ore_texture;
    private static final Texture food_texture;
	private static final Texture no_robotic_texture;

	static {
		no_cust_texture = new Texture(Gdx.files.internal("roboticon_images/robot.png"));
		energy_texture = new Texture(Gdx.files.internal("roboticon_images/robot_energy.png"));
		ore_texture = new Texture(Gdx.files.internal("roboticon_images/robot_ore.png"));
		food_texture = new Texture(Gdx.files.internal("roboticon_images/robot_question.png")); //TODO: Create food roboticon texture and insert path here
		no_robotic_texture = new Texture(Gdx.files.internal("roboticon_images/no_roboticons.png"));
	}

    public Integer roboticonAmount = 0;
    public ArrayList<Roboticon> roboticons = new ArrayList<Roboticon>();
    private RoboticonQuest game;
    private RoboticonMarketScreen screen;
    public int currentlySelectedRoboticonPos;
    private Texture roboticonTexture;
    private Label topText;
    private Label playerStats;
    private Label marketStats;
    private Label roboticonID;
    private Image roboticonImage = new Image();
    private Label lblRoboticonAmount;
    private SelectBox<String> customisationDropDown;

	public RoboticonMarketActors(final RoboticonQuest game, RoboticonMarketScreen screen) {
		this.game = game;
		this.screen = screen;

		this.roboticonID = new Label("", game.skin);
		this.marketStats = new Label("", game.skin);

		widgetUpdate();

		// Buy Roboticon Text: Top Left
		final Label lblBuyRoboticon = new Label("Purchase Roboticons:", game.skin);

		//Roboticon text to go next to + and - buttons
		final Label lblRoboticons = new Label("Roboticons:", game.skin);

        this.lblRoboticonAmount = new Label(roboticonAmount.toString(), game.skin);

		// Button to increase number of roboticons bought
		final TextButton addRoboticonButton = new TextButton("+", game.skin);
		addRoboticonButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                addRoboticonFunction();
            }
		});

		// Button to decrease number of roboticons bought
		final TextButton subRoboticonButton = new TextButton("-", game.skin);
		subRoboticonButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                subRoboticonFunction();
            }
		});


        // Button to buy the selected amount of roboticons from the market
        final TextButton buyRoboticonsButton = new TextButton("Buy Roboticons", game.skin);
		buyRoboticonsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                buyRoboticonFunction();
            }
		});

		final Label marketStatistics = new Label("Market Statistics:", game.skin);


		// Current Roboticon Text: Top Right
		String playerRoboticonText = "Player " + (game.getPlayerInt() + 1) + "'s Roboticons:";
		final Label lblCurrentRoboticon = new Label(playerRoboticonText, game.skin);

		// Image widget which displays the roboticon in the player's inventory

		// Buttons to move backwards and forwards in the player's roboticon inventory
		final TextButton moveLeftRoboticonInventoryBtn = new TextButton("<", game.skin);
		moveLeftRoboticonInventoryBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                moveLeftRoboticonInventoryFunction();
            }
		});

		final TextButton moveRightRoboticonInventoryBtn = new TextButton(">", game.skin);
		moveRightRoboticonInventoryBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                moveRightRoboticonInventoryFunction();
            }
		});


		// Purchase Customisation Text: Bottom Right
		final Label lblPurchaseCustomisation = new Label("Customisation Type:", game.skin);

		// Drop down menu to select how to customise the selected roboticion
        this.customisationDropDown = new SelectBox<String>(game.skin);
        String[] customisations = {"Energy", "Ore", "Food"};
        //TODO this food implementation needs finishing
        this.customisationDropDown.setItems(customisations);


		// Button to buy the selected customisation and customise the selected roboticon
		final TextButton buyCustomisationButton = new TextButton("Buy Roboticon Customisation", game.skin);
		buyCustomisationButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                if (-1 == currentlySelectedRoboticonPos) {
                    // nothing selected.
                    return;
                }
                HashMap<String, ResourceType> converter = new HashMap<String, ResourceType>();
                converter.put("Energy", ResourceType.ENERGY);
                converter.put("Ore", ResourceType.ORE);
                converter.put("Food", ResourceType.FOOD);


                buyCustomisationFunction(converter.get(customisationDropDown.getSelected()), currentlySelectedRoboticonPos);
            }
		});

		final TextButton nextButton = new TextButton("Next ->", game.skin);
		nextButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.nextPhase();
			}
		});

		// Top Row Text
		add(lblBuyRoboticon).padTop(40).padLeft(68);
		add();
		add();
		add(lblCurrentRoboticon).padTop(40).padLeft(150);

		row();

		// Roboticon inc & dec buttons,
		add(lblRoboticons).padTop(40);
		add(subRoboticonButton).padTop(40).padLeft(-90);
		add(lblRoboticonAmount).padTop(40).padLeft(-80);
		add(addRoboticonButton).padTop(40).padLeft(-320);

		add();
		add();
		add();

		row();

		// Roboticon in inventory selection (moved to different row to preserve position of other buttons)
		add();
		add(buyRoboticonsButton).padLeft(-100).padBottom(160);
		add();
		add();

		add(moveLeftRoboticonInventoryBtn).padTop(40).padLeft(-350).padBottom(200);
		add(roboticonImage).padLeft(-150).padRight(75).padBottom(100).padTop(-50);
		add(moveRightRoboticonInventoryBtn).padTop(40).padLeft(-100).padBottom(200);

		row();

		add();
		add(marketStatistics).padLeft(-100).padTop(-170);
		add();
		add();

		add();
		add(roboticonID).padLeft(-235).padTop(-170);

        row();
        // Purchase customisation label
		add();
		add(marketStats).padLeft(-100).padTop(-100);
		add();
		add();

		add();
		add(lblPurchaseCustomisation).padLeft(-235).padTop(-100);

		row();

		// Customisation Drop Down Menu
		add();
		add();
		add();
		add();

		add();
		add(customisationDropDown).padLeft(-235).padTop(-50);

		row();

		// Buy Customisation Button
		add();
		add();
		add();
		add();

		add();
		add(buyCustomisationButton).padLeft(-235);

		row();

		add();
		add();
		add();
		add();

		add();
		add(nextButton).padTop(40);

	}

	public String padZero(int number, int length) {
		String s = "" + number;
		while (s.length() < length) {
			s = "0" + s;
		}
		return s;
	}

	public void setCurrentlySelectedRoboticon(int roboticonPos) {
		if (roboticonPos != -1) {

			ResourceType roboticonType = roboticons.get(roboticonPos).getCustomisation();

			switch (roboticonType) {
				case Unknown:
					roboticonTexture = no_cust_texture;
					break;
				case ENERGY:
					roboticonTexture = energy_texture;
					break;
				case ORE:
					roboticonTexture = ore_texture;
					break;
                case FOOD:
                    roboticonTexture = food_texture;
                    break;
				default:
					break;
			}

			int id = roboticons.get(roboticonPos).getID();
			this.roboticonID.setText("Roboticon Issue Number: " + padZero(id, 4));

		} else {
			roboticonTexture = no_robotic_texture;
			this.roboticonID.setText("Roboticon Issue Number: ####");
		}

		roboticonImage.setDrawable(new TextureRegionDrawable(new TextureRegion(roboticonTexture)));
	}

	public void widgetUpdate() {
		roboticons.clear();
		for (Roboticon r : game.getPlayer().getRoboticons()) {
			if (!r.isInstalled()) {
				roboticons.add(r);
			}
		}

		// Draws turn and phase info on screen
		if (this.topText != null) this.topText.remove();
		String phaseText = "Player " + (game.getPlayerInt() + 1) + "; Phase " + game.getPhase();
		this.topText = new Label(phaseText, game.skin);
		topText.setWidth(120);
		topText.setPosition(screen.getStage().getWidth() / 2 - 40, screen.getStage().getViewport().getWorldHeight() - 20);
		screen.getStage().addActor(topText);

		// Draws player stats on screen
		if (this.playerStats != null) this.playerStats.remove();
		String statText = "Ore: " + game.getPlayer().getOre() + " Energy: " + game.getPlayer().getEnergy() + " Food: "
				+ game.getPlayer().getFood() + " Money: " + game.getPlayer().getMoney();
		this.playerStats = new Label(statText, game.skin);
		playerStats.setWidth(250);
		playerStats.setPosition(0, screen.getStage().getViewport().getWorldHeight() - 20);
		screen.getStage().addActor(playerStats);

		if (roboticons.size() == 0) {
			currentlySelectedRoboticonPos = -1;
		} else if (currentlySelectedRoboticonPos == -1) {
			currentlySelectedRoboticonPos = 0;
		}

		setCurrentlySelectedRoboticon(currentlySelectedRoboticonPos);

        marketStats.setText("Market - Roboticons: " + game.market.getResource(ResourceType.ROBOTICON));

	}

    public void addRoboticonFunction() {
        roboticonAmount += 1;
        this.lblRoboticonAmount.setText(roboticonAmount.toString());
    }

    public void subRoboticonFunction() {
        if (roboticonAmount > 0) {
            roboticonAmount -= 1;
            lblRoboticonAmount.setText(roboticonAmount.toString());
        }
    }

    public void buyRoboticonFunction() {
        game.getPlayer().purchaseRoboticonsFromMarket(roboticonAmount, game.market);
        roboticonAmount = 0;
        lblRoboticonAmount.setText(roboticonAmount.toString());
        widgetUpdate();
    }

    public void moveLeftRoboticonInventoryFunction() {
        if (currentlySelectedRoboticonPos > 0) {
            currentlySelectedRoboticonPos--;
            setCurrentlySelectedRoboticon(currentlySelectedRoboticonPos);
        }
    }

    public void moveRightRoboticonInventoryFunction() {
        if (currentlySelectedRoboticonPos < roboticons.size() - 1) {
            currentlySelectedRoboticonPos++;
            setCurrentlySelectedRoboticon(currentlySelectedRoboticonPos);
        }
    }

    public void buyCustomisationFunction(ResourceType customisation, int pos) {
        game.getPlayer().purchaseCustomisationFromMarket(customisation, roboticons.get(pos), game.market);
        widgetUpdate();
    }


}
