package nl.rsdt.japp.jotial.maps.kml;


import android.util.Xml;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.lang.System.in;


/**
 * Created by Mattijn on 11/10/2015.
 */
public class KmlReader {
    public KmlFile parse(InputStream file) throws XmlPullParserException, IOException {
        KmlFile kmlFile ;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            kmlFile = readKml(parser);
        } finally {
            in.close();
        }
        return kmlFile;
    }

    private KmlFile readKml(XmlPullParser parser)  throws XmlPullParserException, IOException  {
        KmlDocument document = new KmlDocument();
        String ns = null;
        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("Document")) {
                document = readDocument(parser);
            }
        }

        return document.toKmlFile();
    }

    private KmlDocument readDocument(XmlPullParser parser) throws XmlPullParserException, IOException{
        KmlDocument document = new KmlDocument();
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("name")) {
                document.setName(getTextAndGoToCloseTag(parser));
            }
            if(tagName.equals("Folder")){
                document.addFolder(readFolder(parser));
            }
            if(tagName.equals("Style")){
                document.addStyle(readStyle(parser));
            }
        }
        return document;
    }
    private KmlFolder readFolder(XmlPullParser parser) throws IOException, XmlPullParserException {
        KmlFolder folder = new KmlFolder();

        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("name")) {
                folder.setType(KmlFolder.Type.valueOf(getTextAndGoToCloseTag(parser)));
            }
            if (tagName.equals("PlaceMark")) {
                folder.addPlacemark(readPlaceMark(parser));
            }
        }

        return folder;
    }
    private KmlStyle readStyle(XmlPullParser parser) throws IOException, XmlPullParserException{
        KmlStyle kmlStyle = new KmlStyle();
        kmlStyle.setId(parser.getAttributeValue(0));
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("IconStyle")) {
                readIconStyle(parser, kmlStyle);
            }
            if (tagName.equals("BalloonStyle")) {
                readBalloonStyle(parser, kmlStyle);
            }
            if (tagName.equals("LineStyle")) {
                readLineStyle(parser, kmlStyle);
            }
            if (tagName.equals("PolyStyle")) {
                readPolyStyle(parser, kmlStyle);
            }
        }

        return kmlStyle;
    }
    private void readIconStyle(XmlPullParser parser, KmlStyle kmlStyle) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("color")) {
                kmlStyle.setIconColor(getTextAndGoToCloseTag(parser));
            }
            if (tagName.equals("scale")) {
                String scale = getTextAndGoToCloseTag(parser);
                kmlStyle.setIconScale(Double.valueOf(scale));
            }
            if (tagName.equals("Icon")) {
                while (parser.next() != XmlPullParser.END_TAG){
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    if (tagName.equals("href")){
                        kmlStyle.setIconUrl(getTextAndGoToCloseTag(parser));
                    }
                }
            }
        }
    }
    private void readBalloonStyle(XmlPullParser parser, KmlStyle kmlStyle) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("bgColor")) {
                kmlStyle.setBalloonBgColor(getTextAndGoToCloseTag(parser));
            }
            if (tagName.equals("text")) {
                kmlStyle.setBalloonText(getTextAndGoToCloseTag(parser));
            }
        }
    }
    private void readLineStyle(XmlPullParser parser, KmlStyle kmlStyle) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("color")) {
                kmlStyle.setLineColor(getTextAndGoToCloseTag(parser));
            }
            if (tagName.equals("width")) {
                String lineWidth= getTextAndGoToCloseTag(parser);
                kmlStyle.setLineWidth(Double.valueOf(lineWidth));
            }
        }
    }
    private void readPolyStyle(XmlPullParser parser, KmlStyle kmlStyle) throws IOException, XmlPullParserException {
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("color")) {
                kmlStyle.setPolyLineColor(getTextAndGoToCloseTag(parser));
            }
            if (tagName.equals("fill")) {
                String polylineFill = getTextAndGoToCloseTag(parser);
                kmlStyle.setPolyLineFill(Integer.valueOf(polylineFill));
            }
            if (tagName.equals("outline")) {
                String polyLineOutline = getTextAndGoToCloseTag(parser);
                kmlStyle.setPolyLineOutline(Integer.valueOf(polyLineOutline));
            }
        }
    }

    private KmlPlaceMark readPlaceMark(XmlPullParser parser) throws IOException, XmlPullParserException {
        KmlPlaceMark placeMark = new KmlPlaceMark();
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("name")) {
                placeMark.setName(getTextAndGoToCloseTag(parser));
            }
            if (tagName.equals("styleUrl")) {
                placeMark.setStyleUrl(getTextAndGoToCloseTag(parser));
            }
            if (tagName.equals("Point")){
                placeMark.setCoordinates(readPoint(parser));
            }
            if (tagName.equals("Polygon")){
                placeMark.setCoordinates(readPolygon(parser));
            }
        }
        return placeMark;
    }

    private List<KmlLocation> readPolygon(XmlPullParser parser) throws IOException, XmlPullParserException {
        String coordinates = "";
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("outerBoundaryIs")) {
                while (parser.next() != XmlPullParser.END_TAG){
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String tagNameinner = parser.getName();
                    if (tagNameinner.equals("LinearRing")) {
                        while (parser.next() != XmlPullParser.END_TAG){
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }
                            String tagNameinner2 = parser.getName();
                            if (tagNameinner2.equals("coordinates")) {
                                coordinates = getTextAndGoToCloseTag(parser);
                            }
                        }
                    }
                }
            }
        }
        return KmlLocation.readCoordinates(coordinates);
    }

    private List<KmlLocation> readPoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        String coordinates = "";
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("coordinates")) {
                coordinates = getTextAndGoToCloseTag(parser);
            }
        }
        return KmlLocation.readCoordinates(coordinates);
    }

    private String getTextAndGoToCloseTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() == XmlPullParser.TEXT) {
                text = parser.getText();
            }
        }
        return text;
    }

}
