package src;

public class Kurir {
    private String idKurir;
    private String nama;
    private String platKendaraan;

    public Kurir(String idKurir, String nama, String platKendaraan) {
        this.idKurir = idKurir; 
        this.nama = nama; 
        this.platKendaraan = platKendaraan;
    }
    public String getNama() { return nama; }
    public String getPlatKendaraan() { return platKendaraan; }
}
