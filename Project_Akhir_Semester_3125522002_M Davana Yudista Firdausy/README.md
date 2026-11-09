# FleetSumenep - Sistem Pemantauan Armada Rental & Manajemen Parkir
## Capstone Project Praktikum Pemrograman Berorientasi Objek (P1 - P15)

---

## Identitas Mahasiswa & Akademik
- **Nama**  : M. Davana Yudista Firdausy
- **NRP**   : 3125522002
- **Kelas** : A D3 TI-SM
- **Institusi** : Politeknik Elektronika Negeri Surabaya (PENS PSDKU Sumenep)
- **Dosen Pengampu** : Nirwana Haidar Hari, S.Pd., M.Kom.
- **GitHub Repository** : https://github.com/DavaYudist/PEMROGRAMAN-BERORIENTASI-OBJEK.git

---

## Ringkasan Eksekutif Proyek
**FleetSumenep** adalah sistem manajemen armada rental mobil dan lot parkir garasi pool terpadu yang dirancang menggunakan seluruh kaidah dan pilar Pemrograman Berorientasi Objek (OOP) dari Pertemuan 1 hingga Pertemuan 15. Sistem ini mengintegrasikan arsitektur *Dual-Launcher* yang dapat dioperasikan melalui jendela grafis desktop native (*JavaFX WebView WebKit + Leaflet.js*) maupun melalui menu konsol interaktif berbasis terminal (*CLI Console Menu*).

---

## Pemetaan Integrasi Konsep P1–P15 OOP

| Modul | Konsep & Topik | Implementasi pada Proyek FleetSumenep |
|:---:|:---|:---|
| **P1–P4** | *Class, Encapsulation, State Validation* | Seluruh field berstatus `private`, validasi argumen konstruktor (plat tidak kosong, tarif > 0, odo >= 0). |
| **P5–P7** | *Inheritance, Polymorphism, Abstraction, Interface* | Interface `PelacakGPS`, abstract class `Kendaraan`, subclasses `MobilReguler` dan `MobilVIP`, kalkulasi sewa polimorfis. |
| **P8–P9** | *Aggregation, Composition, MVC/GUI Architecture* | `PerusahaanRental` mengagregasi armada; GUI JavaFX WebView memetakan koordinat Leaflet dan grid parkir pool. |
| **P10** | *Exception Handling & Defensive State Validation* | Package `model.exception` (`KendaraanTidakTersediaException`, `KendaraanRusakException`, `KapasitasPenuhException`). *State Invariance* terjamin. |
| **P11** | *Collections & Generics* | Pengelolaan armada menggunakan `List<Kendaraan>`, `Map<String, Kendaraan>`, dan `Set<String>` untuk jaminan keunikan plat nomor. |
| **P12–P13** | *Data Persistence, File I/O & Menu CLI* | Dual-engine persistence (MySQL via JDBC & File Teks pipe-delimited via `FleetFileRepository`). Menu konsol interaktif dengan pelacak *Dirty Flag* (`berubah`). |
| **P14** | *Refactoring, Invariants & Regression Testing* | Refactoring method menu terurai, eliminasi code smells, dan runner otomatis `TestRegressionRunner.java` (12 skenario lulus 100%). |
| **P15** | *Demonstration & Portfolio Documentation* | Dokumen presentasi 5-slide (`presentation_overview.pdf`), checklist demonstrasi, dan sampel data persisten. |

---

## Struktur Direktori Proyek

```
Project_Akhir_Semester_3125522002_M Davana Yudista Firdausy/
|-- data/
|   `-- armada_store.txt           # Berkas persisten teks format pipe-delimited (|)
|-- lib/
|   |-- javafx-sdk-17.0.12/        # OpenJFX 17 Windows SDK runtime & native libraries
|   `-- mysql-connector-j-8.3.0.jar# MySQL JDBC Driver Connector
|-- SourceCode/
|   |-- Launcher.java              # Peluncur GUI Desktop Native (JavaFX WebView)
|   |-- Main.java                  # Controller GUI, WebView Bridge & Leaflet integration
|   |-- CliMain.java               # Peluncur Menu Konsol Interaktif (P13 CLI)
|   |-- TestRegressionRunner.java  # Automated E2E Regression Suite (12 Skenario)
|   |-- TestMySQLPersistence.java  # Verifikasi transaksi ACID database
|   |-- TestP10Runner.java         # Verifikasi exception handling & validasi state
|   |-- model/
|   |   |-- PelacakGPS.java        # Interface kontrak GPS telemetri
|   |   |-- Kendaraan.java         # Superclass abstrak armada
|   |   |-- MobilReguler.java      # Subclass kategori armada Reguler
|   |   |-- MobilVIP.java          # Subclass kategori armada VIP (suhu kabin)
|   |   |-- PerusahaanRental.java  # Agregator armada & manajer transaksi
|   |   |-- DatabaseManager.java   # Singleton JDBC manager & schema migration
|   |   |-- FleetFileRepository.java# Repository persistensi file teks (P12)
|   |   `-- exception/             # Paket Custom Checked Exceptions (P10)
|   |       |-- KendaraanTidakTersediaException.java
|   |       |-- KendaraanRusakException.java
|   |       `-- KapasitasPenuhException.java
|   |-- bin/                       # Bytecode compiled (.class files)
|   `-- web/                       # Aset web offline (Leaflet.js, CSS, HTML layouts)
|-- Screenshot/                    # Bukti uji regresi dan antarmuka aplikasi
|-- presentation_overview.md       # Materi presentasi 5-slide Markdown
|-- presentation_overview.pdf      # Dokumen resmi presentasi 5-slide PDF
|-- run.bat                        # Skrip 1-klik Windows Command Prompt
|-- run.ps1                        # Skrip 1-klik PowerShell
`-- README.md                      # Dokumentasi komprehensif Capstone Project
```

---

## Panduan Kompilasi & Menjalankan Aplikasi

Aplikasi telah dilengkapi skrip otomatis multi-pilihan:

### Menggunakan PowerShell:
```powershell
.\run.ps1
```
Pilih opsi:
- `1` untuk GUI Hybrid Desktop (JavaFX WebView + Peta Leaflet).
- `2` untuk Konsol CLI Interaktif (`CliMain`).
- `3` untuk Menjalankan Uji Regresi Otomatis (`TestRegressionRunner`).

### Menggunakan Command Prompt / Batch (Klik Dua Kali):
```cmd
run.bat
```

---

## Checklist Demonstrasi Proyek (P15)

1. [x] **Kompilasi Bersih**: Seluruh kode terkompilasi dengan kode keluar 0 (`javac`).
2. [x] **Alur Normal GUI**: Pemantauan peta Leaflet real-time, penyewaan mobil standby, pembaruan tagihan dan lot parkir.
3. [x] **Alur Normal CLI**: Menampilkan armada, menambah unit baru, menyewa mobil, mengembalikan ke pool.
4. [x] **Uji Kondisi Gagal**: Input durasi negatif ditolak, mobil "Dalam Perbaikan" diblokir, plat duplikat ditolak.
5. [x] **Uji Konsistensi State**: Operasi yang ditolak mempertahankan state objek awal secara atomik.
6. [x] **Persistensi File**: Data disimpan ke `data/armada_store.txt`, program ditutup dan dibuka kembali, data terbukti ter-reload 100% utuh.
7. [x] **Dirty-State Confirmation**: Peringatan konfirmasi muncul saat pengguna mencoba keluar atau memuat ulang ketika ada data yang belum disimpan.
