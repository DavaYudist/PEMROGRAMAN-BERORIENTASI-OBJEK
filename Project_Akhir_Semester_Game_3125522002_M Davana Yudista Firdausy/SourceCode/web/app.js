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

    // Event Banner
    const eventBanner = document.getElementById('hud-event-banner');
    if (eventBanner) {
        if (data.sisaEvent > 0 && data.event && data.event !== 'NORMAL') {
            eventBanner.style.display = 'flex';
            eventBanner.innerHTML = `
                <div>🎉 <strong>Event Musiman: ${data.eventJudul}</strong> (Tarif & Frekuensi Order Dinamis)</div>
                <div style="background:rgba(255,255,255,0.25); padding:2px 8px; border-radius:6px; font-size:11px;">
                    Sisa: <strong>${data.sisaEvent} Hari</strong>
                </div>
            `;
        } else {
            eventBanner.style.display = 'none';
        }
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

    // 6. Render Usaha Facilities
    renderUsahaFacilities(data);

    // 7. Render Transactions
    renderTransactions(data.transaksi || []);

    // 8. Check Emergency Crisis
    checkEmergencyCrisis(data.krisis);
}

function getTierLevel(kategori) {
    if (!kategori) return 0;
    const k = kategori.toUpperCase();
    if (k === 'LCGC') return 0;
    if (k === 'KELUARGA') return 1;
    if (k === 'MEDIUM_SUV') return 2;
    if (k === 'MEWAH_VIP') return 3;
    return 1;
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

        card.innerHTML = `
            <div class="order-header">
                <div>
                    <div class="order-renter">${o.nama}</div>
                    <div class="order-purpose">${o.keperluan} • Durasi: <strong>${o.durasiJam} Jam</strong> (Jadwal: Hari ${o.hariMulai}, ${String(o.jamMulai).padStart(2, '0')}:00 WIB)</div>
                </div>
                <span class="badge-rating" style="background:#f1f5f9; color:var(--text-secondary); font-size:11px;">
                    Kelas: <strong>${o.kategori}</strong>
                </span>
            </div>

            <!-- Indonesian Risk Badges (Taploka Risk Screening) -->
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
                ${o.jaminan ? '<span class="risk-badge badge-safe">🛡️ Jaminan Motor/Ijazah Ditahan</span>' : ''}
                ${o.sopir ? '<span class="risk-badge badge-safe">👨✈️ Dengan Supir Resmi</span>' : ''}
                <span class="risk-badge ${riskClass === 'high-risk' ? 'badge-danger' : (riskClass === 'medium-risk' ? 'badge-warning' : 'badge-safe')}">
                    Skor Risiko: ${o.skorRisiko}% (${o.tingkatBahaya})
                </span>
            </div>

            <div style="display:flex; justify-content:flex-end; align-items:center; margin-top:12px; flex-wrap:wrap; gap:8px;">
                <div class="order-actions">
                    <!-- Tombol Terima MEMBUKA BOTTOM SHEET MODAL "PILIH UNIT" -->
                    <button class="btn-action btn-accept" style="background:#eab308; color:#1e1b4b; font-weight:800; border:1px solid #ca8a04;" onclick="openPickCarModal(${o.id})">
                        ✔ Terima
                    </button>
                    ${!o.jaminan ? `<button class="btn-action btn-mitigate" onclick="actionMintaJaminan(${o.id})">✋ Minta Jaminan (-40% Risiko)</button>` : ''}
                    ${!o.sopir ? `<button class="btn-action btn-mitigate" onclick="actionTawarkanSopir(${o.id})">👨✈️ Tawarkan Supir (0% Risiko)</button>` : ''}
                    <button class="btn-action btn-reject" onclick="actionTolakPesanan(${o.id})">✖️ Tolak</button>
                </div>
            </div>
        `;
        container.appendChild(card);
    });
}

// ==========================================================
// TAPLOKA "PILIH UNIT" BOTTOM SHEET MODAL DISPATCH LOGIC
// ==========================================================
function getCarVectorSvg(kategori, color) {
    const c = color || '#059669';
    return `
        <svg class="car-thumb-svg" viewBox="0 0 64 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="8" y="16" width="48" height="15" rx="4" fill="${c}" />
            <path d="M16 16 L22 7 L42 7 L48 16 Z" fill="${c}" opacity="0.9" />
            <rect x="23" y="9" width="8" height="6" rx="1.5" fill="#e2e8f0" />
            <rect x="33" y="9" width="8" height="6" rx="1.5" fill="#e2e8f0" />
            <circle cx="18" cy="31" r="5" fill="#1e293b" />
            <circle cx="18" cy="31" r="2" fill="#94a3b8" />
            <circle cx="46" cy="31" r="5" fill="#1e293b" />
            <circle cx="46" cy="31" r="2" fill="#94a3b8" />
        </svg>
    `;
}

