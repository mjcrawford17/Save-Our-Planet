package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a field (group) of {@link AreaSquare}s on the Save Our Planet game board.
 * <p>
 * A field groups related area squares together thematically (e.g. Renewable Energy,
 * Waste Reduction). A player must own all areas within a field before they are
 * eligible to invest in efficiency upgrades on those squares.
 * </p>
 */
public class Field {
    private String name;
    private List<AreaSquare> areas;

    /**
     * Constructs a Field with the given name and an empty list of areas.
     *
     * @param name the display name of this field (e.g. "Renewable Energy")
     */
    public Field(String name) {
        this.name = name;
        this.areas = new ArrayList<>();
    }

    /**
     * Adds an {@link AreaSquare} to this field and sets the square's field reference
     * back to this field.
     *
     * @param area the area square to add to this field
     */
    public void addArea(AreaSquare area) {
        areas.add(area);
        area.setField(this);
    }

    /**
     * Returns the list of area squares belonging to this field.
     *
     * @return list of {@link AreaSquare}s in this field
     */
    public List<AreaSquare> getAreas() {
        return areas;
    }

    /**
     * Returns the display name of this field.
     *
     * @return the field's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of this field.
     *
     * @param name the new name to assign
     */
    public void setName(String name) {
        this.name = name;
    }
}
