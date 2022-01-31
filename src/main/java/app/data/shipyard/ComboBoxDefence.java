package app.data.shipyard;

import ogame.DataTechnology;
import ogame.Type;
import ogame.defence.Defence;

import java.util.ArrayList;

public class ComboBoxDefence {
    private final Defence DEFENCE;
    private final String VALUE;

    public ComboBoxDefence(Defence defence) {
        this.DEFENCE = defence;
        this.VALUE = defence.getName();
    }

    public Defence getDefence() {
        return DEFENCE;
    }
    public static ArrayList<ComboBoxDefence> list(){
        ArrayList<ComboBoxDefence> list = new ArrayList<>();
        ArrayList<DataTechnology> defence = DataTechnology.dataTechnologyList(Type.DEFENCE);
        for(DataTechnology dataTechnology : defence)
            list.add(new ComboBoxDefence(new Defence(dataTechnology)));

        return list;
    }
    @Override
    public String toString() {
        return VALUE;
    }
}
