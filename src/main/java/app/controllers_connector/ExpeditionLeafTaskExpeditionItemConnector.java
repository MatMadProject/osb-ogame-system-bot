package app.controllers_connector;

import app.controllers.ExpeditionLeafTaskController;
import app.controllers.ExpeditionLeafTaskExpeditionItemController;
import app.data.expedition.Expedition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ExpeditionLeafTaskExpeditionItemConnector implements LeafTaskConnector{
    private ExpeditionLeafTaskExpeditionItemController controller;
    private HBox content;

    public ExpeditionLeafTaskExpeditionItemConnector(Expedition expedition, ExpeditionLeafTaskController expeditionLeafTaskController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/expedition-leaftask-expedition-item.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {
            controller.setExpeditionLeafTaskController(expeditionLeafTaskController);
            controller.setExpedition(expedition);
        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update();
        }
        return content;
    }

    public ExpeditionLeafTaskExpeditionItemController getController() {
        return controller;
    }
}