function openPickCarModal(orderId) {
    if (!gameState || !gameState.pesanan) return;
    const o = gameState.pesanan.find(x => x.id === orderId);
    if (!o) return;

    const modal = document.getElementById('sheet-pick-car');
    const contextChip = document.getElementById('sheet-order-context');
    const listContainer = document.getElementById('sheet-cars-list');

    contextChip.innerHTML = `
        <span>👤 <strong>${o.nama}</strong></span> • 
        <span>Kelas Diminta: <strong>${o.kategori}</strong></span> • 
        <span>Durasi: <strong>${o.durasiJam} Jam</strong></span> • 
        <span>Jadwal: <strong>Hari ${o.hariMulai}, ${String(o.jamMulai).padStart(2,'0')}:00</strong></span>
    `;

    const currentDay = gameState.waktu ? gameState.waktu.hari : 1;
    const currentHour = gameState.waktu ? gameState.waktu.jam : 0;
    const currentAbsoluteHour = (currentDay - 1) * 24 + currentHour;

    // View spans 7 days (168 hours) starting from current day
    const s0 = (currentDay - 1) * 24;
    const SPAN_HOURS = 168;

    // Target order window
    const reqStart = (o.hariMulai - 1) * 24 + o.jamMulai;
    const washBuffer = gameState.wash ? 0 : 6; // Car wash in-house = 0 buffer
    const reqEnd = reqStart + o.durasiJam + washBuffer;

    const wantLeft = Math.max(0, ((reqStart - s0) / SPAN_HOURS) * 100);
    const wantWidth = Math.max(2.5, ((reqEnd - reqStart) / SPAN_HOURS) * 100);

    const orderTier = getTierLevel(o.kategori);

    listContainer.innerHTML = '';

    (gameState.armada || []).forEach(car => {
        const carTier = getTierLevel(car.kategori);
        const isUpgradeGratis = carTier > orderTier;
        const isTierTooLow = carTier < orderTier;

        // Collision Check: Find active bookings for this car
        const carBookings = (gameState.sesiSewa || []).filter(b => b.plat === car.plat && !b.selesai && !b.kabur);
        
        let hasClash = false;
        carBookings.forEach(b => {
            const bStart = (b.hariMulai - 1) * 24 + b.jamMulai;
            const bEnd = bStart + b.durasiJam + washBuffer;
            if (reqStart < bEnd && bStart < reqEnd) {
                hasClash = true;
            }
        });

        // Current status check
        if (car.status === 'DISEWA' && reqStart <= currentAbsoluteHour) {
            hasClash = true;
        }

        const isUnavailable = car.status === 'HILANG' || car.kondisiMesin < 40 || isTierTooLow || hasClash;
        const isOk = !isUnavailable;

        // Render solid booking blocks on mini track
        let bookingBlocksHtml = '';
        carBookings.forEach(b => {
            const bStart = (b.hariMulai - 1) * 24 + b.jamMulai;
            const bEnd = bStart + b.durasiJam;
            if (bEnd <= s0 || bStart >= s0 + SPAN_HOURS) return;

            const a = Math.max(bStart, s0);
            const z = Math.min(bEnd, s0 + SPAN_HOURS);
            const leftPct = ((a - s0) / SPAN_HOURS) * 100;
            const widthPct = Math.max(2, ((z - a) / SPAN_HOURS) * 100);

            bookingBlocksHtml += `
                <div class="track-blk solid" style="left:${leftPct}%; width:${widthPct}%;">
                    ${b.penyewa}
                </div>
            `;
        });

        // Render requested slot (dashed rectangular outline)
        const wantDashedHtml = `
            <div class="track-blk want ${hasClash ? 'clash' : ''}" style="left:${wantLeft}%; width:${wantWidth}%;">
                ${hasClash ? '✖ Bentrok' : 'Slot'}
            </div>
        `;

        const row = document.createElement('div');
        row.className = `car-pick-opt ${isOk ? 'clickable' : 'dis'}`;

        let statusBadgeHtml = '';
        if (car.status === 'HILANG') {
            statusBadgeHtml = `<span class="badge-status-clash">🚨 Unit Hilang</span>`;
        } else if (car.kondisiMesin < 40) {
            statusBadgeHtml = `<span class="badge-status-clash">🔧 Wajib Servis</span>`;
        } else if (isTierTooLow) {
            statusBadgeHtml = `<span class="badge-status-clash">Kelas Kurang (${car.kategori})</span>`;
        } else if (hasClash) {
            statusBadgeHtml = `<span class="badge-status-clash">Bentrok jadwal</span>`;
        } else {
            statusBadgeHtml = `<span class="badge-status-free">Kosong</span>`;
        }

        row.innerHTML = `
            <div class="car-thumb-box">
                ${getCarVectorSvg(car.kategori, isOk ? '#059669' : '#94a3b8')}
            </div>
            <div class="car-pick-info">
                <div class="car-pick-header">
                    <span class="badge-plate">[ ${car.plat} ]</span>
                    <span class="car-pick-title">${car.merk}</span>
                    ${isUpgradeGratis ? `<span class="badge-upgrade">Upgrade gratis</span>` : ''}
                </div>
                <div class="car-pick-sub">
                    ${statusBadgeHtml}
                    <span>• Kondisi: <strong>${Math.round(car.kondisiMesin)}%</strong></span>
                    <span>• ${formatRupiah(car.tarifHarian)}/hr</span>
                    ${car.isBekas ? '<span style="color:#b45309; font-weight:700;">(Bekas)</span>' : ''}
                </div>
                <!-- Mini Timeline Track (Kotak Putus-Putus) -->
                <div class="mini-track">
                    ${bookingBlocksHtml}
                    ${wantDashedHtml}
                </div>
            </div>
        `;

        if (isOk) {
            row.onclick = () => confirmDispatchCar(o.id, car.plat);
        }

        listContainer.appendChild(row);
    });

    modal.classList.add('on');
}

