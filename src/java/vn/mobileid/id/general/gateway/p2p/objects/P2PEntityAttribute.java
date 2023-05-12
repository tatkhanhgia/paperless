/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.gateway.p2p.objects;


/**
 *
 * @author VUDP
 */
public class P2PEntityAttribute {
    private int p2pID;
    private int entityID;
    private P2PIPAccessPrivilege p2pIPAccessPrivilege;
    private P2PFunctionAccessPrivilege p2pFunctionAccessPrivilege;

    public int getP2pID() {
        return p2pID;
    }

    public void setP2pID(int p2pID) {
        this.p2pID = p2pID;
    }

    public int getEntityID() {
        return entityID;
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
    }

    public P2PIPAccessPrivilege getP2pIPAccessPrivilege() {
        return p2pIPAccessPrivilege;
    }

    public void setP2pIPAccessPrivilege(P2PIPAccessPrivilege p2pIPAccessPrivilege) {
        this.p2pIPAccessPrivilege = p2pIPAccessPrivilege;
    }

    public P2PFunctionAccessPrivilege getP2pFunctionAccessPrivilege() {
        return p2pFunctionAccessPrivilege;
    }

    public void setP2pFunctionAccessPrivilege(P2PFunctionAccessPrivilege p2pFunctionAccessPrivilege) {
        this.p2pFunctionAccessPrivilege = p2pFunctionAccessPrivilege;
    }
    
    
}
