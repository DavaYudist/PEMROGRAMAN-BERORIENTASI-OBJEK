// FLEETSUMENEP TYCOON - WEB GAME SIMULATION LOGIC (APP.JS)

let gameState = null;
let activeCrisisPlat = null;

function formatRupiah(num) {
    return 'Rp ' + Math.round(Number(num) || 0).toLocaleString('id-ID');
}

function showToastAlert(msg, isSuccess) {
    const toast = document.getElementById('toast-box');
    toast.textContent = msg;
    toast.className = isSuccess ? 'toast-success' : 'toast-error';
    toast.style.display = 'block';
    setTimeout(() => { toast.style.display = 'none'; }, 4000);
}

// Navigation Tabs
function switchNavTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.dock-btn').forEach(el => el.classList.remove('active'));

    const tab = document.getElementById('tab-' + tabName);
    const btn = document.getElementById('dock-btn-' + tabName);
    if (tab) tab.classList.add('active');
    if (btn) btn.classList.add('active');
}

// In-Game Simulation Speed
function setSimSpeed(spd) {
    document.querySelectorAll('.btn-speed').forEach(b => b.classList.remove('active'));
    if (spd === 0) document.getElementById('spd-pause').classList.add('active');
    else if (spd === 0.5) document.getElementById('spd-half').classList.add('active');
    else if (spd === 1) document.getElementById('spd-1x').classList.add('active');
    else if (spd === 2) document.getElementById('spd-2x').classList.add('active');
    else if (spd === 4) document.getElementById('spd-4x').classList.add('active');

    if (window.javaBridge && typeof window.javaBridge.setKecepatan === 'function') {
        window.javaBridge.setKecepatan(spd);
    }
}

// Callback Global dari Backend Java
function updateGameState(data) {
    if (!data) return;
    gameState = data;

    // 1. HUD & Metrics
    document.getElementById('hud-garage-name').textContent = data.namaGarasi || 'Garasi Magang Sumenep';
    document.getElementById('hud-cash-val').textContent = formatRupiah(data.saldo);
    if (data.waktu) {
        document.getElementById('hud-clock-val').textContent = `⏱️ ${data.waktu.waktuString}`;
    }

    // 2. Stats Banner
    document.getElementById('stat-total-armada').textContent = `${data.totalArmada} Unit`;
    const onRoadCount = data.armada ? data.armada.filter(c => c.status === 'DISEWA').length : 0;
    document.getElementById('stat-onroad').textContent = `${onRoadCount} Unit Jalan`;
    document.getElementById('stat-kapasitas').textContent = `${data.kapasitas} Bay Slot`;
    document.getElementById('stat-supir').textContent = `${data.jumlahSupir} Orang`;

    // 3. Render Orders
    renderOrders(data.pesanan || [], data.armada || []);

    // 4. Render Gantt Timeline 7-Hari
    renderGantt(data.armada || [], data.sesiSewa || [], data.waktu);

    // 5. Render Fleet Dossier
    renderFleet(data.armada || []);

    // 6. Render Transactions
    renderTransactions(data.transaksi || []);

    // 7. Check Emergency Crisis
    checkEmergencyCrisis(data.krisis);
}

