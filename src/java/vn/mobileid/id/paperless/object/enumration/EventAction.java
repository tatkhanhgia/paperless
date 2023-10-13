/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum EventAction {
    Run(1),
    Update(2),
    Login(3),
    New(4),
    Edit(5),
    PDF_Download(6),
    Remove_posttk(7),
    Edit_postttk(8),
    Delete_user(9),
    Test_run(10),
    Disable_url_qr_code(11),
    Toggle_revoke_mode(12),
    Change_user_role(13),
    CSV_Task(14),
    Copy(15),
    Add_user(16),
    Edit_qr_size(17),
    Edit_qr_background(18),
    Purchage_when_trial(19),
    Edit_type(20),
    Upload(21),
    Revoke(22);
    
    private int id;

    private EventAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    
}