function closePickCarModal() {
    const modal = document.getElementById('sheet-pick-car');
    if (modal) modal.classList.remove('on');
}

function confirmDispatchCar(orderId, plat) {
    if (window.javaBridge) {
        if (typeof window.javaBridge.konfirmasiSewa === 'function') {
            window.javaBridge.konfirmasiSewa(orderId, plat);
        } else if (typeof window.javaBridge.terimaPesanan === 'function') {
            window.javaBridge.terimaPesanan(orderId, plat);
        }
    }
    closePickCarModal();
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

// Render Tab 4: Usaha Facilities (Taploka Business Upgrades)
function renderUsahaFacilities(data) {
    if (!data) return;

    // 1. Badan Hukum CV
    const btnCv = document.getElementById('btn-upgrade-cv');
    if (btnCv) {
        if (data.cv) {
            btnCv.textContent = '✓ Badan Usaha CV Aktif';
            btnCv.disabled = true;
            btnCv.style.opacity = '0.7';
            btnCv.style.background = '#059669';
        } else {
            btnCv.textContent = 'Daftarkan CV (Rp 15jt)';
            btnCv.disabled = false;
            btnCv.style.opacity = '1';
        }
    }

    // 2. Keanggotaan Asosiasi Rental
    const btnAsos = document.getElementById('btn-upgrade-asos');
    if (btnAsos) {
        if (data.asos) {
            btnAsos.textContent = '✓ Anggota Asosiasi (Akses Blacklist)';
            btnAsos.disabled = true;
            btnAsos.style.opacity = '0.7';
            btnAsos.style.background = '#059669';
        } else {
            btnAsos.textContent = 'Gabung Asosiasi (Rp 5jt)';
            btnAsos.disabled = false;
            btnAsos.style.opacity = '1';
        }
    }

    // 3. Bengkel & Mekanik In-House
    const btnMech = document.getElementById('btn-upgrade-mech');
    if (btnMech) {
        if (data.mech) {
            btnMech.textContent = '✓ Bengkel Aktif (Diskon Servis 50%)';
            btnMech.disabled = true;
            btnMech.style.opacity = '0.7';
            btnMech.style.background = '#059669';
        } else {
            btnMech.textContent = 'Buka Bengkel (Rp 8jt)';
            btnMech.disabled = false;
            btnMech.style.opacity = '1';
        }
    }

    // 4. Cuci Mobil & Detailing In-House
    const btnWash = document.getElementById('btn-upgrade-wash');
    if (btnWash) {
        if (data.wash) {
            btnWash.textContent = '✓ Car Wash Aktif (Turnaround Cepat)';
            btnWash.disabled = true;
            btnWash.style.opacity = '0.7';
            btnWash.style.background = '#059669';
        } else {
            btnWash.textContent = 'Pasang Car Wash (Rp 6jt)';
            btnWash.disabled = false;
            btnWash.style.opacity = '1';
        }
    }

    // 5. Iklan Digital
    const btnIklan = document.getElementById('btn-upgrade-iklan');
    const descIklan = document.getElementById('desc-iklan');
    const biayaIklan = [0, 3000000, 10000000, 35000000];
    if (btnIklan) {
        const lvl = data.iklan || 0;
        if (lvl >= 3) {
            btnIklan.textContent = '✓ Iklan Level Max (3)';
            btnIklan.disabled = true;
            btnIklan.style.opacity = '0.7';
            btnIklan.style.background = '#059669';
            if (descIklan) descIklan.textContent = 'Kampanye iklan digital maksimal (Order 1.65x)';
        } else {
            const nextLvl = lvl + 1;
            btnIklan.textContent = `Upgrade ke Level ${nextLvl} (${formatRupiah(biayaIklan[nextLvl])})`;
            btnIklan.disabled = false;
            btnIklan.style.opacity = '1';
            if (descIklan) descIklan.textContent = `Level saat ini: ${lvl} • Naikkan ke level ${nextLvl} untuk melipatgandakan pelanggan`;
        }
    }
}

function actionUpgradeCV() {
    if (confirm("Daftarkan Badan Hukum CV Rental Resmi seharga Rp 15.000.000?")) {
        if (window.javaBridge && typeof window.javaBridge.upgradeCV === 'function') {
            window.javaBridge.upgradeCV();
        }
    }
}

function actionUpgradeAsosiasi() {
    if (confirm("Gabung Asosiasi Rental Indonesia seharga Rp 5.000.000 untuk verifikasi blacklist pelanggan?")) {
        if (window.javaBridge && typeof window.javaBridge.upgradeAsosiasi === 'function') {
            window.javaBridge.upgradeAsosiasi();
        }
    }
}

function actionUpgradeBengkel() {
    if (confirm("Bangun bengkel & sediakan mekanik in-house seharga Rp 8.000.000 (Dapatkan diskon biaya servis 50%)?")) {
        if (window.javaBridge && typeof window.javaBridge.upgradeBengkel === 'function') {
            window.javaBridge.upgradeBengkel();
        }
    }
}

function actionUpgradeCarWash() {
    if (confirm("Bangun fasilitas cuci mobil & detailing in-house seharga Rp 6.000.000 (Turnaround mobil instan tanpa antri cuci luar)?")) {
        if (window.javaBridge && typeof window.javaBridge.upgradeCarWash === 'function') {
            window.javaBridge.upgradeCarWash();
        }
    }
}

function actionUpgradeIklan() {
    if (confirm("Tingkatkan level kampanye iklan digital Anda?")) {
        if (window.javaBridge && typeof window.javaBridge.upgradeIklan === 'function') {
            window.javaBridge.upgradeIklan();
        }
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

// Emergency Crisis Management & Branching Engine (Sub-Agent 1)
function checkEmergencyCrisis(krisis) {
    const modal = document.getElementById('modal-krisis');
    if (krisis && krisis.butuhTindakan) {
        activeCrisisPlat = krisis.plat;
        document.getElementById('krisis-desc-text').textContent = krisis.pesan;

        // Reset Crisis Modal to Initial State
        const statusCard = document.getElementById('krisis-status-card');
        const primaryAction = document.getElementById('krisis-primary-action');
        const escalationList = document.getElementById('krisis-escalation-list');
        const closeBtn = document.getElementById('krisis-close-btn-container');

        if (statusCard) statusCard.style.display = 'none';
        if (primaryAction) primaryAction.style.display = 'block';
        if (escalationList) escalationList.style.display = 'none';
        if (closeBtn) closeBtn.style.display = 'none';

        modal.style.display = 'flex';
    } else {
        modal.style.display = 'none';
        activeCrisisPlat = null;
    }
}

// Callback dari Java ketika tombol Telepon selesai dievaluasi
function onTeleponResult(message, isSuccess) {
    const statusCard = document.getElementById('krisis-status-card');
    const primaryAction = document.getElementById('krisis-primary-action');
    const escalationList = document.getElementById('krisis-escalation-list');
    const closeBtn = document.getElementById('krisis-close-btn-container');

    if (!statusCard) return;

    statusCard.style.display = 'block';
    statusCard.textContent = message;

    if (isSuccess) {
        // SCENARIO A: Terhubung & Kooperatif (Grace Period + Denda)
        statusCard.style.background = '#d1fae5';
        statusCard.style.color = '#065f46';
        statusCard.style.border = '1.5px solid #34d399';
        if (primaryAction) primaryAction.style.display = 'none';
        if (escalationList) escalationList.style.display = 'none';
        if (closeBtn) closeBtn.style.display = 'block';
    } else {
        // SCENARIO B: Nomor Mati / Tidak Aktif -> Buka Opsi Eskalasi Lanjutan
        statusCard.style.background = '#fee2e2';
        statusCard.style.color = '#991b1b';
        statusCard.style.border = '1.5px solid #f87171';
        if (primaryAction) primaryAction.style.display = 'none';
        if (escalationList) escalationList.style.display = 'flex';
        if (closeBtn) closeBtn.style.display = 'none';
    }
}

// Callback dari Java ketika tindakan eskalasi selesai
function onEscalationResult(message, isSuccess) {
    const statusCard = document.getElementById('krisis-status-card');
    const escalationList = document.getElementById('krisis-escalation-list');
    const closeBtn = document.getElementById('krisis-close-btn-container');

    if (!statusCard) return;

    statusCard.style.display = 'block';
    statusCard.textContent = message;

    if (isSuccess) {
        statusCard.style.background = '#d1fae5';
        statusCard.style.color = '#065f46';
        statusCard.style.border = '1.5px solid #34d399';
        if (escalationList) escalationList.style.display = 'none';
        if (closeBtn) closeBtn.style.display = 'block';
    } else {
        statusCard.style.background = '#fef3c7';
        statusCard.style.color = '#92400e';
        statusCard.style.border = '1.5px solid #f59e0b';
    }
}

function closeCrisisModal() {
    const modal = document.getElementById('modal-krisis');
    if (modal) modal.style.display = 'none';
    activeCrisisPlat = null;
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
    const isBekas = document.querySelector('input[name="beli-tipe"]:checked')?.value === 'bekas';

    let baseMerk = 'Toyota All New Avanza';
    let harga = 265000000;
    let tarif = 550000;

    if (kat === 'LCGC') {
        baseMerk = 'Daihatsu Sigra 1.2 R';
        harga = 145000000;
        tarif = 350000;
    } else if (kat === 'MEDIUM_SUV') {
        baseMerk = 'Toyota Innova Reborn Diesel';
        harga = 460000000;
        tarif = 900000;
    } else if (kat === 'MEWAH_VIP') {
        baseMerk = 'Toyota Alphard VIP Luxury';
        harga = 1400000000;
        tarif = 2800000;
    }

    if (isBekas) {
        merkInput.value = baseMerk + ' (Bekas)';
        const hargaBekas = harga * 0.60;
        const preview = document.getElementById('beli-price-preview');
        if (preview) {
            preview.innerHTML = `Harga Beli Bursa Bekas (Diskon 60%): <strong>${formatRupiah(hargaBekas)}</strong> (Kondisi 72%)`;
        }
    } else {
        merkInput.value = baseMerk;
        const preview = document.getElementById('beli-price-preview');
        if (preview) {
            preview.innerHTML = `Harga Beli Unit Baru (100% Mulus): <strong>${formatRupiah(harga)}</strong>`;
        }
    }
}

function submitBeliMobil() {
    const kat = document.getElementById('beli-kategori').value;
    const plat = document.getElementById('beli-plat').value.trim();
    const merk = document.getElementById('beli-merk').value.trim();
    const isBekas = document.querySelector('input[name="beli-tipe"]:checked')?.value === 'bekas';

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
        if (isBekas && typeof window.javaBridge.beliMobilBekas === 'function') {
            window.javaBridge.beliMobilBekas(kat, plat, merk, harga, tarif);
        } else {
            window.javaBridge.beliMobil(kat, plat, merk, harga, tarif);
        }
        closeBeliModal();
    }
}

