package model;

/**
 * P7: Interface Kontrak Telemetri Spasial & GPS.
 * Menjamin objek kendaraan dapat melacak koordinat posisi dan kekuatan sinyal GSM/GPS.
 */
public interface PelacakTelemetri {
    double getLatitude();
    double getLongitude();
    int getSinyalGpsPersen();
    void updateKoordinat(double lat, double lng);
    boolean isGpsAktif();
}

