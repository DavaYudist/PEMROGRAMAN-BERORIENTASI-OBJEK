package model;

import java.util.Locale;

/**
 * Superclass Abstrak Kendaraan
 * Mengimplementasikan PelacakGPS serta mengenkapsulasi data spesifikasi, tarif harian (24 jam),
 * kondisi mesin, dan status operasional armada.
 * Dilengkapi validasi defensif sebelum mutasi state (Modul 10).
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public abstract class Kendaraan implements PelacakGPS {
    private String platNomor;
    private String merk;
    private double lat, lng;
    private int baterai;
    private boolean disewa;
    private String slotParkir; // e.g. "Slot A-01"
    private String penyewa;    // Nama pelanggan penyewa, kosong saat standby di pool

    // Atribut Tarif Harian (Base 24 Jam) & Kondisi Kendaraan
    private double tarifPerHari;
    private String kondisiMesin; // "Sangat Baik", "Baik", "Dalam Perbaikan", "Perlu Servis"
    private int odoMeterKm;
    private String catatanFisik;
    private boolean denganSupir = false;

    public Kendaraan(String platNomor, String merk, double startLat, double startLng, int baterai) {
        this(platNomor, merk, startLat, startLng, baterai, 600000.0, 25000, "Sangat Baik", "Mulus, Bodi Bersih, AC Dingin");
    }

    public Kendaraan(String platNomor, String merk, double startLat, double startLng, int baterai, 
                     double tarifPerHari, int odoMeterKm, String kondisiMesin, String catatanFisik) {
        // Validasi Defensif Argumen Konstruktor (Modul 10)
        if (platNomor == null || platNomor.trim().isEmpty()) {
            throw new IllegalArgumentException("Plat nomor tidak boleh null atau kosong.");
        }
        if (tarifPerHari <= 0) {
            throw new IllegalArgumentException("Tarif sewa per hari harus bernilai positif (> 0).");
        }
        if (odoMeterKm < 0) {
            throw new IllegalArgumentException("Odometer kilometer tidak boleh bernilai negatif (>= 0).");
        }

        this.platNomor = platNomor.trim();
        this.merk = (merk != null && !merk.trim().isEmpty()) ? merk.trim() : "Kendaraan Operasional";
        this.lat = startLat;
        this.lng = startLng;
        this.disewa = false;
        this.slotParkir = "-";
        this.penyewa = "";
        this.tarifPerHari = tarifPerHari;
        this.odoMeterKm = odoMeterKm;
        setKondisiMesin(kondisiMesin);
        this.catatanFisik = (catatanFisik != null && !catatanFisik.isEmpty()) ? catatanFisik : "Bodi Bersih, Terawat";
        setBaterai(baterai);
    }
    
    public void setLokasi(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public void setBaterai(int baterai) {
        this.baterai = Math.max(0, Math.min(100, baterai));
    }
    
    public String getPlatNomor() {
        return platNomor;
    }

    public void setPlatNomor(String platNomor) {
        if (platNomor == null || platNomor.trim().isEmpty()) {
            throw new IllegalArgumentException("Plat nomor tidak boleh null atau kosong.");
        }
        this.platNomor = platNomor.trim();
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        if (merk == null || merk.trim().isEmpty()) {
            throw new IllegalArgumentException("Merk kendaraan tidak boleh kosong.");
        }
        this.merk = merk.trim();
    }

    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLng() {
        return lng;
    }

    @Override
    public int getBaterai() {
        return baterai;
    }
    
    public boolean isDisewa() {
        return disewa;
    }

    public void setDisewa(boolean disewa) {
        this.disewa = disewa;
        if (!disewa) {
            this.penyewa = "";
        }
    }

    public String getSlotParkir() {
        return slotParkir;
    }

    public void setSlotParkir(String slotParkir) {
        this.slotParkir = (slotParkir != null) ? slotParkir : "-";
    }

    public String getPenyewa() {
        return penyewa;
    }

    public void setPenyewa(String penyewa) {
        this.penyewa = (penyewa != null) ? penyewa : "";
    }

    public double getTarifPerHari() {
        return tarifPerHari;
    }

    public void setTarifPerHari(double tarifPerHari) {
        if (tarifPerHari <= 0) {
            throw new IllegalArgumentException("Tarif sewa per hari harus bernilai positif (> 0).");
        }
        this.tarifPerHari = tarifPerHari;
    }

    /**
     * Menghitung nilai tarif prorata per jam berdasarkan tarif harian / 24.0
     */
    public double getTarifPerJamEquivalent() {
        return tarifPerHari / 24.0;
    }

    public String getKondisiMesin() {
        return kondisiMesin;
    }

    public void setKondisiMesin(String kondisiMesin) {
        if (kondisiMesin == null || kondisiMesin.trim().isEmpty()) {
            this.kondisiMesin = "Sangat Baik";
            return;
        }
        String km = kondisiMesin.trim();
        if (km.equalsIgnoreCase("Dalam Perbaikan")) {
            this.kondisiMesin = "Dalam Perbaikan";
        } else if (km.equalsIgnoreCase("Perlu Servis") || km.equalsIgnoreCase("Perlu Servis Ringan")) {
            this.kondisiMesin = "Perlu Servis";
        } else if (km.equalsIgnoreCase("Baik")) {
            this.kondisiMesin = "Baik";
        } else {
            this.kondisiMesin = "Sangat Baik";
        }
    }

    public boolean isBisaDisewa() {
        return !disewa && !"Dalam Perbaikan".equalsIgnoreCase(kondisiMesin);
    }

    public int getOdoMeterKm() {
        return odoMeterKm;
    }

    public void setOdoMeterKm(int odoMeterKm) {
        if (odoMeterKm < 0) {
            throw new IllegalArgumentException("Odometer kilometer tidak boleh bernilai negatif (>= 0).");
        }
        this.odoMeterKm = odoMeterKm;
    }

    public String getCatatanFisik() {
        return catatanFisik;
    }

    public void setCatatanFisik(String catatanFisik) {
        this.catatanFisik = (catatanFisik != null) ? catatanFisik : "";
    }

    public boolean isDenganSupir() {
        return denganSupir;
    }

    public void setDenganSupir(boolean denganSupir) {
        this.denganSupir = denganSupir;
    }

    public abstract String getKategori();

    /**
     * Metode Polimorfis: Menghitung total tagihan sewa berdasarkan durasi (jam),
     * opsi layanan pengemudi (dengan supir / lepas kunci), dan persentase diskon admin.
     * Melakukan validasi input defensif (durasiJam > 0, diskonPersen 0-100%).
     */
    public abstract double hitungBiayaSewa(int durasiJam, boolean denganSupir, double diskonPersen);

    /**
     * Overload untuk kompatibilitas backward
     */
    public double hitungBiayaSewa(int durasiJam) {
        if (durasiJam <= 0) {
            throw new IllegalArgumentException("Durasi sewa harus lebih dari 0 jam.");
        }
        return hitungBiayaSewa(durasiJam, true, (durasiJam >= 24) ? 10.0 : 0.0);
    }
    
    // Serialisasi data JSON polimorfis untuk antarmuka WebView
    public String toJson() {
        return String.format(Locale.US, 
            "{\"plat\":\"%s\", \"merk\":\"%s\", \"lat\":%.6f, \"lng\":%.6f, \"baterai\":%d, \"kategori\":\"%s\", \"disewa\":%b, \"slot\":\"%s\", \"baySlot\":\"%s\", \"penyewa\":\"%s\", \"status\":\"%s\", \"tarifPerHari\":%.0f, \"tarifPerJam\":%.0f, \"tarif\":%.0f, \"kondisiMesin\":\"%s\", \"odoMeterKm\":%d, \"catatanFisik\":\"%s\", \"bisaDisewa\":%b, \"denganSupir\":%b}", 
            platNomor, merk, lat, lng, baterai, getKategori(), disewa, slotParkir, slotParkir, penyewa, 
            (disewa ? "DISEWA" : "STANDBY"), tarifPerHari, getTarifPerJamEquivalent(), tarifPerHari, 
            escapeJson(kondisiMesin), odoMeterKm, escapeJson(catatanFisik), isBisaDisewa(), denganSupir);
    }

    protected String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", " ");
    }
}
