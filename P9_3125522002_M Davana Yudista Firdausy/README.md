# FleetSumenep DESKTOP - Sistem Pemantauan Armada Rental & Manajemen Parkir
**Modul 9: Implementasi UML Class Diagram ke Program Java Native Desktop (Hybrid JavaFX WebView Architecture)**

---

## Identitas Mahasiswa
- **Nama**  : M. Davana Yudista Firdausy
- **NRP**   : 3125522002
- **Kelas** : A D3 TI-SM
- **Institusi** : PENS PSDKU Sumenep
- **Mata Kuliah** : Pemrograman Berorientasi Objek (PBO)

---

## Ringkasan Proyek
Proyek ini mengintegrasikan konsep Pemrograman Berorientasi Objek tingkat lanjut (P1–P7: Enkapsulasi, Abstraksi, Pewarisan, Polimorfisme, Interface, dan Agregasi) ke dalam arsitektur **Hybrid Native Desktop Window**. Dengan memanfaatkan komponen **JavaFX WebView (WebKit)**, aplikasi ini menampilkan peta interaktif Leaflet.js dan manajemen parkir pool secara langsung di dalam jendela desktop mandiri tanpa memerlukan browser web eksternal.

Aplikasi desktop ini memiliki navigasi tab multi-halaman:
1. **📍 Page 1 (Live GPS Fleet Tracking Map)**:
   - Menampilkan peta geografis interaktif berbasis **Leaflet.js** (aset offline lokal) yang berpusat di **Sumenep, Madura** (`[-7.015, 113.860]`).
   - Merender marker kendaraan berbasis SVG secara dinamis (warna Hijau Zamrud untuk `MobilReguler`, Amber/Emas untuk `MobilVIP`).
   - Dilengkapi sidebar telemetri live yang memuat plat nomor, kategori, level baterai dengan progress bar, koordinat real-time, status operasional (bergerak vs parkir standby), dan iklim suhu kabin untuk kelas VIP.
   - Fitur tombol pintas: Pusatkan Peta ke Sumenep, filter kategori armada, dan fly-to-vehicle saat kartu telemetri diklik.

2. **🅿️ Page 2 (Parking & Fleet Availability)**:
   - Visualisasi tata letak denah bay parkir (Depot Pool Pusat Sumenep) dengan 8 slot parkir (`Slot A-01` s/d `Slot A-04`, `Slot B-01` s/d `Slot B-04`).
   - **5 Kartu Metrik KPI**: Total Kapasitas Parkir, Unit Terparkir (Standby Siap Sewa), Slot Parkir Kosong, Unit Sedang Beroperasi di Jalan, dan Rasio Okupansi Parkir (%).
   - **Kartu Slot Terisi**: Menampilkan identitas armada terparkir, status baterai, dan tombol aksi interaktif `[ 🚀 Sewakan ke Jalan ]`.
   - **Kartu Slot Kosong**: Menampilkan slot bay yang tersedia untuk menerima unit kembali ke pool.
   - **Daftar Unit di Jalan (Active on the Road)**: Menampilkan mobil yang sedang disewa pelanggan beserta tombol aksi `[ 🅿️ Kembalikan ke Parkir Pool ]`.
   - Aksi sewa dan pengembalian unit berkomunikasi dua arah secara real-time dengan domain model OOP di Java melalui `javaBridge`.

---

## Implementasi Konsep P1–P7 OOP

1. **Antarmuka (Interface - P7)**:
   - `PelacakGPS`: Mendefinisikan kontrak telemetri posisi geografis (`getLat()`, `getLng()`) dan daya baterai (`getBaterai()`).
2. **Abstraksi (Abstract Class - P7)**:
   - `Kendaraan`: Merupakan kelas abstrak yang mengimplementasikan `PelacakGPS`. Mengenkapsulasi plat nomor, merk, koordinat posisi, daya baterai, status sewa, slot parkir, dan nama penyewa, serta mendeklarasikan metode abstrak `getKategori()`.
