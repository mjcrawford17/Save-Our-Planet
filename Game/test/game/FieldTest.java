package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    private Field field;
    private AreaSquare areaSquare1;
    private AreaSquare areaSquare2;

    @BeforeEach
    public void setUp() {
        field = new Field("Forest");
        areaSquare1 = new AreaSquare("Forest Valley", 10, field, 100, 50);
        areaSquare2 = new AreaSquare("Forest Peak", 11, field, 150, 75);
    }

    @Test
    public void addArea() {
        field.addArea(areaSquare1);

        assertEquals(1,
                field.getAreas().size(),
                "Field should contain 1 area");

        assertTrue(field.getAreas().contains(areaSquare1),
                "Added area should be in the list");
    }

    @Test
    public void addMultipleAreas() {
        field.addArea(areaSquare1);
        field.addArea(areaSquare2);

        assertEquals(2,
                field.getAreas().size(),
                "Field should contain 2 areas");

        assertTrue(field.getAreas().contains(areaSquare1),
                "First area should be in the list");

        assertTrue(field.getAreas().contains(areaSquare2),
                "Second area should be in the list");
    }

    @Test
    public void addAreaSetsFieldReference() {
        field.addArea(areaSquare1);

        assertEquals(field,
                areaSquare1.getField(),
                "AreaSquare should have field reference set");
    }

    @Test
    public void getAreas() {
        assertNotNull(field.getAreas(),
                "getAreas should not return null");

        assertEquals(0,
                field.getAreas().size(),
                "Field should initially be empty");

        field.addArea(areaSquare1);
        field.addArea(areaSquare2);

        assertNotNull(field.getAreas(),
                "getAreas should not return null");

        assertEquals(2,
                field.getAreas().size(),
                "getAreas should return all added areas");
    }

    @Test
    public void getAreasReturnsCorrectOrder() {
        field.addArea(areaSquare1);
        field.addArea(areaSquare2);

        assertEquals(areaSquare1,
                field.getAreas().get(0),
                "First area should be at index 0");

        assertEquals(areaSquare2,
                field.getAreas().get(1),
                "Second area should be at index 1");
    }

    @Test
    public void getName() {
        String name = field.getName();

        assertNotNull(name,
                "Name should not be null");

        assertEquals("Forest",
                name,
                "Name should be 'Forest'");
    }

    @Test
    public void setName() {
        field.setName("Mountain");

        assertEquals("Mountain",
                field.getName(),
                "Name should be updated to 'Mountain'");
    }

    @Test
    public void setNameMultipleTimes() {
        field.setName("Mountain");
        assertEquals("Mountain",
                field.getName(),
                "First name change should work");

        field.setName("Desert");
        assertEquals("Desert",
                field.getName(),
                "Second name change should work");
    }

    @Test
    public void fieldInitialization() {
        Field newField = new Field("Ocean");

        assertNotNull(newField,
                "Field should not be null");

        assertEquals("Ocean",
                newField.getName(),
                "Field name should be set correctly");

        assertNotNull(newField.getAreas(),
                "Areas list should be initialized");

        assertEquals(0,
                newField.getAreas().size(),
                "Areas list should be empty initially");
    }
}