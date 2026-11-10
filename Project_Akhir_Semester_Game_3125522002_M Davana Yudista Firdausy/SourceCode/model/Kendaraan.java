package model;

import java.io.Serializable;
import java.util.Locale;

/**
 * P5-P7: Superclass Abstrak Kendaraan Armada Rental.
 * Mengimplementasikan kontrak PelacakTelemetri dan RemoteEngineControl.
 */
public abstract class Kendaraan implements PelacakTelemetri, RemoteEngineControl, Serializable {
    private static final long serialVersionUID = 1L;

    public enum StatusSewa {
        SIAP,
        DISEWA,
        HILANG,
        SERVIS
    }

    private final String platNomor; // Immutable
    private String merk;
    private final double hargaBeliRealistis;
    private double tarifHarian;
    private int odoMeterKm;
    private double kondisiMesin; // 0.0 - 100.0 persen
    private boolean isGPSInstalled;
    private StatusSewa statusSewa;

    // Telemetri Koordinat & GPS
    private double lat;
    private double lng;
    private int sinyalGps;
    private boolean isEngineCutoff;

    // Data sewa aktif
    private String penyewaSaatIni;
    private int jamMulaiSewa;
    private int durasiJamSewa;
    private boolean denganSopir;
    private String slotGarasi;

    public Kendaraan(String platNomor, String merk, double hargaBeliRealistis, double tarifHarian, int odoMeterKm) {
        if (platNomor == null || platNomor.trim().isEmpty()) {
            throw new IllegalArgumentException("Plat nomor kendaraan tidak boleh kosong.");
        }
        if (hargaBeliRealistis <= 0) {
            throw new IllegalArgumentException("Harga beli kendaraan harus lebih besar dari 0.");
        }
        if (tarifHarian <= 0) {
            throw new IllegalArgumentException("Tarif sewa harian harus lebih besar dari 0.");
        }
        if (odoMeterKm < 0) {
            throw new IllegalArgumentException("Odometer awal tidak boleh negatif.");
        }

        this.platNomor = platNomor.toUpperCase().trim();
        this.merk = merk;
        this.hargaBeliRealistis = hargaBeliRealistis;
        this.tarifHarian = tarifHarian;
        this.odoMeterKm = odoMeterKm;
        this.kondisiMesin = 100.0;
        this.isGPSInstalled = false;
        this.statusSewa = StatusSewa.SIAP;
        
        // Default koordinat garasi pusat Sumenep
        this.lat = -7.015243;
        this.lng = 113.864210;
        this.sinyalGps = 100;
        this.isEngineCutoff = false;
        this.penyewaSaatIni = "-";
        this.slotGarasi = "A-01";
    }

    // Abstract methods untuk spesifikasi subclass
    public abstract String getKategori();
    public abstract double getBiayaServisRutin();
    public abstract double hitungBiayaSewa(int durasiJam, boolean denganSopir);

    // PelacakTelemetri implementation
    @Override
    public double getLatitude() { return lat; }

    @Override
    public double getLongitude() { return lng; }

    @Override
    public int getSinyalGpsPersen() {
        return isGPSInstalled ? sinyalGps : 0;
    }

    @Override
    public void updateKoordinat(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public boolean isGpsAktif() {
        return isGPSInstalled && !isEngineCutoff;
    }

    // RemoteEngineControl implementation
    @Override
    public boolean matikanMesinRemote() {
        if (!isGPSInstalled) {
            return false;
        }
        this.isEngineCutoff = true;
        return true;
    }

    @Override
    public boolean hidupkanMesinRemote() {
        if (!isGPSInstalled) {
            return false;
        }
        this.isEngineCutoff = false;
        return true;
    }

    @Override
    public boolean isMesinDimatikanRemote() {
        return isEngineCutoff;
    }

    // Maintenance & Modifikasi
    public void pasangGPS() {
        this.isGPSInstalled = true;
    }

    public void kurangiKondisi(double wearPercent) {
        this.kondisiMesin = Math.max(0.0, this.kondisiMesin - wearPercent);
        if (this.kondisiMesin < 40.0 && this.statusSewa == StatusSewa.SIAP) {
            this.statusSewa = StatusSewa.SERVIS;
        }
    }

    public void perbaikiMesin() {
        this.kondisiMesin = 100.0;
        if (this.statusSewa == StatusSewa.SERVIS) {
            this.statusSewa = StatusSewa.SIAP;
        }
    }

    public void tambahOdometer(int km) {
        if (km > 0) this.odoMeterKm += km;
    }

    // Getters & Setters
    public String getPlatNomor() { return platNomor; }
    public String getMerk() { return merk; }
    public void setMerk(String merk) { this.merk = merk; }
    public double getHargaBeliRealistis() { return hargaBeliRealistis; }
    public double getTarifHarian() { return tarifHarian; }
    public void setTarifHarian(double tarifHarian) {
        if (tarifHarian <= 0) throw new IllegalArgumentException("Tarif sewa harus positif.");
        this.tarifHarian = tarifHarian;
    }
    public int getOdoMeterKm() { return odoMeterKm; }
    public double getKondisiMesin() { return kondisiMesin; }
    public boolean isGPSInstalled() { return isGPSInstalled; }
    public StatusSewa getStatusSewa() { return statusSewa; }
    public void setStatusSewa(StatusSewa statusSewa) { this.statusSewa = statusSewa; }
    public String getPenyewaSaatIni() { return penyewaSaatIni; }
    public void setPenyewaSaatIni(String penyewaSaatIni) { this.penyewaSaatIni = penyewaSaatIni; }
    public int getJamMulaiSewa() { return jamMulaiSewa; }
    public void setJamMulaiSewa(int jamMulaiSewa) { this.jamMulaiSewa = jamMulaiSewa; }
    public int getDurasiJamSewa() { return durasiJamSewa; }
    public void setDurasiJamSewa(int durasiJamSewa) { this.durasiJamSewa = durasiJamSewa; }
    public boolean isDenganSopir() { return denganSopir; }
    public void setDenganSopir(boolean denganSopir) { this.denganSopir = denganSopir; }
    public String getSlotGarasi() { return slotGarasi; }
    public void setSlotGarasi(String slotGarasi) { this.slotGarasi = slotGarasi; }

    public String toJson() {
        return String.format(Locale.US,
            "{\"plat\":\"%s\",\"merk\":\"%s\",\"kategori\":\"%s\",\"hargaBeli\":%.0f,\"tarifHarian\":%.0f," +
            "\"odoMeter\":%d,\"kondisiMesin\":%.1f,\"isGPS\":%b,\"status\":\"%s\",\"isCutoff\":%b," +
            "\"penyewa\":\"%s\",\"durasiJam\":%d,\"sopir\":%b,\"slot\":\"%s\",\"lat\":%.6f,\"lng\":%.6f}",
            platNomor, merk, getKategori(), hargaBeliRealistis, tarifHarian,
            odoMeterKm, kondisiMesin, isGPSInstalled, statusSewa.name(), isEngineCutoff,
            penyewaSaatIni.replace("\"", "\\\""), durasiJamSewa, denganSopir, slotGarasi, lat, lng);
    }
}
