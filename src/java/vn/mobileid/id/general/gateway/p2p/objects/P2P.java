/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.gateway.p2p.objects;

import java.util.HashMap;
import java.util.List;
//import vn.mobileid.esigncloud.dao.CacheProperties;
//import vn.mobileid.esigncloud.dao.Ssl2Properties;

/**
 *
 * @author VUDP
 */
public class P2P {
    private int p2pID;
    private String name;
    private String uri;
    private String uuid;
    private boolean ssl2Enabled;
    //private Ssl2Properties ssl2Properties;
    private boolean awsEnabled;
    private boolean etsi102204Enabled;
    private boolean cacheEnable;
    //private CacheProperties cacheProperties;
    private List<P2PEntityAttribute> p2pEntityAttribute;
    
//    private HashMap<String, Value> engineProperties;

    public int getP2pID() {
        return p2pID;
    }

    public void setP2pID(int p2pID) {
        this.p2pID = p2pID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isSsl2Enabled() {
        return ssl2Enabled;
    }

    public void setSsl2Enabled(boolean ssl2Enabled) {
        this.ssl2Enabled = ssl2Enabled;
    }
/*
    public Ssl2Properties getSsl2Properties() {
        return ssl2Properties;
    }

    public void setSsl2Properties(Ssl2Properties ssl2Properties) {
        this.ssl2Properties = ssl2Properties;
    }
*/
    public boolean isAwsEnabled() {
        return awsEnabled;
    }

    public void setAwsEnabled(boolean awsEnabled) {
        this.awsEnabled = awsEnabled;
    }

    public boolean isEtsi102204Enabled() {
        return etsi102204Enabled;
    }

    public void setEtsi102204Enabled(boolean etsi102204Enabled) {
        this.etsi102204Enabled = etsi102204Enabled;
    }

    public boolean isCacheEnable() {
        return cacheEnable;
    }

    public void setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
    }
/*
    public CacheProperties getCacheProperties() {
        return cacheProperties;
    }

    public void setCacheProperties(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }
*/
    public List<P2PEntityAttribute> getP2pEntityAttribute() {
        return p2pEntityAttribute;
    }

    public void setP2pEntityAttribute(List<P2PEntityAttribute> p2pEntityAttribute) {
        this.p2pEntityAttribute = p2pEntityAttribute;
    }

//    public HashMap<String, Value> getEngineProperties() {
//        return engineProperties;
//    }
//
//    public void setEngineProperties(HashMap<String, Value> engineProperties) {
//        this.engineProperties = engineProperties;
//    }
    
    
}
