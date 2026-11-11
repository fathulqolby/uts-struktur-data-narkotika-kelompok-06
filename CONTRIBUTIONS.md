# Kontribusi Anggota – Kelompok 06

Dokumen ini mencatat kontribusi yang dapat ditelusuri di repository:
Issue, Pull Request, dan review. Daftar PR disusun dari riwayat merge di branch `main`.
Branch `main` diproteksi: setiap PR wajib di-approve anggota lain sebelum di-merge.

## Ringkasan

| | Shabil | Fathul |
|---|---|---|
| PR dibuat dan di-merge | 13 | 13 |
| PR rekan yang di-approve | 13 | 13 |
| Issue (assignee) | 9 | 10 |
| Extractor data | P001–P025 | P026–P050 |
| Reviewer data | P026–P050 | P001–P025 |

## M. Shabil Dwi Nur Basyah (202510370110055) – @shabildwi

### Ekstraksi data
- Extractor P001–P025 (putusan PN Pekanbaru 2023)
- Reviewer P026–P050

### Issue
| Issue | Judul | Jenis | Diselesaikan di |
|---|---|---|---|
| ✏️ (A1) | Dataset P001–P025 (PN Pekanbaru 2023) dan provenance | retrospektif | #5, #8 |
| ✏️ (A3) | ADR-001: pemilihan struktur data utama dan sekunder | retrospektif | #9 |
| ✏️ (A5) | Struktur utama `ArrayDinamis<T>` | retrospektif | #13 |
| ✏️ (A8) | Fitur cari, filter, dan pengurutan | retrospektif | #16, #17, #20 |
| ✏️ (A10) | Fitur ekspor dan menu konsol | retrospektif | #24, #25 |
| ✏️ (A11) | Dokumen pengujian: skenario uji, trace manual, analisis kompleksitas | retrospektif | #26, #27, #28, #29 |
| ✏️ (A13) | README lengkap | retrospektif | #31 |

### Pull Request yang dibuat
| PR | Isi |
|---|---|
| [#5](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/5) | data: P001–P025 dan provenance-nya (extractor Shabil) |
| [#8](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/8) | docs: aturan ekstraksi data dan tautan PDF Google Drive |
| [#9](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/9) | docs(adr): ADR-001 |
| [#13](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/13) | feat(struktur): `ArrayDinamis<T>` + 15 test |
| [#16](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/16) | feat(aplikasi): cari + 6 test |
| [#17](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/17) | feat(aplikasi): filter + 7 test |
| [#20](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/20) | feat(aplikasi): pengurutan insertion sort + 5 test |
| [#24](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/24) | feat(aplikasi): ekspor CSV/TXT + 5 test |
| [#25](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/25) | feat(aplikasi): menu konsol + 7 test |
| [#26](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/26) | docs: peta 7 skenario uji |
| [#28](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/28) | docs(trace): skenario 5 vs provenance |
| [#29](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/29) | docs: analisis kompleksitas |
| [#31](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/31) | docs(readme): README lengkap |

### Review (approve) PR milik Fathul
[#6](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/6), [#7](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/7), [#11](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/11), [#12](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/12), [#14](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/14), [#15](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/15), [#18](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/18), [#19](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/19), [#21](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/21), [#22](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/22), [#23](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/23), [#27](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/27), [#30](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/30)

## Fathul Qolby (202510370110014) – @fathulqolby

### Ekstraksi data
- Extractor P026–P050 (putusan PN Pekanbaru 2024)
- Reviewer P001–P025

### Issue
| Issue | Judul | Jenis | Diselesaikan di |
|---|---|---|---|
| ✏️ (A2) | Dataset P026–P050 (PN Pekanbaru 2024) dan provenance | retrospektif | #6, #7, #11 |
| ✏️ (A4) | Fondasi kode: Maven, StrukturData, Putusan, parser CSV | retrospektif | #12 |
| ✏️ (A6) | Struktur sekunder `StackRiwayat<T>` | retrospektif | #14 |
| ✏️ (A7) | Fitur impor, tampil, tambah, dan hapus | retrospektif | #15, #18 |
| ✏️ (A9) | Fitur agregasi dan audit riwayat | retrospektif | #19, #21, #22, #23 |
| ✏️ (A11) | Dokumen pengujian: skenario uji, trace manual, analisis kompleksitas | retrospektif | #26, #27, #28, #29 |
| ✏️ (A12) | ADR-002: algoritma pencarian dan pengurutan | retrospektif | #30 |

### Pull Request yang dibuat
| PR | Isi |
|---|---|
| [#6](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/6) | data: P026–P050 dan provenance-nya (extractor Fathul) |
| [#7](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/7) | fix(data): nama dan lokasi file dataset |
| [#11](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/11) | fix(data): baris berlebih di CSV yang ditemukan oleh test |
| [#12](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/12) | feat: fondasi kode (Maven, `StrukturData`, `Putusan`, parser + 12 test) |
| [#14](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/14) | feat(struktur): `StackRiwayat<T>` + 10 test |
| [#15](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/15) | feat(aplikasi): impor dan tampil + 4 test |
| [#18](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/18) | feat(aplikasi): tambah dan hapus + 7 test |
| [#19](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/19) | feat(aplikasi): agregasi (dibuat di branch `feat/tambah-hapus`) |
| [#21](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/21) | chore(aplikasi): merapikan kelas di branch `feat/tambah-hapus` |
| [#22](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/22) | feat(aplikasi): agregasi + 6 test (PR terpisah) |
| [#23](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/23) | feat(aplikasi): audit riwayat + 5 test |
| [#27](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/27) | docs(trace): trace manual skenario 3 dan 6 |
| [#30](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/30) | docs(adr): ADR-002 |

### Review (approve) PR milik Shabil
[#5](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/5), [#8](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/8), [#9](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/9), [#13](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/13), [#16](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/16), [#17](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/17), [#20](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/20), [#24](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/24), [#25](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/25), [#26](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/26), [#28](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/28), [#29](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/29), [#31](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/31)

## Catatan riwayat
- Sebagian besar pekerjaan dikerjakan tanpa Issue. Issue bertanda *retrospektif* dibuat setelah pekerjaan selesai
  untuk mendokumentasikan pembagian kerja, dan isinya menyatakan hal tersebut.
- PR [#19](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/19) dan [#21](https://github.com/fathulqolby/uts-struktur-data-narkotika-kelompok-06/pull/21) berasal dari branch `feat/tambah-hapus`: kelas agregasi dan pengurutan sempat dibuat di branch tersebut.
  Agregasi kemudian dikeluarkan (#21) dan diajukan ulang sebagai PR sendiri (#22). Pengurutan yang dipakai di `main` berasal dari PR #20.
  Riwayat commit tidak diubah (tanpa force push).
- PR audit pertama ditutup tanpa di-merge karena tidak sengaja mengubah `Putusan.java`, lalu dibuat ulang dari `main` sebagai PR #23.