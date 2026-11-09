package SourceCode;

import SourceCode.model.Buku;
import SourceCode.model.Perpustakaan;

public class Main {
    public static void main(String[] args) {
        Perpustakaan perpustakaan = new Perpustakaan();

        System.out.println("=================================================");
        System.out.println(" PRAKTIKUM P11 - KOLEKSI OBJEK PERPUSTAKAAN     ");
        System.out.println(" Nama: M. Davana Yudista Firdausy (3125522002)   ");
        System.out.println("=================================================");

        System.out.println("\n[Langkah 1] Menambah Buku Awal:");
        perpustakaan.tambahBuku(new Buku("B001", "Dasar Java"));
        perpustakaan.tambahBuku(new Buku("B002", "Konsep OOP"));
        perpustakaan.tampilkanBuku();

        System.out.println("\n[Langkah 2] Pencarian Buku 'B002':");
        Buku hasil = perpustakaan.cariBuku("B002");
        System.out.println(hasil != null ? "Ditemukan: " + hasil.getJudul() : "Buku tidak ditemukan.");

        System.out.println("\n[Langkah 3] Menghapus Buku 'B001':");
        boolean terhapus = perpustakaan.hapusBuku("B001");
        System.out.println(terhapus ? "Buku berhasil dihapus." : "Buku gagal dihapus.");

        System.out.println("\n[Langkah 4] Menampilkan Isi Koleksi Akhir:");
        perpustakaan.tampilkanBuku();
    }
}
