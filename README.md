# Aplikasi Arsip Putusan Narkotika – PN Pekanbaru

UTS Struktur Data – Kelompok 06
Universitas Muhammadiyah Malang – Semester Ganjil 2026/2027
Dosen pengampu: Galih W. Wicaksono

## Anggota Kelompok

| Nama                     | NIM             | Akun GitHub  |
|--------------------------|-----------------|--------------|
| M. Shabil Dwi Nur Basyah | 202510370110055 | @shabildwi   |
| Fathul Qolby             | 202510370110014 | @fathulqolby |

Rincian kontribusi (Issue, Pull Request, review): [`CONTRIBUTIONS.md`](CONTRIBUTIONS.md).

## Deskripsi

Aplikasi konsol Java berorientasi objek untuk menyimpan dan mengolah 50 putusan
pengadilan narkotika Pengadilan Negeri Pekanbaru (2023–2024) menggunakan struktur data
yang dibuat dari nol, generik `<T>`, tanpa koleksi bawaan `java.util`.

Fitur: impor CSV, tampil, tambah, hapus, cari, filter tunggal/gabungan, agregasi,
pengurutan berdasarkan vonis, audit riwayat operasi, dan ekspor hasil ke CSV/TXT.

## Dataset

- 50 putusan pidana narkotika Pengadilan Negeri Pekanbaru:
  P001–P025 (tahun 2023, extractor Shabil) dan P026–P050 (tahun 2024, extractor Fathul)
- Data terstruktur: [`data/putusan-50.csv`](data/putusan-50.csv) (16 atribut sesuai skema 2.2)
- Sumber per atribut: [`docs/data-provenance.csv`](docs/data-provenance.csv)
  (9 atribut × 50 putusan = 450 baris, diperiksa silang antaranggota)
