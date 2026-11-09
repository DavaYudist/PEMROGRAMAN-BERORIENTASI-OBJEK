package model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import model.exception.KapasitasPenuhException;
import model.exception.KendaraanRusakException;
import model.exception.KendaraanTidakTersediaException;

/**
 * Agregator Utama: PerusahaanRental (Capstone Project)
 * Mengelola armada kendaraan dan slot bay parkir di Depot Sumenep.
 * Mengintegrasikan prinsip OOP P1-P15:
 * - P1-P4: Enkapsulasi, validasi state.
 * - P5-P7: Abstraksi, pewarisan, polimorfisme, interface.
 * - P8-P10: Agregasi, exception handling bertingkat & atomisitas.
 * - P11: Generic Collections (List, Map, Set).
 * - P12-P13: Dual-engine persistence (MySQL & File Teks) serta CLI/GUI integration.
 * - P14-P15: Refactoring, class invariants, regression testing.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class PerusahaanRental {
    private String namaCabang;
    
    // P11 - Pengelolaan Kumpulan Objek menggunakan Generic Collections
    private final List<Kendaraan> armada;
    private final List<Kendaraan> armadaDisewa;
    private final Map<String, Kendaraan> mapArmada;
    private final Set<String> platTerdaftar;
    private final List<String> daftarSlotKapasitas;

    // P12 - File Repository Engine
    private final FleetFileRepository fileRepository;
    private final Path defaultFilePath = Paths.get("data", "armada_store.txt");

    public PerusahaanRental(String namaCabang) {
        this.namaCabang = (namaCabang != null && !namaCabang.trim().isEmpty()) ? namaCabang.trim() : "FleetSumenep Pusat";
        this.armada = new ArrayList<>();
        this.armadaDisewa = new ArrayList<>();
        this.mapArmada = new HashMap<>();
        this.platTerdaftar = new HashSet<>();
        this.daftarSlotKapasitas = new ArrayList<>();
        this.fileRepository = new FleetFileRepository();

        // Inisialisasi awal: Muat data dari MySQL jika ada, atau fallback ke file, atau in-memory default
        loadInitialData();
    }

    private synchronized void loadInitialData() {
        // Coba muat dari Database MySQL terlebih dahulu
        boolean success = loadDataFromDatabase();
        if (!success) {
            // Jika database MySQL offline, coba muat dari file persistensi data/armada_store.txt
            try {
                if (defaultFilePath.toFile().exists()) {
                    muatDariFile(defaultFilePath);
                    System.out.println("[PerusahaanRental] Sukses memuat data dari file repository: " + defaultFilePath);
                    return;
                }
            } catch (Exception e) {
                System.err.println("[PerusahaanRental] Gagal memuat data dari file: " + e.getMessage());
            }

            // Fallback terakhir ke dataset memori standar
            initDefaultArmadaMemory();
        }
    }

    /**
     * Memuat konfigurasi kapasitas parkir, daftar armada, dan transaksi sewa aktif dari MySQL.
     */
    public synchronized boolean loadDataFromDatabase() {
        armada.clear();
        armadaDisewa.clear();
        mapArmada.clear();
        platTerdaftar.clear();
        daftarSlotKapasitas.clear();

        try {
            DatabaseManager db = DatabaseManager.getInstance();
            db.initializeDatabase();

            try (Connection conn = db.getConnection()) {
                int totalKapasitas = 8;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT total_kapasitas FROM konfigurasi_parkir WHERE id = 1")) {
                    if (rs.next()) {
                        totalKapasitas = rs.getInt("total_kapasitas");
                    }
                }
                initSlotKapasitasMemory(totalKapasitas);

                String sql = "SELECT a.plat_nomor, a.merk, a.kategori, a.lat, a.lng, a.baterai, " +
                             "       a.tarif_per_hari, a.odo_meter, a.kondisi_mesin, a.catatan_fisik, " +
                             "       a.slot_parkir, a.status_sewa, a.suhu_kabin, " +
                             "       s.nama_penyewa, s.durasi_jam, s.dengan_supir, s.diskon_persen, s.total_biaya " +
                             "FROM armada a " +
                             "LEFT JOIN sewa_aktif s ON a.plat_nomor = s.plat_nomor " +
                             "ORDER BY a.plat_nomor ASC";

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        String plat = rs.getString("plat_nomor");
                        String merk = rs.getString("merk");
                        String kategori = rs.getString("kategori");
                        double lat = rs.getDouble("lat");
                        double lng = rs.getDouble("lng");
                        int bat = rs.getInt("baterai");
                        double tarifPerHari = rs.getDouble("tarif_per_hari");
                        int odo = rs.getInt("odo_meter");
                        String kondisi = rs.getString("kondisi_mesin");
                        String catatan = rs.getString("catatan_fisik");
                        String slot = rs.getString("slot_parkir");
                        String statusSewa = rs.getString("status_sewa");
                        double suhu = rs.getDouble("suhu_kabin");
                        boolean wasNullSuhu = rs.wasNull();

                        Kendaraan k;
                        if ("VIP".equalsIgnoreCase(kategori)) {
                            k = new MobilVIP(plat, merk, lat, lng, bat, (wasNullSuhu ? 20.0 : suhu),
                                             tarifPerHari, odo, kondisi, catatan);
                        } else {
                            k = new MobilReguler(plat, merk, lat, lng, bat,
                                                 tarifPerHari, odo, kondisi, catatan);
                        }

                        k.setSlotParkir(slot);

                        if ("DISEWA".equalsIgnoreCase(statusSewa)) {
                            k.setDisewa(true);
                            String penyewa = rs.getString("nama_penyewa");
                            k.setPenyewa((penyewa != null && !penyewa.trim().isEmpty()) ? penyewa : "Pelanggan Prioritas");
                            k.setDenganSupir(rs.getBoolean("dengan_supir"));
                            armadaDisewa.add(k);
                        } else {
                            k.setDisewa(false);
                            k.setPenyewa("");
                            k.setDenganSupir(false);
                        }

                        armada.add(k);
                        mapArmada.put(plat.toUpperCase(), k);
                        platTerdaftar.add(plat.toUpperCase());

                        if (slot != null && !"-".equals(slot) && !daftarSlotKapasitas.contains(slot)) {
                            daftarSlotKapasitas.add(slot);
                        }
                    }
                }

                if (!armada.isEmpty()) {
                    System.out.println("[PerusahaanRental] Sukses memuat " + armada.size() + " armada (" + 
                                       armadaDisewa.size() + " disewa) dari MySQL.");
                    return true;
                }
            }
        } catch (Exception e) {
            // MySQL offline
        }
        return false;
    }

    public synchronized void initDefaultArmadaMemory() {
        armada.clear();
        armadaDisewa.clear();
        mapArmada.clear();
        platTerdaftar.clear();
        initSlotKapasitasMemory(8);

        MobilReguler avanza = new MobilReguler("M 1001 SM", "Toyota Avanza Veloz", -7.0059, 113.8782, 88, 600000.0, 34200, "Sangat Baik", "Mulus, Bodi Bersih, AC Dingin");
        avanza.setSlotParkir("Slot A-01");
        avanza.setDisewa(true);
        avanza.setPenyewa("Ahmad Dani (Dishub Kab. Sumenep)");
        internalAdd(avanza);
        armadaDisewa.add(avanza);

        MobilVIP alphard = new MobilVIP("M 9999 VIP", "Toyota Alphard Executive", -7.0282, 113.8609, 94, 20.5, 2400000.0, 18500, "Sangat Baik", "Interior Mewah, Kulit Nappa, AC Dual Zone");
        alphard.setSlotParkir("Slot A-02");
        alphard.setDisewa(true);
        alphard.setPenyewa("Rombongan Tamu Pemkab Sumenep");
        alphard.setDenganSupir(true);
        internalAdd(alphard);
        armadaDisewa.add(alphard);

        MobilReguler innova = new MobilReguler("M 5555 XY", "Toyota Innova Reborn", -7.0150, 113.8650, 100, 900000.0, 42000, "Baik", "Kabin Lapang, Servis Rutin Auto2000");
        innova.setSlotParkir("Slot A-03");
        internalAdd(innova);

        MobilVIP palisade = new MobilVIP("M 8888 VIP", "Hyundai Palisade Signature", -7.0142, 113.8642, 96, 19.8, 2800000.0, 12000, "Sangat Baik", "Captain Seat, Sunroof, Audio Infinity");
        palisade.setSlotParkir("Slot A-04");
        internalAdd(palisade);

        MobilReguler brio = new MobilReguler("M 2345 AB", "Honda Brio RS", -7.0162, 113.8662, 85, 500000.0, 28000, "Dalam Perbaikan", "Sedang servis rem & ganti oli di bengkel resmi");
        brio.setSlotParkir("Slot B-01");
        internalAdd(brio);

        System.out.println("[PerusahaanRental] In-memory default dataset siap (5 armada: 2 disewa, 2 standby, 1 perbaikan).");
    }

    private void internalAdd(Kendaraan k) {
        armada.add(k);
        mapArmada.put(k.getPlatNomor().toUpperCase(), k);
        platTerdaftar.add(k.getPlatNomor().toUpperCase());
    }

    public synchronized void resetToDefaultTestDataset() {
        initDefaultArmadaMemory();
    }

    private void initSlotKapasitasMemory(int newCapacity) {
        daftarSlotKapasitas.clear();
        for (int i = 0; i < newCapacity; i++) {
            char block = (char) ('A' + (i / 4));
            int num = (i % 4) + 1;
            daftarSlotKapasitas.add(String.format("Slot %c-%02d", block, num));
        }
    }

    public synchronized boolean setTotalKapasitasSlot(int newCapacity) {
        if (newCapacity < 4 || newCapacity < armada.size()) {
            return false;
        }

        initSlotKapasitasMemory(newCapacity);

        for (Kendaraan k : armada) {
            if (k.getSlotParkir() == null || "-".equals(k.getSlotParkir()) || !daftarSlotKapasitas.contains(k.getSlotParkir())) {
                assignPersistentSlot(k);
            }
        }

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE konfigurasi_parkir SET total_kapasitas = ? WHERE id = 1")) {
            ps.setInt(1, newCapacity);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }

        return true;
    }

    public synchronized boolean tambahKapasitasSlot(int tambahanSlot) {
        if (tambahanSlot <= 0) return false;
        return setTotalKapasitasSlot(daftarSlotKapasitas.size() + tambahanSlot);
    }

    public synchronized boolean kurangiKapasitasSlot(int jumlahKurang) throws KapasitasPenuhException {
        if (jumlahKurang <= 0) {
            throw new IllegalArgumentException("Jumlah pengurangan slot harus lebih besar dari 0.");
        }
        int currentSize = daftarSlotKapasitas.size();
        int targetSize = currentSize - jumlahKurang;

        if (targetSize < 4) {
            throw new IllegalArgumentException("Kapasitas pool minimal adalah 4 bay.");
        }
        if (targetSize < armada.size()) {
            throw new KapasitasPenuhException("Pengurangan slot gagal: Kapasitas target (" + targetSize + 
                ") lebih kecil dari total armada aktif (" + armada.size() + ").", currentSize, targetSize);
        }

        for (int i = targetSize; i < currentSize; i++) {
            String slotName = daftarSlotKapasitas.get(i);
            for (Kendaraan k : armada) {
                if (slotName.equals(k.getSlotParkir())) {
                    throw new KapasitasPenuhException("Pengurangan slot ditolak: Bay " + slotName + 
                        " sedang ditempati oleh armada " + k.getPlatNomor() + " (" + k.getMerk() + ").", currentSize, targetSize);
                }
            }
        }

        return setTotalKapasitasSlot(targetSize);
    }

    /**
     * P11 - Menambah objek kendaraan ke collection dengan validasi duplikasi.
     */
    public synchronized boolean tambahKendaraan(Kendaraan k) throws KapasitasPenuhException {
        if (k == null) {
            throw new IllegalArgumentException("Objek kendaraan tidak boleh null.");
        }

        String platKey = k.getPlatNomor().trim().toUpperCase();
        if (platTerdaftar.contains(platKey) || mapArmada.containsKey(platKey)) {
            throw new IllegalArgumentException("Kendaraan dengan plat nomor " + k.getPlatNomor() + " sudah terdaftar.");
        }

        int maxLimit = 32;
        if (armada.size() >= daftarSlotKapasitas.size()) {
            if (daftarSlotKapasitas.size() >= maxLimit) {
                throw new KapasitasPenuhException("Kapasitas parkir pool telah penuh (" + daftarSlotKapasitas.size() + 
                    "/" + maxLimit + " bay).", daftarSlotKapasitas.size(), armada.size() + 1);
            }
            tambahKapasitasSlot(4);
        }

        assignPersistentSlot(k);

        // Sinkronisasi database MySQL jika aktif
        String sql = "INSERT INTO armada (plat_nomor, merk, kategori, lat, lng, baterai, " +
                     "tarif_per_hari, odo_meter, kondisi_mesin, catatan_fisik, slot_parkir, status_sewa, suhu_kabin) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getPlatNomor());
            ps.setString(2, k.getMerk());
            ps.setString(3, k.getKategori());
            ps.setDouble(4, k.getLat());
            ps.setDouble(5, k.getLng());
            ps.setInt(6, k.getBaterai());
            ps.setDouble(7, k.getTarifPerHari());
            ps.setInt(8, k.getOdoMeterKm());
            ps.setString(9, k.getKondisiMesin());
            ps.setString(10, k.getCatatanFisik());
            ps.setString(11, k.getSlotParkir());
            ps.setString(12, k.isDisewa() ? "DISEWA" : "STANDBY");
            if (k instanceof MobilVIP) {
                ps.setDouble(13, ((MobilVIP) k).getSuhuKabin());
            } else {
                ps.setNull(13, java.sql.Types.DOUBLE);
            }
            ps.executeUpdate();
        } catch (Exception ignored) {
        }

        internalAdd(k);
        return true;
    }

    /**
     * P11 - Mencari kendaraan berdasarkan plat nomor.
     */
    public Kendaraan cariKendaraan(String platNomor) {
        if (platNomor == null) return null;
        return mapArmada.get(platNomor.trim().toUpperCase());
    }

    public Kendaraan findKendaraan(String platNomor) {
        return cariKendaraan(platNomor);
    }

    /**
     * P11 - Menghapus kendaraan dari koleksi.
     */
    public synchronized boolean hapusKendaraan(String platNomor) {
        Kendaraan target = cariKendaraan(platNomor);
        if (target == null) {
            throw new IllegalArgumentException("Kendaraan dengan plat " + platNomor + " tidak ditemukan.");
        }
        if (target.isDisewa() || armadaDisewa.contains(target)) {
            throw new IllegalStateException("Kendaraan " + platNomor + " sedang disewa di jalan, tidak dapat dihapus.");
        }

        String sql = "DELETE FROM armada WHERE plat_nomor = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, platNomor);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }

        target.setSlotParkir("-");
        armada.remove(target);
        mapArmada.remove(platNomor.trim().toUpperCase());
        platTerdaftar.remove(platNomor.trim().toUpperCase());
        return true;
    }

    /**
     * P11 - Menampilkan semua kendaraan ke konsol.
     */
    public void tampilkanSemuaKendaraan() {
        if (armada.isEmpty()) {
            System.out.println("Koleksi armada masih kosong.");
            return;
        }

        System.out.println("==========================================================================================");
        System.out.printf("%-4s | %-12s | %-25s | %-8s | %-10s | %-12s | %-10s\n",
                "No", "Plat Nomor", "Merk / Seri", "Kategori", "Bay Slot", "Status", "Tarif Harian");
        System.out.println("------------------------------------------------------------------------------------------");
        int no = 1;
        for (Kendaraan k : armada) {
            System.out.printf(Locale.US, "%-4d | %-12s | %-25s | %-8s | %-10s | %-12s | Rp %,.0f\n",
                    no++, k.getPlatNomor(), k.getMerk(), k.getKategori(), k.getSlotParkir(),
                    (k.isDisewa() ? "DISEWA (" + k.getPenyewa() + ")" : "STANDBY"), k.getTarifPerHari());
        }
        System.out.println("==========================================================================================");
        System.out.println("Total armada: " + armada.size() + " unit (Sedang disewa: " + armadaDisewa.size() + " unit)");
    }

    /**
     * P10 - Memproses transaksi sewa armada dengan validasi defensif komprehensif.
     */
    public synchronized String prosesSewa(String platNomor, String namaPenyewa, int durasiJam, boolean denganSupir, double diskonPersen)
            throws KendaraanTidakTersediaException, KendaraanRusakException, IllegalArgumentException {
        
        // 1. Validasi eksistensi armada
        Kendaraan k = cariKendaraan(platNomor);
        if (k == null) {
            throw new IllegalArgumentException("Kendaraan dengan plat " + platNomor + " tidak ditemukan.");
        }

        // 2. Validasi kelayakan kondisi mesin
        if ("Dalam Perbaikan".equalsIgnoreCase(k.getKondisiMesin())) {
            throw new KendaraanRusakException("Unit sedang dalam perbaikan dan tidak layak operasi.", k.getPlatNomor(), k.getKondisiMesin());
        }

        // 3. Validasi status ketersediaan
        if (k.isDisewa()) {
            throw new KendaraanTidakTersediaException("Kendaraan sedang disewa oleh pelanggan lain.", k.getPlatNomor());
        }

        // 4. Validasi parameter input
        if (durasiJam <= 0) {
            throw new IllegalArgumentException("Durasi sewa harus lebih dari 0 jam.");
        }
        if (diskonPersen < 0.0 || diskonPersen > 100.0) {
            throw new IllegalArgumentException("Diskon harus di antara 0% dan 100%.");
        }
        if (namaPenyewa == null || namaPenyewa.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama penyewa tidak boleh kosong.");
        }

        double totalBiaya = k.hitungBiayaSewa(durasiJam, denganSupir, diskonPersen);
        String finalPenyewa = namaPenyewa.trim();

        // 5. Transaksi ACID database
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps1 = conn.prepareStatement("UPDATE armada SET status_sewa = 'DISEWA' WHERE plat_nomor = ?")) {
                    ps1.setString(1, platNomor);
                    ps1.executeUpdate();
                }

                String sqlSewa = "INSERT INTO sewa_aktif (plat_nomor, nama_penyewa, no_hp, durasi_jam, dengan_supir, diskon_persen, total_biaya) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                                 "ON DUPLICATE KEY UPDATE nama_penyewa = VALUES(nama_penyewa), no_hp = VALUES(no_hp), " +
                                 "durasi_jam = VALUES(durasi_jam), dengan_supir = VALUES(dengan_supir), " +
                                 "diskon_persen = VALUES(diskon_persen), total_biaya = VALUES(total_biaya)";
                try (PreparedStatement ps2 = conn.prepareStatement(sqlSewa)) {
                    ps2.setString(1, platNomor);
                    ps2.setString(2, finalPenyewa);
                    ps2.setString(3, "08" + (int)(Math.random() * 90000000 + 10000000));
                    ps2.setInt(4, durasiJam);
                    ps2.setBoolean(5, denganSupir);
                    ps2.setDouble(6, diskonPersen);
                    ps2.setDouble(7, totalBiaya);
                    ps2.executeUpdate();
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception ignored) {
        }

        // 6. Mutasi state memori HANYA setelah seluruh validasi lulus
        k.setDisewa(true);
        k.setPenyewa(finalPenyewa);
        k.setDenganSupir(denganSupir);
        if (!armadaDisewa.contains(k)) {
            armadaDisewa.add(k);
        }

        return String.format(Locale.US,
            "SUKSES|Plat: %s | Unit: %s | Penyewa: %s | Durasi: %d Jam | Layanan: %s | Diskon: %.0f%% | Total Tagihan: Rp %,.0f | Status: Meluncur ke Peta",
            k.getPlatNomor(), k.getMerk(), k.getPenyewa(), durasiJam, (denganSupir ? "Dengan Supir" : "Lepas Kunci"), diskonPersen, totalBiaya);
    }

    public synchronized String prosesSewa(String platNomor, String namaPenyewa, int durasiJam)
            throws KendaraanTidakTersediaException, KendaraanRusakException, IllegalArgumentException {
        return prosesSewa(platNomor, namaPenyewa, durasiJam, false, 0.0);
    }

    public synchronized void kembalikanArmada(Kendaraan k) {
        if (k == null || !armadaDisewa.contains(k)) return;

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps1 = conn.prepareStatement(
                        "UPDATE armada SET status_sewa = 'STANDBY', lat = -7.0150, lng = 113.8650 WHERE plat_nomor = ?")) {
                    ps1.setString(1, k.getPlatNomor());
                    ps1.executeUpdate();
                }
                try (PreparedStatement ps2 = conn.prepareStatement(
                        "DELETE FROM sewa_aktif WHERE plat_nomor = ?")) {
                    ps2.setString(1, k.getPlatNomor());
                    ps2.executeUpdate();
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception ignored) {
        }

        k.setDisewa(false);
        k.setPenyewa("");
        k.setDenganSupir(false);
        k.setLokasi(-7.0150, 113.8650);
        armadaDisewa.remove(k);
    }

    public synchronized boolean kembalikanArmada(String platNomor) {
        Kendaraan k = cariKendaraan(platNomor);
        if (k == null) {
            throw new IllegalArgumentException("Kendaraan dengan plat " + platNomor + " tidak ditemukan.");
        }
        if (!k.isDisewa()) {
            throw new IllegalStateException("Kendaraan " + platNomor + " saat ini tidak sedang disewa (STANDBY di depot).");
        }
        kembalikanArmada(k);
        return true;
    }

    public synchronized boolean editKendaraan(String platNomor, double tarifHarianBaru, String kondisiBaru, String catatanBaru) {
        Kendaraan k = cariKendaraan(platNomor);
        if (k == null) {
            throw new IllegalArgumentException("Kendaraan dengan plat " + platNomor + " tidak ditemukan.");
        }
        if (tarifHarianBaru <= 0) {
            throw new IllegalArgumentException("Tarif harian harus bernilai positif (> 0).");
        }

        k.setTarifPerHari(tarifHarianBaru);
        if (kondisiBaru != null && !kondisiBaru.trim().isEmpty()) {
            k.setKondisiMesin(kondisiBaru.trim());
        }
        if (catatanBaru != null) {
            k.setCatatanFisik(catatanBaru);
        }
        return true;
    }

    private void assignPersistentSlot(Kendaraan k) {
        for (String slot : daftarSlotKapasitas) {
            boolean terpakai = false;
            for (Kendaraan ada : armada) {
                if (ada != k && slot.equals(ada.getSlotParkir())) {
                    terpakai = true;
                    break;
                }
            }
            if (!terpakai) {
                k.setSlotParkir(slot);
                return;
            }
        }
        initSlotKapasitasMemory(daftarSlotKapasitas.size() + 4);
        assignPersistentSlot(k);
    }

    /**
     * P12 - Simpan data armada ke file teks.
     */
    public synchronized void simpanKeFile(Path lokasi) throws IOException {
        fileRepository.simpan(lokasi, armada);
    }

    /**
     * P12 - Muat data armada dari file teks secara defensif.
     */
    public synchronized void muatDariFile(Path lokasi) throws IOException {
        List<Kendaraan> dataBaru = fileRepository.muat(lokasi);

        // Penggantian collection hanya setelah data file terverifikasi valid tanpa exception
        armada.clear();
        armadaDisewa.clear();
        mapArmada.clear();
        platTerdaftar.clear();

        for (Kendaraan k : dataBaru) {
            internalAdd(k);
            if (k.isDisewa()) {
                armadaDisewa.add(k);
            }
        }

        // Sinkronisasi slot parkir
        for (Kendaraan k : armada) {
            if (k.getSlotParkir() == null || "-".equals(k.getSlotParkir()) || !daftarSlotKapasitas.contains(k.getSlotParkir())) {
                assignPersistentSlot(k);
            }
        }
    }

    public List<Kendaraan> getArmada() {
        return Collections.unmodifiableList(armada);
    }

    public List<Kendaraan> getArmadaDisewa() {
        return Collections.unmodifiableList(armadaDisewa);
    }

    public List<String> getDaftarSlotKapasitas() {
        return Collections.unmodifiableList(daftarSlotKapasitas);
    }

    public String getNamaCabang() {
        return namaCabang;
    }

    public String getFleetJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < armada.size(); i++) {
            sb.append(armada.get(i).toJson());
            if (i < armada.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public String getParkingJson() {
        int totalSlots = daftarSlotKapasitas.size();
        int occupiedStandby = 0;
        int onRoadCount = armadaDisewa.size();

        StringBuilder slotsJson = new StringBuilder("[");
        for (int i = 0; i < totalSlots; i++) {
            String slotName = daftarSlotKapasitas.get(i);
            Kendaraan penghuni = null;
            for (Kendaraan k : armada) {
                if (slotName.equals(k.getSlotParkir())) {
                    penghuni = k;
                    break;
                }
            }

            slotsJson.append("{");
            slotsJson.append(String.format("\"slotNomor\":\"%s\",", slotName));
            slotsJson.append(String.format("\"baySlot\":\"%s\",", slotName));
            if (penghuni != null) {
                if (!penghuni.isDisewa()) {
                    occupiedStandby++;
                    slotsJson.append("\"status\":\"STANDBY\",");
                } else {
                    slotsJson.append("\"status\":\"DISEWA\",");
                }
                slotsJson.append(String.format("\"plat\":\"%s\",", penghuni.getPlatNomor()));
                slotsJson.append(String.format("\"merk\":\"%s\",", penghuni.getMerk()));
                slotsJson.append(String.format("\"kategori\":\"%s\",", penghuni.getKategori()));
                slotsJson.append(String.format(Locale.US, "\"baterai\":%d,", penghuni.getBaterai()));
                slotsJson.append(String.format(Locale.US, "\"lat\":%.6f,", penghuni.getLat()));
                slotsJson.append(String.format(Locale.US, "\"lng\":%.6f,", penghuni.getLng()));
                slotsJson.append(String.format("\"penyewa\":\"%s\",", penghuni.getPenyewa()));
                slotsJson.append(String.format(Locale.US, "\"tarifPerHari\":%.0f,", penghuni.getTarifPerHari()));
                slotsJson.append(String.format(Locale.US, "\"tarifPerJam\":%.0f,", penghuni.getTarifPerJamEquivalent()));
                slotsJson.append(String.format(Locale.US, "\"tarif\":%.0f,", penghuni.getTarifPerHari()));
                slotsJson.append(String.format("\"kondisiMesin\":\"%s\",", penghuni.getKondisiMesin()));
                slotsJson.append(String.format(Locale.US, "\"odoMeterKm\":%d,", penghuni.getOdoMeterKm()));
                slotsJson.append(String.format("\"catatanFisik\":\"%s\",", penghuni.getCatatanFisik()));
                slotsJson.append(String.format("\"bisaDisewa\":%b,", penghuni.isBisaDisewa()));
                if (penghuni instanceof MobilVIP) {
                    slotsJson.append(String.format(Locale.US, "\"suhu\":%.1f,", ((MobilVIP) penghuni).getSuhuKabin()));
                }
                slotsJson.append("\"kendaraan\":").append(penghuni.toJson());
            } else {
                slotsJson.append("\"status\":\"KOSONG\",");
                slotsJson.append("\"plat\":\"\",");
                slotsJson.append("\"merk\":\"\",");
                slotsJson.append("\"kategori\":\"\",");
                slotsJson.append("\"baterai\":0,");
                slotsJson.append("\"lat\":0.0,");
                slotsJson.append("\"lng\":0.0,");
                slotsJson.append("\"penyewa\":\"\",");
                slotsJson.append("\"bisaDisewa\":false,");
                slotsJson.append("\"kendaraan\":null");
            }
            slotsJson.append("}");
            if (i < totalSlots - 1) slotsJson.append(",");
        }
        slotsJson.append("]");

        int trueEmptySlots = totalSlots - armada.size();
        if (trueEmptySlots < 0) trueEmptySlots = 0;
        double occupancyRate = (totalSlots > 0) ? ((double) occupiedStandby / totalSlots) * 100.0 : 0.0;

        StringBuilder onRoadJson = new StringBuilder("[");
        for (int i = 0; i < armadaDisewa.size(); i++) {
            onRoadJson.append(armadaDisewa.get(i).toJson());
            if (i < armadaDisewa.size() - 1) onRoadJson.append(",");
        }
        onRoadJson.append("]");

        return String.format(Locale.US,
            "{\"totalKapasitas\":%d, \"slotTerisi\":%d, \"slotKosong\":%d, \"unitDiJalan\":%d, \"okupansi\":%.1f, \"slots\":%s, \"unitDiJalanList\":%s}",
            totalSlots, occupiedStandby, trueEmptySlots, onRoadCount, occupancyRate, slotsJson.toString(), onRoadJson.toString());
    }

    public String getStatsJson(int totalPelanggan) {
        int total = armada.size();
        int disewa = armadaDisewa.size();
        int tersedia = total - disewa;
        return String.format(Locale.US, 
            "{\"totalMobil\":%d, \"mobilDisewa\":%d, \"mobilTersedia\":%d, \"totalPelanggan\":%d}", 
            total, disewa, tersedia, totalPelanggan);
    }
}
