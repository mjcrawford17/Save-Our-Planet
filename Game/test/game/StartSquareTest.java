package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StartSquareTest {

    private StartSquare startSquare;
    private Player player;

    @BeforeEach
    public void setUp() {
        startSquare = new StartSquare("Sustainability Hub", 0);
        player = new Player("TestPlayer", 0);
    }

    @Test
    public void getResourceGrant() {
        int grant = startSquare.getResourceGrant();

        assertEquals(Settings.PASS_START_MINUS_RESOURCES, grant);
        assertEquals(15, grant);
    }

    @Test
    public void landedOn() {
        int initialResources = player.getResources();

        startSquare.landedOn(player);

        assertEquals(initialResources - Settings.PASS_START_MINUS_RESOURCES,
                player.getResources());
    }

    @Test
    public void landedOnReducesResources() {
        player.setResources(100);

        startSquare.landedOn(player);

        assertEquals(85, player.getResources());
    }

    @Test
    public void landedOnMultipleTimes() {
        player.setResources(100);

        startSquare.landedOn(player);
        assertEquals(85, player.getResources());

        startSquare.landedOn(player);
        assertEquals(70, player.getResources());
    }

    @Test
    public void passedOver() {
        int initialResources = player.getResources();

        startSquare.passedOver(player);

        assertEquals(initialResources - Settings.PASS_START_MINUS_RESOURCES,
                player.getResources());
    }

    @Test
    public void passedOverReducesResources() {
        player.setResources(100);

        startSquare.passedOver(player);

        assertEquals(85, player.getResources());
    }

    @Test
    public void passedOverMultipleTimes() {
        player.setResources(100);

        startSquare.passedOver(player);
        assertEquals(85, player.getResources());

        startSquare.passedOver(player);
        assertEquals(70, player.getResources());
    }

    @Test
    public void getDescription() {
        String description = startSquare.getDescription();

        assertNotNull(description);
        assertTrue(description.contains("Sustainability Hub"));
        assertTrue(description.contains("deducts"));
        assertTrue(description.contains("15"));
    }

    @Test
    public void startSquareInitialization() {
        assertNotNull(startSquare);
        assertEquals("Sustainability Hub", startSquare.getName());
        assertEquals(0, startSquare.getPosition());
        assertEquals(15, startSquare.getResourceGrant());
    }

    @Test
    public void startSquareInheritance() {
        assertTrue(startSquare instanceof Square);
    }

    @Test
    public void landedOnAndPassedOverHaveSameEffect() {
        player.setResources(100);
        startSquare.landedOn(player);
        int afterLanding = player.getResources();

        Player player2 = new Player("TestPlayerTwo", 1);
        player2.setResources(100);
        startSquare.passedOver(player2);
        int afterPassing = player2.getResources();

        assertEquals(afterLanding, afterPassing);
    }
}