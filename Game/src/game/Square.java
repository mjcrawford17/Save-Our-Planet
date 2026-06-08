package game;

/**
 * Abstract base class representing a square on the Save Our Planet game board.
 * <p>
 * All square types (area, blank, start) extend this class and must implement
 * {@link #landedOn(Player)} and {@link #getDescription()} to define their
 * specific behaviour when a player lands on them.
 * </p>
 */
public abstract class Square {

    private String name;
    private int position;

    /**
     * Constructs a Square with the given name and board position.
     * @param name the display name of the square
     * @param position the position of the square on the board
     */
    public Square(String name, int position) {
        this.name = name;
        this.position = position;
    }

    /**
     * Returns the display name of this square.
     *
     * @return the square's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of this square.
     *
     * @param name the new name to assign
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the board position of this square.
     *
     * @return the square's position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the board position of this square.
     *
     * @param position the new position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Defines the behaviour that occurs when a player lands on this square.
     *
     * @param player the player who has landed on this square
     */
    public abstract void landedOn(Player player);

    /**
     * Returns a human-readable description of this square, including
     * relevant details such as ownership, cost, or status.
     *
     * @return a descriptive string for display purposes
     */
    public abstract String getDescription();

    /**
     * Returns a string representation of this square, including its name and position.
     *
     * @return formatted string in the form "Name (Position N)"
     */
    @Override
    public String toString() {
        return name + " (Position " + position + ")";
    }
}
