package model;

import model.exception.GarasiPenuhException;
import model.exception.SaldoKasTidakCukupException;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

/**
 * P8-P11: Core Controller Perusahaan Rental (Garasi Magang Tycoon).
 * Mengagregasi kas operasional, waktu simulasi, manajer pesanan, dan armada kendaraan.
 */
public class PerusahaanRental implements Serializable {
    private static final long serialVersionUID = 1L;

    private String namaGarasi;
    private final KasPerusahaan kas;
    private final WaktuSimulasi waktu;
    private final ManajerPesanan manajerPesanan;

    private final List<Kendaraan> armada;
    private final Map<String, Kendaraan> mapArmada;

    private int kapasitasGarasi; // Jumlah slot bay tersedia
    private int jumlahSupir;     // Jumlah supir tetap yang direkrut

    public PerusahaanRental(String namaGarasi, double modalAwal) {
        this.namaGarasi = namaGarasi;
        this.kas = new KasPerusahaan(modalAwal);
        this.waktu = new WaktuSimulasi();
        this.manajerPesanan = new ManajerPesanan();
        this.armada = new ArrayList<>();
        this.mapArmada = new HashMap<>();
        this.kapasitasGarasi = 8;
        this.jumlahSupir = 2;

        initDefaultFleet();
    }

    private void initDefaultFleet() {
        try {
            // Armada permulaan rental
            Kendaraan m1 = new MobilLCGC("M 1122 SM", "Daihatsu Sigra 1.2 R", 145000000, 350000, 22000);
            Kendaraan m2 = new MobilKeluarga("M 2233 SM", "Toyota All New Avanza", 265000000, 550000, 18500);
            Kendaraan m3 = new MobilMediumSUV("M 3344 SM", "Toyota Innova Reborn Diesel", 460000000, 900000, 31000);
            
            // Pasang GPS di Avanza & Innova sejak awal
            m2.pasangGPS();
            m3.pasangGPS();

            m1.setSlotGarasi("Bay A-01");
            m2.setSlotGarasi("Bay A-02");
            m3.setSlotGarasi("Bay A-03");

            tambahKendaraanInternal(m1);
            tambahKendaraanInternal(m2);
            tambahKendaraanInternal(m3);
        } catch (Exception e) {
            System.err.println("Gagal inisialisasi default fleet: " + e.getMessage());
        }
    }

    private void tambahKendaraanInternal(Kendaraan k) {
        armada.add(k);
        mapArmada.put(k.getPlatNomor(), k);
    }

    public synchronized void tambahKendaraan(Kendaraan k) throws GarasiPenuhException {
        if (armada.size() >= kapasitasGarasi) {
            throw new GarasiPenuhException(
                "Kapasitas garasi penuh (" + kapasitasGarasi + " slot). Silakan upgrade garasi terlebih dahulu!",
                kapasitasGarasi, armada.size());
        }
        if (mapArmada.containsKey(k.getPlatNomor())) {
            throw new IllegalArgumentException("Plat nomor " + k.getPlatNomor() + " sudah terdaftar di armada.");
        }
        int nextSlot = armada.size() + 1;
        String block = (nextSlot <= 4) ? "A" : "B";
        k.setSlotGarasi(String.format("Bay %s-%02d", block, (nextSlot <= 4 ? nextSlot : nextSlot - 4)));
        tambahKendaraanInternal(k);
    }

    public synchronized void hapusKendaraan(String plat) {
        Kendaraan k = mapArmada.remove(plat);
        if (k != null) {
            armada.remove(k);
        }
    }

    public Kendaraan findKendaraan(String plat) {
        if (plat == null) return null;
        return mapArmada.get(plat.toUpperCase().trim());
    }

    // ==========================================================
    // AKSI MANAJEMEN EKONOMI TYCOON
    // ==========================================================

