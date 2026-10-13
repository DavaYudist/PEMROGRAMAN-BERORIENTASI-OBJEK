import os
import base64
import subprocess

workspace_dir = r"c:\Users\dava1\Documents\KULIAH\TUGAS KULIAH SEMESTER 3\PEMROGRAMAN BERBASIS OBJEK\CODE"
uts_dir = os.path.join(workspace_dir, "UTS_3125522002_M Davana Yudista Firdausy")
screenshots_dir = os.path.join(uts_dir, "screenshots")

terminal_img_path = os.path.join(screenshots_dir, "terminal_execution.png")
diagram_img_path = os.path.join(screenshots_dir, "class_diagram.png")

with open(terminal_img_path, "rb") as f:
    terminal_b64 = base64.b64encode(f.read()).decode("utf-8")

with open(diagram_img_path, "rb") as f:
    diagram_b64 = base64.b64encode(f.read()).decode("utf-8")

html_content = f"""<!DOCTYPE html>
<html lang="id">
<head>
<meta charset="UTF-8">
<title>Laporan Evaluasi Tengah Semester (ETS) - PBO Tipe C</title>
<style>
  @page {{
    size: A4 portrait;
    margin: 15mm 18mm 15mm 18mm;
  }}
  * {{
    box-sizing: border-box;
    -webkit-print-color-adjust: exact !important;
    print-color-adjust: exact !important;
  }}
  body {{
    font-family: 'Segoe UI', Arial, Helvetica, sans-serif;
    color: #1e293b;
    background: #ffffff;
    line-height: 1.5;
    font-size: 11pt;
    margin: 0;
    padding: 0;
  }}

  .header-card {{
    border-bottom: 3px solid #1e40af;
    padding-bottom: 12px;
    margin-bottom: 18px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }}
  .header-title-box {{
    flex: 1;
  }}
  .main-title {{
    font-size: 16pt;
    font-weight: 800;
    color: #1e3a8a;
    letter-spacing: 0.5px;
    margin: 0 0 4px 0;
    text-transform: uppercase;
  }}
  .sub-title {{
    font-size: 12pt;
    font-weight: 700;
    color: #0369a1;
    margin: 0 0 4px 0;
  }}
  .sub-title-dept {{
    font-size: 10pt;
    color: #64748b;
    font-weight: 600;
    margin: 0;
  }}

  .identity-card {{
    background: #f8fafc;
    border: 1px solid #cbd5e1;
    border-left: 5px solid #2563eb;
    border-radius: 6px;
    padding: 10px 14px;
    margin-bottom: 18px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 6px 20px;
    font-size: 10pt;
  }}
  .identity-item {{
    display: flex;
  }}
  .identity-label {{
    width: 140px;
    font-weight: 600;
    color: #475569;
  }}
  .identity-val {{
    font-weight: 700;
    color: #0f172a;
  }}

  h2.section-title {{
    font-size: 11.5pt;
    font-weight: 800;
    color: #1e3a8a;
    border-bottom: 1.5px solid #93c5fd;
    padding-bottom: 4px;
    margin: 14px 0 8px 0;
    display: flex;
    align-items: center;
  }}
  .section-num {{
    background: #1e3a8a;
    color: #ffffff;
    width: 22px;
    height: 22px;
    border-radius: 50%;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 9.5pt;
    margin-right: 8px;
  }}

  p, li {{
    font-size: 10pt;
    color: #334155;
    text-align: justify;
    line-height: 1.48;
    margin-top: 4px;
    margin-bottom: 6px;
  }}
  ul {{
    margin: 4px 0 8px 20px;
    padding: 0;
  }}
  li {{
    margin-bottom: 4px;
  }}

  .highlight-term {{
    font-weight: 700;
    color: #0f172a;
  }}

  .rule-box {{
    background: #eff6ff;
    border-left: 4px solid #3b82f6;
    padding: 8px 12px;
    margin: 6px 0 10px 0;
    border-radius: 0 6px 6px 0;
    font-size: 9.5pt;
  }}
  .rule-box strong {{
    color: #1d4ed8;
  }}

  .feature-table {{
    width: 100%;
    border-collapse: collapse;
    margin: 8px 0 12px 0;
    font-size: 9.5pt;
  }}
  .feature-table th {{
    background: #1e40af;
    color: #ffffff;
    font-weight: 700;
    padding: 7px 10px;
    border: 1px solid #1e40af;
    text-align: left;
  }}
  .feature-table td {{
    padding: 7px 10px;
    border: 1px solid #cbd5e1;
    color: #334155;
  }}
  .feature-table tr:nth-child(even) {{
    background: #f8fafc;
  }}

  .img-container {{
    text-align: center;
    margin: 10px 0 4px 0;
  }}
  .img-container img {{
    max-width: 95%;
    height: auto;
    border-radius: 6px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    border: 1px solid #cbd5e1;
  }}
  .img-caption {{
    font-size: 9pt;
    font-weight: 600;
    color: #475569;
    font-style: italic;
    margin-top: 6px;
    text-align: center;
  }}

  .page-break {{
    page-break-before: always;
    break-before: page;
  }}

  .footer-note {{
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    font-size: 8pt;
    color: #94a3b8;
    display: flex;
    justify-content: space-between;
    border-top: 1px solid #e2e8f0;
    padding-top: 4px;
  }}
</style>
</head>
<body>

  <!-- HEADER -->
  <div class="header-card">
    <div class="header-title-box">
      <h1 class="main-title">EVALUASI TENGAH SEMESTER (ETS)</h1>
      <div class="sub-title">PRAKTIKUM PEMROGRAMAN BERORIENTASI OBYEK — TIPE C</div>
      <div class="sub-title-dept">Politeknik Elektronika Negeri Surabaya (PENS) PSDKU Sumenep</div>
    </div>
  </div>

  <!-- IDENTITAS MAHASISWA -->
  <div class="identity-card">
    <div class="identity-item">
      <span class="identity-label">Nama Mahasiswa</span>
      <span class="identity-val">: M. Davana Yudista Firdausy</span>
    </div>
    <div class="identity-item">
      <span class="identity-label">Mata Kuliah</span>
      <span class="identity-val">: Praktikum PBO</span>
    </div>
    <div class="identity-item">
      <span class="identity-label">NRP</span>
      <span class="identity-val">: 3125522002</span>
    </div>
    <div class="identity-item">
      <span class="identity-label">Topik Kasus</span>
      <span class="identity-val">: Sistem Pengiriman Barang</span>
    </div>
    <div class="identity-item">
      <span class="identity-label">Kelas</span>
      <span class="identity-val">: 3D3PSDKU-SM-IT A</span>
    </div>
    <div class="identity-item">
      <span class="identity-label">Dosen Pengampu</span>
      <span class="identity-val">: Tim Dosen PBO PENS</span>
    </div>
  </div>

  <!-- BAGIAN 1 -->
  <h2 class="section-title"><span class="section-num">1</span> Arsitektur Class, Encapsulation, dan Business Rule</h2>
  <p>
    Sistem Pengiriman Barang ini dibangun di atas fondasi <strong>Object-Oriented Programming (OOP)</strong> yang kokoh dengan total 6 entitas (1 <strong>Interface</strong> dan 5 <strong>Class</strong>), yang telah memenuhi seluruh standar kualifikasi evaluasi:
  </p>
  <ul>
    <li>
      <span class="highlight-term">Encapsulation</span>: Seluruh atribut pada superclass <code>Pengiriman</code> (seperti <code>noResi</code>, <code>beratKg</code>, <code>jarakKm</code>, <code>status</code>) serta subclass dideklarasikan menggunakan access modifier <code>private</code>. Pengaksesan dan modifikasi nilai state objek secara ketat diatur hanya melalui metode accessor (<code>getter</code>) dan mutator (<code>setter</code>).
    </li>
    <li>
      <span class="highlight-term">Validation (Business Rule)</span>: Dua aturan bisnis krusial diimplementasikan langsung pada metode mutator di superclass <code>Pengiriman</code> guna menjamin integritas data:
      <div class="rule-box">
        &bull; <strong>Validasi Berat:</strong> Bobot paket (<code>beratKg</code>) harus bernilai lebih besar dari nol (<code>beratKg &gt; 0</code>).<br>
        &bull; <strong>Validasi Jarak:</strong> Jarak tempuh pengiriman (<code>jarakKm</code>) harus bernilai lebih besar dari nol (<code>jarakKm &gt; 0</code>).<br>
        &bull; <strong>Penanganan Pelanggaran:</strong> Apabila nilai parameter tidak valid (<span class="highlight-term">Negative Testing</span>), sistem langsung mencetak pesan error validasi ke konsol dan menolak mutasi nilai field.
      </div>
    </li>
  </ul>

  <!-- BAGIAN 2 -->
  <h2 class="section-title"><span class="section-num">2</span> Relationship, Inheritance, dan Abstraction</h2>
  <p>
    Rancangan relasi antar-kelas merepresentasikan ekosistem logistik terpadu secara akurat:
  </p>
  <ul>
    <li>
      <span class="highlight-term">Abstraction &amp; Interface</span>: Dirancang sebuah <strong>Interface</strong> <code>LayananLacak</code> yang mendefinisikan kontrak perilaku pelacakan (<code>lacakStatus()</code>). Kontrak ini diimplementasikan oleh <strong>Abstract Class</strong> <code>Pengiriman</code>. Kelas <code>Pengiriman</code> tidak dapat diinstansiasi secara langsung karena berperan murni sebagai kerangka dasar abstrak bagi seluruh jenis paket.
    </li>
    <li>
      <span class="highlight-term">Inheritance</span>: <code>Pengiriman</code> menurunkan karakteristiknya kepada dua concrete subclass: <code>PengirimanReguler</code> dan <code>PengirimanExpress</code>. Keduanya mewarisi atribut umum resi, bobot, jarak, dan status, namun memiliki spesialisasi fitur (seperti atribut tambahan <code>asuransi</code> pada kelas Express).
    </li>
    <li>
      <span class="highlight-term">Relationship (Association &amp; Aggregation)</span>: Kelas <code>Manifest</code> bertindak sebagai koordinator (<em>orchestrator</em>). Kelas ini mengimplementasikan:
      <br>&bull; <span class="highlight-term">Association</span>: Relasi antara <code>Manifest</code> dengan aktor <code>Kurir</code> (sebuah manifes dikelola oleh seorang kurir bertugas).
      <br>&bull; <span class="highlight-term">Aggregation</span>: Relasi antara <code>Manifest</code> dengan <code>List&lt;Pengiriman&gt;</code> (sebuah manifes menampung sekumpulan paket yang dapat berdiri sendiri tanpa ketergantungan daur hidup mutlak).
    </li>
  </ul>

  <div class="page-break"></div>

  <!-- BAGIAN 3 -->
  <h2 class="section-title"><span class="section-num">3</span> Polymorphism &amp; Fitur Kreativitas Bisnis</h2>
  <p>
    <span class="highlight-term">Polymorphism</span> (<span class="highlight-term">Dynamic Method Dispatch</span>) diterapkan secara nyata pada kelas <code>Manifest</code>. Saat metode <code>cetakManifest()</code> dieksekusi, sistem melakukan iterasi polimorfik terhadap koleksi objek bertipe referensi superclass <code>Pengiriman</code>. Karena metode <code>hitungBiaya()</code> dan <code>estimasiWaktu()</code> dideklarasikan sebagai <span class="highlight-term">Abstract Method</span>, maka secara dinamis pada saat runtime JVM mengeksekusi logika kalkulasi spesifik dari masing-masing concrete subclass:
  </p>

  <table class="feature-table">
    <thead>
      <tr>
        <th style="width: 24%;">Layanan Paket</th>
        <th style="width: 44%;">Rumus Kalkulasi Tarif (Fitur Kreatif)</th>
        <th style="width: 32%;">Estimasi Waktu Tiba</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td><strong>Pengiriman Reguler</strong></td>
        <td><code>(berat &times; Rp5.000) + (jarak &times; Rp1.500)</code></td>
        <td>3-5 Hari Kerja</td>
      </tr>
      <tr>
        <td><strong>Pengiriman Express</strong></td>
        <td>
          <code>(berat &times; Rp10.000) + (jarak &times; Rp3.000)</code><br>
          <span style="color: #2563eb; font-weight: 600;">+ Proteksi Asuransi Opsional:</span> Rp50.000
        </td>
        <td>1-2 Hari Kerja (Prioritas Tinggi)</td>
      </tr>
    </tbody>
  </table>

  <!-- BAGIAN 4 -->
  <h2 class="section-title"><span class="section-num">4</span> Bukti Pengujian Eksekusi Program (Skenario Runtime)</h2>
  <p>
    Berikut adalah tangkapan layar pembuktian hasil kompilasi program tanpa error serta eksekusi tiga skenario runtime: pembuatan objek manifes dan kurir (<span class="highlight-term">Association</span>), pengujian validasi data input negatif (<span class="highlight-term">Negative Testing</span>), dan demonstrasi kalkulasi polimorfik pada pencetakan manifes tagihan (<span class="highlight-term">Polymorphism</span>):
  </p>

  <div class="img-container">
    <img src="data:image/png;base64,{terminal_b64}" alt="Screenshot Terminal Hasil Eksekusi">
    <div class="img-caption">
      Gambar 1. Hasil eksekusi terminal membuktikan perlindungan enkapsulasi pada input minus dan berjalannya polimorfisme perhitungan tarif.
    </div>
  </div>

  <div class="page-break"></div>

  <!-- BAGIAN 5 -->
  <h2 class="section-title"><span class="section-num">5</span> Visualisasi UML Class Diagram</h2>
  <p>
    Struktur arsitektur berorientasi objek yang utuh dimodelkan dalam Diagram Kelas UML berikut, mengilustrasikan realisasi antarmuka (<span class="highlight-term">Interface Implementation</span>), pewarisan (<span class="highlight-term">Inheritance</span>), serta hubungan objek (<span class="highlight-term">Association</span> dan <span class="highlight-term">Aggregation</span>):
  </p>

  <div class="img-container" style="margin-top: 15px;">
    <img src="data:image/png;base64,{diagram_b64}" style="max-width: 82%;" alt="UML Class Diagram">
    <div class="img-caption" style="margin-top: 10px;">
      Gambar 2. Diagram Kelas UML Sistem Pengiriman Barang (Tipe C) mendokumentasikan hierarki abstraksi, relasi Kurir, dan agregasi Manifest.
    </div>
  </div>

  <table class="feature-table" style="margin-top: 20px;">
    <thead>
      <tr>
        <th style="width: 25%;">Komponen OOP</th>
        <th style="width: 25%;">Nama Entitas</th>
        <th style="width: 50%;">Peran dalam Arsitektur Sistem</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td><strong>Interface</strong></td>
        <td><code>LayananLacak</code></td>
        <td>Kontrak perilaku standar pelacakan status kiriman (<code>lacakStatus()</code>).</td>
      </tr>
      <tr>
        <td><strong>Abstract Class</strong></td>
        <td><code>Pengiriman</code></td>
        <td>Fondasi abstraksi paket, mengenkapsulasi data resi &amp; validasi berat/jarak.</td>
      </tr>
      <tr>
        <td><strong>Concrete Subclasses</strong></td>
        <td><code>PengirimanReguler</code><br><code>PengirimanExpress</code></td>
        <td>Implementasi polimorfik rumus ongkir dan estimasi pengiriman dinamis.</td>
      </tr>
      <tr>
        <td><strong>Actor Class</strong></td>
        <td><code>Kurir</code></td>
        <td>Entitas pembawa paket (ID, Nama, Plat Nomor). Berelasi asosiasi ke Manifest.</td>
      </tr>
      <tr>
        <td><strong>Orchestrator Class</strong></td>
        <td><code>Manifest</code></td>
        <td>Mengkoordinasikan daftar paket secara agregasi dan menghitung total pendapatan.</td>
      </tr>
    </tbody>
  </table>

</body>
</html>
"""

html_out_path = os.path.join(uts_dir, "report.html")
with open(html_out_path, "w", encoding="utf-8") as f:
    f.write(html_content)

print("Generated report.html successfully!")

pdf_out_path = os.path.join(uts_dir, "class-diagram.pdf")
readme_pdf_path = os.path.join(uts_dir, "Readme.pdf")
chrome_exe = r"C:\Program Files\Google\Chrome\Application\chrome.exe"

html_url = "file:///" + html_out_path.replace("\\", "/")

cmd = [
    chrome_exe,
    "--headless=new",
    "--disable-gpu",
    "--no-pdf-header-footer",
    f"--print-to-pdf={pdf_out_path}",
    html_url
]

subprocess.run(cmd, check=True)
print(f"Generated class-diagram.pdf successfully! Size: {os.path.getsize(pdf_out_path)} bytes")

import shutil
shutil.copyfile(pdf_out_path, readme_pdf_path)
print(f"Generated Readme.pdf successfully! Size: {os.path.getsize(readme_pdf_path)} bytes")

