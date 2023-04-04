package com.example.myapplication.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.bluetoothBeacon.BeaconBlueTooth;
import com.example.myapplication.constants.Constants;
import com.example.myapplication.coordinateTranslator.CoordinateTranslator;
import com.example.myapplication.databinding.FragmentMapBinding;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 3;

    private static final String beaconLayout = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private BeaconManager beaconManager;

    private ArrayList<BeaconBlueTooth> dataBaseBeacon;
    private static final int numberOfBecons = 2;
    private static final double coordCof = 9.000009000009e-6;
    private PointOnMap location;
    private CoordinateTranslator coordTranslator;
    TestView view;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel notificationsViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Resources res = getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.map, null);

        coordTranslator = new CoordinateTranslator(
                Constants.BLLAT,
                Constants.BLLNG,
                Constants.TLLAT,
                Constants.TLLNG,
                Constants.BRLAT,
                Constants.BRLNG,
                Constants.TRLAT,
                Constants.TRLNG);
        View drawView = binding.draw;
        view = (TestView) drawView;

        return root;
    }

    public void getPermission(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_BACKGROUND_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
    }

    public void startBeaconManager(){
        beaconManager = BeaconManager.getInstanceForApplication(getContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(beaconLayout));
        Beacon.setHardwareEqualityEnforced(false);
        beaconManager.startRangingBeacons(new Region("btLocation", null, null, null));
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                getNewLocation(beacons);
                for(Beacon b : beacons){
                    System.out.println(b.getBluetoothName() + " " + b.getDistance() + " " + b.getBluetoothAddress());
                }
            }
        });
    }

    private void getNewLocation(Collection<Beacon> beacons) {
        double[][] positions = new double[numberOfBecons + 1][numberOfBecons + 1];
        double[] distances = new double[numberOfBecons + 1];
        ArrayList<Beacon> copyBeacons = orderByDist(beacons);
        int cont = 0;
        for(Beacon b : copyBeacons){
            if(cont > numberOfBecons){
                break;
            }
            ArrayList<Double> position = getCoordinates(b);
            if(position != null){
                positions[cont][0] = position.get(0);
                positions[cont][1] = position.get(1);
                distances[cont] = b.getDistance() * coordCof;
                cont++;
            }
        }
        if(cont <= numberOfBecons){
            return;
        }
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        double[] centroid = optimum.getPoint().toArray();

        System.out.println("====================================");
        System.out.println(centroid[0] + " " + centroid[1]);
        ArrayList<Double> loc = coordTranslator.coordTranslateToPhone(centroid[0],centroid[1],view.HOR_SIZE,view.VERT_SIZE);
        double xx = loc.get(0), yy = loc.get(1);
        view.location.x = (int) xx;
        view.location.y = (int) yy;
    }

    private ArrayList<Beacon> orderByDist(Collection<Beacon> beacons){
        ArrayList<Beacon> output = new ArrayList<>(beacons);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            output.sort(new Comparator<Beacon>() {
                @Override
                public int compare(Beacon b1, Beacon b2) {
                    return Double.compare(b1.getDistance(), b2.getDistance());
                }
            });
        }
        return output;
    }

    public ArrayList<Double> getCoordinates(Beacon b){
        ArrayList<Double> output = new ArrayList<>();
        for(BeaconBlueTooth iter : dataBaseBeacon){
            if(iter.getMacAdd().equals(b.getBluetoothAddress().toLowerCase())){
                output.add(iter.getLatitude());
                output.add(iter.getLongitude());
                return output;
            }
        }
        System.out.println("Can t find beacon");
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class PointOnMap{
        public double x, y;
        public PointOnMap(double x, double y){
            this.x = x;
            this.y = y;
        }
    }
}