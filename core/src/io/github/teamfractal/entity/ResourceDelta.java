package io.github.teamfractal.entity;

public class ResourceDelta {
    public ResourceDelta(){}
    public ResourceDelta(int food, int energy, int ore, int money) {
        this.food = food;
        this.energy = energy;
        this.ore = ore;
        this.money = money;
    }

    public int roboticon;
    public int food;
    public int energy;
    public int ore;
    public int money;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);

        /*
        if (roboticon > 0) {
            sb.append("\nRoboticon  x" + roboticon);
        }
        */

        if (food > 0) {
            sb.append("\nFood  x" + food);
        }

        if (energy > 0) {
            sb.append("\nEnergy  x" + energy);
        }

        if (ore > 0) {
            sb.append("\nOre  x" + ore);
        }

        if (money > 0) {
            sb.append("\nMoney  x" + money);
        }

        return sb.toString();
    }
}
