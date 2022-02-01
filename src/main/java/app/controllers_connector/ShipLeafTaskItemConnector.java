package app.controllers_connector;

import app.controllers.ShipLeafTaskController;
import app.controllers.ShipLeafTaskItemController;
import app.data.shipyard.ShipItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ShipLeafTaskItemConnector implements LeafTaskConnector {
    private ShipLeafTaskItemController controller;
    private HBox content;

    public ShipLeafTaskItemConnector(ShipItem shipItem, ShipLeafTaskController shipLeafTaskController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ship-leaftask-item.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {
            controller.setShipLeafTaskController(shipLeafTaskController);
            controller.setShipItem(shipItem);
        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update();
        }
        return content;
    }

    public ShipLeafTaskItemController getController() {
        return controller;
    }
}
