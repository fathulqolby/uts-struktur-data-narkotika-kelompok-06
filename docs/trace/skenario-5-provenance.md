# Skenario 5: Filter Gabungan vs Pemeriksaan Provenance

## Syarat filter
| Syarat | Nilai | Aturan di kode (`KriteriaFilter.cocok`) |
|---|---|---|
| Jenis narkotika | `sabu` | `mengandungJenis`: nilai gabungan seperti `sabu\|ekstasi` ikut cocok |
| Pasal terbukti | `112(1)` | diawali `112(1)`, sehingga `112(1) jo 132(1)` ikut cocok |
| Vonis | 60–72 bulan | inklusif; kode khusus 999/9999 tidak ikut |
| Berat bersih | 0–1 gram | inklusif; berat 0 (tidak tercatat) tidak ikut |

Cara menjalankan di menu: **6** → `sabu` → `112(1)` → `0` → `1` → `60` → `72`.
Test: `FilterPutusanTest#gabungan` dan `MenuKonsolTest#filterGabungan`.

## Hasil program
`5 putusan cocok`: **P021, P022, P023, P024, P046**

## Pemeriksaan manual ke PDF sumber
Setiap nilai dicocokkan ke halaman PDF yang tercatat di `docs/data-provenance.csv`.

| caseId | Jenis (hlm) | Berat g (hlm) | Pasal terbukti (hlm) | Vonis bln (hlm) | Extractor / Reviewer | Cocok? |
|---|---|---|---|---|---|---|
| P021 | sabu (2) | 0.12 (2) | 112(1) jo 132(1) (30) | 66 (30) | Shabil / Fathul | ✓ |
| P022 | sabu (2) | 0.12 (2) | 112(1) jo 132(1) (30) | 66 (30) | Shabil / Fathul | ✓ |
| P023 | sabu (3) | 0.62 (3) | 112(1) (25) | 66 (25) | Shabil / Fathul | ✓ |
| P024 | sabu (5) | 0.26 (5) | 112(1) (24) | 66 (24) | Shabil / Fathul | ✓ |
| P046 | sabu (3) | 0.21 (3) | 112(1) (23) | 60 (23) | Fathul / Shabil | ✓ |

Semua nilai di PDF sesuai dengan `data/putusan-50.csv` dan memenuhi keempat syarat.
PDF sumber tersedia di Google Drive (tautan di README bagian Dataset).

## Putusan yang hampir lolos (memenuhi 3 dari 4 syarat)
Tabel ini membuktikan setiap syarat benar-benar menyaring data.

| caseId | Jenis | Pasal terbukti | Berat g | Vonis bln | Gagal di syarat |
|---|---|---|---|---|---|
| P001 | sabu | 112(1) | 1.63 | 72 | berat > 1 |
| P038 | sabu | 112(1) | 1.39 | 72 | berat > 1 |
| P017 | sabu | 114(1) jo 132(1) | 0.52 | 68 | pasal bukan 112(1) |
| P025 | sabu | 114(1) | 0.16 | 66 | pasal bukan 112(1) |
| P045 | sabu | 114(1) | 0.12 | 69 | pasal bukan 112(1) |
| P032 | sabu | 112(1) jo 132(1) | 0.22 | 84 | vonis > 72 |
| P033 | sabu | 112(1) jo 132(1) | 0.22 | 84 | vonis > 72 |
| P047 | sabu | 112(1) jo 132(1) | 0.19 | 48 | vonis < 60 |

Catatan batas: P001 dan P038 memiliki vonis tepat 72 bulan (batas atas inklusif), tetapi tetap tidak lolos karena beratnya lebih dari 1 gram.

## Kesimpulan
Hasil filter program (5 putusan) **sama** dengan hasil pemeriksaan manual terhadap PDF sumber,
dan tidak ada putusan lain yang memenuhi keempat syarat.