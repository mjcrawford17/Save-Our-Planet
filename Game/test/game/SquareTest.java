package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SquareTest {

    private Square square;
    private Player player;

    @BeforeEach
    public void setUp() {
        // Using BlankSquare as concrete implementation of abstract Square
        square = new BlankSquare("Test Square", 5);
        player = new Player("TestPlayer", 0);
    }

    @Test
    public void getName() {
        String name = square.getName();

        assertNotNull(name, "Name should not be null");
        assertEquals("Test Square", name, "Name should be 'Test Square'");
    }

    @Test
    public void setName() {
        square.setName("New Name");

        assertEquals("New Name", square.getName(), "Name should be updated to 'New Name'");
    }

    @Test
    public void setNameMultipleTimes() {
        square.setName("First Name");
        assertEquals("First Name", square.getName(), "First name change");

        square.setName("Second Name");
        assertEquals("Second Name", square.getName(), "Second name change");
    }

    @Test
    public void getPosition() {
        int position = square.getPosition();

        assertEquals(5, position, "Position should be 5");
    }

    @Test
    public void setPosition() {
        square.setPosition(10);

        assertEquals(10, square.getPosition(), "Position should be updated to 10");
    }

    @Test
    public void setPositionMultipleTimes() {
        square.setPosition(3);
        assertEquals(3, square.getPosition(), "First position change");

        square.setPosition(8);
        assertEquals(8, square.getPosition(), "Second position change");
    }

    @Test
    public void landedOn() {
        assertNotNull(square);
        assertNotNull(player);

        square.landedOn(player);
    }

    @Test
    public void getDescription() {
        String description = square.getDescription();

        assertNotNull(description, "Description should not be null");
        assertFalse(description.isEmpty(), "Description should not be empty");
    }

    @Test
    public void testToString() {
        String str = square.toString();

        assertNotNull(str, "toString should not return null");
        assertTrue(str.contains("Test Square"));
        assertTrue(str.contains("5"));
    }

    @Test
    public void squareInitialization() {
        assertNotNull(square);
        assertEquals("Test Square", square.getName());
        assertEquals(5, square.getPosition());
    }

    @Test
    public void squareWithDifferentPositions() {
        Square square0 = new BlankSquare("Start", 0);
        Square square11 = new BlankSquare("End", 11);

        assertEquals(0, square0.getPosition());
        assertEquals(11, square11.getPosition());
    }
}