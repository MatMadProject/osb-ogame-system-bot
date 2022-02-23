package app.data.shipyard;

import app.data.DataLoader;
import ogame.DataTechnology;
import ogame.defence.Defence;
import ogame.planets.Planet;

import java.io.Serializable;
import java.util.Objects;

public class DefenceItem extends ShipyardItem implements Serializable {
    private static final long serialVersionUID = 1992L;
    private Defence defence;

    public DefenceItem(Planet planet, Defence defence, int value, long timePeriod) {
        super(planet,value,timePeriod);

        this.defence = defence;
        setId(DataLoader.listDefenceItem.getId()+"");
    }

    public Defence getDefence() {
        return defence;
    }

    public void setDefence(Defence defence) {
        this.defence = defence;
    }



    public DefenceItem copy(){
        DefenceItem defenceItem = new DefenceItem(getPlanet(),defence,getValue(), getTimePeriodInSeconds());
        defenceItem.setId(getId());
        defenceItem.setStatus(getStatus());
        defenceItem.setStatusTimeInMilliseconds(getStatusTimeInMilliseconds());
        return defenceItem;
    }

    public boolean isShield(){
        return defence.getDataTechnology() == DataTechnology.SHIELD_DOME_SMALL
                || defence.getDataTechnology() == DataTechnology.SHIELD_DOME_LARGE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefenceItem)) return false;
        if (!super.equals(o)) return false;
        DefenceItem that = (DefenceItem) o;
        return Objects.equals(getDefence(), that.getDefence()) && Objects.equals(getPlanet(),that.getPlanet())
                && Objects.equals(getId(),that.getId());
    }



    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDefence());
    }

    @Override
    public String toString() {
        return "DefenceItem{" +
                "defence=" + defence +
                '}';
    }
}
