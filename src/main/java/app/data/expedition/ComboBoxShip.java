package app.data.expedition;

import ogame.DataTechnology;
import ogame.Type;
import ogame.ships.Ship;

import java.util.ArrayList;

public class ComboBoxShip {

    final Ship SHIP;
    final String VALUE;

    public ComboBoxShip(Ship ship) {
        this.SHIP = ship;
        this.VALUE = ship.getName();
    }

    public Ship getShip() {
        return SHIP;
    }

    public static ArrayList<ComboBoxShip> list (){
        ArrayList<ComboBoxShip> list = new ArrayList<>();
        ArrayList<DataTechnology> civil = DataTechnology.dataTechnologyList(Type.CIVIL);
        ArrayList<DataTechnology> battle = DataTechnology.dataTechnologyList(Type.BATTLE);

        for(DataTechnology ship : civil){
            if(ship == DataTechnology.SOLAR_SATELITE || ship == DataTechnology.RESBUGGY || ship == DataTechnology.UNDEFINED)
                continue;
            list.add(new ComboBoxShip(new Ship(ship)));
        }

        for(DataTechnology ship : battle)
            if(ship != DataTechnology.UNDEFINED)
                list.add(new ComboBoxShip(new Ship(ship)));

        return list;
    }
    public static ArrayList<ComboBoxShip> allShips (){
        ArrayList<ComboBoxShip> list = new ArrayList<>();
        ArrayList<DataTechnology> civil = DataTechnology.dataTechnologyList(Type.CIVIL);
        ArrayList<DataTechnology> battle = DataTechnology.dataTechnologyList(Type.BATTLE);

        for(DataTechnology ship : civil){
            if(ship == DataTechnology.UNDEFINED)
                continue;
            list.add(new ComboBoxShip(new Ship(ship)));
        }

        for(DataTechnology ship : battle)
            if(ship != DataTechnology.UNDEFINED)
                list.add(new ComboBoxShip(new Ship(ship)));

        return list;
    }

    @Override
    public String toString() {
        return VALUE;
    }
}
