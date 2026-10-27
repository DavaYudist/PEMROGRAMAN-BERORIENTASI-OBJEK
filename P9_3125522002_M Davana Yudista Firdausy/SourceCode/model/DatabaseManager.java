package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton Helper DatabaseManager
 * Mengelola koneksi JDBC MySQL ke Laragon (db_fleet_sumenep).
 * Menjalankan migrasi DDL otomatis (auto-create database & tables) dan initial seeding.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (3125522002)
 */
public class DatabaseManager {
    private static DatabaseManager instance;

    private static final String DB_NAME = "db_fleet_sumenep";
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseManager] Driver MySQL JDBC tidak ditemukan di classpath: " + e.getMessage());
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASS);
    }

    /**
     * Inisialisasi Database, Tabel, dan Seeding data awal bila armada kosong.
     */
    public synchronized void initializeDatabase() {
        // 1. Buat database jika belum ada
        try (Connection serverConn = getServerConnection();
             Statement stmt = serverConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME + ";");
            System.out.println("[DatabaseManager] Database '" + DB_NAME + "' terverifikasi/tersedia.");
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Gagal memverifikasi/membuat database: " + e.getMessage());
            return;
        }

        // 2. Buat tabel dan lakukan migrasi
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Tabel konfigurasi_parkir
            String sqlKonfigurasi = "CREATE TABLE IF NOT EXISTS konfigurasi_parkir (" +
                    "id INT PRIMARY KEY DEFAULT 1, " +
                    "total_kapasitas INT NOT NULL" +
                    ");";
            stmt.executeUpdate(sqlKonfigurasi);

            // Tabel armada
            String sqlArmada = "CREATE TABLE IF NOT EXISTS armada (" +
                    "plat_nomor VARCHAR(20) PRIMARY KEY, " +
                    "merk VARCHAR(100) NOT NULL, " +
                    "kategori ENUM('Reguler', 'VIP') NOT NULL, " +
                    "lat DOUBLE NOT NULL, " +
                    "lng DOUBLE NOT NULL, " +
                    "baterai INT NOT NULL, " +
                    "tarif_per_hari DOUBLE NOT NULL, " +
                    "odo_meter INT NOT NULL, " +
                    "kondisi_mesin VARCHAR(50) NOT NULL, " +
                    "catatan_fisik TEXT, " +
                    "slot_parkir VARCHAR(20) NOT NULL, " +
                    "status_sewa ENUM('STANDBY', 'DISEWA') DEFAULT 'STANDBY', " +
                    "suhu_kabin DOUBLE DEFAULT NULL" +
                    ");";
            stmt.executeUpdate(sqlArmada);

            // Tabel sewa_aktif
            String sqlSewaAktif = "CREATE TABLE IF NOT EXISTS sewa_aktif (" +
                    "plat_nomor VARCHAR(20) PRIMARY KEY, " +
                    "nama_penyewa VARCHAR(100) NOT NULL, " +
                    "no_hp VARCHAR(30), " +
                    "durasi_jam INT NOT NULL, " +
                    "dengan_supir BOOLEAN DEFAULT FALSE, " +
                    "diskon_persen DOUBLE DEFAULT 0.0, " +
                    "total_biaya DOUBLE NOT NULL, " +
                    "waktu_mulai TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (plat_nomor) REFERENCES armada(plat_nomor) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(sqlSewaAktif);

            System.out.println("[DatabaseManager] Seluruh skema tabel (armada, sewa_aktif, konfigurasi_parkir) siap.");

            // 3. Seeding konfigurasi parkir jika belum ada
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM konfigurasi_parkir WHERE id = 1")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO konfigurasi_parkir (id, total_kapasitas) VALUES (1, 8);");
                    System.out.println("[DatabaseManager] Seeding kapasitas parkir default (8 bay) berhasil.");
                }
            }

            // 4. Seeding data armada jika kosong
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM armada")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("[DatabaseManager] Tabel armada kosong. Melakukan initial seeding data default...");
                    seedInitialFleet(conn);
                }
            }

        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Gagal inisialisasi tabel: " + e.getMessage());
        }
    }

    private void seedInitialFleet(Connection conn) throws SQLException {
        String insertArmada = "INSERT INTO armada (plat_nomor, merk, kategori, lat, lng, baterai, tarif_per_hari, odo_meter, kondisi_mesin, catatan_fisik, slot_parkir, status_sewa, suhu_kabin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(insertArmada)) {
            // Avanza
            ps.setString(1, "M 1001 SM");
            ps.setString(2, "Toyota Avanza Veloz");
            ps.setString(3, "Reguler");
            ps.setDouble(4, -7.0059);
            ps.setDouble(5, 113.8782);
            ps.setInt(6, 88);
            ps.setDouble(7, 600000.0);
            ps.setInt(8, 34200);
            ps.setString(9, "Sangat Baik");
            ps.setString(10, "Mulus, Bodi Bersih, AC Dingin");
            ps.setString(11, "Slot A-01");
            ps.setString(12, "DISEWA");
            ps.setNull(13, java.sql.Types.DOUBLE);
            ps.addBatch();

            // Alphard
            ps.setString(1, "M 9999 VIP");
            ps.setString(2, "Toyota Alphard Executive");
            ps.setString(3, "VIP");
            ps.setDouble(4, -7.0282);
            ps.setDouble(5, 113.8609);
            ps.setInt(6, 94);
            ps.setDouble(7, 2400000.0);
            ps.setInt(8, 18500);
            ps.setString(9, "Sangat Baik");
            ps.setString(10, "Interior Mewah, Kulit Nappa, AC Dual Zone");
            ps.setString(11, "Slot A-02");
            ps.setString(12, "DISEWA");
            ps.setDouble(13, 20.5);
            ps.addBatch();

            // Innova
            ps.setString(1, "M 5555 XY");
            ps.setString(2, "Toyota Innova Reborn");
            ps.setString(3, "Reguler");
            ps.setDouble(4, -7.0150);
            ps.setDouble(5, 113.8650);
            ps.setInt(6, 100);
            ps.setDouble(7, 900000.0);
            ps.setInt(8, 42000);
            ps.setString(9, "Baik");
            ps.setString(10, "Kabin Lapang, Servis Rutin Auto2000");
            ps.setString(11, "Slot A-03");
            ps.setString(12, "STANDBY");
            ps.setNull(13, java.sql.Types.DOUBLE);
            ps.addBatch();

            // Palisade
            ps.setString(1, "M 8888 VIP");
            ps.setString(2, "Hyundai Palisade Signature");
            ps.setString(3, "VIP");
            ps.setDouble(4, -7.0142);
            ps.setDouble(5, 113.8642);
            ps.setInt(6, 96);
            ps.setDouble(7, 2800000.0);
            ps.setInt(8, 12000);
            ps.setString(9, "Sangat Baik");
            ps.setString(10, "Captain Seat, Sunroof, Audio Infinity");
            ps.setString(11, "Slot A-04");
            ps.setString(12, "STANDBY");
            ps.setDouble(13, 19.8);
            ps.addBatch();

            // Brio
            ps.setString(1, "M 2345 AB");
            ps.setString(2, "Honda Brio RS");
            ps.setString(3, "Reguler");
            ps.setDouble(4, -7.0162);
            ps.setDouble(5, 113.8662);
            ps.setInt(6, 85);
            ps.setDouble(7, 500000.0);
            ps.setInt(8, 28000);
            ps.setString(9, "Dalam Perbaikan");
            ps.setString(10, "Sedang servis rem & ganti oli di bengkel resmi");
            ps.setString(11, "Slot B-01");
            ps.setString(12, "STANDBY");
            ps.setNull(13, java.sql.Types.DOUBLE);
            ps.addBatch();

            ps.executeBatch();
            System.out.println("[DatabaseManager] 5 armada awal berhasil di-seed ke database.");
        }

        // Seeding 2 sewa aktif awal
        String insertSewa = "INSERT INTO sewa_aktif (plat_nomor, nama_penyewa, no_hp, durasi_jam, dengan_supir, diskon_persen, total_biaya) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSewa)) {
            // Sewa Avanza
            ps.setString(1, "M 1001 SM");
            ps.setString(2, "Ahmad Dani (Dishub Kab. Sumenep)");
            ps.setString(3, "08123456789");
            ps.setInt(4, 24);
            ps.setBoolean(5, false);
            ps.setDouble(6, 0.0);
            ps.setDouble(7, 600000.0);
            ps.addBatch();

            // Sewa Alphard
            ps.setString(1, "M 9999 VIP");
            ps.setString(2, "Rombongan Tamu Pemkab Sumenep");
            ps.setString(3, "08987654321");
            ps.setInt(4, 24);
            ps.setBoolean(5, true);
            ps.setDouble(6, 0.0);
            ps.setDouble(7, 2550000.0);
            ps.addBatch();

            ps.executeBatch();
            System.out.println("[DatabaseManager] Data sewa aktif awal berhasil di-seed.");
        }
    }
}
