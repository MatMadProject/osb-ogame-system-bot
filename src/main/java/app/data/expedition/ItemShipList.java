package app.data.expedition;

import ogame.ships.Ship;

import java.io.Serializable;
import java.util.Objects;

public class ItemShipList implements Serializable {
    private static final long serialVersionUID = 1992L;
    private final Ship ship;
    private final int value;

    public ItemShipList(Ship ship, int value) {
        this.ship = ship;
        this.value = value;
    }

    public Ship getShip() {
        return ship;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemShipList that = (ItemShipList) o;
        return Objects.equals(ship, that.ship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ship);
    }

    @Override
    public String toString() {
        return "ItemShipList{" +
                "ship=" + ship +
                ", value=" + value +
                '}';
    }
}
