package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Save Our Planet game.
 * <p>
 * Each player has a name, a board position, a CO2 resource balance, and a list
 * of area squares they own. Players aim to reduce their CO2 resources to zero (or below)
 * to win. If a player's resources reach or exceed {@link Settings#MAX_RESOURCES},
 * they can no longer make purchases or investments.
 * </p>
 */
public class Player {

    private String name;
    private int index;
    private int position;
    private int resources;
    private boolean inGame;
    private List<AreaSquare> ownedAreas;

    /**
     * Default Constructor
     */
    public Player() {
    }

    /**
     * Constructs a Player with the given name and player index.
     * <p>
     * Initialises the player at board position 0 with the starting resource
     * amount defined in {@link Settings#STARTING_RESOURCES}.
     * </p>
     *
     * @param name  the display name of the player (must not be blank)
     * @param index the player number, used to assign a console colour
     */
    public Player(String name, int index) {
        setName(name);
        this.index = index;
        this.resources = Settings.STARTING_RESOURCES;
        this.position = 0;
        this.inGame = Settings.gameRunning;
        this.ownedAreas = new ArrayList<>();
    }

    public Player(String testPlayer) {
    }

    /**
     * Directly sets the player's CO2 resource balance.
     *
     * @param resources the new resource value to assign
     */
    public void setResources(int resources) {
        this.resources = resources;
    }

    /**
     * Increases the player's CO2 resources by the given amount and prints a confirmation message.
     *
     * @param amount the number of CO2 resources to add
     */
    public void addResources(int amount) {
        setResources(resources + amount);
        System.out.println(getName() + " has gained " + amount + " resources. Balance: " + resources);
    }

    /**
     * Decreases the player's CO2 resources by the given amount.
     * <p>
     * If the deduction would exceed the player's current balance, bankruptcy is
     * checked first. The deduction is still applied regardless.
     * </p>
     *
     * @param amount the number of CO2 resources to deduct
     */
    public void deductResources(int amount) {
        if (amount > resources) {
            isBankrupt();
        }
        setResources(resources - amount);
        System.out.println(getName() + " was deducted " + amount + " resources. Balance: " + resources);
    }

    /**
     * Determines whether the player can afford a given cost without exceeding the
     * maximum resource cap ({@link Settings#MAX_RESOURCES}) or going below zero.
     *
     * @param amount the cost to check affordability for
     * @return {@code true} if the player can afford the amount, {@code false} otherwise
     */
    public boolean canAfford(int amount) {
        if (resources + amount > Settings.MAX_RESOURCES) {
            return false;
        } else {
            return resources - amount >= 0;
        }
    }

    /**
     * Returns whether the player currently has any CO2 resources remaining.
     *
     * @return {@code true} if resources are greater than zero
     */
    public boolean hasResources() {
        return resources > 0;
    }

    /**
     * Returns the player's current CO2 resource balance.
     *
     * @return the current resource total
     */
    public int getResources() {
        return resources;
    }

    /**
     * Ends the game by setting {@link Settings#gameRunning} to {@code false}
     * and prints a farewell message.
     */
    public void quitGame() {
        Settings.gameRunning = false;
        this.inGame = Settings.gameRunning;
        System.out.println(getName() + " has decided to end the game.");
    }

    /**
     * Checks whether this player is bankrupt (resources at or below zero).
     * Prints a message if so.
     *
     * @return {@code true} if the player has run out of resources
     */
    public boolean isBankrupt() {
        if (resources <= 0) {
            System.out.println(getName() + " has ran out of resources!");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the player's name with their assigned colour.
     * Use {@link #getCleanName()} to get the plain name without colour formatting.
     *
     * @return the colour-formatted display name
     */
    public String getName() {
        return Settings.PLAYER_COLOUR[index] + name + Settings.DEFAULT_TEXT_COLOUR;
    }

    /**
     * Returns the player's raw name without any colour formatting.
     * Used for comparisons and equality checks.
     *
     * @return the clean player name
     */
    public String getCleanName() {
        return name;
    }

    /**
     * Sets the player's name after validating it is not null or blank.
     *
     * @param name the name to assign
     * @throws IllegalArgumentException if the name is null or blank
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be blank.");
        }

        String clean = name.trim();

        // Regex: only letters, spaces, hyphens, apostrophes
        if (!clean.matches("^[A-Za-z][A-Za-z\\s'-]{1,20}$")) {
            throw new IllegalArgumentException(
                    "Invalid name. Names must contain only letters, spaces, hyphens, or apostrophes."
            );
        }

        this.name = clean;
    }


    /**
     * Returns the player's current position on the game board.
     *
     * @return the board position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the player's board position, wrapping around using modulo arithmetic
     * to handle passing the start square.
     *
     * @param position the new position
     * @param board the {@link GameBoard} used to determine total square count
     */
    public void setPosition(int position, GameBoard board) {
        this.position = position % board.getTotalSquares();
    }

    /**
     * Returns a summary string of the player's current game state, including
     * their name, resource balance, board position, and number of areas owned.
     *
     * @param gameBoard the {@link GameBoard} used to look up owned squares
     * @return a formatted status string
     */
    public String getStatus(GameBoard gameBoard) {
        return getName() + " | Resources: " + resources + " | Position: " + position + " | Areas Owned: " + gameBoard.getSquaresOwnedByPlayer(getName()).size();
    }

    /**
     * Returns a string representation of this player including their name and resource balance.
     *
     * @return formatted string in the form "Name (Resources: N)"
     */
    @Override
    public String toString() {
        return getName() + " (Resources: " + resources + ")";
    }
}
