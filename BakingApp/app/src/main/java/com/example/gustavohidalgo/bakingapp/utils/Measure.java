package com.example.gustavohidalgo.bakingapp.utils;

import java.security.Key;

/**
 * Created by gustavo.hidalgo on 18/01/30.
 */

public enum Measure {
    CUP ("cup", "cups"),
    TBLSP ("table spoon", "table spoons"),
    TSP ("tea spoon", "tea spoons"),
    K ("kg", "kg"),
    G ("g", "g"),
    OZ ("oz", "oz"),
    UNIT ("", "");

    private final String mSingle;
    private final String mPlural;

    Measure(String single, String plural){
        this.mSingle = single;
        this.mPlural = plural;
    }

    public String getSingle() {
        return mSingle;
    }

    public String getPlural() {
        return mPlural;
    }

    public static String getMeasure(String jsonMeasure, double quantity){
        StringBuilder rightMeasure = new StringBuilder();

        if(quantity == (long) quantity) {
            rightMeasure.append(String.format("%d", (long) quantity));
        } else {
            rightMeasure.append(String.format("%s", quantity));
        }

        boolean isSingle = quantity <= 1;

        switch (Measure.valueOf(jsonMeasure)){
            case CUP:
                if(isSingle) {
                    rightMeasure.append(" ").append(Measure.CUP.getSingle()).append(" of ");
                } else {
                    rightMeasure.append(" ").append(Measure.CUP.getPlural()).append(" of ");
                }
                break;
            case TBLSP:
                if(isSingle) {
                    rightMeasure.append(" ").append(Measure.TBLSP.getSingle()).append(" of ");
                } else {
                    rightMeasure.append(" ").append(Measure.TBLSP.getPlural()).append(" of ");
                }
                break;
            case TSP:
                if(isSingle) {
                    rightMeasure.append(" ").append(Measure.TSP.getSingle()).append(" of ");
                } else {
                    rightMeasure.append(" ").append(Measure.TSP.getPlural()).append(" of ");
                }
                break;
            case K:
                rightMeasure.append(Measure.K.getSingle()).append(" of ");
                break;
            case G:
                rightMeasure.append(Measure.G.getSingle()).append(" of ");
                break;
            case OZ:
                rightMeasure.append(Measure.OZ.getSingle()).append(" of ");
                break;
            case UNIT:
                rightMeasure.append(" ").append(Measure.UNIT.getSingle());
                break;
        }

        return rightMeasure.toString();
    }


}
