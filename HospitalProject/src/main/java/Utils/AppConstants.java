/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Utils;

public class AppConstants {

    public static final String OTP_PURPOSE_CHANGE_PASSWORD = "change_password";
    public static final String OTP_PURPOSE_ACTIVATE = "change_password";
    public static final String CONFIRM_BOOKING_PURPOSE = "confirm_booking";
    public static final int OTP_EXPIRY_MINUTES = 10;
    public static final int ACTIVATION_EXPIRY_HOURS = 24;
    public static final int CONFIRM_BOOKING_EXPIRY_MINUTES = 30;

    public enum Api {
        PROVINCES("https://production.cas.so/address-kit/2025-07-01/provinces"),
        COMMUNES("https://production.cas.so/address-kit/2025-07-01/provinces/%s/communes");

        private final String url;
        Api(String url) { this.url = url; }
        public String getUrl() { return url; }
    }

    public enum Avatar {
        MALE("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQqnoHMxasmwM-DCy3XT8GKrxkRf7SV8keJgw&s"),
        FEMALE("https://media.istockphoto.com/id/2060009001/vector/avatar-user-profile-person-icon-profile-picture-for-social-media-profiles-icons-screensavers.jpg?s=612x612&w=0&k=20&c=onk7rmEoISSvHVlqc-SiBvcUr8ilCm2u9kcw3_Bm_SA="),
        OTHER("https://media.istockphoto.com/id/1220827245/vector/anonymous-gender-neutral-face-avatar-incognito-head-silhouette.jpg?s=612x612&w=0&k=20&c=GMdiPt_h8exnrAQnNo7dIKjwZyYqjH4lRQqV8AOx4QU=");

        private final String url;
        Avatar(String url) { this.url = url; }
        public String getUrl() { return url; }
    }
}

