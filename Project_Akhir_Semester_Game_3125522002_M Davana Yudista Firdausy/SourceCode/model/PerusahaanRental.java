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

    // Fasilitas & Legalitas Usaha (Taploka In-Game Mechanics)
    private boolean isBadanUsahaCV;     // CV_COST = 15jt
    private boolean isMemberAsosiasi;   // ASOS_COST = 5jt
    private boolean isBengkelMekanik;   // MECH_COST = 8jt (diskon servis 50%)
    private boolean isCarWashInhouse;   // WASH_COST = 6jt (turnaround cepat)
    private int levelIklan;             // Level 0 - 3

    // Event Musiman Dinamis (Taploka Seasonal Multipliers)
    public enum EventMusiman {
        NORMAL("Operasional Normal", 1.0, 1.0, 0),
        MUDIK("Mudik Lebaran", 2.0, 1.5, 8),
        LIBUR_SEKOLAH("Liburan Sekolah", 1.4, 1.2, 7),
        NATARU("Libur Natal & Tahun Baru", 1.4, 1.25, 6),
        BULAN_PUASA("Bulan Puasa Ramadhan", 0.6, 0.9, 6),
        KENAIKAN_BBM("Kenaikan Harga BBM", 0.7, 1.0, 8);

        public final String judul;
        public final double multiplierOrder;
        public final double multiplierTarif;
        public final int durasiHari;

        EventMusiman(String judul, double mo, double mt, int durasi) {
            this.judul = judul;
            this.multiplierOrder = mo;
            this.multiplierTarif = mt;
            this.durasiHari = durasi;
        }
    }

    private EventMusiman eventAktif = EventMusiman.NORMAL;
    private int sisaHariEvent = 0;

    public PerusahaanRental(String namaGarasi, double modalAwal) {
        this.namaGarasi = namaGarasi;
        this.kas = new KasPerusahaan(modalAwal);
        this.waktu = new WaktuSimulasi();
        this.manajerPesanan = new ManajerPesanan();
        this.armada = new ArrayList<>();
        this.mapArmada = new HashMap<>();
        this.kapasitasGarasi = 8;
        this.jumlahSupir = 2;
        this.isBadanUsahaCV = false;
        this.isMemberAsosiasi = false;
        this.isBengkelMekanik = false;
        this.isCarWashInhouse = false;
        this.levelIklan = 0;

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

    public synchronized void beliMobilBekas(String kategori, String plat, String merk, double hargaBeli, double tarifHarian)
            throws SaldoKasTidakCukupException, GarasiPenuhException {
        if (armada.size() >= kapasitasGarasi) {
            throw new GarasiPenuhException("Kapasitas garasi tidak mencukupi untuk unit baru.", kapasitasGarasi, armada.size());
        }

        double hargaDiskonBekas = hargaBeli * 0.60; // 60% harga baru (Bursa Mobil Bekas)
        kas.kurangiPengeluaran(hargaDiskonBekas, "Beli Unit Bekas: " + merk + " [" + plat + "]");

        Kendaraan bekas;
        switch (kategori.toUpperCase()) {
            case "LCGC":
                bekas = new MobilLCGC(plat, merk + " (Bekas)", hargaDiskonBekas, tarifHarian, 0);
                break;
            case "MEDIUM_SUV":
                bekas = new MobilMediumSUV(plat, merk + " (Bekas)", hargaDiskonBekas, tarifHarian, 0);
                break;
            case "MEWAH_VIP":
                bekas = new MobilMewah(plat, merk + " (Bekas)", hargaDiskonBekas, tarifHarian, 0);
                break;
            case "KELUARGA":
            default:
                bekas = new MobilKeluarga(plat, merk + " (Bekas)", hargaDiskonBekas, tarifHarian, 0);
                break;
        }

        bekas.setBekas(true); // Kondisi awal 72%
        bekas.tambahOdometer(45000 + (new Random()).nextInt(25000));
        tambahKendaraan(bekas);
    }

    // ==========================================================
    // UPGRADE FASILITAS & LEGALITAS BISNIS TAPLOKA
    // ==========================================================

    public synchronized void upgradeBadanUsahaCV() throws SaldoKasTidakCukupException {
        if (isBadanUsahaCV) return;
        double biaya = 15000000.0;
        kas.kurangiPengeluaran(biaya, "Pendirian Badan Hukum CV Rental Resmi");
        this.isBadanUsahaCV = true;
    }

    public synchronized void upgradeMemberAsosiasi() throws SaldoKasTidakCukupException {
        if (isMemberAsosiasi) return;
        double biaya = 5000000.0;
        kas.kurangiPengeluaran(biaya, "Keanggotaan Asosiasi Rental Indonesia");
        this.isMemberAsosiasi = true;
    }

    public synchronized void upgradeBengkelMekanik() throws SaldoKasTidakCukupException {
        if (isBengkelMekanik) return;
        double biaya = 8000000.0;
        kas.kurangiPengeluaran(biaya, "Pengadaan Bengkel & Mekanik In-House");
        this.isBengkelMekanik = true;
    }

    public synchronized void upgradeCarWash() throws SaldoKasTidakCukupException {
        if (isCarWashInhouse) return;
        double biaya = 6000000.0;
        kas.kurangiPengeluaran(biaya, "Fasilitas Cuci Mobil & Detailing In-House");
        this.isCarWashInhouse = true;
    }

    public synchronized void upgradeIklanDigital() throws SaldoKasTidakCukupException {
        double[] biayaIklan = {0.0, 3000000.0, 10000000.0, 35000000.0};
        if (levelIklan >= 3) return;
        double biaya = biayaIklan[levelIklan + 1];
        kas.kurangiPengeluaran(biaya, "Upgrade Kampanye Iklan Digital Level " + (levelIklan + 1));
        this.levelIklan++;
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
        if (isBengkelMekanik) {
            biaya *= 0.5; // Diskon 50% jika memiliki bengkel sendiri
        }
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

    // ==========================================================
    // ALOKASI UNIT & TIMELINE COLLISION CHECK (DIRECTIVE 2 & 3)
    // ==========================================================

    public synchronized boolean konfirmasiSewaDenganUnit(String idPesanan, String platNomor) throws Exception {
        return manajerPesanan.konfirmasiSewaDenganUnit(idPesanan, platNomor, waktu, kas, this);
    }

    public synchronized boolean konfirmasiSewaDenganUnit(long idPesanan, String platNomor) throws Exception {
        return manajerPesanan.konfirmasiSewaDenganUnit(idPesanan, platNomor, waktu, kas, this);
    }

    public boolean cekJadwalBentrok(String platNomor, int jamMulai, int durasiJam) {
        return manajerPesanan.cekJadwalBentrok(platNomor, waktu.getHari(), jamMulai, durasiJam, isCarWashInhouse);
    }

    public boolean cekJadwalBentrok(String platNomor, int hariMulai, int jamMulai, int durasiJam) {
        return manajerPesanan.cekJadwalBentrok(platNomor, hariMulai, jamMulai, durasiJam, isCarWashInhouse);
    }

    // Resolusi Krisis Delegator
    public String aksiTelepon(String plat) {
        return manajerPesanan.aksiTeleponPenyewa(plat, this);
    }

    public String aksiMatikanMesin(String plat) throws SaldoKasTidakCukupException {
        return manajerPesanan.aksiMatikanMesinRemote(plat, this);
    }

    public String aksiLaporPolisi(String plat) throws SaldoKasTidakCukupException {
        return manajerPesanan.aksiLaporPolisi(plat, kas, this);
    }

    public String aksiByOneAkamsi(String plat) throws SaldoKasTidakCukupException {
        return manajerPesanan.aksiByOneAkamsi(plat, kas, this);
    }

    public String aksiIkhlas(String plat) {
        return manajerPesanan.aksiIkhlas(plat, this);
    }

    // Step Simulator
    public synchronized void stepSimulation(int deltaMenit) {
        int hariSebelum = waktu.getHari();
        waktu.majuMenit(deltaMenit);

        if (waktu.getHari() > hariSebelum) {
            evaluasiEventMusiman();
        }

        try {
            manajerPesanan.evaluasiWaktu(waktu, kas, this);
        } catch (Exception e) {
            System.err.println("[SIMULASI EVENT] " + e.getMessage());
        }
    }

    private void evaluasiEventMusiman() {
        if (sisaHariEvent > 0) {
            sisaHariEvent--;
            if (sisaHariEvent == 0) {
                eventAktif = EventMusiman.NORMAL;
            }
        } else {
            Random r = new Random();
            if (r.nextDouble() < 0.15) {
                EventMusiman[] events = EventMusiman.values();
                int idx = 1 + r.nextInt(events.length - 1);
                eventAktif = events[idx];
                sisaHariEvent = eventAktif.durasiHari;
            }
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
    public boolean isBadanUsahaCV() { return isBadanUsahaCV; }
    public boolean isMemberAsosiasi() { return isMemberAsosiasi; }
    public boolean isBengkelMekanik() { return isBengkelMekanik; }
    public boolean isCarWashInhouse() { return isCarWashInhouse; }
    public int getLevelIklan() { return levelIklan; }
    public EventMusiman getEventAktif() { return eventAktif; }
    public int getSisaHariEvent() { return sisaHariEvent; }

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
            "\"cv\":%b,\"asos\":%b,\"mech\":%b,\"wash\":%b,\"iklan\":%d," +
            "\"event\":\"%s\",\"eventJudul\":\"%s\",\"sisaEvent\":%d," +
            "\"armada\":%s,\"pesanan\":%s,\"sesiSewa\":%s,\"transaksi\":%s}",
            namaGarasi, kas.getSaldo(), kas.getSaldoFormatted(), kapasitasGarasi,
            armada.size(), jumlahSupir, waktu.toJson(), krisisJson,
            isBadanUsahaCV, isMemberAsosiasi, isBengkelMekanik, isCarWashInhouse, levelIklan,
            eventAktif.name(), eventAktif.judul, sisaHariEvent,
            getArmadaJson(), manajerPesanan.getAntrianJson(),
            manajerPesanan.getSesiSewaJson(), kas.getRiwayatJson(10));
    }
}

