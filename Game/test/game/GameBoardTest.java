package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

public class GameBoardTest {

    private GameBoard gameBoard;

    @BeforeEach
    public void setUp() {
        gameBoard = new GameBoard();
    }

    @Test
    public void getSquare() {
        Square square = gameBoard.getSquare(0);

        assertNotNull(square, "Square should not be null");
        assertTrue(square instanceof StartSquare,
                "Position 0 should be StartSquare");
        assertEquals(0,
                square.getPosition(),
                "Square position should be 0");
    }

    @Test
    public void getSquareAtDifferentPositions() {
        Square square1 = gameBoard.getSquare(0);
        Square square5 = gameBoard.getSquare(5);
        Square square11 = gameBoard.getSquare(11);

        assertNotNull(square1, "Square at position 0 should not be null");
        assertNotNull(square5, "Square at position 5 should not be null");
        assertNotNull(square11, "Square at position 11 should not be null");

        assertEquals(0, square1.getPosition(), "Position 0 square position");
        assertEquals(5, square5.getPosition(), "Position 5 square position");
        assertEquals(11, square11.getPosition(), "Position 11 square position");
    }

    @Test
    public void getSquareTypes() {
        assertTrue(gameBoard.getSquare(0) instanceof StartSquare,
                "Position 0 should be StartSquare");

        assertTrue(gameBoard.getSquare(1) instanceof AreaSquare,
                "Position 1 should be AreaSquare");

        assertTrue(gameBoard.getSquare(4) instanceof BlankSquare,
                "Position 4 should be BlankSquare");
    }

    @Test
    public void getField() {
        Field field = gameBoard.getField(0);

        assertNotNull(field, "Field should not be null");
        assertEquals("Waste Reduction",
                field.getName(),
                "Field name should be 'Waste Reduction'");
    }

    @Test
    public void getFieldAtDifferentNumbers() {
        Field field0 = gameBoard.getField(0);
        Field field1 = gameBoard.getField(1);
        Field field2 = gameBoard.getField(2);
        Field field3 = gameBoard.getField(3);

        assertNotNull(field0);
        assertNotNull(field1);
        assertNotNull(field2);
        assertNotNull(field3);

        assertEquals("Waste Reduction", field0.getName());
        assertEquals("Sustainable Transport", field1.getName());
        assertEquals("Renewable Energy", field2.getName());
        assertEquals("Environmental Restoration", field3.getName());
    }

    @Test
    public void getTotalSquares() {
        assertEquals(12,
                gameBoard.getTotalSquares(),
                "Board should have 12 squares");
    }

    @Test
    public void boardInitialization() {
        assertNotNull(gameBoard);
        assertTrue(gameBoard.getTotalSquares() > 0);

        for (int i = 0; i < gameBoard.getTotalSquares(); i++) {
            assertNotNull(gameBoard.getSquare(i),
                    "Square at position " + i + " should not be null");
        }
    }

    @Test
    public void fieldAreasInitialization() {
        Field field0 = gameBoard.getField(0);
        Field field1 = gameBoard.getField(1);

        assertFalse(field0.getAreas().isEmpty(),
                "Field 0 should have areas");

        assertFalse(field1.getAreas().isEmpty(),
                "Field 1 should have areas");
    }

    @Test
    public void squareNamesInitialization() {
        assertNotNull(gameBoard.getSquare(0).getName(),
                "Square 0 should have a name");

        assertNotNull(gameBoard.getSquare(1).getName(),
                "Square 1 should have a name");

        assertFalse(gameBoard.getSquare(0).getName().isEmpty(),
                "Square names should not be empty");
    }

    @Test
    public void developableSquaresOnlyIncludeFullyOwnedField() {
        Player owner = new Player("Ethan", 0);

        AreaSquare wasteA = (AreaSquare) gameBoard.getSquare(5);
        AreaSquare wasteB = (AreaSquare) gameBoard.getSquare(9);
        AreaSquare transportA = (AreaSquare) gameBoard.getSquare(2);

        assertTrue(wasteA.purchase(owner));
        assertTrue(wasteB.purchase(owner));
        assertFalse(transportA.purchase(owner),
                "Third purchase should fail after affordability limit");

        List<AreaSquare> developable =
                gameBoard.getDevelopableSquaresForPlayer(owner.getName());

        assertEquals(2,
                developable.size(),
                "Only two Waste Reduction areas should be developable");

        assertTrue(developable.stream()
                        .allMatch(a -> "Waste Reduction".equals(a.getFieldName())),
                "All should belong to Waste Reduction");
    }

    @Test
    public void playerOwnsAllInFieldReturnsFalseForSplitOwnership() {
        Field wasteReduction = gameBoard.getField(0);

        Player alice = new Player("Alice", 0);
        Player ben = new Player("Ben", 1);

        AreaSquare wasteA = (AreaSquare) gameBoard.getSquare(5);
        AreaSquare wasteB = (AreaSquare) gameBoard.getSquare(9);

        assertTrue(wasteA.purchase(alice));
        assertTrue(wasteB.purchase(ben));

        assertFalse(gameBoard.playerOwnsAllInField(alice.getName(), wasteReduction));
        assertFalse(gameBoard.playerOwnsAllInField(ben.getName(), wasteReduction));
    }

    @Test
    public void getSquaresOwnedByPlayerReturnsOwnedSquares() {
        Player alice = new Player("Alice", 0);

        AreaSquare wasteA = (AreaSquare) gameBoard.getSquare(5);
        AreaSquare wasteB = (AreaSquare) gameBoard.getSquare(9);

        assertTrue(wasteA.purchase(alice));
        assertTrue(wasteB.purchase(alice));

        List<AreaSquare> owned =
                gameBoard.getSquaresOwnedByPlayer(alice.getName());

        assertEquals(2,
                owned.size(),
                "Alice should own exactly 2 squares");

        assertTrue(owned.contains(wasteA));
        assertTrue(owned.contains(wasteB));
    }

    @Test
    public void getSquaresOwnedByPlayerReturnsEmptyForNoOwnership() {
        Player alice = new Player("Alice", 0);

        List<AreaSquare> owned =
                gameBoard.getSquaresOwnedByPlayer(alice.getName());

        assertNotNull(owned);
        assertTrue(owned.isEmpty(),
                "Player with no squares should return empty list");
    }

    @Test
    public void playerOwnsAllInFieldReturnsTrueWhenFullyOwned() {
        Field wasteReduction = gameBoard.getField(0);
        Player alice = new Player("Alice", 0);

        AreaSquare wasteA = (AreaSquare) gameBoard.getSquare(5);
        AreaSquare wasteB = (AreaSquare) gameBoard.getSquare(9);

        assertTrue(wasteA.purchase(alice));
        assertTrue(wasteB.purchase(alice));

        assertTrue(gameBoard.playerOwnsAllInField(alice.getName(), wasteReduction),
                "Should return true when player owns all in field");
    }

    @Test
    public void displayBoardDoesNotThrow() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Alice", 0));

        assertDoesNotThrow(() ->
                        gameBoard.displayBoard(players),
                "displayBoard should not throw exception");
    }

    @Test
    public void displayFieldOwnershipSummaryDoesNotThrow() {
        assertDoesNotThrow(() ->
                        gameBoard.displayFieldOwnershipSummary(),
                "displayFieldOwnershipSummary should not throw exception");
    }
}