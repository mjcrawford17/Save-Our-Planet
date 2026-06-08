package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Save Our Planet game board.
 * <p>
 * Manages all {@link Square}s and {@link Field}s that make up the board. Responsible
 * for initialising the board layout, querying ownership and developability, and rendering
 * the board state to the console during gameplay.
 * </p>
 */
public class GameBoard {
    private final List<Square> squares;
    private final List<Field> fields;
    private int totalSquares;

    /**
     * Constructs a GameBoard and immediately initialises all squares and fields
     * via {@link #initializeBoard()}.
     */
    public GameBoard() {
        squares = new ArrayList<>();
        fields = new ArrayList<>();
        initializeBoard();
    }

    /**
     * Initialises all fields and squares on the board in positional order.
     * <p>
     * Creates 4 fields (ordered least to most expensive) and 12 squares:
     * 1 start square, 10 area squares across 4 fields, and 1 blank square.
     * After creating all squares, each field is populated with its respective areas.
     * </p>
     */
    private void initializeBoard() {
        //Create 4 Fields (Least expensive to most)
        fields.add(new Field("Waste Reduction"));
        fields.add(new Field("Sustainable Transport"));
        fields.add(new Field("Renewable Energy"));
        fields.add(new Field("Environmental Restoration"));

        //Squares by Order
        //Position 0: Start Square (Sustainability Hub)
        squares.add(new StartSquare("Sustainability Hub", 0));
        //Position 1
        squares.add(new AreaSquare("Solar Power", 1, fields.get(2), Settings.FIELD3_COST, Settings.BASE_COST + 10));
        //Position 2
        squares.add(new AreaSquare("Cycling Infrastructure", 2, fields.get(1), Settings.FIELD2_COST, Settings.BASE_COST + 5));
        //Position 3
        squares.add(new AreaSquare("Wind Turbines", 3, fields.get(2), Settings.FIELD3_COST, Settings.BASE_COST + 10));
        //Position 4: Blank Square (Carbon Neutral Zone)
        squares.add(new BlankSquare("Carbon Neutral Zone", 4));
        //Position 5
        squares.add(new AreaSquare("Recycling Program", 5, fields.get(0), Settings.FIELD1_COST, Settings.BASE_COST));
        //Position 6
        squares.add(new AreaSquare("Hydroelectricity", 6, fields.get(2), Settings.FIELD3_COST, Settings.BASE_COST + 10));
        //Position 7
        squares.add(new AreaSquare("Public Transport", 7, fields.get(1), Settings.FIELD2_COST, Settings.BASE_COST + 5));
        //Position 8
        squares.add(new AreaSquare("Reforestation", 8, fields.get(3), Settings.FIELD4_COST, Settings.BASE_COST + 15));
        //Position 9
        squares.add(new AreaSquare("Compost Initiative", 9, fields.get(0), Settings.FIELD1_COST, Settings.BASE_COST));
        //Position 10
        squares.add(new AreaSquare("Electric Vehicles", 10, fields.get(1), Settings.FIELD2_COST, Settings.BASE_COST + 5));
        //Position 11
        squares.add(new AreaSquare("Ocean Protection", 11, fields.get(3), Settings.FIELD4_COST, Settings.BASE_COST + 15));

        //Fields
        //Field 1
        fields.get(0).addArea((AreaSquare) squares.get(5));
        fields.get(0).addArea((AreaSquare) squares.get(9));

        //Field 2
        fields.get(1).addArea((AreaSquare) squares.get(2));
        fields.get(1).addArea((AreaSquare) squares.get(7));
        fields.get(1).addArea((AreaSquare) squares.get(10));

        //Field 3
        fields.get(2).addArea((AreaSquare) squares.get(1));
        fields.get(2).addArea((AreaSquare) squares.get(3));
        fields.get(2).addArea((AreaSquare) squares.get(6));

        //Field 4
        fields.get(3).addArea((AreaSquare) squares.get(8));
        fields.get(3).addArea((AreaSquare) squares.get(11));

        totalSquares = squares.size();
    }

    /**
     * Returns all area squares on the board currently owned by the named player.
     *
     * @param playerName the name of the player to search for
     * @return a list of {@link AreaSquare}s owned by that player; empty if none
     */
    public List<AreaSquare> getSquaresOwnedByPlayer(String playerName) {

        List<AreaSquare> ownedSquares = new ArrayList<>();

        for (int i = 0; i < totalSquares; i++) {
            Square square = squares.get(i);
            if (square instanceof AreaSquare area) {
                if (!(area.getOwner() == null)) {
                    if (area.getOwner().getName().equals(playerName)) {
                        ownedSquares.add(area);
                    }
                }
            }
        }
        return ownedSquares;
    }

