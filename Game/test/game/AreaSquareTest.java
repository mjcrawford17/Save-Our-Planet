package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AreaSquareTest {

    private Player testPlayer;
    private AreaSquare testArea;

    @BeforeEach
    public void setUp() {
        testPlayer = new Player("Aoife", 2);
        Field testField = new Field("Green");
        testArea = new AreaSquare("Forest", 1, testField, 50, 10);
        testPlayer.setResources(100);
    }

    @Test
    public void testInitialState() {
        assertNull(testArea.getOwner(),
                "new area should not have an owner");

        assertFalse(testArea.isOwned(),
                "area should not be owned initially");
    }

    @Test
    public void testPurchaseValid() {
        testPlayer.setResources(100);

        boolean result = testArea.purchase(testPlayer);

        assertTrue(result,
                "true on successful purchase");

        assertEquals(testPlayer,
                testArea.getOwner(),
                "player should now own the area");

        assertEquals(150,
                testPlayer.getResources(),
                "CO2 should increase");
    }

    @Test
    public void testPurchaseFails() {
        testPlayer.setResources(150);

        boolean result = testArea.purchase(testPlayer);

        assertFalse(result,
                "should be false if over limit");

        assertNull(testArea.getOwner(),
                "area owner still null");

        assertEquals(150,
                testPlayer.getResources(),
                "resources should not change if purchase fails");
    }

    @Test
    public void testPenaltyCalculation() {
        assertEquals(10,
                testArea.getPenaltyCost(),
                "penalty should match base cost at level 0");
    }

    @Test
    public void testSetOwnerWhenUnownedSetsOwner() {
        Player p2 = new Player("Bob", 1);

        testArea.setOwner(p2);

        assertEquals(p2,
                testArea.getOwner(),
                "Owner should be assigned when area is unowned");
    }

    @Test
    public void testSetOwnerWhenAlreadyOwnedDoesNotChangeOwner() {
        Player p2 = new Player("Bob", 1);

        testArea.purchase(testPlayer);

        testArea.setOwner(p2);

        assertEquals(testPlayer,
                testArea.getOwner(),
                "Owner should remain unchanged when already owned");
    }

    @Test
    public void testPenaltyCostAtVariousLevels() {
        testArea.setEfficiencyLevel(0);
        assertEquals(10,
                testArea.getPenaltyCost());

        testArea.setEfficiencyLevel(2);
        assertEquals(20,
                testArea.getPenaltyCost());

        testArea.setEfficiencyLevel(Settings.MAX_MINOR_DEVELOPMENTS + 1);
        assertEquals(45,
                testArea.getPenaltyCost(),
                "Should use major penalty cost");
    }

    @Test
    public void testDevelopmentCostRounding() {
        assertEquals(25,
                testArea.getDevelopmentCost());
    }

    @Test
    public void testDevelopmentFlags() {
        testArea.setEfficiencyLevel(Settings.MAX_EFFICIENCY_LEVEL);

        assertTrue(testArea.isFullyDeveloped());
        assertTrue(testArea.hasMajorEfficiencyDevelopment());
    }

    @Test
    public void testLandedOnUnowned() {
        assertNull(testArea.getOwner());

        int before = testPlayer.getResources();

        testArea.landedOn(testPlayer);

        assertEquals(before,
                testPlayer.getResources(),
                "Unowned area should not change resources");
    }

    @Test
    public void testLandedOnOwnedBySelf() {
        testArea.purchase(testPlayer);

        int before = testPlayer.getResources();

        testArea.landedOn(testPlayer);

        assertEquals(before,
                testPlayer.getResources(),
                "Landing on own area should not change resources");
    }

    @Test
    public void testCanDevelopUnowned() {
        assertFalse(testArea.canDevelop(),
                "Unowned area cannot be developed");
    }

    @Test
    public void testCanDevelopOwnerDoesNotOwnAll() {
        Player owner = new Player("Owner", 1);
        owner.setResources(200);

        testArea.purchase(owner);

        AreaSquare other = new AreaSquare("Other", 2, testArea.getField(), 50, 10);
        testArea.getField().addArea(other);

        assertFalse(testArea.canDevelop(),
                "Must own all areas in field");
    }

    @Test
    public void testLandedOnOwnedByOtherTransfersPenaltyAtDevelopmentLevel() {
        Player owner = new Player("Alice", 0);
        owner.setResources(100);

        testArea.purchase(owner);
        owner.setResources(80);

        Player visitor = new Player("Brian", 1);
        visitor.setResources(50);

        testArea.setEfficiencyLevel(2);

        testArea.landedOn(visitor);

        assertEquals(70,
                visitor.getResources());

        assertEquals(60,
                owner.getResources());
    }

    @Test
    public void testLandedOnOwnedByOtherCanBankruptOwner() {
        Player owner = new Player("Clare", 0);
        owner.setResources(100);

        testArea.purchase(owner);
        owner.setResources(5);

        Player visitor = new Player("David", 1);
        visitor.setResources(40);

        testArea.setEfficiencyLevel(0);

        testArea.landedOn(visitor);

        assertEquals(50,
                visitor.getResources());

        assertEquals(-5,
                owner.getResources(),
                "Owner can go negative");

        assertTrue(owner.isBankrupt(),
                "Owner should be bankrupt");
    }

    @Test
    public void testPenaltyCostAtMaxMinorDevelopments() {
        testArea.setEfficiencyLevel(Settings.MAX_MINOR_DEVELOPMENTS);

        assertEquals(45,
                testArea.getPenaltyCost(),
                "Max minor development penalty");
    }

    @Test
    public void testGetDescriptionContainsKeyFields() {
        Player owner = new Player("Alice", 0);
        owner.setResources(100);

        testArea.purchase(owner);

        String desc = testArea.getDescription();

        assertTrue(desc.contains("Forest"));
        assertTrue(desc.contains("Green"));
        assertTrue(desc.contains("Alice"));
        assertTrue(desc.contains(String.valueOf(testArea.getPurchaseCost())));
        assertTrue(desc.contains(String.valueOf(testArea.getPenaltyCost())));
        assertTrue(desc.contains(String.valueOf(testArea.getEfficiencyLevel())));
    }

    @Test
    public void testGetDescriptionUnowned() {
        String desc = testArea.getDescription();

        assertTrue(desc.contains("No one"));
    }

    @Test
    public void testGetDescriptionShowsMajorLabel() {
        Player owner = new Player("Bob", 0);
        owner.setResources(100);

        testArea.purchase(owner);
        testArea.setEfficiencyLevel(Settings.MAX_EFFICIENCY_LEVEL);

        String desc = testArea.getDescription();

        assertTrue(desc.contains("[MAJOR]"));
    }

    @Test
    public void testPurchaseWhenAlreadyOwnedDoesNotChangeOwner() {
        Player first = new Player("First", 0);
        Player second = new Player("Second", 1);

        first.setResources(100);
        second.setResources(100);

        testArea.purchase(first);

        boolean result = testArea.purchase(second);

        assertFalse(result);

        assertEquals(first,
                testArea.getOwner());
    }
}