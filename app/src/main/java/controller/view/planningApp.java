package controller.view;

import controller.general.Controller;
import controller.general.ControllerImpl;
import daw.manager.Manager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class planningApp extends Application {

    final static Controller controller = new ControllerImpl();

    public static Controller getController() {
        return controller;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("view/planningView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }
}
