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
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileManager {

    Path path;
    String nameOfTheServer;
    Map<TileId, Image> cacheMemory;

    public record TileId(int zoom, int indexX, int indexY) {

        public static boolean isValid(int zoom, int indexX, int indexY) {
            return (indexX >= 0) && (indexY >= 0) && (indexX <= Math.pow(2, zoom)) && (indexY <= Math.pow(2, zoom));
            // utilisation possible de Math.scalb()
        }

    }

    public TileManager(Path path, String nameOfTheServer) {

        this.path = path;
        this.nameOfTheServer = nameOfTheServer;

        cacheMemory = new LinkedHashMap<>(100, 0.75f, true);

    }

    public Image imageForTileAt(TileId tileId) {

        Image imageFinale = null;

            if (TileId.isValid(tileId.zoom(), tileId.indexX(), tileId.indexY())) {

                if (cacheMemory.containsKey(tileId)) {
                    imageFinale = cacheMemory.get(tileId);
                } else {
                    Path p = Path.of(this.path.toString()).resolve(String.valueOf(tileId.zoom()))
                            .resolve(String.valueOf(tileId.indexX())).resolve(String.valueOf(tileId.indexY()) + ".png");
                    if (Files.exists(p)) {
                        try {
                            InputStream inputStream = Files.newInputStream(p);
                            Image image = new Image(inputStream);
                            cacheMemory.put(tileId, image);
                            imageFinale = image;
                        } catch (IOException e) {
                            System.out.println("IOException");
                        }
                    } else {
                        try {
                            URL u = new URL("https://" + this.nameOfTheServer + "/" + tileId.zoom() + "/" +
                                    tileId.indexX() + "/" + tileId.indexY() + ".png");
                            URLConnection c = u.openConnection();
                            c.setRequestProperty("User-Agent", "JaVelo");
                            InputStream i = c.getInputStream();
                            //i.close();

                            Path pathDossier = Path.of(tileId.zoom() + "/" + tileId.indexX());
                            Path pathImage = Path.of(tileId.zoom() + "/" + tileId.indexX() + "/" + tileId.indexY() + ".png");
                            Files.createDirectories(pathDossier);

                            OutputStream o = new FileOutputStream(pathImage.toFile());
                            i.transferTo(o);

                            InputStream inputStream = Files.newInputStream(pathImage);
                            Image image = new Image(inputStream);
                            cacheMemory.put(tileId, image);

                            imageFinale = image;

                        } catch (IOException e) {
                            System.out.println("IOException");
                        }
                    }
                }
            }
        return imageFinale;
    }
}
