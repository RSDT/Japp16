package nl.rsdt.japp.jotial.maps.kml


import android.util.Xml
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.structures.area348.MetaInfo
import nl.rsdt.japp.jotial.net.apis.MetaApi
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream


/**
 * Created by Mattijn on 11/10/2015.
 */
class KmlReader : Callback<MetaInfo> {

    override fun onResponse(call: Call<MetaInfo>, response: Response<MetaInfo>) {
        assert(response.body() != null)

    }

    override fun onFailure(call: Call<MetaInfo>, t: Throwable) {

    }

    interface Callback {
        fun onException(e: Throwable)
        fun onSucces(kml: KmlFile)
    }

    companion object {

        fun parseFromMeta(callback: KmlReader.Callback) {
            val metaApi = Japp.getApi(MetaApi::class.java)
            metaApi.getMetaInfo(JappPreferences.accountKey).enqueue(object : retrofit2.Callback<MetaInfo> {
                override fun onResponse(call: Call<MetaInfo>, response: Response<MetaInfo>) {
                    assert(response.body() != null)
                    if (response.isSuccessful) {
                        val req = Request.Builder().url(response.body()!!.KML_URL!!).build()
                        val client = OkHttpClient.Builder()
                                .build()
                        client.newCall(req).enqueue(object : okhttp3.Callback {
                            override fun onFailure(call: okhttp3.Call, e: IOException) {
                                callback.onException(e)
                            }

                            @Throws(IOException::class)
                            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                                if (response.isSuccessful) {
                                    try {
                                        assert(response.body() != null)
                                        val file = parse(response.body()!!.byteStream())
                                        callback.onSucces(file)
                                    } catch (e: XmlPullParserException) {
                                        e.printStackTrace()
                                        callback.onException(e)
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        callback.onException(e)
                                    }

                                } else {
                                    callback.onException(Exception(response.body()!!.string()))
                                }
                            }
                        })
                    } else {
                        try {
                            callback.onException(Exception(response.errorBody()!!.string()))
                        } catch (e: IOException) {
                            callback.onException(e)
                        }

                    }
                }

                override fun onFailure(call: Call<MetaInfo>, t: Throwable) {

                }
            })
        }

        @Throws(XmlPullParserException::class, IOException::class)
        fun parse(`in`: InputStream): KmlFile {
            val kmlFile: KmlFile
            try {
                val parser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(`in`, null)
                parser.nextTag()
                kmlFile = readKml(parser)
            } finally {
                `in`.close()
            }
            return kmlFile
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun readKml(parser: XmlPullParser): KmlFile {
            var document = KmlDocument()
            val ns: String? = null
            parser.require(XmlPullParser.START_TAG, ns, "kml")
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "Document") {
                    document = readDocument(parser)
                }
            }

