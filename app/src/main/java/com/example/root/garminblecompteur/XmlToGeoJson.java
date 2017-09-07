package com.example.root.garminblecompteur;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.geojson.GeoJSON;

import org.osmdroid.util.GeoPoint;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by cyrilstern1 on 22/08/2017.
 * Allow parse readgpx file and convert to arraylist og geopoint
 */

public class XmlToGeoJson {
    private static XmlToGeoJson xmlToGeoJson = null;
    public XmlToGeoJson() {
    }

    public  static XmlToGeoJson getInstance(){
       if(xmlToGeoJson == null) xmlToGeoJson = new XmlToGeoJson();
        return xmlToGeoJson;
    }

    public ArrayList<GeoPoint> decodeXmlToGeoJson(String path, Context ctx) throws ParserConfigurationException, IOException, SAXException, XmlPullParserException {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new FileReader(path));

            /**
            *  do reading, usually loop until end of file reading
            */
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                sb.append(mLine);
            }
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        ArrayList<GeoPoint> lineStringArray = new ArrayList<GeoPoint>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput( new StringReader(sb.toString()) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
            } else if(eventType == XmlPullParser.START_TAG) {
                if (xpp.getAttributeCount() != 0 && xpp.getName().equals("trkpt") ) {
                    lineStringArray.add(new GeoPoint(Float.parseFloat(xpp.getAttributeValue(0)),Float.parseFloat(xpp.getAttributeValue(1))));
                }

            } else if(eventType == XmlPullParser.END_TAG) {
            } else if(eventType == XmlPullParser.TEXT) {
            }
            eventType = xpp.next();
        }

        return lineStringArray;
    }
}
