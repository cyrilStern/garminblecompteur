package com.example.root.garminblecompteur;

import android.content.Context;
import android.util.Log;

import com.mapbox.services.commons.geojson.GeoJSON;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by cyrilstern1 on 22/08/2017.
 */

public class XmlToGeoJson {
    private static XmlToGeoJson xmlToGeoJson = null;
    public XmlToGeoJson() {
    }

    public  static XmlToGeoJson getInstance(){
       if(xmlToGeoJson == null) xmlToGeoJson = new XmlToGeoJson();
        return xmlToGeoJson;
    }

    public GeoJSON decodeXmlToGeoJson(String path, Context ctx) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        try {
            Document doc = db.parse(new InputSource(ctx.getResources().getAssets().open(path)));
            Log.i("testReaer",String.valueOf(doc.getElementsByTagName("gtr")[0]));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GeoJSON geoJSON  = new GeoJSON() {
            @Override
            public String getType() {
                return null;
            }

            @Override
            public String toJson() {
                return null;
            }
        };
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        return geoJSON;
    }
}
