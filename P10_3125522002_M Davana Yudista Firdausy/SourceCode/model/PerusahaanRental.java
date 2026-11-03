package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import model.exception.KapasitasPenuhException;
import model.exception.KendaraanRusakException;
import model.exception.KendaraanTidakTersediaException;

/**
 * Agregator Utama: PerusahaanRental
 * Mengelola armada (Agregasi Kendaraan) dan kapasitas slot bay parkir di Depot Sumenep.
 * Terintegrasi penuh dengan persistensi basis data MySQL (Laragon - db_fleet_sumenep).
 * Menerapkan prinsip Exception Handling & Validasi Defensif (Modul 10):
 * "Validasi sebelum mutasi state; operasi yang ditolak karena error wajib mempertahankan state objek awal."
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class PerusahaanRental {
    private String namaCabang;
    private List<Kendaraan> armada;
    private List<Kendaraan> armadaDisewa;
    private final List<String> daftarSlotKapasitas;

    public PerusahaanRental(String namaCabang) {
        this.namaCabang = namaCabang;
        this.armada = new ArrayList<>();
        this.armadaDisewa = new ArrayList<>();
        this.daftarSlotKapasitas = new ArrayList<>();
        
        // Memuat seluruh data persisten langsung dari database MySQL (dengan fallback in-memory)
        loadDataFromDatabase();
    }

    /**
     * Memuat konfigurasi kapasitas parkir, daftar armada, dan transaksi sewa aktif dari MySQL.
     * Jika database belum tersedia, sistem otomatis menggunakan fallback dataset memori.
     */
    public synchronized void loadDataFromDatabase() {
        armada.clear();
        armadaDisewa.clear();
        daftarSlotKapasitas.clear();

        boolean loadedFromDb = false;
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            db.initializeDatabase();

            try (Connection conn = db.getConnection()) {
                // 1. Muat total kapasitas parkir
                int totalKapasitas = 8;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT total_kapasitas FROM konfigurasi_parkir WHERE id = 1")) {
                    if (rs.next()) {
                        totalKapasitas = rs.getInt("total_kapasitas");
                    }
                }
                initSlotKapasitasMemory(totalKapasitas);

                // 2. Muat data armada beserta relasi sewa_aktif
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

                        if (slot != null && !"-".equals(slot) && !daftarSlotKapasitas.contains(slot)) {
                            daftarSlotKapasitas.add(slot);
                        }
                    }
                }

                if (!armada.isEmpty()) {
                    loadedFromDb = true;
                    System.out.println("[PerusahaanRental] Sukses memuat " + armada.size() + " armada (" + 
                                       armadaDisewa.size() + " sedang disewa) dari MySQL.");
                }
            }
        } catch (Exception e) {
            System.err.println("[PerusahaanRental] Database offline/kendala (" + e.getMessage() + "). Mengaktifkan in-memory dataset.");
        }

        if (!loadedFromDb) {
            initDefaultArmadaMemory();
        }
    }

    private void initDefaultArmadaMemory() {
        armada.clear();
        armadaDisewa.clear();
        initSlotKapasitasMemory(8);

        MobilReguler avanza = new MobilReguler("M 1001 SM", "Toyota Avanza Veloz", -7.0059, 113.8782, 88, 600000.0, 34200, "Sangat Baik", "Mulus, Bodi Bersih, AC Dingin");
        avanza.setSlotParkir("Slot A-01");
        avanza.setDisewa(true);
        avanza.setPenyewa("Ahmad Dani (Dishub Kab. Sumenep)");
        armada.add(avanza);
        armadaDisewa.add(avanza);

        MobilVIP alphard = new MobilVIP("M 9999 VIP", "Toyota Alphard Executive", -7.0282, 113.8609, 94, 20.5, 2400000.0, 18500, "Sangat Baik", "Interior Mewah, Kulit Nappa, AC Dual Zone");
        alphard.setSlotParkir("Slot A-02");
        alphard.setDisewa(true);
        alphard.setPenyewa("Rombongan Tamu Pemkab Sumenep");
        alphard.setDenganSupir(true);
        armada.add(alphard);
        armadaDisewa.add(alphard);

        MobilReguler innova = new MobilReguler("M 5555 XY", "Toyota Innova Reborn", -7.0150, 113.8650, 100, 900000.0, 42000, "Baik", "Kabin Lapang, Servis Rutin Auto2000");
        innova.setSlotParkir("Slot A-03");
        armada.add(innova);

        MobilVIP palisade = new MobilVIP("M 8888 VIP", "Hyundai Palisade Signature", -7.0142, 113.8642, 96, 19.8, 2800000.0, 12000, "Sangat Baik", "Captain Seat, Sunroof, Audio Infinity");
        palisade.setSlotParkir("Slot A-04");
        armada.add(palisade);

        MobilReguler brio = new MobilReguler("M 2345 AB", "Honda Brio RS", -7.0162, 113.8662, 85, 500000.0, 28000, "Dalam Perbaikan", "Sedang servis rem & ganti oli di bengkel resmi");
        brio.setSlotParkir("Slot B-01");
        armada.add(brio);

        System.out.println("[PerusahaanRental] In-memory dataset siap (5 armada: 2 disewa, 2 standby, 1 dalam perbaikan).");
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

    /**
     * Mengatur total kapasitas slot parkir di pool dan menyimpannya ke MySQL jika tersedia.
     */
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
        } catch (Exception e) {
            // Abaikan jika database offline
        }

        return true;
    }

    /**
     * Menambah kapasitas slot parkir (misal +4 bay)
     */
    public synchronized boolean tambahKapasitasSlot(int tambahanSlot) {
        if (tambahanSlot <= 0) return false;
        return setTotalKapasitasSlot(daftarSlotKapasitas.size() + tambahanSlot);
    }

    /**
     * Mengurangi kapasitas slot bay kosong dari ekor daftar (misal C-04 ke C-01).
     * Menerapkan Exception Handling (Modul 10):
     * Melempar KapasitasPenuhException jika slot yang akan dipotong sedang ditempati armada.
     */
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
                ") lebih kecil dari total armada yang terdaftar (" + armada.size() + ").", currentSize, targetSize);
        }

        // Validasi state slot: Periksa apakah slot ekor yang akan dipotong sedang ditempati armada
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
     * Menambah kendaraan secara dinamis ke armada dan menyimpannya ke MySQL.
     * Menerapkan Exception Handling (Modul 10):
     * Melempar KapasitasPenuhException jika pool garasi sudah mencapai batas maksimal dan tidak bisa diperluas.
     */
    public synchronized boolean tambahKendaraan(Kendaraan k) throws KapasitasPenuhException {
        if (k == null) {
            throw new IllegalArgumentException("Objek kendaraan tidak boleh null.");
        }
        if (armada.contains(k)) {
            return false;
        }

        // Cek duplikasi plat nomor
        for (Kendaraan existing : armada) {
            if (existing.getPlatNomor().equalsIgnoreCase(k.getPlatNomor().trim())) {
                throw new IllegalArgumentException("Kendaraan dengan plat " + k.getPlatNomor() + " sudah terdaftar di armada.");
            }
        }

        int maxLimit = 32; // Batas fisik lahan depot Sumenep
        if (armada.size() >= daftarSlotKapasitas.size()) {
            if (daftarSlotKapasitas.size() >= maxLimit) {
                throw new KapasitasPenuhException("Kapasitas parkir pool telah penuh (" + daftarSlotKapasitas.size() + 
                    "/" + maxLimit + " bay). Tidak dapat menampung unit baru.", daftarSlotKapasitas.size(), armada.size() + 1);
            }
            tambahKapasitasSlot(4);
        }

        assignPersistentSlot(k);

        // Simpan ke MySQL jika koneksi aktif
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
        } catch (Exception e) {
            // Abaikan kegagalan SQL jika offline, lanjutkan ke memori
        }

        armada.add(k);
        return true;
    }

    /**
     * Menghapus unit kendaraan dari armada dan menghapusnya dari MySQL jika sedang TIDAK disewa.
     */
    public synchronized boolean hapusKendaraan(String platNomor) {
        Kendaraan target = findKendaraan(platNomor);
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
        } catch (Exception e) {
            // Lanjutkan jika offline
        }

        target.setSlotParkir("-");
        armada.remove(target);
        return true;
    }

    /**
     * Mengedit tarif harian, kondisi mesin, dan catatan fisik kendaraan.
     */
    public synchronized boolean editKendaraan(String platNomor, double tarifHarianBaru, String kondisiBaru, String catatanBaru) {
        Kendaraan k = findKendaraan(platNomor);
        if (k == null) {
            throw new IllegalArgumentException("Kendaraan dengan plat " + platNomor + " tidak ditemukan.");
        }

        if (tarifHarianBaru <= 0) {
            throw new IllegalArgumentException("Tarif harian harus bernilai positif (> 0).");
        }

        double finalTarif = tarifHarianBaru;
        String finalKondisi = (kondisiBaru != null && !kondisiBaru.trim().isEmpty()) ? kondisiBaru.trim() : k.getKondisiMesin();
        String finalCatatan = (catatanBaru != null) ? catatanBaru : k.getCatatanFisik();

        String sql = "UPDATE armada SET tarif_per_hari = ?, kondisi_mesin = ?, catatan_fisik = ? WHERE plat_nomor = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, finalTarif);
            ps.setString(2, finalKondisi);
            ps.setString(3, finalCatatan);
            ps.setString(4, platNomor);
            ps.executeUpdate();
        } catch (Exception e) {
            // Lanjutkan jika offline
        }

        // Perbarui state objek memori
        k.setTarifPerHari(finalTarif);
        k.setKondisiMesin(finalKondisi);
        k.setCatatanFisik(finalCatatan);
        return true;
    }

    public void registerArmada(Kendaraan k) throws KapasitasPenuhException { 
        tambahKendaraan(k);
    }

    public void registerArmadaWithSlot(Kendaraan k, String slotName) throws KapasitasPenuhException {
        if (!armada.contains(k)) {
            if (!daftarSlotKapasitas.contains(slotName)) {
                daftarSlotKapasitas.add(slotName);
            }
            k.setSlotParkir(slotName);
            tambahKendaraan(k);
        }
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
     * Overload prosesSewa untuk durasi default 24 jam tanpa diskon
     */
    public synchronized String prosesSewa(String platNomor, String namaPenyewa, int durasiJam) 
            throws KendaraanTidakTersediaException, KendaraanRusakException, IllegalArgumentException {
        return prosesSewa(platNomor, namaPenyewa, durasiJam, false, 0.0);
    }

    /**
     * Memproses transaksi penyewaan armada dengan validasi state defensif sesuai Modul 10:
     * "Validate before mutating state; operations rejected due to errors must preserve original state intact."
     * 
     * @throws KendaraanTidakTersediaException jika armada sedang disewa oleh pelanggan lain
     * @throws KendaraanRusakException jika unit armada dalam kondisi "Dalam Perbaikan"
     * @throws IllegalArgumentException jika parameter input tidak valid (unit tidak ditemukan, durasi <= 0, diskon di luar rentang, nama kosong)
     */
    public synchronized String prosesSewa(String platNomor, String namaPenyewa, int durasiJam, boolean denganSupir, double diskonPersen) 
            throws KendaraanTidakTersediaException, KendaraanRusakException, IllegalArgumentException {
        
        // 1. Validasi Eksistensi Armada
        Kendaraan k = findKendaraan(platNomor);
        if (k == null) {
            throw new IllegalArgumentException("Kendaraan dengan plat " + platNomor + " tidak ditemukan.");
        }

        // 2. Validasi Kelayakan Operasional (Kondisi Mesin)
        if ("Dalam Perbaikan".equalsIgnoreCase(k.getKondisiMesin())) {
            throw new KendaraanRusakException("Unit sedang dalam perbaikan dan tidak layak operasi.", k.getPlatNomor(), k.getKondisiMesin());
        }

        // 3. Validasi Ketersediaan Unit Armada
        if (k.isDisewa()) {
            throw new KendaraanTidakTersediaException("Kendaraan sedang disewa oleh pelanggan lain.", k.getPlatNomor());
        }

        // 4. Validasi Parameter Input Transaksi
        if (durasiJam <= 0) {
            throw new IllegalArgumentException("Durasi sewa harus lebih dari 0 jam.");
        }
        if (diskonPersen < 0.0 || diskonPersen > 100.0) {
            throw new IllegalArgumentException("Diskon harus di antara 0% dan 100%.");
        }
        if (namaPenyewa == null || namaPenyewa.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama penyewa tidak boleh kosong.");
        }

        // Perhitungan biaya secara polimorfis
        double totalBiaya = k.hitungBiayaSewa(durasiJam, denganSupir, diskonPersen);
        String finalPenyewa = namaPenyewa.trim();

        // 5. Transaksi ACID ke MySQL (dilakukan jika database aktif)
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
        } catch (Exception e) {
            // Database offline fallback, tetap mutasi memori
        }

        // 6. Mutasi State Objek Memori HANYA SETELAH SELURUH VALIDASI LULUS
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

    public void sewakanArmada(Kendaraan k) {
        sewakanArmada(k, "Penyewa Prioritas Sumenep");
    }

    public void sewakanArmada(Kendaraan k, String namaPenyewa) { 
        if (k != null && armada.contains(k) && !armadaDisewa.contains(k)) {
            if ("Dalam Perbaikan".equalsIgnoreCase(k.getKondisiMesin())) {
                System.out.println("[WARNING] Gagal menyewakan " + k.getPlatNomor() + " karena status Dalam Perbaikan.");
                return;
            }
            k.setDisewa(true);
            k.setPenyewa((namaPenyewa != null && !namaPenyewa.trim().isEmpty()) ? namaPenyewa : "Penyewa Operasional");
            armadaDisewa.add(k); 
        }
    }

    /**
     * Mengembalikan kendaraan ke pool/garasi dan mereset status sewa ke STANDBY.
     */
    public synchronized void kembalikanArmada(Kendaraan k) {
        if (k == null || !armadaDisewa.contains(k)) return;

        // Transaksi ACID ke MySQL
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
        } catch (Exception e) {
            // Lanjutkan jika offline
        }

        // Perbarui state memori: Kembali ke status STANDBY di pool depot
        k.setDisewa(false);
        k.setPenyewa("");
        k.setDenganSupir(false);
        k.setLokasi(-7.0150, 113.8650);
        armadaDisewa.remove(k);
    }

    public synchronized boolean kembalikanArmada(String platNomor) {
        Kendaraan k = findKendaraan(platNomor);
        if (k == null) {
            throw new IllegalArgumentException("Kendaraan dengan plat " + platNomor + " tidak ditemukan.");
        }
        if (!k.isDisewa()) {
            throw new IllegalStateException("Kendaraan " + platNomor + " saat ini tidak sedang disewa (STANDBY di depot).");
        }
        kembalikanArmada(k);
        return true;
    }

    public Kendaraan findKendaraan(String plat) {
        if (plat == null) return null;
        for (Kendaraan k : armada) {
            if (k.getPlatNomor().equalsIgnoreCase(plat.trim())) {
                return k;
            }
        }
        return null;
    }

    public List<Kendaraan> getArmada() { 
        return armada; 
    }

    public List<Kendaraan> getArmadaDisewa() {
        return armadaDisewa;
    }

    public List<String> getDaftarSlotKapasitas() {
        return daftarSlotKapasitas;
    }

    public String getNamaCabang() {
        return namaCabang;
    }
    
    // Serialisasi data seluruh armada untuk pemetaan Leaflet
    public String getFleetJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < armada.size(); i++) {
            sb.append(armada.get(i).toJson());
            if (i < armada.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    // Serialisasi status parkir & ketersediaan armada untuk Page 2 (Parking & Fleet Availability)
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

    // Statistik KPI global
    public String getStatsJson(int totalPelanggan) {
        int total = armada.size();
        int disewa = armadaDisewa.size();
        int tersedia = total - disewa;
        return String.format(Locale.US, 
            "{\"totalMobil\":%d, \"mobilDisewa\":%d, \"mobilTersedia\":%d, \"totalPelanggan\":%d}", 
            total, disewa, tersedia, totalPelanggan);
    }
}
