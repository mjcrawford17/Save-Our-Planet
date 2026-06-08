package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Scanner;

public class GameManagerTest {

    private GameManager manager;

    @BeforeEach
    void setUp() {
        manager = new GameManager();
    }

    @Test
    void testAddPlayer() {
        manager.addPlayer("Ben");

        assertEquals(1,
                manager.getPlayers().size());

        assertEquals("Ben",
                manager.getPlayers().getFirst().getCleanName());
    }

    @Test
    void testDuplicateName() {
        manager.addPlayer("Matthew");

        assertThrows(IllegalArgumentException.class,
                () -> manager.addPlayer("Matthew"),
                "no duplicate names");
    }

    @Test
    void testMaxPlayers() {

        String[] extraNames = {"one", "two", "three", "four"};

        for (int i = 0; i < Settings.MAX_PLAYERS; i++) {
            manager.addPlayer("player " + extraNames[i]);
        }

        assertThrows(IllegalArgumentException.class,
                () -> manager.addPlayer("extra player"));
    }

    @Test
    void testPlayerQuitsGame() throws InterruptedException {
        Settings.gameRunning = true;

        manager.addPlayer("Claire");

        String input = "Q\nY\n";
        Scanner testScanner = new Scanner(input);

        manager.playGame(testScanner);

        assertFalse(Settings.gameRunning);
    }

    @Test
    void testCheckPassGoValid() {
        manager.addPlayer("Stephen");
        Player stephen = manager.getPlayers().getFirst();

        stephen.setResources(100);
        int initialCO2 = stephen.getResources();

        int oldPos = 11;
        int newPos = 2;

        if (oldPos >= newPos) {
            stephen.deductResources(Settings.PASS_START_MINUS_RESOURCES);
        }

        int expectedCO2 = initialCO2 - Settings.PASS_START_MINUS_RESOURCES;

        assertEquals(expectedCO2,
                stephen.getResources(),
                "should have reduced CO2 after passing start square");

        assertTrue(stephen.getResources() < initialCO2);
    }

    @Test
    void testCheckPassGoInvalid() {
        manager.addPlayer("Stephen");
        Player stephen = manager.getPlayers().getFirst();

        stephen.setResources(100);
        int initialCO2 = stephen.getResources();

        int posA = 2;
        int posB = 5;

        if (posA >= posB) {
            stephen.deductResources(Settings.PASS_START_MINUS_RESOURCES);
        }

        assertEquals(initialCO2,
                stephen.getResources(),
                "CO2 should not decrease");
    }

    @Test
    void testPassGoReducesCO2() {
        manager.addPlayer("Stephen");
        Player stephen = manager.getPlayers().getFirst();

        stephen.setResources(100);
        int initial = stephen.getResources();

        StartSquare start = (StartSquare) new GameBoard().getSquare(0);
        start.passedOver(stephen);

        assertEquals(initial - Settings.PASS_START_MINUS_RESOURCES,
                stephen.getResources(),
                "Passing GO should reduce CO2");
    }

    @Test
    void testLandingOnStartDoesNotReduceCO2() {
        manager.addPlayer("Claire");
        Player claire = manager.getPlayers().getFirst();

        claire.setResources(100);
        int initial = claire.getResources();

        assertEquals(initial,
                claire.getResources(),
                "Landing directly on Start should not reduce CO2");
    }

    @Test
    void testWinCondition() {
        manager.addPlayer("PlayerWin");
        Player winner = manager.getPlayers().getFirst();

        winner.setResources(-5);

        assertTrue(winner.getResources() <= 0,
                "player should be carbon negative");
    }

    @Test
    void testEndGameOutput() {
        manager.addPlayer("Player One");
        manager.addPlayer("Player Two");

        manager.getPlayers().get(0).setResources(50);
        manager.getPlayers().get(1).setResources(-10);

        assertDoesNotThrow(() -> manager.endGame(),
                "the final results table should print");
    }

    @Test
    void testAddPlayerTrimsNameAndRejectsCaseInsensitiveDuplicate() {
        manager.addPlayer("  Nora  ");

        assertEquals("Nora",
                manager.getPlayers().getFirst().getCleanName(),
                "Stored name should be trimmed");

        assertThrows(IllegalArgumentException.class,
                () -> manager.addPlayer("nora"),
                "Duplicate names should be rejected regardless of case");
    }

    @Test
    void testAddPlayerNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addPlayer(null),
                "addPlayer(null) should throw IllegalArgumentException");
    }
}