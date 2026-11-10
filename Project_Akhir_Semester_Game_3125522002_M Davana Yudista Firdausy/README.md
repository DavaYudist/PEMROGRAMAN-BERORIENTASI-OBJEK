# FleetSumenep Tycoon - Indonesian Car Rental Management Simulation

**Proyek Akhir Semester (Capstone Project) Pemrograman Berorientasi Objek**  
**Politeknik Elektronika Negeri Surabaya (PENS PSDKU Sumenep)**

### 🎓 Identitas Mahasiswa & Pengembang
- **Nama Mahasiswa**: M. Davana Yudista Firdausy
- **NRP**: 3125522002
- **Kelas**: A D3 Teknik Informatika
- **Tahun Akademik**: Semester 3 (2025/2026)
- **Dosen Pengampu / Penguji**: Tim Dosen PBO PENS PSDKU Sumenep

---

### 🌟 Ringkasan Proyek
**FleetSumenep Tycoon** mentransformasikan sistem manajemen armada mobil konvensional menjadi sebuah **Simulasi Strategi Bisnis Rental Mobil Indonesia (Car Rental Tycoon Web Simulation)** yang sangat realistis dan interaktif. Terinspirasi dari dinamika industri rental domestik, simulasi ini memodelkan:
- Verifikasi profil penyewa (KTP, nomor HP lama/baru, transfer vs tunai, grup asosiasi blacklist).
- Mitigasi risiko taktis: Minta Jaminan motor/ijazah (-40% risiko) dan Tawarkan Supir Tetap (0% risiko penggelapan).
- Krisis Penggelapan Mobil dengan 5 pilihan aksi pemain:
  1. Telepon Nomor Penyewa (nomor dialihkan)
  2. Matikan Mesin Remote (Immobilizer berbasis GPS)
  3. Lapor Polisi & BAP (biaya administrasi Rp 5jt)
  4. By One dengan Akamsi / Ormas (mediasi tebusan lapangan)
  5. Ikhlas Total (Write-Off Aset)
- Ekonomi riil pasar Indonesia (harga beli LCGC, Avanza, Innova, Alphard, tarif harian, biaya ganti oli, ekspansi garasi).
- Antarmuka web modern dengan estetika **Minimalist Green & White** (Forest/Emerald Green, Pure White cards, soft slate background, mobile-first responsive).

---

### 🏛️ Pemetaan Paradigma OOP (P1 - P15)
1. **P1–P4 (Dasar OOP, Enkapsulasi, State Validation)**:
   - `KasPerusahaan`: Menjaga integritas saldo kas IDR, validasi defensif pencegahan saldo minus, pencatatan audit transaksi.
   - `WaktuSimulasi`: Central game clock (hari, jam, menit) dengan pengatur kecepatan (0x Pause, 0.5x, 1x, 2x, 4x).
2. **P5–P7 (Inheritance, Polymorphism, Abstraction, Interface)**:
   - Superclass abstrak `Kendaraan` dengan plat nomor immutable.
   - Subclass konkret: `MobilLCGC`, `MobilKeluarga`, `MobilMediumSUV`, `MobilMewah`.
   - Interface `PelacakTelemetri` (koordinat GPS) dan `RemoteEngineControl` (immobilizer remote cutoff).
3. **P8–P11 (Aggregation, Custom Exceptions, Collections & Generics)**:
   - `PerusahaanRental` mengagregasi `List<Kendaraan>`, `Map<String, Kendaraan>`, dan kas.
   - `ManajerPesanan` mengelola antrian pesanan `List<PesananMasuk>` dan sesi sewa `List<SesiSewa>`.
   - Custom checked exceptions: `MobilDibawaKaburException`, `SaldoKasTidakCukupException`, `GarasiPenuhException`.
4. **P12–P15 (Dual Persistence, Menu CLI, Refactoring & Regression)**:
   - File repository `GameSaveRepository` untuk serialisasi state ke `data/gamestate.txt`.
   - Suite uji otomatis `TestGameSimulationRunner` (12 skenario uji, 100% PASS).

---

### 🚀 Cara Menjalankan Aplikasi
1. Jalankan via Batch Script (Windows CMD):
   ```cmd
   run.bat
   ```
2. Atau jalankan via PowerShell:
   ```powershell
   .\run.ps1
   ```
3. Pilihan mode:
   - **Mode [1]**: Membuka Game Tycoon Desktop WebKit (JavaFX WebView).
   - **Mode [2]**: Menjalankan Test Regression Otomatis (12 Test Cases PASS).
