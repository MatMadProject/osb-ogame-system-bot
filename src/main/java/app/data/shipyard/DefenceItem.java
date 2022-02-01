package app.data.shipyard;

import app.data.DataLoader;
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
        setStatus(Status.ADDED);
        setId(DataLoader.listDefenceItem.getId()+"");
    }

    public Defence getDefence() {
        return defence;
    }

    public void setDefence(Defence defence) {
        this.defence = defence;
    }



    public DefenceItem copy(){
        DefenceItem defenceItem = new DefenceItem(getPlanet(),defence,getValue(),getTimePeriod());
        defenceItem.setId(getId());
        defenceItem.setStatus(getStatus());
        defenceItem.setStatusTime(getStatusTime());
        return defenceItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefenceItem)) return false;
        if (!super.equals(o)) return false;
        DefenceItem that = (DefenceItem) o;
        return Objects.equals(getDefence(), that.getDefence()) && Objects.equals(getPlanet(),that.getPlanet());
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