            return document.toKmlFile()
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun readDocument(parser: XmlPullParser): KmlDocument {
            val document = KmlDocument()
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "name") {
                    document.setName(getTextAndGoToCloseTag(parser))
                }
                if (tagName == "Folder") {
                    document.addFolder(readFolder(parser))
                }
                if (tagName == "Style") {
                    document.addStyle(readStyle(parser))
                }
            }
            return document
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readFolder(parser: XmlPullParser): KmlFolder {
            val folder = KmlFolder()

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "name") {
                    folder.type = KmlFolder.Type.parse(getTextAndGoToCloseTag(parser))
                }
                if (tagName == "Placemark") {
                    folder.addPlacemark(readPlaceMark(parser))
                }
            }

            return folder
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readStyle(parser: XmlPullParser): KmlStyle {
            val kmlStyle = KmlStyle()
            kmlStyle.setId(parser.getAttributeValue(0))
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "IconStyle") {
                    readIconStyle(parser, kmlStyle)
                }
                if (tagName == "BalloonStyle") {
                    readBalloonStyle(parser, kmlStyle)
                }
                if (tagName == "LineStyle") {
                    readLineStyle(parser, kmlStyle)
                }
                if (tagName == "PolyStyle") {
                    readPolyStyle(parser, kmlStyle)
                }
            }

            return kmlStyle
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readIconStyle(parser: XmlPullParser, kmlStyle: KmlStyle) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "color") {
                    kmlStyle.setIconColor(getTextAndGoToCloseTag(parser))
                }
                if (tagName == "scale") {
                    val scale = getTextAndGoToCloseTag(parser)
                    kmlStyle.setIconScale(java.lang.Double.valueOf(scale))
                }
                if (tagName == "Icon") {
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.eventType != XmlPullParser.START_TAG) {
                            continue
                        }
                        if (parser.name == "href") {
                            kmlStyle.setIconUrl(getTextAndGoToCloseTag(parser))
                        }
                    }
                }
            }
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readBalloonStyle(parser: XmlPullParser, kmlStyle: KmlStyle) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "bgColor") {
                    kmlStyle.setBalloonBgColor(getTextAndGoToCloseTag(parser))
                }
                if (tagName == "text") {
                    kmlStyle.setBalloonText(getTextAndGoToCloseTag(parser))
                }
            }
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readLineStyle(parser: XmlPullParser, kmlStyle: KmlStyle) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "color") {
                    kmlStyle.setLineColor(getTextAndGoToCloseTag(parser))
                }
                if (tagName == "width") {
                    val lineWidth = getTextAndGoToCloseTag(parser)
                    kmlStyle.setLineWidth(java.lang.Double.valueOf(lineWidth))
                }
            }
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readPolyStyle(parser: XmlPullParser, kmlStyle: KmlStyle) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "color") {
                    kmlStyle.setPolyLineColor(getTextAndGoToCloseTag(parser))
                }
                if (tagName == "fill") {
                    val polylineFill = getTextAndGoToCloseTag(parser)
                    kmlStyle.setPolyLineFill(Integer.valueOf(polylineFill))
                }
                if (tagName == "outline") {
                    val polyLineOutline = getTextAndGoToCloseTag(parser)
                    kmlStyle.setPolyLineOutline(Integer.valueOf(polyLineOutline))
                }
            }
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readPlaceMark(parser: XmlPullParser): KmlPlaceMark {
            val placeMark = KmlPlaceMark()
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "name") {
                    placeMark.name = getTextAndGoToCloseTag(parser)
                }
                if (tagName == "styleUrl") {
                    placeMark.setStyleUrl(getTextAndGoToCloseTag(parser))
                }
                if (tagName == "Point") {
                    placeMark.setCoordinates(readPoint(parser))
                }
                if (tagName == "Polygon") {
                    placeMark.setCoordinates(readPolygon(parser))
                }
            }
            return placeMark
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readPolygon(parser: XmlPullParser): List<KmlLocation> {
            var coordinates = ""
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "outerBoundaryIs") {
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.eventType != XmlPullParser.START_TAG) {
                            continue
                        }
                        val tagNameinner = parser.name
                        if (tagNameinner == "LinearRing") {
                            while (parser.next() != XmlPullParser.END_TAG) {
                                if (parser.eventType != XmlPullParser.START_TAG) {
                                    continue
                                }
                                val tagNameinner2 = parser.name
                                if (tagNameinner2 == "coordinates") {
                                    coordinates = getTextAndGoToCloseTag(parser)
                                }
                            }
                        }
                    }
                }
            }
            return KmlLocation.readCoordinates(coordinates)
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun readPoint(parser: XmlPullParser): List<KmlLocation> {
            var coordinates = ""
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                val tagName = parser.name
                if (tagName == "coordinates") {
                    coordinates = getTextAndGoToCloseTag(parser)
                }
            }
            return KmlLocation.readCoordinates(coordinates)
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun getTextAndGoToCloseTag(parser: XmlPullParser): String {
            var text = ""
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType == XmlPullParser.TEXT) {
                    text = parser.text
                }
            }
            return text
        }
    }
}
