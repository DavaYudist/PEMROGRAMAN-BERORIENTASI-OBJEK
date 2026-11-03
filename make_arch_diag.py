import os
from PIL import Image, ImageDraw, ImageFont

def create_architecture_diagram(out_path):
    width = 1100
    height = 580
    img = Image.new("RGB", (width, height), color=(255, 255, 255))
    draw = ImageDraw.Draw(img)

    try:
        font_title = ImageFont.truetype("arialbd.ttf", 14)
        font_bold = ImageFont.truetype("arialbd.ttf", 11)
        font_normal = ImageFont.truetype("arial.ttf", 10)
        font_mono = ImageFont.truetype("consola.ttf", 9.5)
    except:
        font_title = ImageFont.load_default()
        font_bold = font_title
        font_normal = font_title
        font_mono = font_title

    # Outer border
    draw.rectangle([5, 5, width-5, height-5], outline=(0, 0, 0), width=1)

    # Title header
    draw.rectangle([5, 5, width-5, 38], fill=(240, 243, 246), outline=(0, 0, 0), width=1)
    draw.text((width // 2 - 270, 12), "DIAGRAM ARSITEKTUR KELAS & EXCEPTION HIERARCHY (MODUL 10)", fill=(0, 0, 0), font=font_title)

    # Left Box: Exception Hierarchy
    draw.rectangle([25, 55, 480, 545], outline=(0, 0, 0), width=2, fill=(250, 250, 250))
    draw.rectangle([25, 55, 480, 85], fill=(225, 230, 238), outline=(0, 0, 0), width=1)
    draw.text((120, 62), "HIERARKI CUSTOM EXCEPTION (CHECKED)", fill=(0, 0, 0), font=font_bold)

    # java.lang.Exception box
    draw.rectangle([130, 100, 370, 140], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
    draw.text((180, 105), "<<java.lang>>", fill=(100, 100, 100), font=font_normal)
    draw.text((195, 120), "Exception", fill=(0, 0, 0), font=font_bold)

    # Lines down to children
    draw.line([250, 140, 250, 165], fill=(0, 0, 0), width=2)
    draw.line([70, 165, 430, 165], fill=(0, 0, 0), width=2)
    
    # 3 children lines
    draw.line([70, 165, 70, 185], fill=(0, 0, 0), width=2)
    draw.line([250, 165, 250, 185], fill=(0, 0, 0), width=2)
    draw.line([430, 165, 430, 185], fill=(0, 0, 0), width=2)

    # Child 1: KendaraanTidakTersediaException
    draw.rectangle([35, 185, 210, 275], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
    draw.text((42, 190), "KendaraanTidakTersedia", fill=(0, 0, 0), font=font_bold)
    draw.text((80, 205), "Exception", fill=(0, 0, 0), font=font_bold)
    draw.line([35, 222, 210, 222], fill=(0, 0, 0), width=1)
    draw.text((42, 228), "- platNomor: String", fill=(0, 0, 0), font=font_mono)
    draw.line([35, 245, 210, 245], fill=(0, 0, 0), width=1)
    draw.text((42, 252), "+ getPlatNomor()", fill=(0, 0, 0), font=font_mono)

    # Child 2: KendaraanRusakException
    draw.rectangle([215, 185, 385, 290], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
    draw.text((230, 190), "KendaraanRusak", fill=(0, 0, 0), font=font_bold)
    draw.text((250, 205), "Exception", fill=(0, 0, 0), font=font_bold)
    draw.line([215, 222, 385, 222], fill=(0, 0, 0), width=1)
    draw.text((220, 228), "- platNomor: String", fill=(0, 0, 0), font=font_mono)
    draw.text((220, 242), "- kondisiMesin: String", fill=(0, 0, 0), font=font_mono)
    draw.line([215, 258, 385, 258], fill=(0, 0, 0), width=1)
    draw.text((220, 265), "+ getKondisiMesin()", fill=(0, 0, 0), font=font_mono)

    # Child 3: KapasitasPenuhException
    draw.rectangle([325, 310, 470, 410], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
    draw.line([430, 185, 430, 310], fill=(0, 0, 0), width=2)
    draw.text((338, 315), "KapasitasPenuh", fill=(0, 0, 0), font=font_bold)
    draw.text((358, 330), "Exception", fill=(0, 0, 0), font=font_bold)
    draw.line([325, 345, 470, 345], fill=(0, 0, 0), width=1)
    draw.text((330, 352), "- kapasitasTersedia", fill=(0, 0, 0), font=font_mono)
    draw.text((330, 366), "- kapasitasDiminta", fill=(0, 0, 0), font=font_mono)
    draw.line([325, 382, 470, 382], fill=(0, 0, 0), width=1)
    draw.text((330, 390), "+ getKapasitas...()", fill=(0, 0, 0), font=font_mono)

    # Standard Runtime Exceptions Box
    draw.rectangle([35, 430, 315, 530], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
    draw.text((50, 435), "UNCHECKED EXCEPTIONS (RUNTIME)", fill=(0, 0, 0), font=font_bold)
    draw.line([35, 452, 315, 452], fill=(0, 0, 0), width=1)
    draw.text((42, 460), "* IllegalArgumentException", fill=(0, 0, 0), font=font_mono)
    draw.text((52, 475), "(durasiJam <= 0, diskon < 0 || > 100)", fill=(100, 100, 100), font=font_normal)
    draw.text((42, 492), "* IllegalStateException", fill=(0, 0, 0), font=font_mono)
    draw.text((52, 507), "(kembalikan unit yang tidak disewa)", fill=(100, 100, 100), font=font_normal)

    # Right Box: PerusahaanRental & Validation Flow
    draw.rectangle([505, 55, 1075, 545], outline=(0, 0, 0), width=2, fill=(250, 250, 250))
    draw.rectangle([505, 55, 1075, 85], fill=(225, 230, 238), outline=(0, 0, 0), width=1)
    draw.text((640, 62), "MEKANISME VALIDASI DEFENTIF SEBELUM MUTASI STATE", fill=(0, 0, 0), font=font_bold)

    # Diagram Step Boxes
    steps = [
        ("LANGKAH 1: Validasi Eksistensi Armada", "findKendaraan(plat) != null", "GAGAL -> throw IllegalArgumentException(\"Tidak ditemukan\")"),
        ("LANGKAH 2: Validasi Kelayakan Operasional Mesin", "!\"Dalam Perbaikan\".equalsIgnoreCase(k.getKondisiMesin())", "GAGAL -> throw KendaraanRusakException(...)"),
        ("LANGKAH 3: Validasi Ketersediaan Unit (State)", "!k.isDisewa()", "GAGAL -> throw KendaraanTidakTersediaException(...)"),
        ("LANGKAH 4: Validasi Parameter Transaksi", "durasiJam > 0 && diskonPersen [0..100] && !nama.isEmpty()", "GAGAL -> throw IllegalArgumentException(...)"),
        ("LANGKAH 5: Eksekusi Transaksi ACID MySQL & Mutasi State", "k.setDisewa(true); k.setPenyewa(nama); armadaDisewa.add(k);", "SEMUA VALIDASI LULUS -> State termutasi secara aman & atomic")
    ]

    y_box = 100
    for title, condition, outcome in steps:
        draw.rectangle([525, y_box, 1055, y_box + 70], outline=(0, 0, 0), width=1, fill=(255, 255, 255))
        draw.text((535, y_box + 6), title, fill=(0, 50, 120), font=font_bold)
        draw.text((545, y_box + 26), f"Pemeriksaan: {condition}", fill=(0, 0, 0), font=font_mono)
        if "LULUS" in outcome:
            draw.text((545, y_box + 46), f"Hasil: {outcome}", fill=(16, 120, 60), font=font_bold)
        else:
            draw.text((545, y_box + 46), f"Hasil: {outcome}", fill=(180, 20, 20), font=font_normal)
        
        if y_box < 440:
            draw.line([790, y_box + 70, 790, y_box + 85], fill=(0, 0, 0), width=2)
            # Arrow head
            draw.polygon([(785, y_box + 80), (795, y_box + 80), (790, y_box + 85)], fill=(0, 0, 0))

        y_box += 85

    img.save(out_path)
    print(f"Architecture diagram saved to {out_path}")

if __name__ == "__main__":
    p10_dir = "P10_3125522002_M Davana Yudista Firdausy"
    sc_dir = os.path.join(p10_dir, "Screenshot")
    out_file = os.path.join(sc_dir, "exception_architecture_diagram.png")
    create_architecture_diagram(out_file)
