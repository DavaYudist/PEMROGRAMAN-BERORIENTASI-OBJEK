# FleetSumenep DESKTOP - Sistem Pemantauan Armada Rental & Manajemen Parkir
**Modul 10: Exception Handling dan Validasi State Objek**

---

## Identitas Mahasiswa
- **Nama**  : M. Davana Yudista Firdausy
- **NRP**   : 3125522002
- **Kelas** : A D3 TI-SM
- **Institusi** : Politeknik Elektronika Negeri Surabaya (PENS PSDKU Sumenep)
- **Mata Kuliah** : Pemrograman Berorientasi Objek (PBO)
- **GitHub Repository** : https://github.com/DavaYudist/PEMROGRAMAN-BERORIENTASI-OBJEK.git

---

## Ringkasan Modul 10: Exception Handling & Validasi Defensif
Modul 10 menerapkan prinsip fundamental rekayasa perangkat lunak berorientasi objek:
> **"Validasi sebelum mutasi state; operasi yang ditolak akibat error wajib mempertahankan state objek awal secara utuh (Atomic & State Invariant)."**

Sistem rental mobil FleetSumenep mengimplementasikan penanganan eksepsi bertingkat (*hierarchical exception handling*) untuk melindungi integritas state objek armada dan kapasitas bay parkir:

1. **Custom Checked Exception (`model.exception`)**:
   - `KendaraanTidakTersediaException`: Dilempar saat terjadi upaya penyewaan pada kendaraan yang sedang beroperasi di jalan (`status == DISEWA`). Mencegah *double-renting* dan korupsi data penyewa aktif.
   - `KendaraanRusakException`: Dilempar saat terjadi upaya penyewaan kendaraan dengan status kondisi mesin `"Dalam Perbaikan"`. Menjamin keselamatan pelanggan dan SLA armada.
   - `KapasitasPenuhException`: Dilempar saat registrasi kendaraan melampaui kapasitas bay parkir depot, atau saat pengurangan slot bay parkir memotong slot yang sedang ditempati unit armada.

2. **Defensive Validation (Unchecked / Runtime Exception)**:
   - `IllegalArgumentException`: Dilempar bila parameter input tidak valid (plat nomor kosong, durasi sewa &le; 0 jam, persentase diskon di luar 0%–100%, tarif harian &le; 0, atau odometer negatif).
   - `IllegalStateException`: Dilempar bila terjadi pemanggilan method yang tidak sesuai dengan siklus hidup objek (misal: mengembalikan kendaraan yang tidak sedang disewa).

3. **Caller-Side Multi-Catch Block (`try - catch - finally`)**:
   - Antarmuka desktop controller (`DesktopBridge`) menangani masing-masing tipe eksepsi secara spesifik dengan pesan error yang presisi bagi pengguna, sementara blok `finally` hanya menyinkronkan view WebView tanpa memanipulasi field objek.

---

## Struktur Direktori Proyek P10

```
P10_3125522002_M Davana Yudista Firdausy/
|-- lib/
|   |-- javafx-sdk-17.0.12/        # OpenJFX 17 Windows SDK runtime
|   `-- mysql-connector-j-8.3.0.jar# JDBC Driver MySQL
|-- SourceCode/
|   |-- Main.java                  # Controller desktop native dengan multi-catch block
|   |-- Launcher.java              # Entry point JavaFX application
|   |-- TestP10Runner.java         # Automated verification runner (5 skenario Modul 10)
|   |-- TestMySQLPersistence.java  # Verifikasi transaksi ACID database
|   |-- model/
|   |   |-- PelacakGPS.java        # Interface kontrak GPS telemetri
|   |   |-- Kendaraan.java         # Superclass abstrak dengan validasi defensif
|   |   |-- MobilReguler.java      # Subclass reguler (validasi durasi & diskon)
|   |   |-- MobilVIP.java          # Subclass VIP (validasi durasi & diskon)
|   |   |-- PerusahaanRental.java  # Agregator armada & transaksi sewa defensif
|   |   |-- DatabaseManager.java   # Singleton JDBC manager & schema migration
|   |   `-- exception/             # Paket Custom Exception Modul 10
|   |       |-- KendaraanTidakTersediaException.java
|   |       |-- KendaraanRusakException.java
|   |       `-- KapasitasPenuhException.java
|   |-- bin/                       # Bytecode compiled .class files
|   `-- web/                       # Asset antarmuka HTML/JS Leaflet & Parking
|-- Screenshot/                    # Tangkapan layar eksekusi & diagram
|   |-- test_runner_terminal.png   # Hasil uji otomatis TestP10Runner (5/5 PASS)
|   |-- exception_architecture_diagram.png # Diagram hierarki eksepsi & alur validasi
|   |-- dashboard_preview.png
|   `-- map_preview.png
|-- run.ps1                        # Skrip kompilasi & peluncuran PowerShell
|-- run.bat                        # Skrip kompilasi & peluncuran Command Prompt
|-- report_template.html           # Template laporan akademik Modul 10
|-- readme.pdf                     # Laporan akademik resmi Modul 10
`-- README.md                      # Dokumentasi teknis proyek
```

---

## Verifikasi Pengujian Otomatis (5 Skenario Uji Modul 10)

Untuk menjalankan uji konsistensi validasi dan keutuhan state:
```bash
java -cp "SourceCode\bin;lib\mysql-connector-j-8.3.0.jar;." SourceCode.TestP10Runner
```

### Hasil Matriks Pengujian:
1. **Skenario 1 (Penyewaan Standby)**: Unit `M 5555 XY` berhasil disewa, state berubah menjadi `DISEWA` (Atomic). **[PASS]**
2. **Skenario 2 (Double-Renting)**: Upaya sewa unit `M 5555 XY` ditolak dengan `KendaraanTidakTersediaException`. State awal tetap utuh tanpa korupsi nama penyewa. **[PASS]**
3. **Skenario 3 (Durasi Sewa Negatif)**: Upaya sewa unit `M 8888 VIP` dengan durasi -5 jam ditolak dengan `IllegalArgumentException`. Unit tetap standby di pool. **[PASS]**
4. **Skenario 4 (Mobil Rusak)**: Upaya sewa unit `M 2345 AB` berstatus "Dalam Perbaikan" ditolak dengan `KendaraanRusakException`. Unit tetap tidak disewa. **[PASS]**
5. **Skenario 5 (Pengembalian Unit)**: Unit `M 5555 XY` sukses dikembalikan ke pool depot dalam status `STANDBY`. **[PASS]**

**Tingkat Keberhasilan:** 5 dari 5 Skenario Lulus (100% PASS).
