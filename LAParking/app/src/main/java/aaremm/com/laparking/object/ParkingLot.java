package aaremm.com.laparking.object;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 03-10-2014.
 */
public class ParkingLot extends GenericJson {

    @Key("_id")
    private String id;
    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL

    @Key("plid")
    public String plid;
    @Key("name")
    public String name;
    @Key("community")
    public String community;
    @Key("cd")
    private String cd;
    @Key("address")
    public String streetAddress;
    @Key("city")
    public String city;
    @Key("state")
    public String state;
    @Key("zip")
    public String zip;
    @Key("lat")
    public double latitude;
    @Key("lng")
    public double longitude;
    @Key("conv")
    public String convenientTo;
    @Key("entrance")
    public String entrance;
    @Key("operator")
    public String operator;
    @Key("type")
    public String type;
    @Key("phone")
    public String phone;
    @Key("hours")
    public String timings;
    @Key("hourly")
    public String hourlyRate;
    @Key("daily")
    public String dailyRate;
    @Key("monthly")
    public String monthlyRate;
    @Key("specialFeats")
    public String features;
    @Key("spaces")
    public String spaces;
    @Key("status")
    public String status;

    @Key("boundary")

    public List<BoundaryLoc> boundary = new ArrayList<BoundaryLoc>();

    public List<BoundaryLoc> getBoundary() {
        return boundary;
    }

    public void setBoundary(List<BoundaryLoc> boundary) {
        this.boundary = boundary;
    }

    public int freeSpaces = -1;
    public String disabled;
    public int LotSize;

    public ParkingLot(){
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public KinveyMetaData getMeta() {
        return meta;
    }

    public void setMeta(KinveyMetaData meta) {
        this.meta = meta;
    }

    public KinveyMetaData.AccessControlList getAcl() {
        return acl;
    }

    public void setAcl(KinveyMetaData.AccessControlList acl) {
        this.acl = acl;
    }

    public String getPlid() {
        return plid;
    }

    public void setPlid(String plid) {
        this.plid = plid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getConvenientTo() {
        return convenientTo;
    }

    public void setConvenientTo(String convenientTo) {
        this.convenientTo = convenientTo;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(String dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(String monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getSpaces() {
        return spaces;
    }

    public void setSpaces(String spaces) {
        this.spaces = spaces;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFreeSpaces() {
        return freeSpaces;
    }

    public void setFreeSpaces(int freeSpaces) {
        this.freeSpaces = freeSpaces;
    }

    public String getDisabled() {
        return disabled;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public int getLotSize() {
        return LotSize;
    }

    public void setLotSize(int lotSize) {
        LotSize = lotSize;
    }
}
