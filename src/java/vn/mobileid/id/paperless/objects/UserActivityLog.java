/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import vn.mobileid.id.general.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
public class UserActivityLog extends DatabaseDefaultObject{
    
    @AnnotationORM(columnName = "ID")
    private long id;
    
    @AnnotationORM(columnName = "USER_ID")
    private long user_id;
           
    @AnnotationORM(columnName = "ENTERPRISE_ID")
    private long enterprise_id;
    
    @AnnotationORM(columnName = "MODULE")
    private String module;
    
    @AnnotationORM(columnName = "ACTION")
    private String action;
    
    @AnnotationORM(columnName = "INFO_KEY")
    private String info_key;
    
    @AnnotationORM(columnName = "INFO_VALUE")
    private String info_value;
    
    @AnnotationORM(columnName = "DETAIL")
    private String detail;
    
    @AnnotationORM(columnName = "AGENT")
    private String agent;
    
    @AnnotationORM(columnName = "AGENT_DETAIL")
    private String agent_detail;
    
    @AnnotationORM(columnName = "IP_ADDRESS")
    private String ip_address;
    
    @AnnotationORM(columnName = "DESCRIPTION")
    private String description;
    
    @AnnotationORM(columnName = "META_DATA")
    private String metadata;

    public UserActivityLog() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getInfo_key() {
        return info_key;
    }

    public void setInfo_key(String info_key) {
        this.info_key = info_key;
    }

    public String getInfo_value() {
        return info_value;
    }

    public void setInfo_value(String info_value) {
        this.info_value = info_value;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getAgent_detail() {
        return agent_detail;
    }

    public void setAgent_detail(String agent_detail) {
        this.agent_detail = agent_detail;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    
    
}
