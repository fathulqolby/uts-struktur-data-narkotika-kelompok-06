# ADR-002: Pemilihan Algoritma Pencarian dan Pengurutan

- **Status:** Diterima
- **Tanggal:** 11 November 2026
- **Pengambil keputusan:** Fathul qolby, M. Shabil Dwi Nur Basyah
- **Terkait:** ADR-001 (array dinamis + Stack), `docs/analisis-kompleksitas.md`

## Konteks
Struktur utama adalah `ArrayDinamis<Putusan>` berisi 50 putusan, disimpan sesuai urutan impor dan berubah saat ada tambah atau hapus.
Aplikasi membutuhkan:
1. Pencarian putusan berdasarkan `caseId` dan `nomorPerkara` (skenario 2).
2. Pengurutan berdasarkan lama vonis (fitur urut), dengan kode khusus 999 (seumur hidup) tetap ikut diurutkan.
3. Pencarian pada riwayat operasi (Stack) tanpa mengubah urutannya (skenario 6).

Batasan dari panduan: tidak boleh memakai koleksi `java.util` maupun `Collections.sort`/`Arrays.sort` untuk struktur inti, sehingga algoritma harus diimplementasikan sendiri.

## Keputusan 1: Pencarian di ArrayDinamis memakai linear search

### Opsi yang dipertimbangkan
| Opsi | Kompleksitas | Kelebihan | Kekurangan |
|---|---|---|---|
| **Linear search** | O(n) | sederhana; berlaku untuk kunci apa pun (caseId, nomor perkara, kriteria bebas lewat `Predicate`); tidak butuh data terurut | 50 langkah pada kasus terburuk |
| Binary search | O(log n) | sekitar 6 langkah untuk n = 50 | arsip harus selalu terurut berdasarkan kunci; setiap tambah harus menyisip di posisi terurut (O(n)); tidak bisa untuk nomor perkara sekaligus caseId tanpa dua salinan terurut |
| Hash table buatan sendiri | O(1) rata-rata | sangat cepat | struktur ketiga di luar ruang lingkup ADR-001; perlu menangani collision dan resize; boros memori untuk n = 50 |

### Alasan
- Untuk n = 50, kasus terburuk linear search hanya 50 perbandingan (terukur: P099 → 50 langkah).
- Satu method `cari(Predicate<T>)` dipakai ulang oleh pencarian caseId, nomor perkara, hapus, dan filter.
- Urutan arsip tetap urutan impor, yang dibutuhkan skenario 3 dan 4 (pergeseran dan pengembalian urutan).

## Keputusan 2: Pengurutan vonis memakai insertion sort pada salinan

### Data pembanding (50 vonis asli)
| Algoritma | Perbandingan (menurun) | Perpindahan (menurun) | Perbandingan (menaik) | Perpindahan (menaik) | Stabil? | Ruang tambahan |
|---|---|---|---|---|---|---|
| **Insertion sort** (dipakai) | **556** | **510** pergeseran | **697** | **652** pergeseran | ya | O(1) |
| Bubble sort (berhenti dini) | 1.189 | 510 tukar | 1.219 | 652 tukar | ya | O(1) |
| Selection sort | 1.225 | ≤ 49 tukar | 1.225 | ≤ 49 tukar | tidak | O(1) |
| Merge sort | 207 | - | 205 | - | ya | O(n) |

Angka insertion sort berasal dari pencacah `PengurutanPutusan` (dibuktikan oleh `PengurutanPutusanTest`). Angka algoritma lain berasal dari simulasi pada 50 nilai `vonisBulan` yang sama dan dicatat di AI_USAGE.md.

### Alasan
- **Stabil:** putusan dengan vonis sama (misalnya banyak yang 66 bulan) tetap mengikuti urutan arsip. Ini dibuktikan oleh test `stabil`. Selection sort tidak stabil, jadi tidak dipilih.
- **Adaptif:** data yang sudah terurut hanya butuh n − 1 = 49 perbandingan dan 0 pergeseran (test `pencacahOperasi`).
- Lebih hemat daripada bubble sort: jumlah perpindahannya sama, tetapi perbandingannya kurang dari separuhnya.
- Merge sort memang lebih sedikit perbandingannya (207), tetapi membutuhkan array bantu O(n) di setiap penggabungan dan implementasi rekursif yang lebih rumit. Untuk n = 50, selisih waktunya tidak terasa, jadi kesederhanaan dan kemudahan trace lebih kami utamakan.
- Pengurutan dilakukan pada **salinan** (`ArrayDinamis.salin()`), sehingga urutan arsip asli tidak berubah (test `arsipTidakBerubah`).

## Keputusan 3: Pencarian di Stack memakai searchPreserve
- Stack hanya boleh diakses lewat puncak, sehingga pencarian dilakukan dengan memindahkan elemen ke stack sementara lalu mengembalikannya (`restore`).
- Waktu O(h), ruang tambahan O(h). Urutan akhir dijamin sama karena terjadi dua kali pembalikan (trace: `docs/trace/trace-skenario-6.md`).
- Alternatif membaca array internal stack secara langsung ditolak, karena melanggar sifat Stack (LIFO) yang dipilih di ADR-001.
  `lihatDariAtas(m)` hanya dipakai untuk menampilkan riwayat (read-only), bukan untuk mencari.

## Konsekuensi
- **Positif:** semua algoritma sederhana, mudah di-trace manual, dan dibuktikan oleh test beserta pencacah langkah.
- **Negatif:** pencarian O(n) dan pengurutan O(n²) tidak cocok jika data membesar (misalnya ribuan putusan).
- **Rencana jika data membesar:** ganti insertion sort dengan merge sort (tetap stabil, O(n log n)), dan pertimbangkan indeks terurut atau hash table buatan sendiri untuk pencarian caseId.

