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
        setId(DataLoader.listShipItem.getId()+"");
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public ShipItem copy(){
        ShipItem shipItem = new ShipItem(getPlanet(),ship,getValue(), getTimePeriodInSeconds());
        shipItem.setId(getId());
        shipItem.setStatus(getStatus());
        shipItem.setStatusTimeInMilliseconds(getStatusTimeInMilliseconds());
        return shipItem;
    }

    public boolean isShipOgameStatusOff(){
        return ship.getStatus() == ogame.Status.OFF;
    }
    public boolean isShipOgameStatusOn(){
        return ship.getStatus() == ogame.Status.ON;
    }
    public boolean isShipOgameStatusActive(){
        return ship.getStatus() == ogame.Status.ACTIVE;
    }
    public boolean isShipOgameStatusUndefined(){
        return ship.getStatus() == ogame.Status.UNDEFINED;
    }
    public boolean isShipOgameStatusDisabled(){
        return ship.getStatus() == ogame.Status.DISABLED;
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
