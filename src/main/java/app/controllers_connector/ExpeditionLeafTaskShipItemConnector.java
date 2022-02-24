package app.controllers_connector;

import app.controllers.ExpeditionLeafTaskShipItemController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ogame.ships.Ship;

import java.io.IOException;

public class ExpeditionLeafTaskShipItemConnector implements LeafTaskConnector{
    private ExpeditionLeafTaskShipItemController controller;
    private HBox content;
    private Ship ship;

    public ExpeditionLeafTaskShipItemConnector(Ship ship){
        this.ship = ship;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/expedition-leaftask-ship-item.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update(ship);
        }
        return content;
    }
}
