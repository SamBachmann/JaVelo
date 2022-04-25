package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Classe générant des fichiers GPX pour représenter l'itinéraire.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 25/04/2022
 */
public class GpxGenerator {
    //Constructeur privé puisque la classe est non-instanciable.
    private GpxGenerator(){}

    public static Document createGpx (Route itineraire, ElevationProfile profileItineraire ){

        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element route = doc.createElement("rte");

        double longueur = 0.0;
        Iterator<Edge> edgeIterator = itineraire.edges().iterator();

        for (PointCh point : itineraire.points()){

            Element routePt = doc.createElement("rtept");
            routePt.setAttribute("lat", String.format("%.5f", point.lat()));
            routePt.setAttribute("lon", String.valueOf(point.lon()));//changer le format

            double localElevation = profileItineraire.elevationAt(longueur);
            Element altitude = doc.createElement("ele");
            altitude.setTextContent(String.valueOf(localElevation));
            routePt.appendChild(altitude);

            route.appendChild(routePt);

            if (edgeIterator.hasNext()) {
                longueur += edgeIterator.next().length();
            }
        }

        root.appendChild(route);
        return doc;
    }

    public static void writeGpx (Document d) throws IOException {}

    /**
     * Méthode privée créant un document GPX, utilisée dans createGpx.
     *
     * @return La base d'un nouveau document.
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Cas ne devant normalement jamais se produire.
        }
    }

}
