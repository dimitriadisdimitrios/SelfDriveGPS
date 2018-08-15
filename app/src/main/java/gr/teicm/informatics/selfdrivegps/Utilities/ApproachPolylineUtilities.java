package gr.teicm.informatics.selfdrivegps.Utilities;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import gr.teicm.informatics.selfdrivegps.Objects.bdccGeoObject;

public class ApproachPolylineUtilities {

    // distance in meters from GLatLng point to GPolyline or GPolygon poly
    public static boolean bdccGeoDistanceCheckWithRadius(ArrayList<LatLng> poly, LatLng point, double radius) {
        int i;
        bdccGeoObject.bdccGeo p = new bdccGeoObject.bdccGeo(point.latitude,point.longitude);

        for(i=0; i < (poly.size()-1) ; i++) {
            LatLng p1 = poly.get(i);
            bdccGeoObject.bdccGeo l1 = new bdccGeoObject.bdccGeo(p1.latitude,p1.longitude);

            LatLng p2 = poly.get(i+1);
            bdccGeoObject.bdccGeo l2 = new bdccGeoObject.bdccGeo(p2.latitude,p2.longitude);

            double distance = p.function_distanceToLineSegMeters(l1, l2);

            if(distance < radius) {
                return true;
            }
        }
        return false;
    }
}