// Render Tab 1: Orders Queue
function renderOrders(orders, armada) {
    const container = document.getElementById('order-list-container');
    const badge = document.getElementById('dock-badge-order');
    const pendingOrders = orders.filter(o => o.status === 'PENDING');
    
    badge.textContent = pendingOrders.length;
    document.getElementById('order-queue-count').textContent = `${pendingOrders.length} Calon Penyewa Mengantre`;

    if (pendingOrders.length === 0) {
        container.innerHTML = `
            <div class="stat-pill-card" style="text-align:center; padding:30px; color:var(--text-muted);">
                Saat ini belum ada pesanan baru yang mengantre. Jalankan simulasi waktu untuk mendatangkan pelanggan!
            </div>
        `;
        return;
    }

    container.innerHTML = '';
    pendingOrders.forEach(o => {
        const card = document.createElement('div');
        let riskClass = 'safe';
        if (o.tingkatBahaya === 'BAHAYA_TINGGI') riskClass = 'high-risk';
        else if (o.tingkatBahaya === 'SEDANG_WASPADA') riskClass = 'medium-risk';

        card.className = `order-card ${riskClass}`;

        // Cari armada yang siap untuk kategori ini
        const availableCars = armada.filter(c => c.kategori === o.kategori && c.status === 'SIAP');

        let selectCarHtml = '';
        if (availableCars.length > 0) {
            selectCarHtml = `<select id="alloc-car-${o.id}" style="padding:6px 10px; border-radius:6px; border:1px solid #cbd5e1; font-size:11.5px; font-weight:700;">`;
            availableCars.forEach(c => {
                selectCarHtml += `<option value="${c.plat}">${c.plat} - ${c.merk} (${formatRupiah(c.tarifHarian)}/hr)</option>`;
            });
            selectCarHtml += `</select>`;
        } else {
            selectCarHtml = `<span style="font-size:11px; color:var(--accent-red); font-weight:700;">⚠️ Unit ${o.kategori} Sedang Kosong</span>`;
        }

        card.innerHTML = `
            <div class="order-header">
                <div>
                    <div class="order-renter">${o.nama}</div>
                    <div class="order-purpose">${o.keperluan} • Durasi: <strong>${o.durasiJam} Jam</strong></div>
                </div>
                <span class="badge-rating" style="background:#f1f5f9; color:var(--text-secondary);">
                    Kategori: ${o.kategori}
                </span>
            </div>

            <!-- Indonesian Risk Badges -->
            <div class="risk-tags">
                <span class="risk-badge ${o.dokumenLengkap ? 'badge-safe' : 'badge-danger'}">
                    ${o.dokumenLengkap ? '✓ KTP + KK Lengkap' : '⚠️ Cuma KTP'}
                </span>
                <span class="risk-badge ${o.nomorLama ? 'badge-safe' : 'badge-warning'}">
                    ${o.nomorLama ? '✓ Nomor HP Lama' : '⚠️ Nomor Baru Aktif'}
                </span>
                <span class="risk-badge ${o.transfer ? 'badge-safe' : 'badge-warning'}">
                    ${o.transfer ? '✓ Transfer Bank DP' : '⚠️ Bayar Tunai Muka'}
                </span>
                <span class="risk-badge ${o.blacklist ? 'badge-danger' : 'badge-safe'}">
                    ${o.blacklist ? '🚨 Tercatat di Grup Asosiasi' : '✓ Bersih Asosiasi'}
                </span>
                ${o.jaminan ? '<span class="risk-badge badge-safe">🛡️ Jaminan Ditahan</span>' : ''}
                ${o.sopir ? '<span class="risk-badge badge-safe">👨✈️ Dengan Supir</span>' : ''}
                <span class="risk-badge ${riskClass === 'high-risk' ? 'badge-danger' : (riskClass === 'medium-risk' ? 'badge-warning' : 'badge-safe')}">
                    Skor Risiko: ${o.skorRisiko}% (${o.tingkatBahaya})
                </span>
            </div>

            <div style="display:flex; justify-content:space-between; align-items:center; margin-top:10px; flex-wrap:wrap; gap:8px;">
                <div style="display:flex; align-items:center; gap:8px;">
                    <span style="font-size:11px; font-weight:700; color:var(--text-secondary);">Pilih Armada:</span>
                    ${selectCarHtml}
                </div>

                <div class="order-actions">
                    ${availableCars.length > 0 ? 
                        `<button class="btn-action btn-accept" onclick="confirmAcceptOrder(${o.id})">✔️ Terima Sewa</button>` : 
                        `<button class="btn-action" style="opacity:0.5; cursor:not-allowed;" disabled>Unit Habis</button>`
                    }
                    ${!o.jaminan ? `<button class="btn-action btn-mitigate" onclick="actionMintaJaminan(${o.id})">✋ Minta Jaminan (-40% Risiko)</button>` : ''}
                    ${!o.sopir ? `<button class="btn-action btn-mitigate" onclick="actionTawarkanSopir(${o.id})">👨✈️ Tawarkan Supir (0% Risiko)</button>` : ''}
                    <button class="btn-action btn-reject" onclick="actionTolakPesanan(${o.id})">✖️ Tolak</button>
                </div>
            </div>
        `;
        container.appendChild(card);
    });
}

