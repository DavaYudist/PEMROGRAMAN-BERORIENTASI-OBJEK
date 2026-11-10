package model;

import java.io.Serializable;
import java.util.Locale;

/**
 * P8-P11: Model Sesi Sewa Aktif untuk Visual Gantt Timeline 7-Hari.
 */
public class SesiSewa implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final String platNomor;
    private final String namaPenyewa;
    private final int hariMulai;
    private final int jamMulai;
    private final int durasiJam;
    private final double totalBiaya;
    private final boolean denganSopir;
    private boolean isSelesai;
    private boolean isDibawaKabur;

    public SesiSewa(long id, String platNomor, String namaPenyewa, int hariMulai, int jamMulai,
                    int durasiJam, double totalBiaya, boolean denganSopir) {
        this.id = id;
        this.platNomor = platNomor;
        this.namaPenyewa = namaPenyewa;
        this.hariMulai = hariMulai;
        this.jamMulai = jamMulai;
        this.durasiJam = durasiJam;
        this.totalBiaya = totalBiaya;
        this.denganSopir = denganSopir;
        this.isSelesai = false;
        this.isDibawaKabur = false;
    }

    public long getId() { return id; }
    public String getPlatNomor() { return platNomor; }
    public String getNamaPenyewa() { return namaPenyewa; }
    public int getHariMulai() { return hariMulai; }
    public int getJamMulai() { return jamMulai; }
    public int getDurasiJam() { return durasiJam; }
    public double getTotalBiaya() { return totalBiaya; }
    public boolean isDenganSopir() { return denganSopir; }
    public boolean isSelesai() { return isSelesai; }
    public void setSelesai(boolean selesai) { isSelesai = selesai; }
    public boolean isDibawaKabur() { return isDibawaKabur; }
    public void setDibawaKabur(boolean dibawaKabur) { isDibawaKabur = dibawaKabur; }

    public int getHariSelesai() {
        int totalJam = (hariMulai - 1) * 24 + jamMulai + durasiJam;
        return (totalJam / 24) + 1;
    }

    public String toJson() {
        return String.format(Locale.US,
            "{\"id\":%d,\"plat\":\"%s\",\"penyewa\":\"%s\",\"hariMulai\":%d,\"jamMulai\":%d," +
            "\"durasiJam\":%d,\"hariSelesai\":%d,\"totalBiaya\":%.0f,\"sopir\":%b,\"selesai\":%b,\"kabur\":%b}",
            id, platNomor, namaPenyewa.replace("\"", "\\\""), hariMulai, jamMulai,
            durasiJam, getHariSelesai(), totalBiaya, denganSopir, isSelesai, isDibawaKabur);
    }
}
