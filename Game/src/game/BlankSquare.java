package game;

/**
 * Represents a blank (neutral) square on the Save Our Planet game board.
 * <p>
 * Landing on a blank square has no mechanical effect on the player's CO2
 * resources or ownership. It serves as a rest point between active squares.
 * </p>
 */
public class BlankSquare extends Square{

    /**
     * Constructs a BlankSquare with the given name and board position.
     *
     * @param name the display name of this square (e.g. "Carbon Neutral Zone")
     * @param position the position of this square on the board
     */
    public BlankSquare(String name, int position) {
        super(name, position);
    }

    /**
     * Handles a player landing on this blank square.
     * <p>
     * No CO2 resources are gained or lost. A message is printed
     * to acknowledge the player's positive environmental impact.
     * </p>
     *
     * @param player the player who has landed on this square
     */
    @Override
    public void landedOn(Player player) {
        System.out.println(player.getName() + " landed on " + getName() + ". Nothing happens here, take a moment to enjoy the positive effect you are having on the environment!");
    }

    /**
     * Returns a description of this blank square indicating it is a resting spot
     * with no required action.
     *
     * @return a descriptive string for display purposes
     */
    @Override
    public String getDescription() {
        return getName() + " this is a resting spot. No action required.";
    }
}

