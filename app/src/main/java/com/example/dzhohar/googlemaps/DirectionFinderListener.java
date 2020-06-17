package com.example.dzhohar.googlemaps;

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Trasa> route);
}
