package app.controllers_connector;

import app.controllers.AutoBuilderLeafTaskItemController;
import app.data.autobuilder.ItemAutoBuilder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javafx.scene.layout.HBox;

import java.io.IOException;

public class AutoBuilderLeafTaskItemConnector implements LeafTaskConnector{

    private AutoBuilderLeafTaskItemController controller;
    private HBox content;
    private ItemAutoBuilder itemAutoBuilder;

    public AutoBuilderLeafTaskItemConnector(ItemAutoBuilder itemAutoBuilder){
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

        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update(itemAutoBuilder);
        }
        return content;
    }
}
