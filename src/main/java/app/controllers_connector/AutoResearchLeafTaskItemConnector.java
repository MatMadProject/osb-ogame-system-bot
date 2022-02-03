package app.controllers_connector;

import app.controllers.AutoResearchLeafTaskController;
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

    public AutoResearchLeafTaskItemConnector(ItemAutoResearch itemAutoResearch, AutoResearchLeafTaskController autoResearchLeafTaskController){
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
            controller.setItemAutoResearch(itemAutoResearch);
            controller.setAutoBuilderLeafTaskController(autoResearchLeafTaskController);
        }
    }

    @Override
    public Node content() {
        if(controller != null)
            controller.create(itemAutoResearch);

        return content;
    }

    public Node contentHistoryItem() {
        if(controller != null){
            controller.createHistoryItem(itemAutoResearch);
            controller.setHistoryItem();
        }
        return content;
    }


    public AutoResearchLeafTaskItemController getController() {
        return controller;
    }
}
