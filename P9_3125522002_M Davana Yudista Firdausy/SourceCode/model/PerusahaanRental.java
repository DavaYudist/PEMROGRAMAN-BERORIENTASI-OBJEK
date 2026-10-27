package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Agregator Utama: PerusahaanRental
 * Mengelola armada (Agregasi Kendaraan) dan kapasitas slot bay parkir di Depot Sumenep.
 * Terintegrasi penuh dengan persistensi basis data MySQL (Laragon - db_fleet_sumenep).
 * Mendukung transaksi ACID untuk sewa/kembali, mutasi armada dinamis,
 * penyesuaian kapasitas parkir, dan serialisasi JSON.
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
        
        // Memuat seluruh data persisten langsung dari database MySQL
        loadDataFromDatabase();
    }

    /**
     * Memuat konfigurasi kapasitas parkir, daftar armada, dan transaksi sewa aktif dari MySQL.
     */
    public synchronized void loadDataFromDatabase() {
        DatabaseManager db = DatabaseManager.getInstance();
        db.initializeDatabase();

        armada.clear();
        armadaDisewa.clear();
        daftarSlotKapasitas.clear();

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

                    // Pastikan daftar slot kapasitas mengenali slot ini jika ada slot kustom
                    if (slot != null && !"-".equals(slot) && !daftarSlotKapasitas.contains(slot)) {
                        daftarSlotKapasitas.add(slot);
                    }
                }
            }

            System.out.println("[PerusahaanRental] Sukses memuat " + armada.size() + " armada (" + 
                               armadaDisewa.size() + " sedang disewa) dari MySQL.");

        } catch (SQLException e) {
            System.err.println("[PerusahaanRental] Gagal memuat data dari MySQL: " + e.getMessage());
            // Fallback memori standar jika koneksi database mengalami kendala
            initSlotKapasitasMemory(8);
        }
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
     * Mengatur total kapasitas slot parkir di pool dan menyimpannya ke MySQL.
     */
    public synchronized boolean setTotalKapasitasSlot(int newCapacity) {
        if (newCapacity < 4 || newCapacity < armada.size()) {
            return false;
        }

        initSlotKapasitasMemory(newCapacity);

        // Pastikan armada yang ada terpetakan ke slot yang valid
        for (Kendaraan k : armada) {
            if (k.getSlotParkir() == null || "-".equals(k.getSlotParkir()) || !daftarSlotKapasitas.contains(k.getSlotParkir())) {
                assignPersistentSlot(k);
            }
        }

        // Sinkronisasi ke MySQL konfigurasi_parkir
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE konfigurasi_parkir SET total_kapasitas = ? WHERE id = 1")) {
            ps.setInt(1, newCapacity);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[PerusahaanRental] Gagal memperbarui konfigurasi_parkir di MySQL: " + e.getMessage());
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
     */
    public synchronized boolean kurangiKapasitasSlot(int jumlahKurang) {
        if (jumlahKurang <= 0) return false;
        int currentSize = daftarSlotKapasitas.size();
        int targetSize = currentSize - jumlahKurang;

        if (targetSize < 4 || targetSize < armada.size()) {
            return false;
        }

        // Periksa apakah slot yang akan dipotong sedang ditempati armada
        for (int i = targetSize; i < currentSize; i++) {
            String slotName = daftarSlotKapasitas.get(i);
            for (Kendaraan k : armada) {
                if (slotName.equals(k.getSlotParkir())) {
                    return false;
                }
            }
        }

        return setTotalKapasitasSlot(targetSize);
    }

    /**
     * Menambah kendaraan secara dinamis ke armada dan menyimpannya ke MySQL.
     */
    public synchronized boolean tambahKendaraan(Kendaraan k) {
        if (k == null || armada.contains(k)) return false;

        // Cek duplikasi plat nomor di memori
        for (Kendaraan existing : armada) {
            if (existing.getPlatNomor().equalsIgnoreCase(k.getPlatNomor().trim())) {
                return false;
            }
        }

        // Jika slot penuh, otomatis ekspansi kapasitas +4 slot
        if (armada.size() >= daftarSlotKapasitas.size()) {
            tambahKapasitasSlot(4);
        }

        assignPersistentSlot(k);

        // Simpan ke MySQL armada
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
            armada.add(k);
            return true;
        } catch (SQLException e) {
            System.err.println("[PerusahaanRental] Gagal menambah kendaraan ke MySQL: " + e.getMessage());
            return false;
        }
    }

    /**
     * Menghapus unit kendaraan dari armada dan menghapusnya dari MySQL jika sedang TIDAK disewa.
     */
    public synchronized boolean hapusKendaraan(String platNomor) {
        Kendaraan target = findKendaraan(platNomor);
        if (target == null) return false;
        if (target.isDisewa() || armadaDisewa.contains(target)) {
            return false;
        }

        String sql = "DELETE FROM armada WHERE plat_nomor = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, platNomor);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                target.setSlotParkir("-");
                armada.remove(target);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PerusahaanRental] Gagal menghapus kendaraan dari MySQL: " + e.getMessage());
        }
        return false;
    }

    /**
     * Mengedit tarif harian, kondisi mesin, dan catatan fisik kendaraan di memori dan MySQL.
     */
    public synchronized boolean editKendaraan(String platNomor, double tarifHarianBaru, String kondisiBaru, String catatanBaru) {
        Kendaraan k = findKendaraan(platNomor);
        if (k == null) return false;

        double finalTarif = (tarifHarianBaru > 0) ? tarifHarianBaru : k.getTarifPerHari();
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

            // Perbarui state objek memori
            k.setTarifPerHari(finalTarif);
            k.setKondisiMesin(finalKondisi);
            k.setCatatanFisik(finalCatatan);
            return true;
        } catch (SQLException e) {
            System.err.println("[PerusahaanRental] Gagal update armada di MySQL: " + e.getMessage());
            return false;
        }
    }

    public void registerArmada(Kendaraan k) { 
        tambahKendaraan(k);
    }

    public void registerArmadaWithSlot(Kendaraan k, String slotName) {
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
        // Jika slot belum tersedia, perluas +4 bay dan cari kembali
        initSlotKapasitasMemory(daftarSlotKapasitas.size() + 4);
        assignPersistentSlot(k);
    }

    /**
     * Memproses transaksi penyewaan armada dengan durasi tertentu dan nama penyewa.
     * Mengunci sewa bila kendaraan dalam kondisi 'Dalam Perbaikan'.
     * Menjalankan transaksi ACID (UPDATE status_sewa dan INSERT sewa_aktif).
     */
    public synchronized String prosesSewa(String platNomor, String namaPenyewa, int durasiJam) {
        return prosesSewa(platNomor, namaPenyewa, durasiJam, false, 0.0);
    }

    public synchronized String prosesSewa(String platNomor, String namaPenyewa, int durasiJam, boolean denganSupir, double diskonPersen) {
        Kendaraan k = findKendaraan(platNomor);
        if (k == null) {
            return "ERROR: Kendaraan dengan plat " + platNomor + " tidak ditemukan.";
        }
        if (k.isDisewa()) {
            return "ERROR: Kendaraan " + platNomor + " sedang disewa oleh " + k.getPenyewa() + ".";
        }
        if ("Dalam Perbaikan".equalsIgnoreCase(k.getKondisiMesin())) {
            return "ERROR: Kendaraan sedang dalam perbaikan dan tidak dapat disewa!";
        }
        if (durasiJam <= 0) {
            return "ERROR: Durasi sewa harus minimal 1 jam.";
        }

        double totalBiaya = k.hitungBiayaSewa(durasiJam, denganSupir, diskonPersen);
        String finalPenyewa = (namaPenyewa != null && !namaPenyewa.trim().isEmpty()) ? namaPenyewa.trim() : "Pelanggan Prioritas Sumenep";

        // Transaksi ACID ke MySQL
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update status_sewa armada
                try (PreparedStatement ps1 = conn.prepareStatement("UPDATE armada SET status_sewa = 'DISEWA' WHERE plat_nomor = ?")) {
                    ps1.setString(1, platNomor);
                    ps1.executeUpdate();
                }

                // 2. Insert invoice ke sewa_aktif
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
        } catch (SQLException e) {
            System.err.println("[PerusahaanRental] Gagal menyimpan sewa ke MySQL: " + e.getMessage());
            return "ERROR: Gagal menyimpan transaksi sewa ke database: " + e.getMessage();
        }

        // Perbarui state memori
        k.setDenganSupir(denganSupir);
        sewakanArmada(k, finalPenyewa);

        return String.format(Locale.US,
            "SUKSES|Plat: %s | Unit: %s | Penyewa: %s | Durasi: %d Jam | Layanan: %s | Diskon: %.0f%% | Total Tagihan: Rp %,.0f | Status: Meluncur ke Peta",
            k.getPlatNomor(), k.getMerk(), k.getPenyewa(), durasiJam, (denganSupir ? "Dengan Supir" : "Lepas Kunci"), diskonPersen, totalBiaya);
    }

    public void sewakanArmada(Kendaraan k) {
        sewakanArmada(k, "Penyewa Prioritas Sumenep");
    }

    public void sewakanArmada(Kendaraan k, String namaPenyewa) { 
        if (armada.contains(k) && !armadaDisewa.contains(k)) {
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
     * Mengembalikan kendaraan ke pool/garasi dan menghapus catatan sewa di MySQL (ACID).
     */
    public synchronized void kembalikanArmada(Kendaraan k) {
        if (k == null || !armadaDisewa.contains(k)) return;

        // Transaksi ACID ke MySQL
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update status armada menjadi STANDBY dan reset koordinat depot
                try (PreparedStatement ps1 = conn.prepareStatement(
                        "UPDATE armada SET status_sewa = 'STANDBY', lat = -7.0150, lng = 113.8650 WHERE plat_nomor = ?")) {
                    ps1.setString(1, k.getPlatNomor());
                    ps1.executeUpdate();
                }

                // 2. Hapus dari tabel sewa_aktif
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
        } catch (SQLException e) {
            System.err.println("[PerusahaanRental] Gagal memproses pengembalian unit ke MySQL: " + e.getMessage());
        }

        // Perbarui state memori
        k.setDisewa(false);
        k.setPenyewa("");
        k.setDenganSupir(false);
        k.setLokasi(-7.0150, 113.8650);
        armadaDisewa.remove(k);
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
