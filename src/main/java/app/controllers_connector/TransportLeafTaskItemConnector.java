package app.controllers_connector;

import app.controllers.TransportLeafTaskController;
import app.controllers.TransportLeafTaskItemController;
import app.data.transport.TransportItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TransportLeafTaskItemConnector implements LeafTaskConnector{
    private TransportLeafTaskItemController controller;
    private HBox content;

    public TransportLeafTaskItemConnector(TransportItem transportItem, TransportLeafTaskController transportLeafTaskController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/transport-leaftask-item.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {
            controller.setTransportLeafTaskController(transportLeafTaskController);
            controller.setTransportItem(transportItem);
        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update();
        }
        return content;
    }

    public TransportLeafTaskItemController getController() {
        return controller;
    }
}