function confirmAcceptOrder(orderId) {
    const select = document.getElementById('alloc-car-' + orderId);
    if (!select || !select.value) {
        showToastAlert('Pilih unit armada yang tersedia terlebih dahulu!', false);
        return;
    }
    const plat = select.value;
    if (window.javaBridge && typeof window.javaBridge.terimaPesanan === 'function') {
        window.javaBridge.terimaPesanan(orderId, plat);
    }
}

function actionMintaJaminan(orderId) {
    if (window.javaBridge) window.javaBridge.mintaJaminan(orderId);
}

function actionTawarkanSopir(orderId) {
    if (window.javaBridge) window.javaBridge.tawarkanSopir(orderId);
}

function actionTolakPesanan(orderId) {
    if (window.javaBridge) window.javaBridge.tolakPesanan(orderId);
}

// Render Tab 2: 7-Day Gantt Timeline
function renderGantt(armada, sesiSewa, waktu) {
    const grid = document.getElementById('gantt-grid-container');
    grid.innerHTML = '';

    const currentDay = waktu ? waktu.hari : 1;

    // Header Row
    grid.innerHTML += `<div class="gantt-col-header" style="text-align:left;">Armada & Kategori</div>`;
    for (let d = 1; d <= 7; d++) {
        const isToday = (d === currentDay);
        grid.innerHTML += `
            <div class="gantt-col-header" style="${isToday ? 'background:var(--primary-soft); color:var(--primary-dark); border-color:var(--primary-light); font-weight:900;' : ''}">
                Hari ${d} ${isToday ? '★ Hari Ini' : ''}
            </div>
        `;
    }

    if (armada.length === 0) {
        grid.innerHTML += `<div style="grid-column:1/-1; padding:20px; text-align:center; color:var(--text-muted);">Belum ada armada terdaftar.</div>`;
        return;
    }

    // Car Rows
    armada.forEach(car => {
        // Car info cell
        grid.innerHTML += `
            <div class="gantt-car-cell">
                <span style="font-size:12px; font-weight:900; color:var(--primary-dark);">${car.plat}</span>
                <span style="font-size:10px; color:var(--text-secondary);">${car.merk} [${car.kategori}]</span>
            </div>
        `;

        // 7 Day Cells
        for (let d = 1; d <= 7; d++) {
            // Check active session for this car on this day
            const activeSession = sesiSewa.find(s => s.plat === car.plat && !s.selesai && d >= s.hariMulai && d <= s.hariSelesai);
            
            if (activeSession) {
                grid.innerHTML += `
                    <div class="gantt-block-cell">
                        <div class="gantt-pill-rented" title="Disewa oleh ${activeSession.penyewa}">
                            <span>👤 ${activeSession.penyewa}</span>
                            <span style="font-size:8.5px; opacity:0.8;">Disewa (${activeSession.durasiJam} Jam)</span>
                        </div>
                    </div>
                `;
            } else if (car.status === 'HILANG') {
                grid.innerHTML += `
                    <div class="gantt-block-cell" style="background:#fee2e2;">
                        <span style="font-size:10px; font-weight:800; color:var(--accent-red);">🚨 HILANG</span>
                    </div>
                `;
            } else if (car.status === 'SERVIS') {
                grid.innerHTML += `
                    <div class="gantt-block-cell" style="background:#fef3c7;">
                        <span style="font-size:10px; font-weight:800; color:var(--accent-amber);">🔧 SERVIS</span>
                    </div>
                `;
            } else {
                grid.innerHTML += `
                    <div class="gantt-block-cell">
                        <span style="font-size:10px; color:var(--primary-main); font-weight:700;">Ready</span>
                    </div>
                `;
            }
        }
    });
}

