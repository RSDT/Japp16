package nl.rsdt.japp.jotial.maps.kml;


import android.util.Xml;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.structures.area348.MetaInfo;
import nl.rsdt.japp.jotial.net.apis.MetaApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.in;


/**
 * Created by Mattijn on 11/10/2015.
 */
public class KmlReader implements Callback<MetaInfo> {

    public static void parseFromMeta(KmlReader.Callback callback) {
        final MetaApi metaApi = Japp.getApi(MetaApi.class);
        metaApi.getMetaInfo(JappPreferences.getAccountKey()).enqueue(new retrofit2.Callback<MetaInfo>() {
            @Override
            public void onResponse(Call<MetaInfo> call, Response<MetaInfo> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    Request req = new Request.Builder().url(response.body().KML_URL).build();
                    OkHttpClient client = new OkHttpClient.Builder()
                            .build();
                    client.newCall(req).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            callback.onException(e);
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    assert response.body() != null;
                                    KmlFile file = parse(response.body().byteStream());
                                    callback.onSucces(file);
                                } catch (XmlPullParserException e) {
                                    e.printStackTrace();
                                    callback.onException(e);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    callback.onException(e);
                                }
                            } else {
                                callback.onException(new Exception(response.body().string()));
                            }
                        }
                    });
                } else{
                    try {
                        callback.onException(new Exception(response.errorBody().string()));
                    } catch (IOException e) {
                        callback.onException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<MetaInfo> call, Throwable t) {

            }
        });
    }

    public static KmlFile parse(InputStream in) throws XmlPullParserException, IOException {
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

    private static KmlFile readKml(XmlPullParser parser)  throws XmlPullParserException, IOException  {
        KmlDocument document = new KmlDocument();
        String ns = null;
        parser.require(XmlPullParser.START_TAG, ns, "kml");
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

    private static KmlDocument readDocument(XmlPullParser parser) throws XmlPullParserException, IOException{
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
    private static KmlFolder readFolder(XmlPullParser parser) throws IOException, XmlPullParserException {
        KmlFolder folder = new KmlFolder();

        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("name")) {
                folder.setType(KmlFolder.Type.parse(getTextAndGoToCloseTag(parser)));
            }
            if (tagName.equals("Placemark")) {
                folder.addPlacemark(readPlaceMark(parser));
            }
        }

        return folder;
    }
    private static KmlStyle readStyle(XmlPullParser parser) throws IOException, XmlPullParserException{
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
    private static void readIconStyle(XmlPullParser parser, KmlStyle kmlStyle) throws IOException, XmlPullParserException {
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
                    if (parser.getName().equals("href")){
                        kmlStyle.setIconUrl(getTextAndGoToCloseTag(parser));
                    }
                }
            }
        }
    }
    private static void readBalloonStyle(XmlPullParser parser, KmlStyle kmlStyle) throws IOException, XmlPullParserException {
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
    private static void readLineStyle(XmlPullParser parser, KmlStyle kmlStyle) throws IOException, XmlPullParserException {
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
    private static void readPolyStyle(XmlPullParser parser, KmlStyle kmlStyle) throws IOException, XmlPullParserException {
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

    private static KmlPlaceMark readPlaceMark(XmlPullParser parser) throws IOException, XmlPullParserException {
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

    private static List<KmlLocation> readPolygon(XmlPullParser parser) throws IOException, XmlPullParserException {
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

    private static List<KmlLocation> readPoint(XmlPullParser parser) throws IOException, XmlPullParserException {
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

    private static String getTextAndGoToCloseTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() == XmlPullParser.TEXT) {
                text = parser.getText();
            }
        }
        return text;
    }

    @Override
    public void onResponse(Call<MetaInfo> call, Response<MetaInfo> response) {
        assert response.body() != null;

    }

    @Override
    public void onFailure(Call<MetaInfo> call, Throwable t) {

    }
    public interface Callback{
        void onException(Throwable e);
        void onSucces(KmlFile kml);
    }
}
