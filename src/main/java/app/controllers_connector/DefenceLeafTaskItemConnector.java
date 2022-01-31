package app.controllers_connector;

import app.controllers.DefenceLeafTaskController;
import app.controllers.DefenceLeafTaskItemController;
import app.data.shipyard.DefenceItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class DefenceLeafTaskItemConnector implements LeafTaskConnector {
    private DefenceLeafTaskItemController controller;
    private HBox content;

    public DefenceLeafTaskItemConnector(DefenceItem defence, DefenceLeafTaskController defenceLeafTaskController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/defence-leaftask-item.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {
            controller.setDefenceLeafTaskController(defenceLeafTaskController);
            controller.setDefence(defence);
        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update();
        }
        return content;
    }

    public DefenceLeafTaskItemController getController() {
        return controller;
    }
}
