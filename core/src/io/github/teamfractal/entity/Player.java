package io.github.teamfractal.entity;

import com.badlogic.gdx.utils.Array;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.animation.AnimationAddResources;
import io.github.teamfractal.animation.IAnimation;
import io.github.teamfractal.animation.IAnimationFinish;
import io.github.teamfractal.entity.enums.PurchaseStatus;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.NotCommonResourceException;
import io.github.teamfractal.exception.NotEnoughResourceException;

import java.util.ArrayList;
import java.util.Random;

public class Player {
	public RoboticonQuest game;
	Array<Roboticon> roboticonList;
	private ArrayList<LandPlot> landList = new ArrayList<LandPlot>();
	//<editor-fold desc="Resource getter and setter">
	private int money = 100;
	private int ore = 0;
	private int energy = 0;
	private int food = 0;

	public Player(RoboticonQuest game){
		this.game = game;
		this.roboticonList = new Array<Roboticon>();


	}

	public int getMoney() {
		return money;
	}

	/**
	 * Set the amount of money player has
	 * @param money                      The amount of new money.
	 * @throws IllegalArgumentException  If the new money if negative, this exception will be thrown.
	 */
	synchronized void setMoney(int money) throws IllegalArgumentException {
		if (money < 0) {
			throw new IllegalArgumentException("Error: Money can't be negative.");
		}

		this.money = money;
	}

	public int getOre() {
		return ore;
	}
	
	/**
	 * Set the amount of ore player has
	 * @param amount                     The new amount for ore.
	 * @throws IllegalArgumentException  If the new ore amount if negative, this exception will be thrown.
	 */
	synchronized void setOre(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Error: Ore can't be negative.");
		}

