import os
import subprocess
import base64
import sys

def get_base64_image(image_path):
    if os.path.exists(image_path):
        with open(image_path, "rb") as img_file:
            ext = os.path.splitext(image_path)[1].replace(".", "").lower()
            if ext == "jpg": ext = "jpeg"
            return f"data:image/{ext};base64," + base64.b64encode(img_file.read()).decode("utf-8")
    return ""

def generate_report():
    base_dir = os.path.abspath("P10_3125522002_M Davana Yudista Firdausy")
    screenshot_dir = os.path.join(base_dir, "Screenshot")
    
    term_img_path = os.path.join(screenshot_dir, "test_runner_terminal.png")
    arch_img_path = os.path.join(screenshot_dir, "exception_architecture_diagram.png")
    dashboard_img_path = os.path.join(screenshot_dir, "dashboard_preview.png")
    map_img_path = os.path.join(screenshot_dir, "map_preview.png")
    
    term_b64 = get_base64_image(term_img_path)
    arch_b64 = get_base64_image(arch_img_path)
    dashboard_b64 = get_base64_image(dashboard_img_path)
    map_b64 = get_base64_image(map_img_path)
    
    html_content = f"""<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Laporan Praktikum PBO - Modul 10 - M. Davana Yudista Firdausy</title>
    <style>
        @page {{
            size: A4 portrait;
            margin: 14mm 16mm 14mm 16mm;
        }}
        * {{
            box-sizing: border-box;
        }}
        body {{
            font-family: 'Times New Roman', Times, serif, Arial, sans-serif;
            color: #000000;
            line-height: 1.35;
            font-size: 9.5pt;
            margin: 0;
            padding: 0;
            background: #ffffff;
        }}
        h1 {{
            font-size: 13pt;
            font-weight: bold;
            color: #000000;
            margin: 0 0 2px 0;
            text-transform: uppercase;
            text-align: center;
            letter-spacing: 0.5px;
        }}
        h2 {{
            font-size: 11pt;
            font-weight: bold;
            color: #000000;
            margin: 0 0 8px 0;
            text-align: center;
            text-transform: uppercase;
            border-bottom: 1.5px solid #000000;
            padding-bottom: 4px;
        }}
        h3 {{
            font-size: 10pt;
            font-weight: bold;
            color: #000000;
            margin: 10px 0 4px 0;
            border-bottom: 1px solid #000000;
            padding-bottom: 2px;
            text-transform: uppercase;
            page-break-after: avoid;
        }}
        h4 {{
            font-size: 9.5pt;
            font-weight: bold;
            color: #000000;
            margin: 8px 0 2px 0;
            page-break-after: avoid;
        }}
        p, li {{
            font-size: 9pt;
            color: #000000;
            text-align: justify;
            margin: 3px 0;
        }}
        ul, ol {{
            margin: 2px 0 4px 0;
            padding-left: 18px;
        }}
        li {{
            margin-bottom: 2px;
        }}
        .identity-box {{
            border: 1px solid #000000;
            background: #ffffff;
            padding: 6px 10px;
            margin-bottom: 8px;
            font-size: 9pt;
        }}
        .identity-box table {{
            width: 100%;
            border-collapse: collapse;
        }}
        .identity-box td {{
            padding: 1.5px 3px;
            border: none;
            font-size: 9pt;
            color: #000000;
        }}
        .identity-box td.label {{
            font-weight: bold;
            width: 130px;
        }}
        table.data-table {{
            width: 100%;
            border-collapse: collapse;
            margin: 5px 0 8px 0;
            font-size: 8.5pt;
        }}
        table.data-table th {{
            background-color: #f2f2f2;
            color: #000000;
            font-weight: bold;
            text-align: left;
            padding: 4px 6px;
            border: 1px solid #000000;
        }}
        table.data-table td {{
            padding: 3.5px 6px;
            border: 1px solid #000000;
            vertical-align: top;
            color: #000000;
        }}
        table.data-table tr {{
            page-break-inside: avoid;
        }}
        .status-tag {{
            font-weight: bold;
            color: #000000;
            text-align: center;
            display: block;
        }}
        .image-container {{
            text-align: center;
            margin: 6px 0 8px 0;
            page-break-inside: avoid;
        }}
        .image-container img {{
            max-width: 98%;
            max-height: 275px;
            height: auto;
            border: 1px solid #000000;
            object-fit: contain;
        }}
        .image-caption {{
            font-size: 8pt;
            font-style: italic;
            color: #000000;
            margin-top: 2px;
        }}
        .code-block {{
            background: #fdfdfd;
            border: 1px solid #000000;
            padding: 5px 8px;
            font-family: 'Consolas', 'Courier New', monospace;
            font-size: 7.8pt;
            color: #000000;
            white-space: pre-wrap;
            margin: 4px 0 6px 0;
            page-break-inside: avoid;
        }}
        .qa-box {{
            border-left: 3px solid #000000;
            padding-left: 8px;
            margin: 6px 0;
        }}
        .qa-question {{
            font-weight: bold;
            font-size: 9pt;
        }}
        .qa-answer {{
            font-size: 9pt;
            margin-top: 2px;
            text-align: justify;
        }}
        .page-break {{
            page-break-after: always;
        }}
    </style>
</head>
<body>

    <!-- ==================== HALAMAN 1 ==================== -->
    <h1>TUGAS PRAKTIK PEMROGRAMAN BERBASIS OBJEK</h1>
    <h2>MODUL 10: EXCEPTION HANDLING DAN VALIDASI STATE OBJEK</h2>

    <div class="identity-box">
        <table>
            <tr>
                <td class="label">Nama Mahasiswa</td>
                <td>: M. Davana Yudista Firdausy</td>
                <td class="label">Mata Kuliah</td>
                <td>: Praktikum Pemrograman Berorientasi Objek</td>
            </tr>
            <tr>
                <td class="label">NRP</td>
                <td>: 3125522002</td>
                <td class="label">Pertemuan / Topik</td>
                <td>: 10 / Exception Handling & Validasi State</td>
            </tr>
            <tr>
                <td class="label">Kelas</td>
                <td>: A D3 TI-SM</td>
                <td class="label">Model Belajar</td>
                <td>: Project Based Learning (PBL)</td>
            </tr>
            <tr>
                <td class="label">Institusi</td>
                <td>: PENS PSDKU Sumenep</td>
                <td class="label">Proyek Domain</td>
                <td>: FleetSumenep - Rental Car Fleet & Bay Monitoring</td>
            </tr>
            <tr>
                <td class="label">GitHub Repository</td>
                <td colspan="3">: <a href="https://github.com/DavaYudist/PEMROGRAMAN-BERORIENTASI-OBJEK.git" style="color: #000000; text-decoration: underline;">https://github.com/DavaYudist/PEMROGRAMAN-BERORIENTASI-OBJEK.git</a></td>
            </tr>
        </table>
    </div>

    <h3>BAGIAN A: ANALISIS PERMASALAHAN & IDENTIFIKASI KONDISI KESALAHAN</h3>
    <p>
        Dalam perancangan sistem perangkat lunak berorientasi objek kelas industri (seperti sistem pemantauan armada rental mobil <em>FleetSumenep</em>), integritas dan keutuhan data (<em>state consistency</em>) adalah prasyarat mutlak. Prinsip fundamental Modul 10 menegaskan bahwa:
    </p>
    <p style="text-align: center; font-weight: bold; border: 1px dashed #000000; padding: 4px; margin: 5px 0;">
        "Validasi wajib diselesaikan sebelum melakukan mutasi state; operasi yang ditolak akibat error atau kegagalan validasi wajib mempertahankan state awal objek secara utuh (Atomic & State Invariant)."
    </p>
    <p>
        Pada arsitektur sistem rental mobil FleetSumenep, telah diidentifikasi minimal empat kondisi kegagalan spesifik yang dapat mengancam konsistensi data jika tidak ditangani dengan mekanisme penanganan eksepsi yang ketat:
    </p>
    <ol>
        <li>
            <strong>Upaya Penyewaan Unit yang Sedang Beroperasi di Jalan (KendaraanTidakTersediaException):</strong><br>
            Kondisi di mana pengguna mencoba menyewakan armada yang field <code>disewa == true</code> (status <code>DISEWA</code>). Jika mutasi langsung dijalankan tanpa validasi pra-syarat, nama penyewa aktif sebelumnya akan tertimpa (terkorupsi), mengakibatkan <em>double-booking</em> dan anomali billing. Sistem mencegah ini dengan melempar custom checked exception <code>KendaraanTidakTersediaException</code>.
        </li>
        <li>
            <strong>Upaya Penyewaan Unit dalam Kondisi Perbaikan (KendaraanRusakException):</strong><br>
            Kondisi di mana kendaraan memiliki status <code>kondisiMesin.equalsIgnoreCase("Dalam Perbaikan")</code>. Dari aspek operasional keselamatan dan SLA perusahaan, armada yang mengalami kerusakan mekanik atau dalam pemeliharaan bengkel dilarang keras beroperasi. Sistem memvalidasi kelayakan fisik armada dan melempar checked exception <code>KendaraanRusakException</code> sebelum parameter sewa lainnya diproses.
        </li>
        <li>
            <strong>Validasi Input Parameter Transaksi & Nilai Atribut (IllegalArgumentException):</strong><br>
            Kondisi di mana pemanggil metode menyuplai argumen tidak logis di luar batas domain, seperti durasi sewa &le; 0 jam, persentase diskon di luar rentang [0.0% &ndash; 100.0%], plat nomor kosong/null, tarif sewa harian &le; 0, atau odometer negatif. Sistem menerapkan <em>defensive input validation</em> pada tingkat konstruktor dan method dengan melempar <code>IllegalArgumentException</code> deskriptif.
        </li>
        <li>
            <strong>Pelanggaran Batas Kapasitas Bay Parkir Depot Pool (KapasitasPenuhException):</strong><br>
            Kondisi saat administrator mencoba meregistrasikan armada baru namun kapasitas fisik bay parkir telah mencapai batas maksimum (32 bay), atau saat pengurangan kapasitas slot parkir (misal -4 bay) mencoba memotong bay parkir yang saat ini masih ditempati oleh unit armada aktif. Sistem memblokir aksi dan melempar <code>KapasitasPenuhException</code> guna menjaga konsistensi alokasi fisik lot parkir.
        </li>
    </ol>

    <h4>Rasionalitas Pemilihan Custom Domain-Specific Exception vs Generic Exception</h4>
    <p>
        Penggunaan custom exception bertipe checked (seperti <code>KendaraanTidakTersediaException</code> dan <code>KendaraanRusakException</code>) jauh lebih unggul dibandingkan melempar generic exception (seperti <code>Exception</code> atau <code>RuntimeException</code>) karena:
    </p>
    <ul>
        <li><strong>Ekspresivitas Semantik Domain:</strong> Nama class eksepsi secara eksplisit mendeskripsikan konteks domain bisnis kegagalan, memudahkan debugging, penelusuran audit log, dan perancangan pesan kesalahan UI.</li>
        <li><strong>Enkapsulasi Data Kontekstual:</strong> Custom exception membawa payload atribut khusus (misal <code>platNomor</code>, <code>kondisiMesin</code>, dan <code>kapasitasDiminta</code>) yang dapat diakses langsung oleh handler eksternal.</li>
        <li><strong>Penegakan Kontrak Compile-Time (Checked Exception):</strong> Melalui klausa <code>throws</code> pada deklarasi method, compiler Java memaksa setiap layer pemanggil (desktop UI controller, REST bridge) untuk menyediakan penanganan multi-catch yang terstruktur, mengeliminasi risiko kelalaian developer saat mengintegrasikan modul.</li>
    </ul>

    <div class="page-break"></div>

    <!-- ==================== HALAMAN 2 ==================== -->
    <h3>BAGIAN B: IMPLEMENTASI KODE PROGRAM (CLASS & EXCEPTION ARCHITECTURE)</h3>
    <p>
        Berikut adalah implementasi arsitektur Exception Handling dan Defensive Programming yang telah dibangun pada modul <code>SourceCode/model/exception/</code> dan <code>SourceCode/model/</code>:
    </p>

    <h4>1. Implementasi Custom Checked Exception</h4>
    <div class="code-block">package model.exception;

/**
 * Custom Checked Exception: KendaraanTidakTersediaException
 * Dilempar ketika armada berstatus DISEWA diminta kembali oleh pelanggan baru.
 */
public class KendaraanTidakTersediaException extends Exception {{
    private String platNomor;

    public KendaraanTidakTersediaException(String message) {{ super(message); }}
    public KendaraanTidakTersediaException(String message, String platNomor) {{
        super(message);
        this.platNomor = platNomor;
    }}
    public String getPlatNomor() {{ return platNomor; }}
}}

// KendaraanRusakException dilempar bila k.getKondisiMesin().equalsIgnoreCase("Dalam Perbaikan")
public class KendaraanRusakException extends Exception {{
    private String platNomor, kondisiMesin;
    public KendaraanRusakException(String message, String platNomor, String kondisiMesin) {{
        super(message);
        this.platNomor = platNomor;
        this.kondisiMesin = kondisiMesin;
    }}
    public String getPlatNomor() {{ return platNomor; }}
    public String getKondisiMesin() {{ return kondisiMesin; }}
}}</div>

    <h4>2. Defensive Programming & Deklarasi throws pada Method prosesSewa()</h4>
    <div class="code-block">public synchronized String prosesSewa(String platNomor, String namaPenyewa, int durasiJam, 
                                     boolean denganSupir, double diskonPersen) 
        throws KendaraanTidakTersediaException, KendaraanRusakException, IllegalArgumentException {{
    
    // 1. Validasi Eksistensi Armada
    Kendaraan k = findKendaraan(platNomor);
    if (k == null) throw new IllegalArgumentException("Kendaraan plat " + platNomor + " tidak ditemukan.");

    // 2. Validasi Kelayakan Operasional (Sebelum Mutasi State)
    if ("Dalam Perbaikan".equalsIgnoreCase(k.getKondisiMesin())) {{
        throw new KendaraanRusakException("Unit sedang dalam perbaikan dan tidak layak operasi.", 
                                          k.getPlatNomor(), k.getKondisiMesin());
    }}

    // 3. Validasi Ketersediaan Unit (Cek apakah sedang disewa)
    if (k.isDisewa()) {{
        throw new KendaraanTidakTersediaException("Kendaraan sedang disewa oleh pelanggan lain.", k.getPlatNomor());
    }}

    // 4. Validasi Argumen Input Transaksi
    if (durasiJam &lt;= 0) throw new IllegalArgumentException("Durasi sewa harus lebih dari 0 jam.");
    if (diskonPersen &lt; 0.0 || diskonPersen &gt; 100.0) throw new IllegalArgumentException("Diskon harus di antara 0% dan 100%.");
    if (namaPenyewa == null || namaPenyewa.trim().isEmpty()) throw new IllegalArgumentException("Nama penyewa tidak boleh kosong.");

    // 5. Eksekusi Perhitungan Tagihan & Transaksi ACID Basis Data
    double totalBiaya = k.hitungBiayaSewa(durasiJam, denganSupir, diskonPersen);
    syncDatabaseSewa(platNomor, namaPenyewa.trim(), durasiJam, denganSupir, diskonPersen, totalBiaya);

    // 6. Mutasi State Objek Memori HANYA DILAKUKAN SETELAH SELURUH VALIDASI LULUS
    k.setDisewa(true);
    k.setPenyewa(namaPenyewa.trim());
    k.setDenganSupir(denganSupir);
    armadaDisewa.add(k);

    return String.format(Locale.US, "SUKSES|Plat: %s | Unit: %s | Total: Rp %,.0f", k.getPlatNomor(), k.getMerk(), totalBiaya);
}}</div>

    <h4>3. Caller-Side Multi-Catch Block (Desktop Bridge Controller)</h4>
    <div class="code-block">public void sewaMobilDenganDurasi(String plat, String penyewa, int jam, boolean denganSupir, double diskonPersen) {{
    Platform.runLater(() -&gt; {{
        try {{
            String hasil = rental.prosesSewa(plat, penyewa, jam, denganSupir, diskonPersen);
            System.out.println("[TRANSAKSI SEWA] " + hasil);
        }} catch (KendaraanTidakTersediaException e) {{
            System.err.println("[VALIDASI SEWA DITOLAK] " + e.getMessage());
        }} catch (KendaraanRusakException e) {{
            System.err.println("[VALIDASI KONDISI DITOLAK] " + e.getMessage());
        }} catch (IllegalArgumentException e) {{
            System.err.println("[VALIDASI INPUT DITOLAK] " + e.getMessage());
        }} catch (Exception e) {{
            System.err.println("[SISTEM EXCEPTION] Error tak terduga: " + e.getMessage());
        }} finally {{
            // Sinkronisasi view selalu dieksekusi tanpa memanipulasi field data objek
            syncAllActiveWebViews();
        }}
    }});
}}</div>

    <div class="image-container">
        <img src="{arch_b64}" alt="Arsitektur Exception & Validasi State FleetSumenep">
        <div class="image-caption">Gambar 1: Diagram Arsitektur Exception Hierarchy & Alur Validasi Defensif FleetSumenep</div>
    </div>

    <div class="page-break"></div>

    <!-- ==================== HALAMAN 3 ==================== -->
    <h3>BAGIAN C: TABEL PENGUJIAN VALIDASI & KONSISTENSI STATE (MATRIX MODUL 10)</h3>
    <p>
        Pengujian verifikasi otomatis dijalankan menggunakan runner kelas murni <code>SourceCode.TestP10Runner</code> pada dataset armada standar tanpa gangguan persistensi acak. Berikut adalah matriks hasil pengujian menyeluruh:
    </p>

    <table class="data-table">
        <thead>
            <tr>
                <th style="width: 25px;">No</th>
                <th style="width: 140px;">Skenario Uji</th>
                <th style="width: 125px;">Input Data</th>
                <th style="width: 130px;">Hasil Diharapkan (Expected)</th>
                <th style="width: 140px;">Hasil Aktual (Actual Output)</th>
                <th style="width: 120px;">State Akhir Objek (Konsistensi)</th>
                <th style="width: 50px;">Status</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>1</td>
                <td><strong>Penyewaan Mobil Tersedia (Standby)</strong></td>
                <td>Plat: <code>M 5555 XY</code>, Penyewa: Budi Santoso, Durasi: 24 Jam, Diskon: 0%</td>
                <td>Sewa berhasil, invoice terbit, status menjadi DISEWA</td>
                <td>SUKSES: Invoice Rp 900.000 terbit, status unit Meluncur ke Peta</td>
                <td><code>disewa = true</code>, penyewa = "Budi Santoso", slot parkir tetap valid</td>
                <td><span class="status-tag">PASS</span></td>
            </tr>
            <tr>
                <td>2</td>
                <td><strong>Double-Renting Armada yang Sedang Disewa</strong></td>
                <td>Plat: <code>M 5555 XY</code> (saat ini berstatus DISEWA), Penyewa baru: Hendra</td>
                <td>Melempar <code>KendaraanTidakTersediaException</code>, sewa ditolak</td>
                <td>Tertangkap: <code>KendaraanTidakTersediaException</code>: "Kendaraan sedang disewa oleh pelanggan lain."</td>
                <td><code>disewa = true</code>, penyewa tetap "Budi Santoso" (State utuh 100%, data tidak terkorupsi)</td>
                <td><span class="status-tag">PASS</span></td>
            </tr>
            <tr>
                <td>3</td>
                <td><strong>Input Durasi Negatif atau Nol</strong></td>
                <td>Plat: <code>M 8888 VIP</code> (Standby), Durasi: -5 Jam, Penyewa: Dr. Syaifullah</td>
                <td>Melempar <code>IllegalArgumentException</code>, operasi dibatalkan</td>
                <td>Tertangkap: <code>IllegalArgumentException</code>: "Durasi sewa harus lebih dari 0 jam."</td>
                <td><code>disewa = false</code>, penyewa = "" (State mobil tetap STANDBY di depot)</td>
                <td><span class="status-tag">PASS</span></td>
            </tr>
            <tr>
                <td>4</td>
                <td><strong>Penyewaan Armada 'Dalam Perbaikan'</strong></td>
                <td>Plat: <code>M 2345 AB</code> (Kondisi: Dalam Perbaikan), Penyewa: Rina Marlina</td>
                <td>Melempar <code>KendaraanRusakException</code>, sewa ditolak demi keselamatan</td>
                <td>Tertangkap: <code>KendaraanRusakException</code>: "Unit sedang dalam perbaikan dan tidak layak operasi."</td>
                <td><code>disewa = false</code>, kondisi mesin tetap "Dalam Perbaikan", penyewa = ""</td>
                <td><span class="status-tag">PASS</span></td>
            </tr>
            <tr>
                <td>5</td>
                <td><strong>Pengembalian Armada yang Telah Disewa</strong></td>
                <td>Plat: <code>M 5555 XY</code> (saat ini disewa), Method: <code>kembalikanArmada()</code></td>
                <td>Status sewa di-reset ke STANDBY, penyewa dikosongkan, koordinat depot di-reset</td>
                <td>Status Pengembalian: BERHASIL, Armada kembali standby di Slot A-03</td>
                <td><code>disewa = false</code>, penyewa = "", armada dikeluarkan dari daftar sewa aktif</td>
                <td><span class="status-tag">PASS</span></td>
            </tr>
        </tbody>
    </table>

    <h3>BAGIAN D: SCREENSHOT HASIL EKSEKUSI & VERIFIKASI PENGUJIAN</h3>
    <p>
        Berikut adalah tangkapan layar langsung dari eksekusi runner otomatis <code>SourceCode.TestP10Runner</code> serta modul antarmuka pemantauan armada desktop native:
    </p>

    <div class="image-container">
        <img src="{term_b64}" alt="Terminal Output TestP10Runner">
        <div class="image-caption">Gambar 2: Log Eksekusi Verifikasi Standalone TestP10Runner (5 dari 5 Kasus Uji Lulus 100% PASS)</div>
    </div>

    <div style="display: flex; gap: 8px; justify-content: center; margin: 5px 0;">
        <div class="image-container" style="flex: 1; margin: 0;">
            <img src="{map_b64}" alt="Tampilan Peta GPS Fleet Tracking" style="max-height: 180px;">
            <div class="image-caption">Gambar 3: Monitoring Peta Telemetri GPS Sumenep</div>
        </div>
        <div class="image-container" style="flex: 1; margin: 0;">
            <img src="{dashboard_b64}" alt="Tampilan Manajemen Bay Parkir & Fleet Admin" style="max-height: 180px;">
            <div class="image-caption">Gambar 4: Antarmuka Billing & Bay Management</div>
        </div>
    </div>

    <div class="page-break"></div>

    <!-- ==================== HALAMAN 4 ==================== -->
    <h3>BAGIAN E: JAWABAN PERTANYAAN EVALUASI MODUL 10</h3>
    <p>
        Berdasarkan pedoman kurikulum Praktikum Pemrograman Berorientasi Objek PENS PSDKU Sumenep, berikut adalah telaah analitis atas pertanyaan evaluasi:
    </p>

    <div class="qa-box">
        <div class="qa-question">1. Jelaskan perbedaan mendasar antara keyword 'throw' dan 'throws' dalam pemrograman Java!</div>
        <div class="qa-answer">
            <strong>Jawaban:</strong><br>
            Keyword <code>throw</code> merupakan sebuah statemen/ekspresi yang digunakan di dalam badan method atau blok kode untuk secara eksplisit membuat (menginstansiasi) dan melempar suatu objek eksepsi ke call stack saat runtime (misalnya: <code>throw new KendaraanRusakException("Pesan");</code>).<br>
            Sebaliknya, keyword <code>throws</code> adalah bagian dari klausul deklarasi signature method yang menginformasikan kepada compiler dan pemanggil method tentang jenis-jenis <em>checked exception</em> yang berpotensi dilempar oleh method tersebut (misalnya: <code>public void prosesSewa() throws KendaraanRusakException, IllegalArgumentException</code>). Keyword <code>throws</code> mewajibkan pemanggil method untuk membungkus panggilan dalam blok <code>try-catch</code> atau meneruskannya kembali ke level pemanggil di atasnya.
        </div>
    </div>

    <div class="qa-box">
        <div class="qa-question">2. Mengapa pemeriksaan state (validasi pra-syarat) wajib dilakukan sebelum melakukan mutasi state objek? Jelaskan dampaknya terhadap konsistensi objek!</div>
        <div class="qa-answer">
            <strong>Jawaban:</strong><br>
            Pemeriksaan state wajib diselesaikan terlebih dahulu untuk menegakkan prinsip <em>State Invariance</em> dan sifat atomisitas (<em>Atomicity</em>) dalam perubahan data berorientasi objek. Jika suatu method memutasi atribut objek (misal: menetapkan <code>disewa = true</code> atau mengisi nama penyewa) sebelum seluruh validasi selesai, kemudian pada baris kode selanjutnya terjadi error (misalnya validasi diskon gagal atau persistensi database terputus), maka objek tersebut akan terjebak dalam kondisi cacat/parsial (<em>inconsistent state</em>). Dengan melakukan validasi defensif sebelum mutasi, kita menjamin bahwa jika terjadi penolakan atau eksepsi, objek senantiasa berada dalam state valid aslinya tanpa korupsi data.
        </div>
    </div>

    <div class="qa-box">
        <div class="qa-question">3. Apa akibat fatal jika sebuah exception ditangkap namun dibiarkan tanpa penanganan apapun (empty catch block / swallowed exception)?</div>
        <div class="qa-answer">
            <strong>Jawaban:</strong><br>
            Membiarkan blok catch kosong (<code>catch (Exception e) {{ }}</code>) merupakan <em>anti-pattern</em> kritis yang menyebabkan fenomena <em>Silent Failure</em>. Kesalahan sistem yang fatal akan ditelan secara diam-diam tanpa ada catatan log, tanpa ada notifikasi kepada pemanggil, dan tanpa pemulihan state. Akibatnya, alur eksekusi aplikasi terus berjalan seolah-olah operasi berhasil, padahal proses bisnis di baliknya gagal total. Hal ini menimbulkan <em>cascading bugs</em> yang sangat sulit dilacak dan berisiko tinggi merusak integritas database serta kestabilan runtime sistem.
        </div>
    </div>

    <div class="qa-box">
        <div class="qa-question">4. Mengapa operasi mutasi state bisnis objek TIDAK BOLEH ditempatkan di dalam blok 'finally'?</div>
        <div class="qa-answer">
            <strong>Jawaban:</strong><br>
            Blok <code>finally</code> memiliki karakteristik deterministik yaitu dijamin <strong>selalu dieksekusi</strong> dalam kondisi apapun, baik saat blok <code>try</code> berjalan sukses, maupun saat terjadi eksepsi yang tertangkap ataupun yang tidak tertangkap. Jika mutasi state (misalnya merubah status unit atau memperbarui kuota) ditaruh di dalam <code>finally</code>, mutasi tersebut akan tetap dijalankan meskipun transaksi bisnis sebelumnya telah dilempar exception dan dinyatakan gagal/ditolak. Blok <code>finally</code> hanya diperuntukkan bagi operasi pelepasan sumber daya (<em>resource cleanup</em>) seperti menutup koneksi database, stream I/O, atau sinkronisasi tampilan grafis.
        </div>
    </div>

    <div style="border-top: 1px solid #000000; margin-top: 15px; padding-top: 6px; font-size: 8.5pt; text-align: center;">
        Laporan Praktikum Pemrograman Berorientasi Objek &bull; Modul 10: Exception Handling & Validasi State Objek &bull; PENS PSDKU Sumenep &bull; 2026
    </div>

</body>
</html>
"""

    html_file = os.path.join(base_dir, "report_template.html")
    with open(html_file, "w", encoding="utf-8") as f:
        f.write(html_content)
    print("Template HTML berhasil ditulis:", html_file)
    
    # 1. Render PDF using headless Chrome
    chrome = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
    pdf_out = os.path.join(base_dir, "readme.pdf")
    pdf_copy = os.path.join(base_dir, "Readme.pdf")
    
    file_url = "file:///" + html_file.replace("\\", "/")
    
    cmd = [
        chrome,
        "--headless=new",
        "--disable-gpu",
        "--no-pdf-header-footer",
        f"--print-to-pdf={pdf_out}",
        file_url
    ]
    
    print("Menghasilkan PDF melalui Chrome Headless...")
    res = subprocess.run(cmd, capture_output=True, text=True)
    print("Return code:", res.returncode)
    
    if os.path.exists(pdf_out) and os.path.getsize(pdf_out) > 0:
        print(f"Berhasil! readme.pdf terbuat: {os.path.getsize(pdf_out)} bytes")
    else:
        print("Gagal membuat PDF melalui Chrome.")

if __name__ == "__main__":
    generate_report()
