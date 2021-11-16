package app.controllers_connector;

import app.controllers.AutoResearchLeafTaskItemController;
import app.data.autoresearch.ItemAutoResearch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class AutoResearchLeafTaskItemConnector implements LeafTaskConnector{

    private AutoResearchLeafTaskItemController controller;
    private HBox content;
    private final ItemAutoResearch itemAutoResearch;

    public AutoResearchLeafTaskItemConnector(ItemAutoResearch itemAutoResearch){
        this.itemAutoResearch = itemAutoResearch;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/auto-research-leaftask-item.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {

        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update(itemAutoResearch);
        }
        return content;
    }
}
