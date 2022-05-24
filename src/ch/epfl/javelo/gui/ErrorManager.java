package ch.epfl.javelo.gui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Classe gérant l'affichage des erreurs d'utilisation à l'écran.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 24/05/2022
 */
public final class ErrorManager {
    private final Pane pane;
    /**
     * Constructeur d'ErrorManager.
     */
    public ErrorManager(){
        this.pane = new Pane();
        this.pane.setMouseTransparent(true);
        VBox vBoxErreurs = new VBox();
        vBoxErreurs.getStylesheets().add("error.css");
        this.pane.getChildren().add(vBoxErreurs);

        Text textError = new Text();
        vBoxErreurs.getChildren().add(textError);
    }

    /**
     * Accesseur du panneau affichant les messages d'erreurs
     *
     * @return Le panneau affichant les messages d'erreurs.
     */
    public Pane pane(){
        return this.pane;
    }

    public void displayError(String messageErreur){
        pane.setVisible(true);
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