		this.ore = amount;
	}

	public int getEnergy() {
		return energy;
	}

	/**
	 * Set the amount of energy player has
	 * @param amount                     The new amount for energy.
	 * @throws IllegalArgumentException  If the new energy amount if negative, this exception will be thrown.
	 */

	synchronized void setEnergy(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Error: Energy can't be negative.");
		}

		this.energy = amount;
	}

	public int getFood() {
		return food;
	}

	/**
	 * Set the amount of food player has
	 * @param amount                     The new amount for food.
	 * @throws IllegalArgumentException  If the new food amount if negative, this exception will be thrown.
	 */

	synchronized void setFood(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Error: Food can't be negative.");
		}

		this.food = amount;
	}

	/**
	 * Set the resource amount current player have.
	 * @param resource  The {@link ResourceType}
	 * @param amount    The new amount.
	 */
	void setResource(ResourceType resource, int amount) {
		switch (resource) {
			case ENERGY:
				setEnergy(amount);
				break;

			case ORE:
				setOre(amount);
				break;

			case FOOD:
				setFood(amount);
				break;

			default:
				throw new NotCommonResourceException(resource);
		}
	}

	/**
	 * Get the resource amount current player have.
	 * @param type   The {@link ResourceType}
	 * @return       The amount of specified resource.
	 */
	public int getResource(ResourceType type) {
		switch (type) {
			case ENERGY:
				return getEnergy();

			case ORE:
				return getOre();

			case FOOD:
				return getFood();


			default:
				throw new NotCommonResourceException(type);
		}
	}

	/**
	 * Purchase roboticon from the market.
	 * @param amount number of roboticons requested
	 * @param market the market being purchased from
	 * @return returns purchase status
	 */
	public PurchaseStatus purchaseRoboticonsFromMarket(int amount, Market market) {
		Random random = new Random();
		
		
		if (!market.hasEnoughResources(ResourceType.ROBOTICON, amount)) {
			return PurchaseStatus.FailMarketNotEnoughResource;
		}

		int cost = amount * market.getSellPrice(ResourceType.ROBOTICON);
		int money = getMoney();
		if (cost > money) {
			return PurchaseStatus.FailPlayerNotEnoughMoney;
		}
		
		market.sellResource(ResourceType.ROBOTICON, amount);
		setMoney(money - cost);
		for (int roboticon = 0; roboticon < amount; roboticon++) {
			roboticonList.add(new Roboticon(random.nextInt(10000)));
		}
		
		return PurchaseStatus.Success;
	}

	/**
	 * Purchase roboticon customisation from the market.
	 * @param resource    The resource type.
	 * @param roboticon   The roboticon to be customised.
	 * @param market      The market.
	 * @return            Purchase status.
	 */
	public PurchaseStatus purchaseCustomisationFromMarket(ResourceType resource,
	                                                      Roboticon roboticon, Market market) {
		
		if (!market.hasEnoughResources(ResourceType.CUSTOMISATION, 1)) {
			return PurchaseStatus.FailMarketNotEnoughResource;
		}

		int cost = market.getSellPrice(ResourceType.CUSTOMISATION);
		int money = getMoney();
		if (cost > money) {
			return PurchaseStatus.FailPlayerNotEnoughMoney;
		}
		
		market.sellResource(ResourceType.CUSTOMISATION, 1);
		setMoney(money - cost);
		customiseRoboticon(roboticon, resource);
		
		return PurchaseStatus.Success;
	}
	
	//</editor-fold>

	/**
	 * Action for player to purchase resources from the market.
	 *
	 * @param amount     Amount of resources to purchase.
	 * @param market     The market instance.
	 * @param resource   The resource type.
	 * @return           If the purchase was success or not.
	 */
	public PurchaseStatus purchaseResourceFromMarket(int amount, Market market, ResourceType resource) {
		if (!market.hasEnoughResources(resource, amount)) {
			return PurchaseStatus.FailMarketNotEnoughResource;
		}

		int cost = amount * market.getSellPrice(resource);
		int money = getMoney();
		if (cost > money) {
			return PurchaseStatus.FailPlayerNotEnoughMoney;
		}

		market.sellResource(resource, amount);
		setMoney(money - cost);
		setResource(resource, getResource(resource) + amount);
		return PurchaseStatus.Success;
	}

	/**
	 * Action for player to sell resources to the market.
	 *
	 * @param amount    Amount of resources to sell.
	 * @param market    The market instance.
	 * @param resource  The resource type.
	 */
	public void sellResourceToMarket(int amount, Market market, ResourceType resource) {
		int resourcePrice = market.getBuyPrice(resource);

		if (getResource(resource) >= amount) {
			market.buyResource(resource, amount);
			setResource(resource, getResource(resource) - amount);
			setMoney(getMoney() + amount * resourcePrice);
		} else {
			throw new NotEnoughResourceException("Player.sellResourceToMarket", resource, amount, getResource(resource));
		}
	}

	/**
	 * Check if the player have enough money for the {@link LandPlot}.
	 * @param plot           The landplot to purchase
	 * @return  true if the player have enough money for that plot.
	 */
	public synchronized boolean haveEnoughMoney(LandPlot plot) {
		return getMoney() >= 10;
	}

	/**
	 * Player add a landplot to their inventory for gold
	 * @param plot           The landplot to purchase
	 */
	public synchronized boolean purchaseLandPlot(LandPlot plot){
		if (plot.hasOwner() || !haveEnoughMoney(plot)) {
			return false;
		}

		landList.add(plot);
		this.setMoney(this.getMoney() - 10);
		plot.setOwner(this);
		game.landPurchasedThisTurn();
		return true;
	}
	/**
	 * Get a landplot to produce resources
	 */
	public void produceResources(){
		for (LandPlot plot : landList) {
			energy += plot.produceResource(ResourceType.ENERGY);
			ore += plot.produceResource(ResourceType.ORE);
            food += plot.produceResource(ResourceType.FOOD);
		}
	}
	/**
	 * Apply roboticon customisation
	 * @param roboticon  The roboticon to be customised
	 * @param type       The roboticon customisation type.
	 * @return           The roboticon
	 */
	Roboticon customiseRoboticon(Roboticon roboticon, ResourceType type) {
		roboticon.setCustomisation(type);
		return roboticon;
	}

	/**
	 * Add landplot to current user.
	 *
	 * @param landPlot  LandPlot to be bind to the user.
	 *                  <code>LandPlot.setOwner(this_user)</code> first.
	 */
	void addLandPlot(LandPlot landPlot) {
		if (landPlot != null && !landList.contains(landPlot) && landPlot.getOwner() == this) {
			landList.add(landPlot);
		}
	}

	/**
	 * Remove the LandPlot from the user.
	 *
	 * @param landPlot  LandPlot to be removed from the user.
	 *                  <code>this_user</code> must be the current owner first.
	 */
	void removeLandPlot(LandPlot landPlot) {
		if (landPlot != null && landList.contains(landPlot) && landPlot.getOwner() == this) {
			landList.add(landPlot);
		}
	}

	/**
	 * Get a string list of roboticons available for the player.
	 * Mainly for the dropdown selection.
	 *
	 * @return  The string list of roboticons.
	 */
	public Array<String> getRoboticonAmountList() {
		int ore = 0;
		int energy = 0;
		int food = 0;
		int uncustomised = 0;
		Array<String> roboticonAmountList = new Array<String>();

		/* TODO: add food */
        for (Roboticon r : roboticonList) {
            if (!r.isInstalled()) {
				switch (r.getCustomisation()) {
					case ORE:
						ore += 1;
						break;
					case ENERGY:
						energy += 1;
						break;
					case FOOD:
						food += 1;
						break;
					default:
						uncustomised += 1;
						break;
				}
			}
		}

		roboticonAmountList.add("Ore Specific x "    + ore);
		roboticonAmountList.add("Energy Specific x " + energy);
		roboticonAmountList.add("Food Specific x " + food);
		roboticonAmountList.add("Uncustomised x "    + uncustomised);
		return roboticonAmountList;
	}
	public Array<Roboticon> getRoboticons(){
		return this.roboticonList;
	}

	/**
	 * Generate resources produced from each LandPlot
	 */
	public void generateResources() {
		int energy = 0;
		int food = 0;
		int ore = 0;

		for (LandPlot land : landList) {
			energy += land.produceResource(ResourceType.ENERGY);
			ore += land.produceResource(ResourceType.ORE);
            food += land.produceResource(ResourceType.ORE);
		}

		setEnergy(getEnergy() + energy);
		setFood(getFood() + food);
		setOre(getOre() + ore);

		IAnimation animation = new AnimationAddResources(this, energy, food, ore);
		animation.setAnimationFinish(new IAnimationFinish() {
			@Override
			public void OnAnimationFinish() {
				if (game.getPlayer() == Player.this){
					game.nextPhase();
				}
			}
		});
		game.gameScreen.addAnimation(animation);
	}


    /**
     * Returns the score of the player which is a combination of ore, energy and food.
     * @return The score of the player.
     */
	public int calculateScore(){
        return ore + energy + food;
    }

    /**
     * Method to be overloaded by AI inheritance
     *
     * @param phase phase in play
     */
    public void takeTurn(int phase) {
        //Overload in AIPlayer
    }
}