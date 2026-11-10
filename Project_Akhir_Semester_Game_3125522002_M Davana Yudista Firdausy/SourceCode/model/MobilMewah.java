package model;

/**
 * P5: Subclass Mobil Mewah VIP (Toyota Alphard, Vellfire, Hyundai Palisade).
 * Kelas premium untuk tamu VIP, pengantin, dan event khusus dengan margin sewa tertinggi.
 */
public class MobilMewah extends Kendaraan {
    private static final long serialVersionUID = 1L;

    public MobilMewah(String platNomor, String merk, double hargaBeli, double tarifHarian, int odoMeterKm) {
        super(platNomor, merk, hargaBeli, tarifHarian, odoMeterKm);
    }

    @Override
    public String getKategori() {
        return "MEWAH_VIP";
    }

    @Override
    public double getBiayaServisRutin() {
        return 3500000.0;
    }

    @Override
    public double hitungBiayaSewa(int durasiJam, boolean denganSopir) {
        if (durasiJam <= 0) throw new IllegalArgumentException("Durasi sewa harus lebih dari 0 jam.");
        double tarifPerJam = getTarifHarian() / 24.0;
        double biayaMobil = tarifPerJam * durasiJam;
        // Layanan supir VIP berseragam rapi
        double biayaSopir = denganSopir ? (350000.0 / 24.0 * durasiJam) : 0.0;
        return biayaMobil + biayaSopir;
    }
}
