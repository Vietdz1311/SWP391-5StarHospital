/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Utils;

/**
 *
 * @author 3DO TECH
 */
public enum ApiEndpoint {
    ACTIVATE("http://localhost:2003/HospitalProject/auth?action=activate"),
    COMFIRM_BOOKING("http://localhost:2003/HospitalProject/booking-confirm?action=confirm");
    ;

    private final String url;

    ApiEndpoint(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
