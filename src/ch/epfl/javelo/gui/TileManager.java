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


public final class TileManager {

    private final Path path;
    private final String nameOfTheServer;
    private final Map<TileId, Image> cacheMemory;

    public record TileId(int zoom, int indexX, int indexY) {

        public static boolean isValid(int zoom, int indexX, int indexY) {
            return (indexX >= 0) && (indexY >= 0) && (indexX <= Math.pow(2, zoom)) && (indexY <= Math.pow(2, zoom));
            // utilisation possible de Math.scalb()
        }
    }

    public TileManager(Path path, String nameOfTheServer) {

        this.path = path;
        this.nameOfTheServer = nameOfTheServer;
        this.cacheMemory = new LinkedHashMap<>(100, 0.75f, true);

    }

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
                    } catch (IOException e) {
                            System.out.println("IOException");
                    }
                } else {
                    URL u = new URL("https://" + this.nameOfTheServer + "/" + tileId.zoom() + "/" +
                                tileId.indexX() + "/" + tileId.indexY() + ".png");
                    URLConnection c = u.openConnection();
                    c.setRequestProperty("User-Agent", "JaVelo");
                    try (InputStream i = c.getInputStream()) {

                        Path pathDossier = Path.of(String.valueOf(tileId.zoom()))
                                .resolve(String.valueOf(tileId.indexX()));
                        Path pathImage = pathDossier.resolve(tileId.indexY() + ".png");
                        Files.createDirectories(pathDossier);

                        OutputStream o = new FileOutputStream(pathImage.toFile());
                        i.transferTo(o);
                        o.close();

                        Image image = new Image(i);
                        cacheMemory.put(tileId, image);
                        imageFinale = image;
                    }
                }
            }
        }
        return imageFinale;
    }
}
