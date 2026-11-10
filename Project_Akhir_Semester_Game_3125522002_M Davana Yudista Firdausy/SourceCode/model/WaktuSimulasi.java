package model;

import java.io.Serializable;
import java.util.Locale;

/**
 * P1-P4: Game Clock & In-Game Time Engine.
 * Mengelola pergerakan hari dan jam operasional simulasi dengan pengaturan kecepatan.
 */
public class WaktuSimulasi implements Serializable {
    private static final long serialVersionUID = 1L;

    private int hari;
    private int jam;
    private int menit;
    private double kecepatan; // 0.0 = Pause, 0.5, 1.0, 2.0, 4.0
    private boolean isPaused;

    public WaktuSimulasi() {
        this.hari = 1;
        this.jam = 8; // Mulai pukul 08:00 WIB
        this.menit = 0;
        this.kecepatan = 1.0;
        this.isPaused = false;
    }

    public synchronized void majuMenit(int deltaMenit) {
        if (deltaMenit < 0) return;
        this.menit += deltaMenit;
        while (this.menit >= 60) {
            this.menit -= 60;
            this.jam++;
            if (this.jam >= 24) {
                this.jam -= 24;
                this.hari++;
            }
        }
    }

    public synchronized void majuJam(int deltaJam) {
        if (deltaJam < 0) return;
        this.jam += deltaJam;
        while (this.jam >= 24) {
            this.jam -= 24;
            this.hari++;
        }
    }

    public synchronized void setKecepatan(double kecepatan) {
        if (kecepatan <= 0.0) {
            this.isPaused = true;
            this.kecepatan = 0.0;
        } else {
            this.isPaused = false;
            this.kecepatan = kecepatan;
        }
    }

    public synchronized void togglePause() {
        this.isPaused = !this.isPaused;
    }

    public int getHari() { return hari; }
    public int getJam() { return jam; }
    public int getMenit() { return menit; }
    public double getKecepatan() { return kecepatan; }
    public boolean isPaused() { return isPaused; }

    public String getNamaHari() {
        String[] daftarHari = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        int idx = (hari - 1) % 7;
        return daftarHari[idx];
    }

    public String getWaktuString() {
        return String.format(Locale.US, "Hari %d (%s), %02d:%02d WIB", hari, getNamaHari(), jam, menit);
    }

    public String toJson() {
        return String.format(Locale.US,
            "{\"hari\":%d,\"namaHari\":\"%s\",\"jam\":%d,\"menit\":%d,\"kecepatan\":%.1f,\"isPaused\":%b,\"waktuString\":\"%s\"}",
            hari, getNamaHari(), jam, menit, kecepatan, isPaused, getWaktuString());
    }
}

