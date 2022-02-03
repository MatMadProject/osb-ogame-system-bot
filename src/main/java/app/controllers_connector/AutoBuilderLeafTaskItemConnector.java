package app.controllers_connector;

import app.controllers.AutoBuilderLeafTaskController;
import app.controllers.AutoBuilderLeafTaskItemController;
import app.data.autobuilder.ItemAutoBuilder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javafx.scene.layout.HBox;

import java.io.IOException;

public class AutoBuilderLeafTaskItemConnector implements LeafTaskConnector{

    private AutoBuilderLeafTaskItemController controller;
    private HBox content;
    private final ItemAutoBuilder itemAutoBuilder;

    public AutoBuilderLeafTaskItemConnector(ItemAutoBuilder itemAutoBuilder, AutoBuilderLeafTaskController autoBuilderLeafTaskController){
        this.itemAutoBuilder = itemAutoBuilder;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/auto-builder-leaftask-item.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {
            controller.setAutoBuilderLeafTaskController(autoBuilderLeafTaskController);
            controller.setItemAutoBuilder(itemAutoBuilder);
        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.create(itemAutoBuilder);
        }
        return content;
    }


    public Node contentHistoryItem() {
        if(controller != null){
            controller.createHistoryItem(itemAutoBuilder);
            controller.setHistoryItem();
        }
        return content;
    }

    public AutoBuilderLeafTaskItemController getController() {
        return controller;
    }
}
