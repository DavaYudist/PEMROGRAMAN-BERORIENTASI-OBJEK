package model;

/**
 * P7: Interface Kontrak Pemutus Mesin Jarak Jauh (Remote Immobilizer / Cutoff).
 * Digunakan sebagai senjata taktis pemilik rental saat mobil dicurigai dibawa kabur.
 */
public interface RemoteEngineControl {
    boolean matikanMesinRemote();
    boolean hidupkanMesinRemote();
    boolean isMesinDimatikanRemote();
}