    public synchronized void beliMobilBaru(String kategori, String plat, String merk, double hargaBeli, double tarifHarian)
            throws SaldoKasTidakCukupException, GarasiPenuhException {
        if (armada.size() >= kapasitasGarasi) {
            throw new GarasiPenuhException("Kapasitas garasi tidak mencukupi untuk unit baru.", kapasitasGarasi, armada.size());
        }

        kas.kurangiPengeluaran(hargaBeli, "Beli Unit Baru: " + merk + " [" + plat + "]");

        Kendaraan baru;
        switch (kategori.toUpperCase()) {
            case "LCGC":
                baru = new MobilLCGC(plat, merk, hargaBeli, tarifHarian, 0);
                break;
            case "MEDIUM_SUV":
                baru = new MobilMediumSUV(plat, merk, hargaBeli, tarifHarian, 0);
                break;
            case "MEWAH_VIP":
                baru = new MobilMewah(plat, merk, hargaBeli, tarifHarian, 0);
                break;
            case "KELUARGA":
            default:
                baru = new MobilKeluarga(plat, merk, hargaBeli, tarifHarian, 0);
                break;
        }

        tambahKendaraan(baru);
    }

    public synchronized void pasangModulGPS(String plat) throws SaldoKasTidakCukupException {
        Kendaraan k = findKendaraan(plat);
        if (k == null) throw new IllegalArgumentException("Kendaraan " + plat + " tidak ditemukan.");
        if (k.isGPSInstalled()) throw new IllegalStateException("Kendaraan sudah terpasang GPS Tracker.");

        double biayaGPS = 1500000.0;
        kas.kurangiPengeluaran(biayaGPS, "Pemasangan Modul GPS Pro & Remote Cutoff [" + plat + "]");
        k.pasangGPS();
    }

    public synchronized void servisKendaraan(String plat) throws SaldoKasTidakCukupException {
        Kendaraan k = findKendaraan(plat);
        if (k == null) throw new IllegalArgumentException("Kendaraan " + plat + " tidak ditemukan.");

        double biaya = k.getBiayaServisRutin();
        kas.kurangiPengeluaran(biaya, "Servis Berkala & Ganti Oli Mesin [" + plat + "]");
        k.perbaikiMesin();
    }

    public synchronized void perluasKapasitasGarasi() throws SaldoKasTidakCukupException {
        double biayaEkspansi = 15000000.0; // Biaya sewa lahan tambahan 4 slot
        kas.kurangiPengeluaran(biayaEkspansi, "Ekspansi Garasi (+4 Bay Slot Parkir)");
        this.kapasitasGarasi += 4;
    }

    public synchronized void rekrutSupirBaru() throws SaldoKasTidakCukupException {
        double biayaPerekrutan = 1000000.0;
        kas.kurangiPengeluaran(biayaPerekrutan, "Rekrutmen & Pelatihan Supir Baru");
        this.jumlahSupir++;
    }

    // Step Simulator
    public synchronized void stepSimulation(int deltaMenit) {
        waktu.majuMenit(deltaMenit);
        try {
            manajerPesanan.evaluasiWaktu(waktu, kas, this);
        } catch (Exception e) {
            System.err.println("[SIMULASI EVENT] " + e.getMessage());
        }
    }

    // Getters
    public String getNamaGarasi() { return namaGarasi; }
    public KasPerusahaan getKas() { return kas; }
    public WaktuSimulasi getWaktu() { return waktu; }
    public ManajerPesanan getManajerPesanan() { return manajerPesanan; }
    public List<Kendaraan> getArmada() { return Collections.unmodifiableList(armada); }
    public int getKapasitasGarasi() { return kapasitasGarasi; }
    public int getJumlahSupir() { return jumlahSupir; }

    public String getArmadaJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < armada.size(); i++) {
            sb.append(armada.get(i).toJson());
            if (i < armada.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public String getGameStateJson() {
        ManajerPesanan.EventKrisis krisis = manajerPesanan.getKrisisAktif();
        String krisisJson = (krisis != null) ? krisis.toJson() : "null";

        return String.format(Locale.US,
            "{\"namaGarasi\":\"%s\",\"saldo\":%.0f,\"saldoFormatted\":\"%s\",\"kapasitas\":%d," +
            "\"totalArmada\":%d,\"jumlahSupir\":%d,\"waktu\":%s,\"krisis\":%s," +
            "\"armada\":%s,\"pesanan\":%s,\"sesiSewa\":%s,\"transaksi\":%s}",
            namaGarasi, kas.getSaldo(), kas.getSaldoFormatted(), kapasitasGarasi,
            armada.size(), jumlahSupir, waktu.toJson(), krisisJson,
            getArmadaJson(), manajerPesanan.getAntrianJson(),
            manajerPesanan.getSesiSewaJson(), kas.getRiwayatJson(10));
    }
}
