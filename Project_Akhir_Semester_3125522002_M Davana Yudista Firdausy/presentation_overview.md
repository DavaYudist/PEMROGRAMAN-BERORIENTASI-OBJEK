# PRESENTASI PROYEK AKHIR PEMROGRAMAN BERORIENTASI OBJEK
## FleetSumenep - Rental Car Fleet Monitoring & Interactive Bay Management System
**Mahasiswa:** M. Davana Yudista Firdausy (NRP: 3125522002)  
**Kelas:** A D3 TI-SM | **Institusi:** Politeknik Elektronika Negeri Surabaya (PENS PSDKU Sumenep)  
**Tahun:** 2026

---

### SLIDE 1: IDENTITAS PROYEK, MASALAH, & TARGET PENGGUNA
* **Nama Proyek:** FleetSumenep - Rental Car Fleet Monitoring & Interactive Bay Management
* **Latar Belakang & Permasalahan:**
  * Pengelolaan rental mobil konvensional rawan mengalami *double-booking*, hilangnya pelacakan posisi geografis armada di jalan, serta kekeliruan alokasi slot bay parkir garasi pool.
  * Kurangnya validasi defensif sebelum mutasi state sering kali menyebabkan data terkorupsi saat transaksi sewa gagal atau mengalami anomali.
* **Pengguna Sasaran:**
  * Administrator Operasional Depot Rental Sumenep.
  * Petugas Dispatcher Armada & Pemantau GPS Telemetri.
  * Bagian Kasir / Front-Office Billing Rental.

---

### SLIDE 2: FITUR UTAMA & BATASAN APLIKASI
* **Fitur Utama:**
  1. **Dual-Launcher Arsitektur:** GUI Desktop Hybrid (JavaFX WebView + Leaflet Map) dan CLI Console Menu interaktif (P13).
  2. **Live GPS Fleet Telemetry:** Pelacakan posisi koordinat real-time kendaraan di Sumenep beserta indikator baterai dan iklim suhu kabin VIP.
  3. **Interactive Parking Bay Management:** Visualisasi grid denah parkir pool garasi 8–32 slot dengan rasio okupansi otomatis.
  4. **Defensive Rental Transaction Engine:** Transaksi penyewaan atomik dengan pencegahan *double-renting*, pemblokiran armada berstatus "Dalam Perbaikan", dan kalkulasi tarif polimorfis.
  5. **Dual-Engine Persistence:** Persistensi relasional MySQL (JDBC ACID) dan persistensi file teks pipe-delimited (`armada_store.txt`) dengan dirty-state confirmation guard.
* **Batasan Sistem:**
  * Kapasitas fisik bay parkir pool garasi dibatasi minimal 4 slot dan maksimal 32 slot.
  * Format penyimpanan file berbasis pipe delimiter (`|`), data input dilarang mengandung karakter pemisah.

---

### SLIDE 3: UML CLASS DIAGRAM & PEMBAGIAN TANGGUNG JAWAB OBJEK
* **Struktur Kelas Domain:**
  * `PelacakGPS` *(Interface)*: Kontrak pelacakan telemetri spasial (`getLat()`, `getLng()`, `getBaterai()`).
  * `Kendaraan` *(Abstract Superclass)*: Menampung enkapsulasi spesifikasi, nomor plat immutable, tarif harian, kondisi mesin, odometer, dan status operasional.
  * `MobilReguler` & `MobilVIP` *(Subclasses)*: Mewarisi `Kendaraan` dengan aturan polimorfis biaya sewa dan atribut suhu kabin khusus VIP.
  * `PerusahaanRental` *(Aggregator Manager)*: Mengelola kumpulan armada (`List<Kendaraan>`, `Map`, `Set`) dan bay parkir.
  * `FleetFileRepository` *(Persistence Repository)*: Bertanggung jawab atas I/O file teks (`simpan` & `muat`) secara defensif.
  * `DatabaseManager` *(Singleton JDBC)*: Mengelola koneksi dan skema basis data MySQL.
  * Paket `model.exception`: `KendaraanTidakTersediaException`, `KendaraanRusakException`, `KapasitasPenuhException`.
  * `Main` / `CliMain` *(Controller / Presentation)*: Menangani interaksi pengguna, menu, dan dirty flag.

---

### SLIDE 4: PENERAPAN PILAR OOP, VALIDASI STATE, & PERSISTENSI DATA
* **Pilar OOP:**
  * **Enkapsulasi:** Seluruh atribut `private` dengan setter yang menerapkan validasi batasan nilai (tarif > 0, odo >= 0).
  * **Pewarisan & Polimorfisme:** Dynamic method dispatch pada `hitungBiayaSewa(durasi, supir, diskon)` dan `toJson()`.
  * **Abstraksi & Interface:** Kontrak `PelacakGPS` menjamin konsistensi akses telemetri antar-layer.
* **Validasi State & Exception Handling (Modul 10):**
  * Prinsip *State Invariance*: Pemeriksaan ketersediaan dan kelayakan armada diselesaikan secara tuntas sebelum mutasi `k.setDisewa(true)` dilakukan. Jika operasi ditolak, state objek awal dijamin utuh 100%.
* **Persistensi Data (Modul 12-13):**
  * Pemuatan data defensif menggunakan koleksi sementara (`hasilSementara`) sebelum menggantikan koleksi utama.
  * Konfirmasi `berubah` (Dirty Flag) melindungi pengguna dari kehilangan data yang belum disimpan.

---

### SLIDE 5: HASIL PENGUJIAN, KETERBATASAN, & RENCANA PENGEMBANGAN
* **Hasil Pengujian Regresi (Modul 14):**
  * 12 dari 12 Skenario Pengujian Lulus (100% PASS), meliputi uji penambahan data, pencegahan plat duplikat, blokir mobil rusak, double-booking rejection, kalkulasi billing, rollback bay parkir, dan verifikasi muat ulang file.
* **Keterbatasan Saat Ini:**
  * Simulasi pergerakan marker peta Leaflet masih berbasis generator pergerakan lokal.
  * Sistem multi-cabang masih dipusatkan pada satu depot pool Sumenep.
* **Rencana Pengembangan Masa Depan:**
  * Integrasi API perangkat GPS tracker IoT fisik melalui protokol MQTT/Websocket.
  * Penambahan modul manajemen pemeliharaan armada otomatis berdasarkan interval odometer (servis berkala).

