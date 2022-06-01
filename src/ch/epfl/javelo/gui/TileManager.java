package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Classe représentant un gestionnaire de tuiles, qui prend comme attributs un chemin vers le disque,
 * le nom du serveur ainsi qu'une mémoire cache.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 05/05/2022
 */
public final class TileManager {

    private static final int INITIAL_CAPACITY = 100;
    private final Path path;
    private final String nameOfTheServer;
    private final Map<TileId, Image> cacheMemory;

    /**
     * Enregistrement représentant l'identité d'une tuile, qui contient le niveau de zoom,
     * son index en x et son index en y.

     * @param zoom Le niveau de zoom d'une tuile
     * @param indexX L'index en x de la tuile.
     * @param indexY L'index de y de la tuile.
     */
    public record TileId(int zoom, int indexX, int indexY) {

        /**
         * Méthode nous indiquant si l'identité de la tuile est valide.
         *
         * @param zoom Le niveau de zoom appliqué.
         * @param indexX L'index x de la tuile.
         * @param indexY L'index y de la tuile.
         * @return Un booléen nous indiquant si une telle tuile existe (et est donc récupérable en ligne).
         */
        public static boolean isValid(int zoom, int indexX, int indexY) {
            return (indexX >= 0) && (indexY >= 0) && (indexX < Math.scalb(1,zoom)) && (indexY < Math.scalb(1,zoom));
        }
    }

    /**
     * Constructeur d'un gestionnaire de tuiles grâce à un chemin vers le disque ainsi qu'un nom de serveur.
     *
     * @param path Le chemin vers le disque.
     * @param nameOfTheServer Le nom du serveur.
     */
    public TileManager(Path path, String nameOfTheServer) {

        this.path = path;
        this.nameOfTheServer = nameOfTheServer;
        this.cacheMemory = new LinkedHashMap<>(INITIAL_CAPACITY, 0.75f, true);
    }

    /**
     * Méthode nous donnant l'image de la tuile d'identité donnée.
     *
     * @param tileId L'identité de la tuile dont on veut retourner l'image.
     * @return L'image de la tuile.
     */
    public Image imageForTileAt(TileId tileId) throws IOException {

        Image imageFinale = null;

        if (TileId.isValid(tileId.zoom(), tileId.indexX(), tileId.indexY())) {

            if (cacheMemory.containsKey(tileId)) {
                imageFinale = cacheMemory.get(tileId);

            } else {
                if (cacheMemory.size() >= 100) {
                    Iterator<TileId> iterator = cacheMemory.keySet().iterator();
                    cacheMemory.remove(iterator.next());
                }
                Path p = Path.of(this.path.toString())
                        .resolve(String.valueOf(tileId.zoom()))
                        .resolve(String.valueOf(tileId.indexX()))
                        .resolve(tileId.indexY() + ".png");
                if (Files.exists(p)) {
                    try (InputStream inputStream = Files.newInputStream(p)) {
                        Image image = new Image(inputStream);
                        cacheMemory.put(tileId, image);
                        imageFinale = image;
                    } catch (IOException ignored) {
                    }
                } else {
                    URL url = new URL("https://" + this.nameOfTheServer + "/" + tileId.zoom() + "/" +
                                tileId.indexX() + "/" + tileId.indexY() + ".png");
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty("User-Agent", "JaVelo");
                    try (InputStream i = connection.getInputStream()) {

                        Path pathDossier = Path.of(String.valueOf(tileId.zoom()))
                                .resolve(String.valueOf(tileId.indexX()));
                        Path pathImage = pathDossier.resolve(tileId.indexY() + ".png");
                        Files.createDirectories(pathDossier);

                        OutputStream o = new FileOutputStream(pathImage.toFile());
                        i.transferTo(o);
                        o.close();

                        InputStream inS = Files.newInputStream(pathImage);
                        Image image = new Image(inS);
                        inS.close();
                        cacheMemory.put(tileId, image);
                        imageFinale = image;
                    }
                }
            }
        }
        return imageFinale;
    }
}
