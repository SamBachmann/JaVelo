package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class TestErrorManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ErrorManager errorManager = new ErrorManager();
        //errorManager.displayError("UNE ERREURE");
        var pane =  errorManager.pane();
        pane.setOnMouseMoved(e-> errorManager.displayError("bouge"));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(pane));
        primaryStage.setTitle("test1");
        primaryStage.show();
    }
}
