package app.data.shipyard;

import app.data.DataLoader;
import ogame.planets.Planet;
import ogame.ships.Ship;

import java.io.Serializable;
import java.util.Objects;

public class ShipItem extends ShipyardItem implements Serializable {
    private static final long serialVersionUID = 1992L;
    private Ship ship;

    public ShipItem(Planet planet, Ship ship, int value, long timePeriod) {
        super(planet, value, timePeriod);
        this.ship = ship;
        setStatus(Status.ADDED);
        setId(DataLoader.listShipItem.getId()+"");
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public ShipItem copy(){
        ShipItem shipItem = new ShipItem(getPlanet(),ship,getValue(),getTimePeriod());
        shipItem.setId(getId());
        shipItem.setStatus(getStatus());
        shipItem.setStatusTime(getStatusTime());
        return shipItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShipItem)) return false;
        if (!super.equals(o)) return false;
        ShipItem shipItem = (ShipItem) o;
        return Objects.equals(getShip(), shipItem.getShip()) && Objects.equals(getPlanet(),shipItem.getPlanet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getShip());
    }

    @Override
    public String toString() {
        return "ShipItem{" +
                "ship=" + ship +
                '}';
    }
}