// Render Tab 3: Fleet Cards
function renderFleet(armada) {
    const container = document.getElementById('fleet-list-container');
    container.innerHTML = '';

    armada.forEach(car => {
        const card = document.createElement('div');
        card.className = 'fleet-card';

        let statusBadgeColor = 'background:#dcfce7; color:#166534;';
        if (car.status === 'DISEWA') statusBadgeColor = 'background:#dbeafe; color:#1e40af;';
        else if (car.status === 'HILANG') statusBadgeColor = 'background:#fee2e2; color:#991b1b;';
        else if (car.status === 'SERVIS') statusBadgeColor = 'background:#fef3c7; color:#92400e;';

        card.innerHTML = `
            <div>
                <div class="fleet-header">
                    <div>
                        <div class="fleet-plat">${car.plat}</div>
                        <div style="font-size:11.5px; font-weight:700; color:var(--text-secondary);">${car.merk}</div>
                    </div>
                    <span class="fleet-tag" style="${statusBadgeColor}">● ${car.status}</span>
                </div>

                <div class="risk-tags" style="margin:6px 0;">
                    <span class="risk-badge badge-safe">${car.kategori}</span>
                    <span class="risk-badge ${car.isGPS ? 'badge-safe' : 'badge-warning'}">
                        ${car.isGPS ? '📡 GPS Pro Aktif' : '⚠️ Tanpa GPS'}
                    </span>
                    <span class="risk-badge badge-safe">Slot: ${car.slot}</span>
                </div>

                <div style="font-size:11px; color:var(--text-secondary); margin:6px 0;">
                    <div>Kondisi Mesin: <strong>${car.kondisiMesin.toFixed(1)}%</strong></div>
                    <div class="meter-bar-container">
                        <div class="meter-bar-fill" style="width:${car.kondisiMesin}%; ${car.kondisiMesin < 50 ? 'background:var(--accent-amber);' : ''}"></div>
                    </div>
                    <div style="display:flex; justify-content:space-between; margin-top:4px;">
                        <span>Odometer: <strong>${car.odoMeter.toLocaleString('id-ID')} km</strong></span>
                        <span>Tarif: <strong>${formatRupiah(car.tarifHarian)}/hr</strong></span>
                    </div>
                </div>
            </div>

            <div style="display:flex; gap:6px; margin-top:10px; border-top:1px solid var(--border-subtle); padding-top:8px;">
                ${!car.isGPS ? 
                    `<button class="btn-action btn-mitigate" style="flex:1;" onclick="actionPasangGPS('${car.plat}')">📡 Pasang GPS (Rp 1.5jt)</button>` : 
                    `<button class="btn-action" style="flex:1; background:#f1f5f9; color:var(--primary-main); font-size:10.5px;" disabled>✓ GPS Aktif</button>`
                }
                <button class="btn-action btn-mitigate" style="flex:1;" onclick="actionServis('${car.plat}')">🔧 Servis</button>
            </div>
        `;
        container.appendChild(card);
    });
}

function actionPasangGPS(plat) {
    if (confirm(`Pasang modul GPS Tracker & Remote Cutoff pada unit [${plat}] seharga Rp 1.500.000?`)) {
        if (window.javaBridge) window.javaBridge.pasangGPS(plat);
    }
}

function actionServis(plat) {
    if (confirm(`Lakukan servis berkala & ganti oli pada unit [${plat}]?`)) {
        if (window.javaBridge) window.javaBridge.servisMobil(plat);
    }
}

// Render Tab 4: Transactions
function renderTransactions(transaksi) {
    const container = document.getElementById('transaksi-list-container');
    if (!container) return;

    if (transaksi.length === 0) {
        container.innerHTML = '<div style="color:var(--text-muted); text-align:center; padding:15px;">Belum ada riwayat transaksi kas tercatat.</div>';
        return;
    }

    container.innerHTML = '';
    transaksi.forEach(t => {
        const isMasuk = (t.tipe === 'PEMASUKAN');
        const row = document.createElement('div');
        row.style.cssText = 'display:flex; justify-content:space-between; padding:6px 0; border-bottom:1px solid #f1f5f9;';
        row.innerHTML = `
            <div>
                <span style="font-weight:700; color:${isMasuk ? 'var(--primary-main)' : 'var(--accent-red)'};">[${t.tipe}]</span>
                <span>${t.keterangan}</span>
            </div>
            <strong style="color:${isMasuk ? 'var(--primary-dark)' : 'var(--accent-red)'};">
                ${isMasuk ? '+' : '-'} ${formatRupiah(t.jumlah)}
            </strong>
        `;
        container.appendChild(row);
    });
}

