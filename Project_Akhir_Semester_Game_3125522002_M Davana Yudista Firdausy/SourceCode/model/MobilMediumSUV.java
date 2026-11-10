package model;

/**
 * P5: Subclass Mobil Medium MPV / SUV (Toyota Innova Reborn / Zenix, Honda CR-V).
 * Kendaraan tangguh dan nyaman, favorit pejabat daerah, rombongan dinas, dan wisata jarak jauh.
 */
public class MobilMediumSUV extends Kendaraan {
    private static final long serialVersionUID = 1L;

    public MobilMediumSUV(String platNomor, String merk, double hargaBeli, double tarifHarian, int odoMeterKm) {
        super(platNomor, merk, hargaBeli, tarifHarian, odoMeterKm);
    }

    @Override
    public String getKategori() {
        return "MEDIUM_SUV";
    }

    @Override
    public double getBiayaServisRutin() {
        return 1250000.0;
    }

    @Override
    public double hitungBiayaSewa(int durasiJam, boolean denganSopir) {
        if (durasiJam <= 0) throw new IllegalArgumentException("Durasi sewa harus lebih dari 0 jam.");
        double tarifPerJam = getTarifHarian() / 24.0;
        double biayaMobil = tarifPerJam * durasiJam;
        double biayaSopir = denganSopir ? (200000.0 / 24.0 * durasiJam) : 0.0;
        return biayaMobil + biayaSopir;
    }
}

