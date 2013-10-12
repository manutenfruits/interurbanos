package com.manutenfruits.interurbanos.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.net.URISyntaxException;

/**    static final String KEY_COMING = "backward";

 * Created by manutenfruits on 12/10/13.
 */
public class BusLine implements Parcelable{

    public static String KEY = "BUSLINE";
    public static String DIRECTION = "BUSDIRECTION";

    private String line;

    private String origin;
    private String destination;

    private URI going;
    private URI coming;

    private boolean nightBus;

    public BusLine(String line, String origin, String destination, String going, String coming) throws URISyntaxException{
        this.line = line;
        this.origin = origin;
        this.destination = destination;
        this.going = new URI(going);
        this.coming = new URI(coming);
        this.nightBus = line.charAt(0) == 'N';
    }

    public BusLine(Parcel in) throws URISyntaxException{
        this.line = in.readString();
        this.origin = in.readString();
        this.destination = in.readString();
        this.going = new URI(in.readString());
        this.coming = new URI(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.line);
        dest.writeString(this.origin);
        dest.writeString(this.destination);
        dest.writeString(this.going.toString());
        dest.writeString(this.coming.toString());
    }

    public static final Parcelable.Creator CREATOR = new Creator<BusLine>() {
        public BusLine createFromParcel(Parcel parcel){
            try{
                return new BusLine(parcel);
            }catch(URISyntaxException e){
                e.printStackTrace();
            }
            return null;
        }

        public BusLine[] newArray(int size){
            return new BusLine[size];
        }
    };

    public String getLine() {
        return line;
    }

    public String getOrigin() {
        return origin;
    }

    public URI getGoing() {
        return going;
    }

    public URI getComing() {
        return coming;
    }

    public String getDestination() {
        return destination;
    }

    public boolean isNightBus(){
        return nightBus;
    }

}
