package game;

/**
 * Represents the start square (Sustainability Hub) on the Save Our Planet game board.
 * <p>
 * Players receive a CO2 deduction both when they land on this square and when
 * they pass over it during normal movement. This models the environmental benefit
 * of completing a full loop of the board.
 * </p>
 */
public class StartSquare extends Square{

    private int resourceGrant;

    /**
     * Constructs a StartSquare with the given name and board position.
     * The resource deduction amount is taken from {@link Settings#PASS_START_MINUS_RESOURCES}.
     *
     * @param name the display name of the start square (e.g. "Sustainability Hub")
     * @param position the position of this square on the board (typically 0)
     */
    public StartSquare(String name, int position) {
        super(name, position);
        this.resourceGrant = Settings.PASS_START_MINUS_RESOURCES;
    }

    /**
     * Returns the amount of CO2 resources deducted when a player interacts with this square.
     *
     * @return the resource deduction amount
     */
    public int getResourceGrant() {
        return resourceGrant;
    }

    /**
     * Handles a player landing directly on this start square.
     * <p>
     * Deducts {@link #resourceGrant} CO2 resources from the player, the same
     * amount as passing over the square.
     * </p>
     *
     * @param player the player who has landed on this square
     */
    @Override
    public void landedOn(Player player) {
        // Resources are typically granted when passing, not just landing
        // but landing also triggers the grant
        System.out.println(player.getName() + " landed on " + getName() + " and removes " + resourceGrant + " resources.");
        player.deductResources(resourceGrant);
    }

    /**
     * Handles a player passing over this start square without landing on it.
     * <p>
     * Deducts {@link #resourceGrant} CO2 resources from the player, rewarding
     * them for completing another loop of the board.
     * </p>
     *
     * @param player the player who has passed over this square
     */
    public void passedOver(Player player) {
        System.out.println(player.getName() + " passed " + getName() + " and removes " + resourceGrant + " resources.");
        player.deductResources(resourceGrant);
    }

    /**
     * Returns a description of this start square indicating the resource deduction applied.
     *
     * @return a descriptive string for display purposes
     */
    @Override
    public String getDescription() {
        return getName() + " deducts " + resourceGrant + " resources";
    }
}
