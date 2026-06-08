package game;

import java.util.List;

/**
 * Represents a purchasable area square on the Save Our Planet game board.
 * <p>
 * Area squares belong to a {@link Field} and can be bought by players. Once a player
 * owns all squares within a field, they become eligible to invest in efficiency
 * upgrades on those squares, increasing the CO2 penalty charged to opponents who land
 * on them. Development progresses through minor levels up to a single major development.
 * </p>
 */
public class AreaSquare extends Square{

    private final String fieldName;
    private Field field;
    private Player owner;
    private int purchaseCost;
    private int baseCost;
    private int majorDevCost;
    private int developmentLevel;

    /**
     * Constructs an AreaSquare with the given properties.
     * The major development cost is taken from {@link Settings#MAJOR_DEVELOPMENT_COST}.
     * Development level starts at 0 (undeveloped).
     *
     * @param name the display name of this square (e.g. "Solar Power")
     * @param position the position on the game board
     * @param field the {@link Field} group this square belongs to
     * @param purchaseCost the CO2 cost for a player to take ownership of this square
     * @param baseCost the base CO2 penalty charged to opponents who land here
     */
    public AreaSquare(String name, int position, Field field, int purchaseCost, int baseCost) {
        super(name, position);
        this.fieldName = field.getName();
        this.field = field;
        this.purchaseCost = purchaseCost;
        this.baseCost = baseCost;
        this.majorDevCost = Settings.MAJOR_DEVELOPMENT_COST;
        this.developmentLevel = 0;
    }

    /**
     * Returns the name of the field this square belongs to.
     *
     * @return the field name string
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the field this square belongs to.
     *
     * @param field the new {@link Field} to assign
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Returns the field this square belongs to.
     *
     * @return the {@link Field} object
     */
    public Field getField() { return field; }

    /**
     * Returns the player who currently owns this square, or {@code null} if unowned.
     *
     * @return the owning {@link Player}, or {@code null}
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Assigns ownership of this square to the given player, if not already owned.
     * Prints a message if the square is already owned.
     *
     * @param owner the {@link Player} to assign as owner
     */
    public void setOwner(Player owner) {
        if(!isOwned()) {
            this.owner = owner;
        } else {
            System.out.println(owner.getName() + " owns this area already.");
        }
    }

    /**
     * Returns whether this square currently has an owner.
     *
     * @return {@code true} if owned, {@code false} otherwise
     */
    public boolean isOwned() {
        return getOwner() != null;
    }

    /**
     * Returns the CO2 cost for a player to purchase this square.
     *
     * @return the purchase cost
     */
    public int getPurchaseCost() {
        return purchaseCost;
    }

    /**
     * Calculates the CO2 penalty charged to an opponent who lands on this square.
     * <p>
     * For each minor development level, base * 5 * Development Level
     * Once the major development is reached, a flat bonus of 35 CO2 is applied to the base cost.
     * </p>
     *
     * @return the current landing penalty cost
     */
    public int getPenaltyCost() {
        if(developmentLevel < Settings.MAX_MINOR_DEVELOPMENTS) {
            return baseCost + 5*developmentLevel;
        } else {
            return baseCost + 35;
        }
    }

    /**
     * Calculates the CO2 cost of a minor efficiency upgrade for this square.
     * <p>
     * Computed as half the purchase cost, rounded to the nearest 5 — similar
     * to the Monopoly house pricing convention.
     * </p>
     *
     * @return the minor development upgrade cost
     */
    public int getDevelopmentCost() {
        return Math.round(((float) this.getPurchaseCost() /2) / (float) (5)) * 5;
    }

    /**
     * Returns the CO2 cost of the major efficiency development for this square.
     *
     * @return the major development cost
     */
    public int getMajorDevCost() {
        return baseCost +majorDevCost;
    }

    /**
     * Returns the current efficiency (development) level of this square.
     * Ranges from 0 (undeveloped) to {@link Settings#MAX_EFFICIENCY_LEVEL}.
     *
     * @return the current development level
     */
    public int getEfficiencyLevel() {
        return developmentLevel;
    }