    /**
     * Determines whether a specific player owns all area squares within a given field.
     *
     * @param playerName the name of the player to check
     * @param field the {@link Field} to evaluate
     * @return {@code true} if the player owns every square in the field and the field is non-empty;
     *         {@code false} otherwise
     */
    public boolean playerOwnsAllInField(String playerName, Field field) {
        List<AreaSquare> fieldAreas = field.getAreas();

        if (fieldAreas.isEmpty()) {
            return false;
        }

        for (AreaSquare area : fieldAreas) {
            if (area.getOwner() == null || !area.getOwner().getName().equals(playerName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns all area squares that a given player is eligible to develop.
     * <p>
     * A player may develop squares in any field where they own all areas.
     * </p>
     *
     * @param playerName the name of the player
     * @return a list of developable {@link AreaSquare}s; empty if none qualify
     */
    public List<AreaSquare> getDevelopableSquaresForPlayer(String playerName) {
        List<AreaSquare> developable = new ArrayList<>();

        for (Field field : fields) {
            if (playerOwnsAllInField(playerName, field)) {
                developable.addAll(field.getAreas());
            }
        }

        return developable;
    }

    /**
     * Prints a formatted ownership summary for all fields to the console.
     * <p>
     * For each field, shows whether it is fully owned by a single player
     * (and flags eligibility for efficiency investments), then lists each
     * area square and its current owner.
     * </p>
     */
    public void displayFieldOwnershipSummary() {
        System.out.println("\n═══ FIELD OWNERSHIP SUMMARY ═══");

        for (Field field : fields) {
            System.out.println("\n" + field.getName() + ":");
            List<AreaSquare> fieldAreas = field.getAreas();

            // Check if completely owned by one player
            boolean fullyOwned = true;
            Player ownerIfComplete = null;

            if (!fieldAreas.isEmpty() && fieldAreas.get(0).getOwner() != null) {
                ownerIfComplete = fieldAreas.get(0).getOwner();
                for (AreaSquare area : fieldAreas) {
                    if (area.getOwner() == null || !area.getOwner().equals(ownerIfComplete)) {
                        fullyOwned = false;
                        break;
                    }
                }
            } else {
                fullyOwned = false;
            }

            // Display ownership status
            if (fullyOwned && ownerIfComplete != null) {
                System.out.printf("%s\tAREA FULLY OWNED BY: %s%s - Eligible for efficiency investments %s%n", Settings.BLUE_TEXT, ownerIfComplete.getName(), Settings.BLUE_TEXT, Settings.DEFAULT_TEXT_COLOUR);
            }
            System.out.println("\tProperties:");
            // Always display individual properties
            for (AreaSquare area : fieldAreas) {
                String owner = area.isOwned() ? area.getOwner().getName() : "Unowned";
                System.out.println("\t  - " + area.getName() + ": " + owner);
            }
        }

        System.out.println("\n═════════════════════════════\n");
    }

    /**
     * Returns the square at the given board position.
     *
     * @param position the position to retrieve
     * @return the {@link Square} at that position
     */
    public Square getSquare(int position) {
        return squares.get(position);
    }

    /**
     * Returns the field at the given index.
     *
     * @param number the field number
     * @return the {@link Field} at that index
     */
    public Field getField(int number) {
        return fields.get(number);
    }

    /**
     * Returns the total number of squares on the board.
     *
     * @return the square count
     */
    public int getTotalSquares() {
        return totalSquares;
    }

    /**
     * Renders the full game board to the console, including all square details
     * and a player summary at the bottom.
     * <p>
     * Each square displays its name, type-specific information (field, ownership,
     * penalty, efficiency level for area squares), and any players currently
     * standing on it.
     * </p>
     *
     * @param players the list of all active {@link Player}s
     */
    public void displayBoard(List<Player> players) {
        try {
            String border = "══════════════════════════════════════════════════════════════════════";
            System.out.println("\n╔" + border + "╗");
            System.out.printf("║%50s%20s║%n", "SAVE OUR PLANET - GAME BOARD", "");
            Thread.sleep(200);

            for (int i = 0; i < totalSquares; i++) {
                Square square = squares.get(i);

                StringBuilder playersHere = new StringBuilder();
                int countPlayersHere = 0;
                for (Player p : players) {
                    if (p.getPosition() == i) {
                        countPlayersHere++;
                        if (playersHere.length() > 0) {
                            playersHere.append(", ");
                        }
                        playersHere.append(p.getName());
                    }
                }

                System.out.println("╠" + border + "╣");
                System.out.printf("║[%2d] %-65s║%n", i, square.getName());

                if (square instanceof AreaSquare area) {
                    System.out.printf("║Field: %-28s\tPurchase Cost: %-16s║%n", area.getFieldName(), area.isOwned() ? "SOLD" : area.getPurchaseCost() + " CO2");
                    System.out.printf("║In Charge: %-" + (area.isOwned() ? 36 : 25) + "s\tLanding Penalty: %-14s║%n", area.isOwned() ? area.getOwner().getName() : "No one", area.getPenaltyCost() + " CO2");
                    System.out.printf("║Efficiency:  %d/%d %-53s║%n", area.getEfficiencyLevel(), Settings.MAX_EFFICIENCY_LEVEL, area.hasMajorEfficiencyDevelopment() ? "[MAJOR]" : "" );

                } else if (square instanceof StartSquare) {
                    System.out.printf("║%-70s║%n", "Pass or land here to reduce CO2");

                } else if (square instanceof BlankSquare) {
                    System.out.printf("║%-70s║%n", "Nothing happens here");
                }

                if (playersHere.length() > 0) {
                    System.out.printf("║>> Players here: %-" + (53 + (countPlayersHere * 11)) + "s║%n", playersHere);
                }
            }

            System.out.println("╠" + border + "╣");
            System.out.printf("║%-70s║%n", "PLAYER SUMMARY");
            System.out.println("╠" + border + "╣");
            for (Player p : players) {
                System.out.printf("║%-25s | CO2: %-6d | Position: %-29s║%n", p.getName(), p.getResources(), squares.get(p.getPosition()).getName());
            }
            System.out.println("╚" + border + "╝\n");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