- PDF sumber 50 putusan (akses publik): [https://drive.google.com/drive/folders/180Y0Mp9ziB2VgpDO5Tz9lbaQwuiU0Qb2?usp=sharing]
  — nama file `putusan_001.pdf` … `putusan_050.pdf` sama dengan kolom `source_file` di provenance
- Nama terdakwa disamarkan dengan kode T001–T050

## Aturan Data

Aturan berikut dipakai saat mengekstrak nilai dari PDF putusan.

### Format dan konversi

| No | Atribut / situasi | Aturan | Contoh |
|---|---|---|---|
| 1 | caseId | P + 3 digit, sama dengan nomor file PDF | P001 |
| 2 | nomorPerkara | Disalin persis dari halaman 1 | 523/Pid.Sus/2023/PN.Pbr |
| 3 | pengadilan | Selalu `PN Pekanbaru` | PN Pekanbaru |
| 4 | tanggalPutusan | Tanggal diucapkan, format ISO `YYYY-MM-DD` | 2023-07-06 |
| 5 | terdakwaKode | T + nomor case; nama asli tidak ditulis | T001 |
| 6 | jenisNarkotika | Huruf kecil: sabu / ganja / ekstasi / lainnya; lebih dari satu dipisah `\|` | sabu\|ekstasi |
| 7 | beratBarangBuktiGram | Total berat **bersih** semua narkotika (Berita Acara Lab), desimal titik | 49.29 |
| 8 | pasalDakwaan | Semua pasal dakwaan berurutan, dipisah `\|` | 114(1)\|112(1) |
| 9 | pasalTerbukti | Pasal dalam amar; jika amar merujuk "dakwaan kedua", ditulis pasalnya | 112(1) |
| 10 | Pasal dengan jo | Ditulis lengkap setelah pasal utama | 114(2) jo 132(1) |
| 11 | tuntutanBulan / vonisBulan | Pidana penjara dalam bulan (tahun × 12 + bulan) | 6 tahun 6 bulan = 78 |
| 12 | Kurungan pengganti denda | Tidak dihitung ke vonisBulan | — |
| 13 | dendaRupiah | Angka tanpa titik/Rp; tanpa denda = 0 | 1000000000 |
| 14 | barangBukti, keadaanMemberatkan, keadaanMeringankan | Ringkasan dengan kata sendiri, butir dipisah `;` | tidak dukung pemberantasan; merusak generasi muda |
| 15 | sourcePages | Halaman per kelompok atribut, `bagian:halaman` dipisah `;` | identitas:1;dakwaan:6;amar:23-24 |
| 16 | Nilai tidak ditemukan | Angka = 0, teks = kosong; tidak ditebak | — |

### Kode khusus

| No | Situasi | Aturan | Contoh |
|---|---|---|---|
| 17 | Pidana seumur hidup | tuntutanBulan / vonisBulan = **999** | P009–P011 (vonis) |
| 18 | Pidana mati | tuntutanBulan = **9999** | P009–P011 (tuntutan) |
| 19 | Perhitungan rata-rata / total | Nilai 999 dan 9999 **dikecualikan**; tetap dipakai saat pengurutan | — |
| 20 | Hanya berat bruto atau barang bukti sisa pakai | beratBarangBuktiGram = 0, status provenance `missing` | P009–P011, P013, P014, P031 |

### Kasus khusus

| No | Situasi | Aturan | Contoh |
|---|---|---|---|
| 21 | Beberapa terdakwa dalam satu putusan | Tetap 1 record, satu kode terdakwa; vonis dari amar | P031 (2), P044 (3) |
| 22 | Perkara terpisah dengan barang bukti sama | Tiap perkara 1 record dengan berat yang sama | P015/P016, P042/P043 |
| 23 | Total berat tertulis di putusan | Dipakai angka total yang tertulis | P012 = 8.39 |
| 24 | Tanggal diucapkan lebih awal dari tanggal musyawarah (salah ketik di putusan) | Dipakai tanggal musyawarah | P030, P039, P040 |
| 25 | "Diucapkan pada hari itu juga" | tanggalPutusan = tanggal musyawarah | P025 |
| 26 | Tahun nomor perkara berbeda dengan tahun putusan | Nomor disalin persis | P012 (2022, diputus 2023) |
| 27 | Angka di amar tidak tertulis ("selama (enam) tahun") | Diambil dari ejaan dalam kurung, diperiksa reviewer | P029, P030 = 72 |
| 28 | Vonis lebih berat dari tuntutan | Dicatat apa adanya | P029, P042, P043 |

### Status provenance

| Status | Arti |
|---|---|
| `verified` | Nilai sudah dicocokkan reviewer dengan PDF dan sesuai |
| `corrected` | Nilai diperbaiki oleh reviewer |
| `missing` | Nilai tidak tersedia di putusan (ditulis 0 / kosong) |

## Struktur Data

| | Struktur utama | Struktur sekunder |
|---|---|---|
| Kelas | `ArrayDinamis<T>` | `StackRiwayat<T>` |
| Dasar | array `T[]` | array `T[]` |
| Kontrak | `implements StrukturData<T>` | operasi Stack: `push`, `pop`, `peek`, `searchPreserve` |
| Kapasitas | awal 10, resize ×2 saat penuh (10 → 20 → 40 → 80) | awal 10, resize ×2 saat penuh |
| Isi | 50 objek `Putusan` | riwayat operasi pengguna (`String`) |
| Dipakai untuk | impor, tampil, tambah, hapus, cari, filter, agregasi, urut, ekspor | fitur audit (tampil dan cari riwayat) |
| Invariant (`validateInvariant()`) | `0 ≤ size ≤ kapasitas`; slot `0..size-1` tidak null; slot `size..kapasitas-1` null | `0 ≤ top ≤ kapasitas`; slot `0..top-1` tidak null; slot `top..kapasitas-1` null |

Alasan pemilihan: [`docs/adr/ADR-001-pemilihan-struktur-data.md`](docs/adr/ADR-001-pemilihan-struktur-data.md).
Alasan algoritma cari dan urut: [`docs/adr/ADR-002-algoritma-cari-dan-urut.md`](docs/adr/ADR-002-algoritma-cari-dan-urut.md).

`java.util` hanya dipakai untuk kebutuhan non-struktur: `java.util.function.Predicate` / `Consumer`
(kriteria pencarian dan tujuan impor). Input/output memakai `java.io` dan `java.nio.file`,
tanggal memakai `java.time.LocalDate`.

## Cara Build, Test, dan Menjalankan

### Kebutuhan
- JDK 17 atau lebih baru
- Maven 3.8+ (atau Maven bawaan IntelliJ IDEA)

### Lewat terminal (dari folder proyek)

```
git clone https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06.git
cd uts-struktur-data-narkotika-kelompok-06
mvn clean test
mvn compile
java -cp target/classes id.ac.umm.kelompok06.aplikasi.Main
```

Hasil yang diharapkan dari `mvn clean test`: **Tests run: 89, Failures: 0, Errors: 0 – BUILD SUCCESS**.

Program memuat `data/putusan-50.csv` secara otomatis saat mulai. Untuk memuat file lain:

```
java -cp target/classes id.ac.umm.kelompok06.aplikasi.Main path/ke/file.csv
```

### Lewat IntelliJ IDEA
1. **File → Open** → pilih folder proyek (IntelliJ mengenali `pom.xml` sebagai proyek Maven).
2. Test: panel **Maven** → `Lifecycle` → `test`.
3. Menjalankan: buka `src/main/java/id/ac/umm/kelompok06/aplikasi/Main.java` → klik ▶ → **Run**.
   *Working directory* harus folder proyek supaya `data/putusan-50.csv` ditemukan.

## Struktur Proyek

```
├── data/
│   └── putusan-50.csv              dataset 50 putusan (16 atribut)
├── docs/
│   ├── adr/                        ADR-001 (struktur data), ADR-002 (algoritma)
│   ├── trace/                      trace manual skenario 3 dan 6, skenario 5 vs provenance
│   ├── demo/                       bahan demo
│   ├── data-provenance.csv         sumber setiap nilai (file, halaman, extractor, reviewer)
│   ├── skenario-uji.md             peta 7 skenario uji ke test
│   └── analisis-kompleksitas.md    Big-O dan data empiris
├── src/main/java/id/ac/umm/kelompok06/
│   ├── struktur/   StrukturData, ArrayDinamis, StackRiwayat
│   ├── model/      Putusan
│   ├── io/         PutusanCsvParser, HasilImpor, DataTidakValidException
│   └── aplikasi/   ArsipPutusan, PencarianPutusan, HasilCari, FilterPutusan, KriteriaFilter,
│                   AgregasiPutusan, JumlahJenis, PengurutanPutusan, AuditRiwayat,
│                   EksporPutusan, MenuKonsol, Main
├── src/test/java/id/ac/umm/kelompok06/   test JUnit 5 (paket sama dengan kode)
├── AI_USAGE.md
├── CONTRIBUTIONS.md
└── pom.xml
```

## Fitur dan Menu

| Menu | Fitur | Struktur / operasi yang dipakai |
|---|---|---|
| 1 | Impor CSV | `PutusanCsvParser` + `ArrayDinamis.add` |
| 2 | Tampil semua | `get(i)` untuk `i = 0..size-1` |
| 3 | Tambah putusan (satu baris CSV) | validasi parser + `contains` (tolak caseId ganda) + `add` |
| 4 | Hapus putusan berdasarkan caseId | `cari` + `remove` (elemen di kanan bergeser ke kiri) |
| 5 | Cari caseId / nomor perkara | linear search, menampilkan jumlah langkah perbandingan |
| 6 | Filter jenis, pasal terbukti, berat, vonis (tunggal atau gabungan) | `KriteriaFilter`, hasil di `ArrayDinamis` baru |
| 7 | Agregasi: jumlah perkara, rata-rata vonis, total denda, distribusi jenis | iterasi O(n), kode 999/9999 dikecualikan dari rata-rata |
| 8 | Urut berdasarkan vonis (menurun / menaik) | insertion sort stabil pada salinan, menampilkan pencacah operasi |
| 9 | Audit: tampilkan / cari riwayat operasi | `StackRiwayat.lihatDariAtas` / `searchPreserve` |
| 10 | Ekspor hasil cari/filter/urut terakhir ke CSV, laporan agregasi ke TXT | ditulis ke folder `ekspor/` |
| 11 | Info struktur data | size, kapasitas, `validateInvariant()` kedua struktur |
| 0 | Keluar | |

Setiap operasi (kecuali melihat audit) dicatat ke `StackRiwayat` dengan format
`yyyy-MM-dd HH:mm:ss | JENIS | detail`, misalnya `2026-11-11 19:00:00 | HAPUS | P030 dari indeks 29`.

Catatan menu 10: yang diekspor adalah **hasil saat operasi cari/filter/urut dijalankan**.
Jika putusan dihapus setelah filter, hasil filter yang sudah tersimpan tidak ikut berubah.

Contoh sesi singkat:

```
Pilih menu: 5
Cari berdasarkan 1) caseId  2) nomor perkara: 1
Kata kunci: P012
'P012' ditemukan di indeks 11 (12 langkah perbandingan)
P012 | 1036/Pid.Sus/2022/PN Pbr   | 2023-01-05 | sabu          |     8.39 g | vonis 7 th 6 bln     | denda Rp2,000,000,000

Pilih menu: 11
Struktur utama  : ArrayDinamis<Putusan>, size=50, kapasitas=80
Struktur sekunder: StackRiwayat<String>, size=2
validateInvariant() kedua struktur: OK
```

## Pengujian

89 test JUnit 5 mencakup struktur data, parser, setiap fitur, dan menu konsol.
Peta 7 skenario uji wajib ke test beserta langkah demonya ada di
[`docs/skenario-uji.md`](docs/skenario-uji.md).

| Skenario | Ringkasan hasil |
|---|---|
| 1. Muat 50 record | 50 diterima, 0 ditolak, `size() == 50` |
| 2. Cari ada / tidak ada / batas | P012 → indeks 11 (12 langkah); P099 → -1 (50 langkah); P001 dan P050 |
| 3. Hapus awal, tengah, akhir | elemen bergeser ke kiri, slot terakhir dikosongkan ([trace](docs/trace/trace-skenario-3.md)) |
| 4. Tambah kembali 3 record | urutan P001..P050 kembali, invariant valid |
| 5. Filter gabungan vs provenance | P021, P022, P023, P024, P046 ([pemeriksaan PDF](docs/trace/skenario-5-provenance.md)) |
| 6. Cari di Stack | urutan riwayat tidak berubah ([trace](docs/trace/trace-skenario-6.md)) |
| 7. Record tidak valid | pesan error menyebut kolom yang salah, struktur tetap konsisten |

## Dokumentasi

| Dokumen | Isi |
|---|---|
| [`docs/adr/ADR-001-pemilihan-struktur-data.md`](docs/adr/ADR-001-pemilihan-struktur-data.md) | alasan memilih array dinamis + Stack |
| [`docs/adr/ADR-002-algoritma-cari-dan-urut.md`](docs/adr/ADR-002-algoritma-cari-dan-urut.md) | alasan linear search, insertion sort, dan searchPreserve |
| [`docs/analisis-kompleksitas.md`](docs/analisis-kompleksitas.md) | Big-O semua operasi dan data empiris |
| [`docs/skenario-uji.md`](docs/skenario-uji.md) | 7 skenario uji wajib |
| [`docs/trace/`](docs/trace/) | trace manual |
| [`docs/data-provenance.csv`](docs/data-provenance.csv) | sumber setiap nilai data |
| [`AI_USAGE.md`](AI_USAGE.md) | catatan penggunaan AI |
| [`CONTRIBUTIONS.md`](CONTRIBUTIONS.md) | kontribusi setiap anggota |

## Keterbatasan

- Pencarian O(n) dan pengurutan O(n²) memadai untuk 50 data, tetapi tidak untuk ribuan data
  (rencana perbaikan ada di ADR-002).
- Menu tambah hanya menambah di akhir arsip. Penyisipan di posisi tertentu (`tambahDi`)
  tersedia di kode dan dibuktikan oleh test skenario 4.
- Perubahan arsip dan riwayat tidak disimpan permanen. Saat program ditutup,
  arsip kembali ke isi `data/putusan-50.csv` (gunakan menu 10 untuk menyimpan hasil).