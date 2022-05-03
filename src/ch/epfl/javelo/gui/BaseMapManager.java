package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public final class BaseMapManager extends Application {


    TileManager tileManager;
    WaypointsManager waypointsManager;
    ObjectProperty<MapViewParameters> property;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property) {

        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.property = property;

    }


      public static void main(String[] args) {
      launch(args);
      }





    @Override
    public void start(Stage primaryStage) throws Exception {

/*
        Label nameL = new Label("Nom :");
        TextField nameF = new TextField();

        Label pwL = new Label("Mot de passe :");
        TextField pwF = new TextField();

        Button connectB = new Button("Connexion");

        GridPane grid = new GridPane();

        grid.addRow(0, nameL, nameF);
        grid.addRow(1, pwL, pwF);
        grid.add(connectB, 0, 2, 2, 1);

        GridPane.setHalignment(connectB, HPos.CENTER);

        // Scene scene = new Scene(grid);

        Pane pane = new StackPane(grid);
        Pane pane1 = new StackPane(pane);

        primaryStage.setScene(new Scene(pane1));
        primaryStage.setTitle("1er programme");
        primaryStage.show();

**/


        //public Pane pane() {

        Canvas canvas = new Canvas();
        Pane pane = new Pane();

        pane.getChildren().add(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        int zoom = this.property.get().zoom();
        int indexXhautGauche = (int) this.property.get().xHautGauche();
        int indexYhautGauche = (int) this.property.get().yHautGauche();


        for (int i = 0; i < canvas.heightProperty().intValue(); i = i + 256) {
            for (int j = 0; j < canvas.widthProperty().intValue(); j = j + 256) {

                TileManager.TileId tileId = new TileManager.TileId(zoom, indexXhautGauche + j, indexYhautGauche + i);
                Image image = this.tileManager.imageForTileAt(tileId);

                GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
                graphicsContext.drawImage(image, (double) j, (double) i);

            }
        }


        primaryStage.setScene(new Scene(canvas.getParent()));
        primaryStage.setTitle("Carte");
        primaryStage.show();
    }
}

