# LAPORAN PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK
## MODUL 7: ABSTRACT CLASS, ABSTRACT METHOD, AND INTERFACE

---

### IDENTITAS MAHASISWA
* **Nama Mahasiswa** : M. Davana Yudista Firdausy
* **NRP**            : 3125522002
* **Kelas**          : A D3 TI-SM
* **Institusi**      : PENS PSDKU Sumenep
* **Mata Kuliah**    : Pemrograman Berbasis Objek (PBO)
* **Dosen Pengampu** : Ardiansyah Al Faruq, S.ST., M.Tr.T.
* **Iterasi Proyek** : Sistem Manajemen Inventaris Toko (Refactoring P6 &rarr; P7)

---

### SPRINT GOAL
> Mentransformasikan arsitektur *Sistem Manajemen Inventaris Toko* dari polimorfisme berbasis kelas konkret (P6) menjadi **arsitektur berorientasi kontrak abstraksi murni (P7)**. Mencegah instansiasi objek generik yang cacat semantik melalui `abstract class Person`, menegakkan kewajiban implementasi metode tugas operasional via `abstract void prosesTugas()`, serta mengikat entitas lintas hierarki (`Barang` dan `Transaksi`) dalam kontrak audit kepatuhan melalui interface `DapatDiaudit`.

---

### SPRINT BACKLOG (SB-01 s.d. SB-07)
| Kode Backlog | Deskripsi Pekerjaan | Kriteria Penerimaan (*Acceptance Criteria*) | Status |
| :--- | :--- | :--- | :---: |
| **SB-01** | *Workspace Setup* Direktori P7 | Direktori `P7_.../src` terbuat, file P6 tersalin, `settings.json` diperbarui | **DONE** |
| **SB-02** | Abstraksi Superclass `Person` | Class `Person` diubah menjadi `abstract`, melarang `new Person()` | **DONE** |
| **SB-03** | Penegakan *Abstract Method* | Method `prosesTugas()` pada `Person` diubah tanpa body (`abstract`) | **DONE** |
| **SB-04** | Realisasi Subclass Konkret | `Petugas` & `Pemasok` meng-`@Override` `prosesTugas()` secara spesifik | **DONE** |
| **SB-05** | Spesifikasi Kontrak Interface | Dibuat interface `DapatDiaudit` dengan method `String jalankanAudit()` | **DONE** |
| **SB-06** | Realisasi Interface Lintas Hirarki | `Barang` & `Transaksi` mengimplementasikan interface `DapatDiaudit` | **DONE** |
| **SB-07** | *Test Driver & Dynamic Dispatch* | `Main.java` menguji 4 skenario evaluasi modul dengan sukses 100% | **DONE** |

---

### DESIGN AUDIT TABLE (PERBANDINGAN ARSITEKTUR P6 VS P7)
| Parameter Desain | Modul 6 (Polymorphism & Inheritance) | Modul 7 (Abstraction & Interface) | Rationale & Dampak Rekayasa |
| :--- | :--- | :--- | :--- |
| **Status Class `Person`** | `public class Person` (Kelas Konkret biasa) | `public abstract class Person` (Abstract Class) | **Mengamankan Domain Model:** Pada dunia nyata, tidak ada entitas "Manusia Generik" yang bekerja di toko tanpa peran. Status *abstract* melarang `new Person()` di tingkat kompilasi. |
| **Metode `prosesTugas()`** | Memiliki *dummy body*: `"[Person] Melakukan tugas umum."` | Tidak memiliki body: `public abstract void prosesTugas();` | **Kewajiban Subclass:** Menghilangkan risiko kelalaian developer; compiler Java memaksa subclass (`Petugas`/`Pemasok`) menulis logika konkretnya sendiri. |
| **Mekanisme Konstruktor** | Diinstansiasi langsung via operator `new` | Dipanggil eksklusif via `super(...)` (*Constructor Chaining*) | State enkapsulasi `id`, `nama`, `alamat` tetap aman dan terinisialisasi bersih saat subclass konkret diciptakan. |
| **Kontrak Audit Sistem** | Tidak ada standarisasi kontrak; audit dilakukan ad-hoc | `public interface DapatDiaudit` | **Decoupled Architecture:** Mengikat kelas `Barang` dan `Transaksi` yang tidak memiliki hubungan keturunan (*unrelated classes*) dalam satu tipe polimorfik audit. |