    /**
     * Sets the efficiency (development) level of this square directly.
     *
     * @param developmentLevel the new development level to assign
     */
    public void setEfficiencyLevel(int developmentLevel) {
        this.developmentLevel = developmentLevel;
    }

    /**
     * Returns whether this square has had the major efficiency development applied.
     *
     * @return {@code true} if development level equals {@link Settings#MAX_EFFICIENCY_LEVEL}
     */
    public boolean hasMajorEfficiencyDevelopment() {
        return developmentLevel == Settings.MAX_EFFICIENCY_LEVEL;
    }

    /**
     * Returns whether this square has reached its maximum efficiency level.
     *
     * @return {@code true} if no further development is possible
     */
    public boolean isFullyDeveloped() {
        return developmentLevel >= Settings.MAX_EFFICIENCY_LEVEL;
    }

    /**
     * Attempts to purchase this square for the given player.
     * <p>
     * Deducts the purchase cost from the player's resources if they can afford it
     * and immediately assigns ownership. Returns {@code false} and prints a message
     * if the player cannot afford it.
     * </p>
     *
     * @param player the {@link Player} attempting to purchase this square
     * @return {@code true} if the purchase was successful, {@code false} otherwise
     */
    public boolean purchase(Player player) {
        if (isOwned()) {
            System.out.println(getName() + " is already owned by " + owner.getName());
            return false;
        }
        if (player.canAfford(this.purchaseCost)) {
            player.addResources(this.purchaseCost);
            this.owner = player;
            System.out.println(player.getName() + " has purchased " + getName());
            return true;
        } else {
            System.out.println(player.getName() + " cannot afford this area");
            return false;
        }
    }

    /**
     * Determines whether this square is eligible to be developed by its owner.
     * <p>
     * A square can only be developed if the owner also owns every other square
     * within the same field.
     * </p>
     *
     * @return {@code true} if this square can be developed, {@code false} otherwise
     */
    public boolean canDevelop() {
        // A square can only be developed if the owner owns all squares in the same field
        if (owner == null) {
            return false;
        }

        List<AreaSquare> fieldAreas = field.getAreas();
        for (AreaSquare area : fieldAreas) {
            if (area.getOwner() == null || !area.getOwner().equals(owner)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles a player landing on this area square.
     * <p>
     * Three outcomes are possible:
     * <ul>
     *   <li>Unowned — the square is available for purchase (prompt handled externally).</li>
     *   <li>Owned by the landing player — no CO2 exchange occurs.</li>
     *   <li>Owned by another player — the landing player gains CO2 (penalty), and the
     *       owner loses the same amount, reflecting environmental accountability.</li>
     * </ul>
     * </p>
     *
     * @param player the player who has landed on this square
     */
    @Override
    public void landedOn(Player player) {
        System.out.println(player.getName() + " landed on " + getName() + " [" + fieldName + "].");

        if (!isOwned()) {
            // No owner, offer to purchase
            System.out.println("No one is in charge of this area.");

        } else if (owner.equals(player)) {
            // Player owns it nothing to pay
            System.out.println("You are in charge of this area.");

        } else {
            // Someone else owns it pay penalty
            int penalty = getPenaltyCost();
            System.out.println(owner.getName() + " is in charge of this area.");
            System.out.println("You gain " + penalty + " resources.");

            int oldBalance = player.getResources();
            player.addResources(penalty);
            owner.deductResources(penalty);

            System.out.println(player.getName() + "'s balance: " + oldBalance + " -> " + player.getResources());

            owner.isBankrupt();
        }
    }

    /**
     * Returns a formatted description of this square's current state, including
     * field, owner, purchase cost, penalty cost, and development level.
     *
     * @return a descriptive string for display purposes
     */
    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" [").append(fieldName).append("]");
        sb.append(" | In charge: ").append(isOwned() ? owner.getName() : "No one");
        sb.append(" | Purchase cost: ").append(purchaseCost);
        sb.append(" | Penalty cost: ").append(getPenaltyCost());
        sb.append(" | Development: ").append(developmentLevel).append("/").append(Settings.MAX_EFFICIENCY_LEVEL);

        if (hasMajorEfficiencyDevelopment()) {
            sb.append(" [MAJOR]");
        }

        return sb.toString();
    }
}
