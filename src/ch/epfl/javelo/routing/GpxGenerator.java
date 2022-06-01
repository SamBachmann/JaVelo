package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Locale;

/**
 * Classe générant des fichiers GPX pour représenter l'itinéraire.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 25/04/2022
 */
public final class GpxGenerator {

    //Constructeur privé puisque la classe est non-instanciable.
    private GpxGenerator(){}

    /**
     * Crée le document GPX décrivant un itinéraire, à l'aide d'une route et d'un profil.
     *
     * @param itineraire L'itinéraire dont on veut le document GPX.
     * @param profileItineraire Le profil de cet itinéraire.
     * @return Le document au format GPX créé.
     */
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
        root.appendChild(route);

        double longueur = 0.0;
        Iterator<Edge> edgeIterator = itineraire.edges().iterator();

        for (PointCh point : itineraire.points()){

            Element routePt = doc.createElement("rtept");
            routePt.setAttribute("lat", String.format(
                    Locale.ROOT,
                    "%.5f",
                    Math.toDegrees(point.lat())));
            routePt.setAttribute("lon", String.format(
                    Locale.ROOT,
                    "%.5f",
                    Math.toDegrees(point.lon())));

            double localElevation = profileItineraire.elevationAt(longueur);
            Element altitude = doc.createElement("ele");
            altitude.setTextContent(String.format(
                    Locale.ROOT,
                    "%.2f",
                    localElevation));
            routePt.appendChild(altitude);

            route.appendChild(routePt);

            if (edgeIterator.hasNext()) {
                longueur += edgeIterator.next().length();
            }
        }

        return doc;
    }

    public static void writeGpx (String fileName, Route itineraire, ElevationProfile profileItineraire)
            throws IOException {

        Document doc = createGpx(itineraire, profileItineraire);
        Writer w = new FileWriter(fileName);

        try{
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e);
        }

    }

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