3. **Pewarisan (Inheritance - P5)**:
   - `MobilReguler` dan `MobilVIP` mewarisi seluruh perilaku dasar kelas induk `Kendaraan`.
   - `MobilVIP` memperluas state dengan atribut khusus kontrol iklim kabin `suhuKabin`.
4. **Polimorfisme (Polymorphism - P6)**:
   - `getKategori()` dioverride secara spesifik: menghasilkan `"Reguler"` pada `MobilReguler` dan `"VIP"` pada `MobilVIP`.
   - `toJson()` dioverride secara polimorfis oleh `MobilVIP` untuk menambahkan field `"suhu"` ke payload JSON yang dikonsumsi oleh antarmuka Leaflet Map dan Parking Grid.
5. **Enkapsulasi & Validasi (Encapsulation - P1 s.d. P4)**:
   - Seluruh atribut bersifat `private`.
   - Metode `setBaterai(int)` melakukan clamping nilai pada interval logis 0–100%.
   - Metode `setLokasi(double, double)` menjamin integritas koordinat spasial kendaraan.
6. **Agregasi (Aggregation - P9)**:
   - `PerusahaanRental` mengagregasikan koleksi `List<Kendaraan>` (`armada`) dan `armadaDisewa`.
   - Mengelola alokasi 8 slot parkir secara otomatis ketika armada didaftarkan, disewakan, maupun dikembalikan ke pool.

---

## Struktur Direktori Proyek

```
P9_3125522002_M Davana Yudista Firdausy/
|-- lib/
|   `-- javafx-sdk-17.0.12/        # OpenJFX 17 Windows SDK (lib & native dlls)
|-- SourceCode/
|   |-- Main.java                 # Peluncur utama Native JavaFX Window & Controller
|   |-- Launcher.java             # Wrapper runner standar JavaFX
|   |-- model/
|   |   |-- PelacakGPS.java       # Interface kontrak koordinat & baterai
|   |   |-- Kendaraan.java        # Abstract Superclass representasi armada
|   |   |-- MobilReguler.java     # Subclass kendaraan kategori Reguler
|   |   |-- MobilVIP.java         # Subclass kendaraan kategori VIP (iklim kabin)
|   |   `-- PerusahaanRental.java # Agregator armada & kalkulator slot parkir
|   `-- web/
|       |-- lib/
|       |   |-- leaflet.js        # Leaflet JS library offline
|       |   `-- leaflet.css       # Leaflet CSS library offline
|       |-- map.html              # Antarmuka Page 1 (Live GPS Tracking Map)
|       `-- parking.html          # Antarmuka Page 2 (Parking & Fleet Availability)
|-- UML/
|   `-- class-diagram.md          # Spesifikasi UML Class Diagram Mermaid
|-- Screenshot/                   # Dokumentasi tangkapan layar verifikasi
|-- run.ps1                       # Skrip peluncuran 1-klik PowerShell
|-- run.bat                       # Skrip peluncuran 1-klik Windows Command Prompt
|-- md.pdf                        # Dokumen Laporan Praktikum Resmi
`-- README.md                     # Dokumentasi Proyek Markdown
```

---

## Panduan Menjalankan Aplikasi

Aplikasi dapat dijalankan secara instan tanpa perlu mengunduh atau mengonfigurasi perangkat lunak eksternal:

### Cara 1: Menggunakan PowerShell
```powershell
.\run.ps1
```

### Cara 2: Menggunakan Batch Script (Klik Dua Kali)
Klik dua kali berkas `run.bat` di dalam direktori proyek.

### Cara 3: Menjalankan Perintah Manual
```powershell
# 1. Kompilasi
$fxLib = "lib/javafx-sdk-17.0.12/lib"
$fxBin = "lib/javafx-sdk-17.0.12/bin"
javac -encoding UTF-8 -d SourceCode/bin --module-path $fxLib --add-modules javafx.controls,javafx.web (Get-ChildItem -Path SourceCode -Filter "*.java" -Recurse).FullName

# 2. Jalankan
java "-Djava.library.path=$fxBin" --module-path $fxLib --add-modules javafx.controls,javafx.web -cp SourceCode/bin SourceCode.Launcher
```
