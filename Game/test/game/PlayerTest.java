package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private GameBoard gameBoard;

    @BeforeEach
    public void setUp() {
        player = new Player("TestPlayer", 2);
        gameBoard = new GameBoard();
        Settings.gameRunning = true;
    }

    @Test
    public void setResources() {
        player.setResources(100);

        assertEquals(100,
                player.getResources(),
                "Resources should be set to 100");
    }

    @Test
    public void setResourcesMultipleTimes() {
        player.setResources(100);
        assertEquals(100,
                player.getResources(),
                "First resource set should work");

        player.setResources(50);
        assertEquals(50,
                player.getResources(),
                "Second resource set should work");
    }

    @Test
    public void addResources() {
        int initialResources = player.getResources();
        player.addResources(20);

        assertEquals(initialResources + 20,
                player.getResources(),
                "Resources should increase by 20");
    }

    @Test
    public void addMultipleResourceAmounts() {
        player.setResources(100);
        player.addResources(10);
        assertEquals(110,
                player.getResources(),
                "After first addition");

        player.addResources(30);
        assertEquals(140,
                player.getResources(),
                "After second addition");
    }

    @Test
    public void deductResources() {
        player.setResources(100);
        player.deductResources(30);

        assertEquals(70,
                player.getResources(),
                "Resources should decrease by 30");
    }

    @Test
    public void deductResourcesMultipleTimes() {
        player.setResources(100);
        player.deductResources(20);
        assertEquals(80,
                player.getResources(),
                "After first deduction");

        player.deductResources(30);
        assertEquals(50,
                player.getResources(),
                "After second deduction");
    }

    @Test
    public void canAfford() {
        player.setResources(100);
        int oneTooMany = 1 + (Settings.MAX_RESOURCES - player.getResources());

        assertTrue(player.canAfford(50),
                "Player should afford 50 resources");

        assertFalse(player.canAfford(oneTooMany),
                "Player should not afford exceeding max resources");
    }

    @Test
    public void hasResources() {
        player.setResources(50);
        assertTrue(player.hasResources(),
                "Player with 50 resources should have resources");

        player.setResources(0);
        assertFalse(player.hasResources(),
                "Player with 0 resources should not have resources");

        player.setResources(1);
        assertTrue(player.hasResources(),
                "Player with 1 resource should have resources");
    }

    @Test
    public void getResources() {
        player.setResources(75);

        assertEquals(75,
                player.getResources(),
                "Should return 75 resources");
    }

    @Test
    public void quitGame() {
        assertTrue(Settings.gameRunning,
                "Game should be running initially");

        player.quitGame();

        assertFalse(Settings.gameRunning,
                "Game should not be running after quit");
    }

    @Test
    public void isBankrupt() {
        player.setResources(0);
        assertTrue(player.isBankrupt(),
                "Player with 0 resources should be bankrupt");

        player.setResources(1);
        assertFalse(player.isBankrupt(),
                "Player with 1 resource should not be bankrupt");

        player.setResources(50);
        assertFalse(player.isBankrupt(),
                "Player with 50 resources should not be bankrupt");
    }

    @Test
    public void getName() {
        String name = player.getCleanName();

        assertNotNull(name,
                "Name should not be null");

        assertEquals("TestPlayer",
                name,
                "Name should be 'TestPlayer'");
    }

    @Test
    public void setName() {
        player.setName("NewName");

        assertEquals("NewName",
                player.getCleanName(),
                "Name should be updated to 'NewName'");
    }

    @Test
    public void setNameThrowsExceptionOnNull() {
        assertThrows(IllegalArgumentException.class,
                () -> player.setName(null));
    }

    @Test
    public void setNameThrowsExceptionOnBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> player.setName("   "));
    }

    @Test
    public void getPosition() {
        int position = player.getPosition();

        assertEquals(0,
                position,
                "Initial position should be 0");
    }

    @Test
    public void setPosition() {
        player.setPosition(5, gameBoard);

        assertEquals(5,
                player.getPosition(),
                "Position should be set to 5");
    }

    @Test
    public void setPositionWrapsAround() {
        player.setPosition(15, gameBoard);

        assertEquals(3,
                player.getPosition(),
                "Position should wrap around (15 % 12 = 3)");
    }

    @Test
    public void getStatus() {
        player.setResources(100);
        player.setPosition(5, gameBoard);

        String status = player.getStatus(gameBoard);

        assertNotNull(status,
                "Status should not be null");

        assertTrue(status.contains("TestPlayer"));
        assertTrue(status.contains("100"));
        assertTrue(status.contains("5"));
    }

    @Test
    public void testToString() {
        player.setResources(100);

        String str = player.toString();

        assertNotNull(str,
                "toString should not return null");

        assertTrue(str.contains("TestPlayer"));
        assertTrue(str.contains("100"));
    }

    @Test
    public void playerInitialization() {
        Player newPlayer = new Player("InitPlayer", 2);

        assertNotNull(newPlayer);
        assertEquals("InitPlayer",
                newPlayer.getCleanName());

        assertEquals(0,
                newPlayer.getPosition());

        assertEquals(Settings.STARTING_RESOURCES,
                newPlayer.getResources());
    }

    @Test
    public void deductResourcesTriggersBankruptcy() {
        player.setResources(10);
        player.deductResources(20);

        assertTrue(player.isBankrupt());
    }

    @Test
    public void deductResourcesCanGoNegative() {
        player.setResources(10);
        player.deductResources(20);

        assertEquals(-10,
                player.getResources(),
                "Resources should go negative when over-deducted");

        assertTrue(player.isBankrupt());
    }

    @Test
    public void canAffordPreventsNegativeResources() {
        player.setResources(10);

        assertTrue(player.canAfford(5));
        assertFalse(player.canAfford(20));
    }

    @Test
    public void setNameAcceptsValidNames() {
        assertDoesNotThrow(() -> player.setName("Alice"));
        assertDoesNotThrow(() -> player.setName("Bob Marley"));
        assertDoesNotThrow(() -> player.setName("Jean-Paul"));
        assertDoesNotThrow(() -> player.setName("O'Connor"));
    }

    @Test
    public void setNameThrowsExceptionOnInvalidRegex() {
        IllegalArgumentException ex1 =
                assertThrows(IllegalArgumentException.class,
                        () -> player.setName("A"));
        assertEquals("Invalid name. Names must contain only letters, spaces, hyphens, or apostrophes.",
                ex1.getMessage());

        IllegalArgumentException ex2 =
                assertThrows(IllegalArgumentException.class,
                        () -> player.setName("1234"));
        assertEquals("Invalid name. Names must contain only letters, spaces, hyphens, or apostrophes.",
                ex2.getMessage());

        IllegalArgumentException ex3 =
                assertThrows(IllegalArgumentException.class,
                        () -> player.setName("$%^&"));
        assertEquals("Invalid name. Names must contain only letters, spaces, hyphens, or apostrophes.",
                ex3.getMessage());

        IllegalArgumentException ex4 =
                assertThrows(IllegalArgumentException.class,
                        () -> player.setName("John!!"));
        assertEquals("Invalid name. Names must contain only letters, spaces, hyphens, or apostrophes.",
                ex4.getMessage());
    }

    @Test
    public void canAffordReturnsFalseWhenPurchaseExceedsMaxResources() {
        player.setResources(160);

        assertFalse(player.canAfford(25),
                "canAfford should return false when exceeding MAX_RESOURCES");
    }

    @Test
    public void getNameContainsColourFormatting() {
        String formatted = player.getName();
        String clean = player.getCleanName();

        assertTrue(formatted.contains(clean));
        assertNotEquals(formatted, clean,
                "getName and getCleanName should differ due to colour codes");
    }
}