// Emergency Crisis Management
function checkEmergencyCrisis(krisis) {
    const modal = document.getElementById('modal-krisis');
    if (krisis && krisis.butuhTindakan) {
        activeCrisisPlat = krisis.plat;
        document.getElementById('krisis-desc-text').textContent = krisis.pesan;
        modal.style.display = 'flex';
    } else {
        modal.style.display = 'none';
        activeCrisisPlat = null;
    }
}

function resolveCrisis(actionType) {
    if (!activeCrisisPlat) return;

    if (actionType === 'telepon') {
        if (window.javaBridge) window.javaBridge.aksiTelepon(activeCrisisPlat);
    } else if (actionType === 'cutoff') {
        if (window.javaBridge) window.javaBridge.aksiMatikanMesin(activeCrisisPlat);
    } else if (actionType === 'polisi') {
        if (confirm("Buat laporan polisi & bayar biaya operasional Rp 5.000.000?")) {
            if (window.javaBridge) window.javaBridge.aksiLaporPolisi(activeCrisisPlat);
        }
    } else if (actionType === 'akamsi') {
        if (confirm("Kirim tim ormas / akamsi untuk mediasi lapangan dan negosiasi tebusan?")) {
            if (window.javaBridge) window.javaBridge.aksiByOne(activeCrisisPlat);
        }
    } else if (actionType === 'ikhlas') {
        if (confirm(`Yakin ingin mengikhlaskan unit [${activeCrisisPlat}] dan menghapusnya dari pembukuan aset total loss?`)) {
            if (window.javaBridge) window.javaBridge.aksiIkhlas(activeCrisisPlat);
        }
    }
}

// Upgrades
function actionPerluasGarasi() {
    if (confirm("Perluas kapasitas garasi (+4 Bay Slot) seharga Rp 15.000.000?")) {
        if (window.javaBridge) window.javaBridge.perluasGarasi();
    }
}

function actionRekrutSupir() {
    if (confirm("Rekrut supir tetap baru seharga Rp 1.000.000 (biaya administrasi & seragam)?")) {
        if (window.javaBridge) window.javaBridge.rekrutSupir();
    }
}

function actionSimpanGame() {
    if (window.javaBridge) window.javaBridge.simpanKeFile();
}

function actionMuatGame() {
    if (confirm("Muat ulang state permainan dari file data/gamestate.txt?")) {
        if (window.javaBridge) window.javaBridge.muatDariFile();
    }
}

// Modal Beli Mobil
function openBeliModal() {
    document.getElementById('modal-beli').style.display = 'flex';
}

function closeBeliModal() {
    document.getElementById('modal-beli').style.display = 'none';
}

function updateBeliPreset() {
    const kat = document.getElementById('beli-kategori').value;
    const merkInput = document.getElementById('beli-merk');
    if (kat === 'LCGC') merkInput.value = 'Daihatsu Sigra 1.2 R';
    else if (kat === 'KELUARGA') merkInput.value = 'Toyota All New Avanza';
    else if (kat === 'MEDIUM_SUV') merkInput.value = 'Toyota Innova Reborn Diesel';
    else if (kat === 'MEWAH_VIP') merkInput.value = 'Toyota Alphard VIP Luxury';
}

function submitBeliMobil() {
    const kat = document.getElementById('beli-kategori').value;
    const plat = document.getElementById('beli-plat').value.trim();
    const merk = document.getElementById('beli-merk').value.trim();

    if (!plat || !merk) {
        showToastAlert('Plat nomor dan merk kendaraan wajib diisi!', false);
        return;
    }

    let harga = 265000000;
    let tarif = 550000;
    if (kat === 'LCGC') { harga = 145000000; tarif = 350000; }
    else if (kat === 'MEDIUM_SUV') { harga = 460000000; tarif = 900000; }
    else if (kat === 'MEWAH_VIP') { harga = 1400000000; tarif = 2800000; }

    if (window.javaBridge) {
        window.javaBridge.beliMobil(kat, plat, merk, harga, tarif);
        closeBeliModal();
    }
}
