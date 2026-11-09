package SourceCode;

import model.*;
import model.exception.KapasitasPenuhException;
import model.exception.KendaraanRusakException;
import model.exception.KendaraanTidakTersediaException;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main Application Launcher untuk Sistem Pemantauan Armada Rental Mobil (FleetSumenep)
 * Mengintegrasikan JavaFX WebView (WebKit) dengan model OOP domain (P1-P7)
 * Memuat 3 modul antarmuka: Leaflet Maps, Manajemen Parkir, dan Manajemen Armada/Billing.
 * 
 * Mahasiswa: M. Davana Yudista Firdausy (NRP: 3125522002) - Kelas A D3 TI-SM
 */
public class Main extends Application {

    private PerusahaanRental rental;
    private WebView mapWebView;
    private WebEngine mapEngine;
    private WebView parkingWebView;
    private WebEngine parkingEngine;
    private WebView adminWebView;
    private WebEngine adminEngine;

    private Button btnNavMap;
    private Button btnNavParking;
    private Button btnNavAdmin;
    private Button btnSimulasiToggle;
    private Label lblStatusBar;
    private Label lblLastUpdate;

    private boolean isSimulasiBerjalan = true;
    private Timer simulationTimer;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    // Parameter pergerakan simulasi di jalanan Sumenep
    private double dir1 = 1.0;
    private double dir2 = 1.0;

