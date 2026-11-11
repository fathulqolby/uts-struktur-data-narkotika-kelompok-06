# Analisis Kompleksitas

n = jumlah putusan di arsip (50), h = jumlah entri riwayat di stack, k = jumlah jenis narkotika berbeda.

## 1. ArrayDinamis<T> (struktur utama)

| Operasi | Waktu | Ruang tambahan | Alasan |
|---|---|---|---|
| `add(x)` | O(1) amortized, O(n) saat resize | O(n) saat resize | menulis di `data[size]`; saat penuh, array baru 2× kapasitas dan n elemen disalin |
| `get(i)` / `set(i, x)` | O(1) | O(1) | akses langsung ke indeks array |
| `insert(i, x)` | O(n) | O(1) | elemen `i..size-1` digeser satu ke kanan |
| `remove(i)` | O(n) | O(1) | elemen `i+1..size-1` digeser satu ke kiri; hapus akhir O(1) |
| `search(x)` / `cari(p)` / `contains(x)` | O(n) | O(1) | linear search, data tidak terurut berdasarkan kunci |
| `size()` / `isEmpty()` | O(1) | O(1) | membaca field `size` |
| `clear()` | O(kapasitas) | O(1) | semua slot di-set null |
| `salin()` | O(n) | O(n) | menyalin isi ke ArrayDinamis baru |
| `validateInvariant()` | O(kapasitas) | O(1) | memeriksa slot aktif dan slot kosong |

### Bukti amortized pada `add`
Kapasitas awal 10, resize ×2. Saat memuat 50 putusan:

| Resize ke- | Terjadi saat add ke- | Kapasitas | Elemen disalin |
|---|---|---|---|
| 1 | 11 | 10 → 20 | 10 |
| 2 | 21 | 20 → 40 | 20 |
| 3 | 41 | 40 → 80 | 40 |

Total kerja = 50 penulisan + 70 salinan = **120 ≤ 3n**, sehingga rata-rata per `add` konstan: **O(1) amortized**.
Bukti: menu **11** menampilkan `size=50, kapasitas=80`.

### Pergeseran pada `remove` (skenario 3)
Jumlah pergeseran = `size − 1 − i`.

| Posisi | Contoh (n = 50) | Pergeseran |
|---|---|---|
| Awal (i = 0) | hapus P001 | 49 (terburuk) |
| Tengah (i = 24) | hapus P026 setelah P001 dihapus | 24 |
| Akhir (i = size − 1) | hapus P050 | 0 (terbaik) |

Trace langkah demi langkah: `docs/trace/trace-skenario-3.md`.

## 2. StackRiwayat<T> (struktur sekunder)

| Operasi | Waktu | Ruang tambahan | Alasan |
|---|---|---|---|
| `push(x)` | O(1) amortized | O(h) saat resize | sama seperti `add` |
| `pop()` / `peek()` | O(1) | O(1) | hanya mengubah atau membaca `top` |
| `lihatDariAtas(m)` | O(1) | O(1) | akses `data[top − m]` |
| `searchPreserve(p)` | O(h) | O(h) | maksimal h pop ke stack sementara + h push saat restore |
| `restore(s)` | O(k) | O(1) | k elemen dipindah kembali |

`searchPreserve` terburuk (elemen tidak ada): 2h pop + 2h push = 4h operasi, tetap **O(h)**.
Trace: `docs/trace/trace-skenario-6.md`.

## 3. Fitur aplikasi

| Fitur | Kelas / method | Waktu | Catatan |
|---|---|---|---|
| Impor | `ArsipPutusan.impor` | O(n) | parse tiap baris + `add` O(1) amortized |
| Tampil | `ArsipPutusan.tampil` | O(n) | `get(i)` untuk i = 0..n−1 |
| Tambah | `ArsipPutusan.tambah` | O(n) | `contains` untuk menolak duplikat O(n) + `add` O(1) |
| Tambah di posisi | `ArsipPutusan.tambahDi` | O(n) | `contains` + `insert` |
| Hapus by caseId | `ArsipPutusan.hapus` | O(n) | `cari` O(n) + `remove` O(n) |
| Cari caseId / nomor perkara | `PencarianPutusan` | O(n) | linear search |
| Filter (tunggal/gabungan) | `FilterPutusan.filter` | O(n) | setiap putusan diperiksa sekali; hasil di ArrayDinamis baru, ruang O(n) |
| Agregasi | `AgregasiPutusan` | O(n·k) | distribusi jenis: untuk tiap putusan, cari jenisnya di ArrayDinamis berisi k jenis |
| Urut vonis | `PengurutanPutusan` | O(n²) terburuk, O(n) terbaik | insertion sort pada salinan; ruang O(n) |
| Audit tampil | `AuditRiwayat.tampilkan(m)` | O(m) | `lihatDariAtas` O(1) per entri |
| Audit cari | `AuditRiwayat.cari` | O(h) | `searchPreserve` |
| Ekspor CSV/TXT | `EksporPutusan` | O(n) | satu baris per putusan |

## 4. Data empiris (n = 50)
Diambil dari pencacah langkah di kode dan dibuktikan oleh test.

### Pencarian linear
| Kasus | Kunci | Indeks | Langkah | Test |
|---|---|---|---|---|
| Terbaik | P001 | 0 | 1 | `PencarianPutusanTest#posisiBatas` |
| Rata-rata (contoh) | P012 | 11 | 12 | `PencarianPutusanTest#caseIdAda` |
| Terburuk (ada) | P050 | 49 | 50 | `PencarianPutusanTest#posisiBatas` |
| Terburuk (tidak ada) | P099 | -1 | 50 | `PencarianPutusanTest#caseIdTidakAda` |

Rata-rata untuk kunci yang ada = (n + 1) / 2 = 25,5 langkah, jadi **O(n)**.

### Insertion sort berdasarkan vonis
| Kasus | Perbandingan | Pergeseran |
|---|---|---|
| Data asli, urut menurun | 556 | 510 |
| Data asli, urut menaik | 697 | 652 |
| Data sudah terurut (terbaik) | 49 (= n − 1) | 0 |
| Batas teoretis terburuk | 1.225 (= n(n−1)/2) | 1.225 |

Data asli berada di antara kasus terbaik dan terburuk karena sebagian besar vonis berada di rentang 60–96 bulan dan banyak yang bernilai sama.
Insertion sort dipilih karena **stabil** (putusan dengan vonis sama tetap mengikuti urutan arsip) dan cukup untuk n = 50. Alasan lengkapnya ada di ADR-002.

## 5. Mengapa kompleksitas ini dapat diterima
- n = 50, sehingga operasi O(n²) terburuk (1.225 perbandingan) tetap seketika.
- Operasi yang paling sering dipakai (tampil, get, add) bernilai O(1) atau O(n).
- Kelemahan array dinamis adalah hapus/sisip di awal yang O(n). Untuk arsip putusan, hapus jauh lebih jarang daripada baca, sehingga kelemahan ini dapat diterima (lihat ADR-001).