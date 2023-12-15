/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.policy.object;

/**
 *
 * @author GiaTK
 */
public class PolicyPasswordExpired {
    private int minute_lock;

    public PolicyPasswordExpired() {
    }

    public int getMinute_lock() {
        return minute_lock;
    }

    public void setMinute_lock(int minute_lock) {
        this.minute_lock = minute_lock;
    }
}
