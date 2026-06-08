package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BlankSquareTest {

    private BlankSquare blankSquare;
    private Player player;

    @BeforeEach
    public void setUp() {
        blankSquare = new BlankSquare("Rest Area", 5);
        player = new Player("TestPlayer", 2);
    }

    @Test
    public void landedOn() {
        int initialResources = player.getResources();

        blankSquare.landedOn(player);

        assertEquals(initialResources,
                player.getResources(),
                "Landing on a blank square should not change player resources");
    }

    @Test
    public void getDescription() {
        String description = blankSquare.getDescription();

        assertNotNull(description, "Description should not be null");
        assertTrue(description.contains("Rest Area"),
                "Description should contain square name");
        assertTrue(description.contains("resting spot"),
                "Description should indicate it's a resting spot");
        assertTrue(description.contains("No action required"),
                "Description should indicate no action required");
    }

    @Test
    public void testBlankSquareInitialization() {
        assertEquals("Rest Area",
                blankSquare.getName(),
                "Square name should be 'Rest Area'");

        assertEquals(5,
                blankSquare.getPosition(),
                "Square position should be 5");
    }
}