# Aplikasi Arsip Putusan Narkotika – PN Pekanbaru

UTS Struktur Data – Kelompok 06
Universitas Muhammadiyah Malang – Semester Ganjil 2026/2027
Dosen pengampu: Galih W. Wicaksono

## Anggota Kelompok

| Nama                     | NIM             | Akun GitHub  |
|--------------------------|-----------------|--------------|
| M. Shabil Dwi Nur Basyah | 202510370110055 | @shabildwi   |
| Fathul Qolby               | 202510370110014 | @fathulqolby |

## Deskripsi

Aplikasi Java berorientasi objek untuk menyimpan dan mengolah 50 putusan
pengadilan narkotika Pengadilan Negeri Pekanbaru menggunakan struktur data
yang dibuat dari nol (tanpa koleksi bawaan `java.util`).

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

- Struktur utama: *(menyusul)*
- Struktur sekunder: *(menyusul)*

## Cara Build, Test, dan Menjalankan

*(menyusul)*