    @Override
    public void start(Stage primaryStage) {
        // 1. Inisialisasi Domain Model OOP (P1 - P7)
        initDomainModel();

        // 2. Inisialisasi WebViews untuk Page 1, Page 2, dan Page 3
        mapWebView = new WebView();
        mapEngine = mapWebView.getEngine();
        mapEngine.setJavaScriptEnabled(true);
        mapEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        parkingWebView = new WebView();
        parkingEngine = parkingWebView.getEngine();
        parkingEngine.setJavaScriptEnabled(true);
        parkingEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        adminWebView = new WebView();
        adminEngine = adminWebView.getEngine();
        adminEngine.setJavaScriptEnabled(true);
        adminEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // Status visibilitas awal: Map aktif (Page 1), lainnya inaktif agar tidak membebani software renderer
        mapWebView.setVisible(true);
        mapWebView.setManaged(true);
        parkingWebView.setVisible(false);
        parkingWebView.setManaged(false);
        adminWebView.setVisible(false);
        adminWebView.setManaged(false);

        // Bridge Java ke JavaScript
        DesktopBridge bridge = new DesktopBridge();

        setupEngine(mapEngine, getLocalAssetFile("map.html"), bridge, () -> {
            if (mapWebView.isVisible()) {
                pushFleetData();
            }
            Platform.runLater(() -> {
                PauseTransition pause = new PauseTransition(Duration.millis(300));
                pause.setOnFinished(ev -> {
                    try {
                        mapEngine.executeScript("if (typeof triggerMapResize === 'function') triggerMapResize();");
                    } catch (Exception ex) {}
                });
                pause.play();
            });
        });

        setupEngine(parkingEngine, getLocalAssetFile("parking.html"), bridge, () -> {
            if (parkingWebView.isVisible()) {
                pushParkingData();
            }
        });

        setupEngine(adminEngine, getLocalAssetFile("fleet_admin.html"), bridge, () -> {
            if (adminWebView.isVisible()) {
                pushAdminData();
            }
        });

        // 3. Bangun Antarmuka Native Desktop Header & Navigation
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");

        VBox topContainer = new VBox();
        topContainer.getChildren().addAll(createAppHeader(), createNavigationToolbar());
        root.setTop(topContainer);

        // Center Area: StackPane memuat ketiga WebView
        StackPane centerStack = new StackPane();
        centerStack.getChildren().addAll(mapWebView, parkingWebView, adminWebView);
        root.setCenter(centerStack);

        // Bottom Status Bar
        root.setBottom(createStatusBar());

        // 4. Timer Simulasi Pergerakan Telemetri
        startSimulationTimer();

        // 5. Tampilkan Window Native Desktop
        Scene scene = new Scene(root, 1280, 840);
        primaryStage.setTitle("FleetSumenep DESKTOP • Rental Car Fleet Monitoring MVP (M. Davana Yudista - 3125522002)");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(680);
        
        primaryStage.setOnCloseRequest(e -> {
            if (simulationTimer != null) {
                simulationTimer.cancel();
            }
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
        System.out.println("==================================================================");
        System.out.println(" FleetSumenep Desktop Hybrid Window Aktif!");
        System.out.println(" Mahasiswa : M. Davana Yudista Firdausy (3125522002)");
        System.out.println(" Modul     : Page 1 (GPS Map), Page 2 (Parking), Page 3 (Fleet Admin)");
        System.out.println(" Pricing   : Daily Base (24 Jam) dengan Prorata Sejam & Diskon");
        System.out.println(" Rasterizer: Software Prism Pipeline (-Dprism.order=sw)");
        System.out.println("==================================================================");
    }

    private void initDomainModel() {
        rental = new PerusahaanRental("BPP Wilayah Sumenep & Madura");
        System.out.println("[FLEET MONITORING] Berhasil menginisialisasi domain model dengan data persisten MySQL Laragon.");
    }

    private File getLocalAssetFile(String fileName) {
        String[] candidatePaths = {
            "P9_3125522002_M Davana Yudista Firdausy/SourceCode/web/" + fileName,
            "SourceCode/web/" + fileName,
            "web/" + fileName
        };
        for (String p : candidatePaths) {
            File f = new File(p);
            if (f.exists()) {
                return f;
            }
        }
        return new File("SourceCode/web/" + fileName);
    }

    private void setupEngine(WebEngine engine, File htmlFile, DesktopBridge bridge, Runnable onLoaded) {
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    JSObject win = (JSObject) engine.executeScript("window");
                    win.setMember("javaBridge", bridge);
                } catch (Exception e) {
                    System.err.println("Gagal menginjeksi javaBridge: " + e.getMessage());
                }
                if (onLoaded != null) {
                    onLoaded.run();
                }
            }
        });

        if (htmlFile.exists()) {
            engine.load(htmlFile.toURI().toString());
        } else {
            engine.loadContent("<html><body><h3>Berkas " + htmlFile.getName() + " tidak ditemukan.</h3></body></html>");
        }
    }

    private Node createAppHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: #1b4332; -fx-border-color: #0f291e; -fx-border-width: 0 0 2 0;");

        VBox brandBox = new VBox(2);
        Label lblBrand = new Label("FleetSumenep DESKTOP • SYSTEM MONITORING & BILLING");
        lblBrand.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label lblSub = new Label("Sistem Pemantauan Armada, Manajemen Parkir & Billing Sewa • M. Davana Yudista Firdausy (3125522002)");
        lblSub.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 11px; -fx-text-fill: #a7f3d0;");
        brandBox.getChildren().addAll(lblBrand, lblSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblBadge = new Label("● Standalone Hybrid WebView (WebKit)");
        lblBadge.setStyle("-fx-background-color: #2d6a4f; -fx-text-fill: #dcfce7; -fx-padding: 4 10 4 10; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;");

        header.getChildren().addAll(brandBox, spacer, lblBadge);
        return header;
    }

    private Node createNavigationToolbar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 20, 8, 20));
        bar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");

        btnNavMap = new Button("📍 Page 1: Live GPS Fleet Tracking");
        btnNavParking = new Button("🅿️ Page 2: Parking & Fleet Availability");
        btnNavAdmin = new Button("💼 Page 3: Manajemen Armada & Billing");

        styleNavButton(btnNavMap, true);
        styleNavButton(btnNavParking, false);
        styleNavButton(btnNavAdmin, false);

        btnNavMap.setOnAction(e -> switchTab(0));
        btnNavParking.setOnAction(e -> switchTab(1));
        btnNavAdmin.setOnAction(e -> switchTab(2));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnSimulasiToggle = new Button("🚗 Simulasi Gerak: AKTIF");
        btnSimulasiToggle.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #166534; -fx-border-color: #86efac; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;");
        btnSimulasiToggle.setOnAction(e -> {
            isSimulasiBerjalan = !isSimulasiBerjalan;
            if (isSimulasiBerjalan) {
                btnSimulasiToggle.setText("🚗 Simulasi Gerak: AKTIF");
                btnSimulasiToggle.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #166534; -fx-border-color: #86efac; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;");
            } else {
                btnSimulasiToggle.setText("⏸️ Simulasi Gerak: JEDA");
                btnSimulasiToggle.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #991b1b; -fx-border-color: #fca5a5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;");
            }
        });

        Button btnRefresh = new Button("🔄 Refresh Data");
        btnRefresh.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-font-weight: bold; -fx-font-size: 11px; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> {
            if (mapWebView.isVisible()) {
                pushFleetData();
                try {
                    mapEngine.executeScript("if (typeof triggerMapResize === 'function') triggerMapResize();");
                } catch (Exception ex) {}
            }
            if (parkingWebView.isVisible()) {
                pushParkingData();
            }
            if (adminWebView.isVisible()) {
                pushAdminData();
            }
        });

        bar.getChildren().addAll(btnNavMap, btnNavParking, btnNavAdmin, spacer, btnSimulasiToggle, btnRefresh);
        return bar;
    }

    private void switchTab(int tabIndex) {
        // Optimasi: Set visible dan managed agar WebView inaktif tidak mengonsumsi render pulse (menghindari crash RTTexture)
        mapWebView.setVisible(tabIndex == 0);
        mapWebView.setManaged(tabIndex == 0);
        parkingWebView.setVisible(tabIndex == 1);
        parkingWebView.setManaged(tabIndex == 1);
        adminWebView.setVisible(tabIndex == 2);
        adminWebView.setManaged(tabIndex == 2);

        styleNavButton(btnNavMap, tabIndex == 0);
        styleNavButton(btnNavParking, tabIndex == 1);
        styleNavButton(btnNavAdmin, tabIndex == 2);

        if (tabIndex == 0) {
            pushFleetData();
            Platform.runLater(() -> {
                PauseTransition pause = new PauseTransition(Duration.millis(150));
                pause.setOnFinished(ev -> {
                    try {
                        mapEngine.executeScript("if (typeof triggerMapResize === 'function') triggerMapResize();");
                    } catch (Exception ex) {}
                });
                pause.play();
            });
        } else if (tabIndex == 1) {
            pushParkingData();
        } else if (tabIndex == 2) {
            pushAdminData();
        }
    }

    private void styleNavButton(Button btn, boolean active) {
        if (active) {
            btn.setStyle("-fx-background-color: #1b4332; -fx-text-fill: #ffffff; -fx-border-color: #1b4332; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 7 16 7 16; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: #f8fafc; -fx-text-fill: #475569; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 7 16 7 16; -fx-font-weight: 600; -fx-font-size: 12px; -fx-cursor: hand;");
        }
    }

    private Node createStatusBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(6, 16, 6, 16));
        bar.setStyle("-fx-background-color: #0f172a; -fx-border-color: #1e293b; -fx-border-width: 1 0 0 0;");

        lblStatusBar = new Label("Status: Terhubung ke Domain Model Java (" + rental.getArmada().size() + " Armada, " + rental.getDaftarSlotKapasitas().size() + " Bay Parkir)");
        lblStatusBar.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblLastUpdate = new Label("Sinkronisasi: " + timeFormat.format(new Date()));
        lblLastUpdate.setStyle("-fx-font-family: 'Segoe UI', Arial; -fx-font-size: 11px; -fx-text-fill: #38bdf8; -fx-font-weight: bold;");

        bar.getChildren().addAll(lblStatusBar, spacer, lblLastUpdate);
        return bar;
    }

    private void startSimulationTimer() {
        simulationTimer = new Timer(true);
        simulationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isSimulasiBerjalan) {
                    // Update pergerakan telemetri untuk seluruh armada yang sedang aktif disewa di jalan
                    for (Kendaraan k : rental.getArmadaDisewa()) {
                        if ("M 1001 SM".equals(k.getPlatNomor())) {
                            double nextLat = k.getLat() + (0.00015 * dir1);
                            double nextLng = k.getLng() + (0.00020 * dir1);
                            if (nextLat > -6.995 || nextLat < -7.025) dir1 *= -1.0;
                            k.setLokasi(nextLat, nextLng);
                        } else if ("M 9999 VIP".equals(k.getPlatNomor())) {
                            double nextLat = k.getLat() - (0.00018 * dir2);
                            double nextLng = k.getLng() - (0.00012 * dir2);
                            if (nextLat > -7.010 || nextLat < -7.040) dir2 *= -1.0;
                            k.setLokasi(nextLat, nextLng);
                        } else {
                            double nextLat = k.getLat() + (0.00012 * dir1);
                            double nextLng = k.getLng() - (0.00010 * dir2);
                            if (nextLat > -6.995 || nextLat < -7.035) dir1 *= -1.0;
                            k.setLokasi(nextLat, nextLng);
                        }
                    }
                }

                Platform.runLater(() -> {
                    lblLastUpdate.setText("Sinkronisasi: " + timeFormat.format(new Date()));
                    lblStatusBar.setText("Status: Terhubung ke Domain Model Java (" + rental.getArmada().size() + " Armada, " + rental.getDaftarSlotKapasitas().size() + " Bay Parkir, " + rental.getArmadaDisewa().size() + " Sedang Disewa)");
                    
                    // Hanya push update DOM ke WebEngine yang sedang aktif/visible
                    if (mapWebView.isVisible()) {
                        pushFleetData();
                    } else if (parkingWebView.isVisible()) {
                        pushParkingData();
                    } else if (adminWebView.isVisible()) {
                        pushAdminData();
                    }
                });
            }
        }, 1000, 1500);
    }

    private void pushFleetData() {
        if (!mapWebView.isVisible()) return;
        try {
            String json = rental.getFleetJson();
            mapEngine.executeScript("if (typeof updateFleetData === 'function') updateFleetData(" + json + ");");
        } catch (Exception e) {
            // Abaikan jika DOM belum selesai dimuat
        }
    }

    private void pushParkingData() {
        if (!parkingWebView.isVisible()) return;
        try {
            String json = rental.getParkingJson();
            parkingEngine.executeScript("if (typeof updateParkingData === 'function') updateParkingData(" + json + ");");
        } catch (Exception e) {
            // Abaikan jika DOM belum selesai dimuat
        }
    }

    private void pushAdminData() {
        if (!adminWebView.isVisible()) return;
        try {
            String fleetJson = rental.getFleetJson();
            String parkingJson = rental.getParkingJson();
            adminEngine.executeScript("if (typeof updateFleetData === 'function') updateFleetData(" + fleetJson + ");");
            adminEngine.executeScript("if (typeof updateParkingData === 'function') updateParkingData(" + parkingJson + ");");
        } catch (Exception e) {
            // Abaikan jika DOM belum selesai dimuat
        }
    }

    private void syncAllActiveWebViews() {
        if (mapWebView.isVisible()) pushFleetData();
        if (parkingWebView.isVisible()) pushParkingData();
        if (adminWebView.isVisible()) pushAdminData();
    }

    /**
     * Bridge Object untuk komunikasi dua arah JavaScript ke Java backend
     */
    public class DesktopBridge {
        public void sewakan(String plat) {
            sewaMobilDenganDurasi(plat, "Pelanggan Prioritas Sumenep", 24);
        }

        public void sewaMobilDenganDurasi(String plat, String penyewa, int jam) {
            sewaMobilDenganDurasi(plat, penyewa, jam, false, 0.0);
        }

        public void sewaMobilDenganDurasi(String plat, String penyewa, int jam, boolean denganSupir, double diskonPersen) {
            Platform.runLater(() -> {
                try {
                    String hasil = rental.prosesSewa(plat, penyewa, jam, denganSupir, diskonPersen);
                    System.out.println("[TRANSAKSI SEWA] " + hasil);
                } catch (KendaraanTidakTersediaException e) {
                    System.err.println("[VALIDASI SEWA DITOLAK] " + e.getMessage());
                } catch (KendaraanRusakException e) {
                    System.err.println("[VALIDASI KONDISI DITOLAK] " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("[VALIDASI INPUT DITOLAK] " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("[SISTEM EXCEPTION] Error tidak terduga: " + e.getMessage());
                } finally {
                    syncAllActiveWebViews();
                }
            });
        }

        public void kembalikan(String plat, int slot) {
            kembalikanMobil(plat);
        }

        public void kembalikanMobil(String plat) {
            Platform.runLater(() -> {
                try {
                    Kendaraan k = rental.findKendaraan(plat);
                    if (k != null) {
                        rental.kembalikanArmada(k);
                        System.out.println("[ACTION] Armada " + plat + " dikembalikan ke bay " + k.getSlotParkir() + " (Standby).");
                    } else {
                        System.err.println("[ACTION GAGAL] Armada " + plat + " tidak ditemukan.");
                    }
                } catch (Exception e) {
                    System.err.println("[ACTION ERROR] Gagal mengembalikan armada: " + e.getMessage());
                } finally {
                    syncAllActiveWebViews();
                }
            });
        }

        public void editKendaraan(String plat, double tarifHarian, String kondisi, String catatan) {
            Platform.runLater(() -> {
                try {
                    boolean sukses = rental.editKendaraan(plat, tarifHarian, kondisi, catatan);
                    if (sukses) {
                        System.out.println("[FLEET ADMIN] Sukses memperbarui spesifikasi " + plat + " (Tarif Harian: Rp " + tarifHarian + ", Kondisi: " + kondisi + ")");
                    } else {
                        System.out.println("[FLEET ADMIN] Gagal memperbarui armada " + plat);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("[FLEET ADMIN - VALIDASI GAGAL] " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("[FLEET ADMIN - ERROR] " + e.getMessage());
                } finally {
                    syncAllActiveWebViews();
                }
            });
        }

        public void tambahMobil(String plat, String merk, String kategori, double tarifHarian, int odo, String kondisi) {
            tambahMobil(plat, merk, kategori, tarifHarian, odo, kondisi, "Bodi mulus, terawat baik");
        }

        public void tambahMobil(String plat, String merk, String kategori, double tarifHarian, int odo, String kondisi, String catatan) {
            Platform.runLater(() -> {
                try {
                    Kendaraan baru;
                    double defaultLat = -7.0150;
                    double defaultLng = 113.8650;
                    if ("VIP".equalsIgnoreCase(kategori)) {
                        baru = new MobilVIP(plat, merk, defaultLat, defaultLng, 100, 21.0, tarifHarian, odo, kondisi, catatan);
                    } else {
                        baru = new MobilReguler(plat, merk, defaultLat, defaultLng, 100, tarifHarian, odo, kondisi, catatan);
                    }
                    boolean sukses = rental.tambahKendaraan(baru);
                    if (sukses) {
                        System.out.println("[FLEET ADMIN] Sukses menambah kendaraan baru " + plat + " (" + merk + ") di bay " + baru.getSlotParkir());
                    } else {
                        System.out.println("[FLEET ADMIN] Gagal menambah kendaraan " + plat + " (Plat duplikat atau data tidak valid)");
                    }
                } catch (KapasitasPenuhException e) {
                    System.err.println("[FLEET ADMIN - KAPASITAS PENUH] " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("[FLEET ADMIN - ARGUMEN INVALID] " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("[FLEET ADMIN - ERROR] " + e.getMessage());
                } finally {
                    syncAllActiveWebViews();
                }
            });
        }

        public void hapusMobil(String plat) {
            Platform.runLater(() -> {
                try {
                    boolean sukses = rental.hapusKendaraan(plat);
                    if (sukses) {
                        System.out.println("[FLEET ADMIN] Berhasil menghapus kendaraan " + plat + " dari armada.");
                    } else {
                        System.out.println("[FLEET ADMIN] Gagal menghapus kendaraan " + plat + ".");
                    }
                } catch (IllegalStateException e) {
                    System.err.println("[FLEET ADMIN - STATE INVALID] " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("[FLEET ADMIN - ARGUMEN INVALID] " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("[FLEET ADMIN - ERROR] " + e.getMessage());
                } finally {
                    syncAllActiveWebViews();
                }
            });
        }

        public double hitungEstimasi(String plat, int jam) {
            return hitungEstimasi(plat, jam, false, 0.0);
        }

        public double hitungEstimasi(String plat, int jam, boolean denganSupir, double diskonPersen) {
            try {
                Kendaraan k = rental.findKendaraan(plat);
                if (k != null) {
                    return k.hitungBiayaSewa(jam, denganSupir, diskonPersen);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("[HITUNG ESTIMASI GAGAL] " + e.getMessage());
            }
            return 0.0;
        }

        public void tambahKapasitas(int tambahan) {
            Platform.runLater(() -> {
                try {
                    rental.tambahKapasitasSlot(tambahan);
                    System.out.println("[PARKING ADMIN] Kapasitas parkir pool diperluas +" + tambahan + " bay.");
                } catch (Exception e) {
                    System.err.println("[PARKING ADMIN - ERROR] " + e.getMessage());
                } finally {
                    syncAllActiveWebViews();
                }
            });
        }

        public void kurangiKapasitas(int jumlahKurang) {
            Platform.runLater(() -> {
                try {
                    boolean sukses = rental.kurangiKapasitasSlot(jumlahKurang);
                    if (sukses) {
                        System.out.println("[PARKING ADMIN] Kapasitas parkir pool berhasil dikurangi -" + jumlahKurang + " bay.");
                    }
                } catch (KapasitasPenuhException e) {
                    System.err.println("[PARKING ADMIN - BAY TERISI] " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("[PARKING ADMIN - ARGUMEN INVALID] " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("[PARKING ADMIN - ERROR] " + e.getMessage());
                } finally {
                    syncAllActiveWebViews();
                }
            });
        }

        public String getFleetData() {
            return rental.getFleetJson();
        }

        public String getParkingData() {
            return rental.getParkingJson();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
