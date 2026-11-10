# DOKUMENTASI ARSITEKTUR & MEKANIKA GAME "RENT CAR MANAGER" (TAPLOKA REVERSE-ENGINEERED)

**Pengembang Capstone:** M. Davana Yudista Firdausy (NRP: 3125522002)  
**Kelas / Program Studi:** A D3 Teknik Informatika - PENS PSDKU Sumenep  
**Mata Kuliah:** Pemrograman Berbasis Objek (PBO P1–P15)  
**Sumber Referensi Ekstraksi Internal:** `game_inner.html` & `game_extracted.js`

---

## 1. SISTEM SKRINING RISIKO PELANGGAN (6 CUSTOMER RISK SIGNALS)
Pada game internal Taploka *Rent Car Manager*, setiap pesanan masuk membawa objek telemetri risiko `sig`:
1. **Dokumen (`sig.doc`)**:
   - `0`: KTP, KK, PBB lengkap (Dokumen Resmi & Terverifikasi).
   - `1`: Cuma KTP (Risiko Tinggi, verifikasi minim).
2. **Nomor Handphone (`sig.hp`)**:
   - `0`: Nomor HP lama / terdaftar lama di database.
   - `1`: Nomor HP baru aktif (Kecurigaan sindikat burner number).
3. **Metode Pembayaran (`sig.pay`)**:
   - `0`: Transfer Bank DP (Jejak digital perbankan terverifikasi).
   - `1`: Tunai Muka (Tanpa jejak transaksi bank).
4. **Wilayah / Domisili Tujuan (`sig.dst`)**:
   - `0`: Dalam Kota (Sumenep & sekitarnya).
   - `1`: Luar Kota (Risiko pelarian unit ke luar pulau / zona aman).
5. **Catatan Asosiasi Rental (`sig.bl`)**:
   - `0`: Bersih dari catatan hitam rental lain.
   - `1`: Tercatat di grup WhatsApp Asosiasi Rental (Suspect penggelapan).
6. **Tipe Entitas (`sig.lg`)**:
   - `0`: Perorangan / Lepas Kunci.
   - `1`: Instansi / Perusahaan Resmi (Rekomendasi instansi, risiko 0%).

### Tindakan Mitigasi Risiko:
- **Minta Jaminan (`jam`)**: Mengurangi peluang penggelapan hingga 40%. Pelanggan curiga berpeluang membatalkan sewa jika diminta jaminan tambahan.
- **Tawarkan Sopir (`sop`)**: Jika diterima, mode beralih ke *Dengan Sopir* (`md = 1`), mereduksi risiko penggelapan menjadi 0% (Unit selalu dalam pengawasan sopir garasi).

---

## 2. MODAL "PILIH UNIT" & ALGORITMA TIMELINE COLLISION DETECTION
Saat tombol **[ ✔ Terima ]** diklik pada kartu calon penyewa, sistem membuka *Bottom-Sheet Modal* **"Pilih unit"** dengan instruksi:
> *"Kotak putus-putus = jadwal yang diminta. Unit harus kosong di situ, termasuk jam untuk cuci."*

### Komponen Kartu Pilihan Armada:
1. **Vector Vehicle Silhouette**: Visualisasi tipe dan status kendaraan.
2. **Badge Plat Nomor**: Immutable plate identitas armada (`[M 1122 SM]`, `[M 2233 SM]`, `[M 3344 SM]`).
3. **Merk & Kelas**: Tipe mobil beserta kategori kelasnya.
4. **Kondisi Mesin**: Persentase keausan mesin & bodi (`Kondisi 88%`).
5. **Mini Timeline Track (Kotak Putus-Putus)**:
   - Span 7-Hari horizontal dengan garis slot grid harian.
   - **Solid Bar**: Mewakili sesi sewa yang sudah aktif berjalan atau terjadwal.
   - **Dashed Rectangular Outline (`.track-blk.want`)**: Mewakili rentang jadwal yang diminta oleh pelanggan.
   - **Aturan Collision (Tabrakan Jadwal)**:
     $$\text{Bentrok} \iff \max(\text{Start}_1, \text{Start}_2) < \min(\text{End}_1, \text{End}_2)$$
     Jika bertabrakan dengan pesanan aktif:
     - Garis putus-putus berubah menjadi merah (`.clash`).
     - Badge menampilkan `[ Bentrok jadwal ]` berwarna merah/amber.
     - Baris dinonaktifkan (`.opt.dis`, opacity 60%, unclickable).
     Jika tidak bertabrakan:
     - Garis putus-putus berwarna hijau emerald (`#059669`).
     - Badge menampilkan `[ Kosong ]`.
     - Baris interaktif (`.opt.clickable`, active hover, focus ring).