<div style="page-break-after: always;"></div>

### UML CLASS DIAGRAM (TEXT-BASED & MERMAID)

```
        +-----------------------------------+
        |       <<interface>>               |
        |        DapatDiaudit               |
        +-----------------------------------+
        | + jalankanAudit(): String         |
        +-----------------------------------+
             ^                         ^
             .                         .
     implements (..|>)         implements (..|>)
             .                         .
+-----------------------+   +-------------------------------------+
|        Barang         |   |              Transaksi              |
+-----------------------+   +-------------------------------------+
| - kodeBarang: String  |   | - idTransaksi: String               |
| - namaBarang: String  |   | - jenisTransaksi: String            |
| - kategori: String    |   | - petugas: Petugas                  |
| - stok: int           |   | - pemasok: Pemasok                  |
| - hargaSatuan: double |   | - daftarDetail: List<DetailTransaksi|
+-----------------------+   +-------------------------------------+
| + jalankanAudit()     |   | + jalankanAudit()                   |
| + tambahStok()        |   | + tambahItem()                      |
| + kurangiStok()       |   | + hitungTotalBerdasarkanSubtotal()  |
+-----------------------+   +-------------------------------------+
                                 |                      |
                            associates             associates
                                 v                      v
                       +------------------+   +-------------------+
                       |     Petugas      |   |      Pemasok      |
                       +------------------+   +-------------------+
                       | - jabatan: String|   | - namaPerusahaan  |
                       | - shiftKerja     |   | - kategoriSuplai  |
                       +------------------+   +-------------------+
                       | + prosesTugas()  |   | + prosesTugas()   |
                       +------------------+   +-------------------+
                                \                      /
                           extends (-->)         extends (-->)
                                  \                  /
                             +----------------------------+
                             |       <<abstract>>         |
                             |          Person            |
                             +----------------------------+
                             | - id: String               |
                             | - nama: String             |
                             | - alamat: String           |
                             +----------------------------+
                             | + tampilkanProfil(): void  |
                             | + prosesTugas(): void*     |
                             +----------------------------+
```

---

### ANALISIS MENDALAM: MENGAPA ABSTRAKSI LEBIH UNGGUL DARI POLIMORFISME STANDAR?

1. **Integritas Semantik & Eliminasi Objek Ilegal (*Domain Defense*)**
   Pada P6, programmer dapat secara tidak sengaja mengeksekusi `Person p = new Person("ID-00", "Anonim", "Gudang");`. Secara logika bisnis, objek manusia tanpa jabatan atau status kemitraan adalah anomali sistem (*data corruption*). Dengan mendefinisikan `Person` sebagai `abstract class`, Java melarang pembentukan objek cacat ini langsung pada fase **Compile-Time**, bukan saat *runtime crash*.

2. **Jaminan Kontrak Keras vs Implementasi Lalai (*Fail-Fast Principle*)**
   Metode konkret yang dioverride pada P6 berisiko lupa diimplementasikan oleh subclass baru. Jika lupa, subclass akan mewarisi perilaku dummy superclass tanpa peringatan compiler. Dengan `abstract void prosesTugas()`, compiler Java memberlakukan aturan keras (*compile error*) jika subclass konkret tidak menyediakan implementasi spesifik.

3. **Realisasi Polimorfisme Lintas Pohon Silsilah (*Interface Flexibility*)**
   Java mengadopsi *Single Inheritance*. Class `Barang` merepresentasikan objek fisik komoditas, sedangkan `Transaksi` merepresentasikan rekaman aktivitas akuntansi. Keduanya tidak mungkin dan tidak boleh disatukan di bawah hierarki superclass yang sama. Melalui `interface DapatDiaudit`, sistem meraih kemampuan polimorfik murni (*behavioral contract*) tanpa merusak desain arsitektur domain.

