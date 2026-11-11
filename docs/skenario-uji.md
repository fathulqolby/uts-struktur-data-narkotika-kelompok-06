# Skenario Uji Wajib

Dokumen ini memetakan 7 skenario uji wajib ke test otomatis (JUnit 5) yang membuktikannya,
beserta langkah demo manual lewat menu konsol.

Jalankan semua test:

```
mvn clean test
```

Jalankan satu kelas test saja, misalnya:

```
mvn test -Dtest=TambahHapusTest
```

Total saat dokumen ini ditulis: **89 test, 0 gagal**.

## Ringkasan

| No | Skenario | Test yang membuktikan | Hasil yang diharapkan |
|---|---|---|---|
| 1 | Muat 50 record | `ArsipPutusanTest#impor50Record`, `PutusanCsvParserTest#fileAsliBerisi50Record` | 50 diterima, 0 ditolak, `size() == 50` |
| 2 | Cari: ada, tidak ada, batas | `PencarianPutusanTest#caseIdAda`, `#caseIdTidakAda`, `#posisiBatas`; `ArrayDinamisTest#searchDitemukan`, `#searchTidakAda` | P012 → indeks 11, 12 langkah; P099 → -1, 50 langkah; P001 → indeks 0, 1 langkah; P050 → indeks 49, 50 langkah |
| 3 | Hapus awal, tengah, akhir + pergeseran | `TambahHapusTest#hapusPertamaTengahTerakhir`; `ArrayDinamisTest#hapusPertama`, `#hapusTengah`, `#hapusTerakhir` | Hapus P001 → P002 pindah ke indeks 0; hapus P026 → P027 pindah ke indeks 24; hapus P050 → elemen terakhir P049; size 47 |
| 4 | Tambah kembali 3 record, invariant dan urutan | `TambahHapusTest#tambahKembaliTiga`; `ArrayDinamisTest#sisipKembali` | Setelah P001, P026, P050 disisipkan kembali: size 50, urutan P001..P050, `validateInvariant()` lolos |
| 5 | Filter gabungan vs provenance | `FilterPutusanTest#gabungan`; `MenuKonsolTest#filterGabungan` | sabu + pasal 112(1) + vonis 60–72 bulan + berat 0–1 g → P021, P022, P023, P024, P046 |
| 6 | Cari di Stack tidak mengubah urutan | `StackRiwayatTest#searchPreserveDitemukan`, `#searchPreserveTidakAda`; `AuditRiwayatTest#cariTidakMengubahUrutan` | `gambarIsi()` sebelum dan sesudah pencarian sama; size tetap |
| 7 | Record tidak valid → pesan error, struktur konsisten | `TambahHapusTest#tambahDariCsvTidakValid`; `PutusanCsvParserTest#fileDenganBarisRusak` (dan 7 test validasi lain); `MenuKonsolTest#inputTidakValid` | Pesan menyebut kolom yang salah; size tetap 50; `validateInvariant()` lolos |

## Rincian per skenario

### Skenario 1: Muat 50 record
- **Data:** `data/putusan-50.csv` (header + 50 baris).
- **Bukti:** `impor()` memanggil `PutusanCsvParser.bacaFile()`, lalu setiap baris valid dimasukkan dengan `ArrayDinamis.add()`.
- **Kapasitas:** 10 → 20 → 40 → 80 (resize ×2 sebanyak 3 kali), jadi setelah impor `size=50, kapasitas=80`.
- **Demo menu:** program menampilkan `Diterima: 50, ditolak: 0` saat mulai, lalu menu **11** menampilkan size dan kapasitas.

### Skenario 2: Pencarian
- **Algoritma:** linear search pada `ArrayDinamis`, dengan langkah perbandingan dihitung.
- **Kasus:** ada (P012), tidak ada (P099), dan batas (P001 sebagai elemen pertama, P050 sebagai elemen terakhir).
- **Demo menu:** **5** → **1** → `P012`, lalu ulangi dengan `P099`.

### Skenario 3: Hapus dengan pergeseran
- **Bukti pergeseran:** setelah `remove(0)`, elemen yang semula di indeks 1 berada di indeks 0. Slot terakhir di-set `null` supaya tidak ada referensi tertinggal.
- **Trace manual:** lihat `docs/trace/trace-skenario-3.md`.
- **Demo menu:** **4** → `P001`, **4** → `P026`, **4** → `P050`, lalu **2** untuk melihat urutan.

### Skenario 4: Tambah kembali
- **Bukti:** `insert(0, P001)`, `insert(25, P026)`, dan `add(P050)` mengembalikan urutan semula P001..P050.
- **Catatan:** menu **3** menambah di akhir (`add`). Penyisipan di posisi tertentu (`insert`) dibuktikan lewat test.

### Skenario 5: Filter gabungan vs provenance
- **Syarat:** jenis `sabu`, pasal terbukti diawali `112(1)`, vonis 60–72 bulan, berat 0–1 gram.
- **Hasil program:** P021, P022, P023, P024, P046.
- **Pemeriksaan manual:** lihat `docs/trace/skenario-5-provenance.md`, yang mencocokkan setiap putusan ke halaman PDF sumber di `docs/data-provenance.csv`.
- **Demo menu:** **6** → `sabu` → `112(1)` → `0` → `1` → `60` → `72`.

### Skenario 6: Pencarian di Stack
- **Algoritma:** `searchPreserve` memindahkan elemen ke stack sementara sampai elemen ditemukan, lalu memindahkannya kembali (`restore`).
- **Bukti:** `gambarIsi()` sebelum dan sesudah pencarian identik, termasuk saat elemen tidak ditemukan (seluruh stack dipindah lalu dikembalikan).
- **Trace manual:** lihat `docs/trace/trace-skenario-6.md`.
- **Demo menu:** jalankan beberapa operasi, lalu **9** → **1** (lihat riwayat), **9** → **2** → `CARI`, lalu **9** → **1** lagi. Urutan riwayat tetap sama.

### Skenario 7: Data tidak valid
- **Contoh:** baris dengan tanggal `20-12-2024` (bukan `YYYY-MM-DD`) ditolak dengan pesan yang menyebut `tanggalPutusan`.
- **Prinsip:** validasi dilakukan oleh parser **sebelum** `add()`, jadi struktur tidak pernah berisi data setengah jadi.
- **Demo menu:** **3** → tempel `P051,rusak`, lalu **6** dengan berat minimum `x`, lalu **11** untuk menunjukkan invariant tetap OK.