---

## 3. MEKANISME "UPGRADE GRATIS" (TIER ALLOCATION RULE)
- **Hierarki Kelas**:
  - Tier 0: `LCGC` (Daihatsu Sigra / Ayla)
  - Tier 1: `KELUARGA` (Toyota Avanza / Xpander)
  - Tier 2: `MEDIUM_SUV` (Toyota Innova Reborn)
  - Tier 3: `MEWAH_VIP` (Toyota Alphard)
- **Aturan Invariant**:
  - Pelanggan **TIDAK BISA** dialokasikan mobil dengan kelas yang lebih rendah dari permintaannya (*Downgrade Rejection*).
  - Jika pemain mengalokasikan mobil dengan kelas lebih tinggi dari permintaan (misal pelanggan meminta LCGC, tetapi pemain memilih Avanza / Innova):
    * Ditampilkan badge `[ Upgrade gratis ]`.
    * Pelanggan **tetap membayar tarif pesanan kelas yang diminta** (ekonomi realistis).
    * Mobil kelas tinggi yang dipakai menanggung jarak tempuh odometer dan keausan mesin.

---

## 4. FASILITAS & PENGEMBANGAN USAHA (TAPLOKA TIERS & FACILITIES)
1. **Badan Hukum CV Resmi (`CV_COST = 15jt`)**:
   Meningkatkan reputasi dan membuka akses pesanan korporat / instansi.
2. **Keanggotaan Asosiasi Rental Indonesia (`ASOS_COST = 5jt`)**:
   Membuka database pengecekan blacklist nasional secara langsung pada kartu profil pelanggan.
3. **Bengkel & Mekanik In-House (`MECH_COST = 8jt`)**:
   Memberikan diskon permanen 50% untuk seluruh biaya servis berkala dan perbaikan armada garasi.
4. **Cuci Mobil & Detailing In-House (`WASH_COST = 6jt`)**:
   Menghilangkan waktu jeda antrean cuci mobil luar (turnaround unit instan).
5. **Kampanye Iklan Digital (4 Level)**:
   - Level 0: Gratis (1.0x pesanan masuk).
   - Level 1: Rp 3.000.000 (1.2x pesanan masuk).
   - Level 2: Rp 10.000.000 (1.4x pesanan masuk).
   - Level 3: Rp 35.000.000 (1.65x pesanan masuk).

---

## 5. BURSA MOBIL BEKAS (USED CAR MARKET)
Pemain dapat membeli unit dari Bursa Mobil Bekas:
- **Harga Pembelian**: 60% dari harga unit baru (Diskon 40%).
- **Kondisi Awal**: 72% kondisi mesin & bodi.
- **Odometer Awal**: 45.000 – 70.000 km.
- **Tingkat Keausan**: Mengalami keausan lebih cepat dibanding unit baru jika tidak dirawat rutin.

---

## 6. KALENDER EVENT MUSIMAN (SEASONAL MULTIPLIERS)
Sistem simulasi mengevaluasi perubahan musim secara dinamis:
- **Mudik Lebaran**: Durasi 8 hari, pengali pesanan 2.0x, kenaikan tarif sewa 1.5x.
- **Libur Sekolah**: Durasi 7 hari, pengali pesanan 1.4x, kenaikan tarif sewa 1.2x.
- **Nataru (Natal & Tahun Baru)**: Durasi 6 hari, pengali pesanan 1.4x, kenaikan tarif sewa 1.25x.
- **Bulan Puasa Ramadhan**: Durasi 6 hari, pesanan melambat 0.6x, tarif 0.9x.
- **Kenaikan BBM**: Durasi 8 hari, penurunan pesanan 0.7x.

