package ch.epfl.javelo.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Classe gérant l'affichage des erreurs d'utilisation à l'écran.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 24/05/2022
 */
public final class ErrorManager {
    private static final double PRESQUE_OPAQUE = 0.8;
    private static final int DUREE_PAUSE = 2;
    private static final int DUREE_APPARITION = 200;
    private static final int OPPACITE_TRANSPARENTE = 0;
    private static final int DUREE_DISPARITION = 500;
    private final VBox vBoxErreurs;
    private final Text texteErreur;
    private final Animation transition;

    /**
     * Constructeur d'ErrorManager.
     */
    public ErrorManager(){

        this.vBoxErreurs = new VBox();
        vBoxErreurs.setMouseTransparent(true);
        vBoxErreurs.getStylesheets().add("error.css");

        FadeTransition ft1 = new FadeTransition(Duration.millis(DUREE_APPARITION), vBoxErreurs);
        ft1.setFromValue(OPPACITE_TRANSPARENTE);

        ft1.setToValue(PRESQUE_OPAQUE);
        PauseTransition pause2sec = new PauseTransition(Duration.seconds(DUREE_PAUSE));
        FadeTransition ft2 = new FadeTransition(Duration.millis(DUREE_DISPARITION), vBoxErreurs);
        ft2.setFromValue(PRESQUE_OPAQUE);
        ft2.setToValue(OPPACITE_TRANSPARENTE);

        this.transition = new SequentialTransition(ft1, pause2sec, ft2);

        this.texteErreur = new Text();
        vBoxErreurs.getChildren().add(texteErreur);
    }

    /**
     * Accesseur du panneau affichant les messages d'erreurs
     *
     * @return Le panneau affichant les messages d'erreurs.
     */
    public Pane pane(){
        return this.vBoxErreurs;
    }

    /**
     * Affiche une erreur à l'écran, gère la transition
     *
     * @param messageErreur Le message d'erreur à afficher à l'écran.
     */
    public void displayError(String messageErreur){
        vBoxErreurs.setVisible(true);
        java.awt.Toolkit.getDefaultToolkit().beep();

        this.texteErreur.setText(messageErreur);

        if (transition.getStatus() == Animation.Status.RUNNING){
            transition.stop();
        }
        transition.play();

    }
}