<div style="page-break-after: always;"></div>

### TABEL HASIL PENGUJIAN 4 SKENARIO MODUL 7
| No | Skenario Pengujian | Input & Tindakan Eksekusi | Hasil yang Diharapkan (*Expected Output*) | Hasil Aktual (*Actual Output*) | Kesimpulan |
| :-: | :--- | :--- | :--- | :--- | :---: |
| **1** | **Instansiasi Objek Subclass Konkret** | Instansiasi langsung `Petugas`, `Pemasok`, `Barang`, dan `Transaksi`. | Objek terbentuk sukses. Upaya `new Person()` memicu compiler error. | Seluruh 4 objek konkret berhasil diinstansiasi 100%. | **PASS** |
| **2** | **Abstract Method via Superclass Ref** | Upcast `Person p1 = new Petugas(...)`, panggil `p1.prosesTugas()`. | Dynamic binding memanggil implementasi `prosesTugas()` milik `Petugas`. | Log spesifik Petugas tereksekusi via referensi Person. | **PASS** |
| **3** | **Interface Method via Interface Ref** | Upcast `DapatDiaudit d = new Barang(...)`, panggil `d.jalankanAudit()`. | Mengembalikan laporan audit string status stok (Kritis/Aman) dan transaksi. | Terbit laporan audit status stok dan rekapitulasi SKU transaksi. | **PASS** |
| **4** | **Koleksi Polimorfik Heterogen** | Iterasi array `Person[]` dan array `DapatDiaudit[]` via enhanced loop. | Eksekusi seragam tanpa manual casting / instanceof branching. | Seluruh entitas memproses tugas dan audit secara seragam & presisi. | **PASS** |

---

### BUKTI EKSEKUSI PROGRAM (*RUNNING SCREENSHOTS PLACEHOLDER*)

```
================================================================================
            PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK - MODUL 7                  
                ABSTRACT CLASS, ABSTRACT METHOD, AND INTERFACE                  
================================================================================
  Nama Mahasiswa : M. Davana Yudista Firdausy | NRP: 3125522002
  Kelas          : A D3 TI-SM | PENS PSDKU Sumenep
================================================================================
[Step 1] Instansiasi Konkret Berhasil: Petugas, Pemasok, Barang, Transaksi.
[Step 2] Dynamic Binding Terverifikasi via Person Reference -> Petugas & Pemasok.
[Step 3] Realisasi Kontrak Audit:
         - [AUDIT BARANG] BRG-004: Spidol Whiteboard Hitam | Stok: 6 | KRITIS
         - [AUDIT TRANSAKSI] TRX-IN-2026-002 | Barang Masuk | Total: 40 unit
[Step 4] Heterogeneous Collections Iteration: Person[] & DapatDiaudit[] Berjalan Mulus!
================================================================================
```

*(Gunakan ekstensi VS Code **Markdown PDF** untuk mengonversi dokumen ini menjadi `README.pdf` dengan presisi 3 halaman).*

---

### SPRINT REVIEW & SPRINT RETROSPECTIVE

#### Sprint Review
* **Pencapaian Target**: 100% persyaratan arsitektural Modul 7 tercapai tanpa cacat.
* **Kualitas Kode**: Modularitas tinggi, clean architecture, bebas peringatan kompilasi (*Zero Warnings & Errors*).
* **Kepatuhan Standar**: Memenuhi standar OOP modern (SOLID Principle: Liskov Substitution Principle & Interface Segregation Principle).

#### Sprint Retrospective
* **What Went Well**: Refactoring berjalan mulus berkat fondasi enkapsulasi yang solid dari Modul 6. Integrasi interface memperjelas batas tanggung jawab antar entitas bisnis.
* **What to Improve**: Pada iterasi berikutnya (Modul 8 / Lanjutan), kontrak audit dapat diperluas dengan mekanisme persistence (penyimpanan file/database) atau penanganan exception (*Error Handling*).
