# Catatan Penggunaan AI

Kelompok 06 menggunakan AI generatif sebagai alat bantu terbatas sesuai
Bagian 6 panduan UTS. Setiap keluaran AI diverifikasi oleh anggota yang
menggunakannya sebelum dipakai.

| No | Tanggal | Anggota | Alat | Tujuan | Ringkasan prompt | Bagian keluaran yang digunakan | Perubahan yang dilakukan | Cara verifikasi |
|---|---|---------|---|---|---|---|---|---|
| 1 | 2026-11-04 | Shabil  | Claude | Memahami tugas dan menyusun rencana kerja | Meminta analisis panduan UTS dan panduan langkah demi langkah | Urutan tahapan kerja, daftar jebakan rubrik | Rencana disesuaikan dengan kesepakatan kelompok | Dicocokkan ulang dengan panduan dosen |
| 2 | 2026-11-04 | Shabil  | Claude | Mengganti nama 25 PDF dan membuat checksum | Meminta cara rename massal file PDF | Script PowerShell rename dan SHA-256 | Nilai awal nomor disesuaikan | Tabel pratinjau dicek, jumlah file = 25 |
| 3 | 2026-11-04 | Shabil  | Claude | Menyusun aturan ekstraksi data | Meminta usulan aturan konversi dan kasus khusus | Aturan 1–37 di tab aturan | Disepakati dan disesuaikan bersama rekan | Diuji saat mengekstrak putusan |
| 4 | 2026-11-04 | Shabil  | Claude | Draf ekstraksi data P001–P025 | Mengunggah PDF putusan, meminta draf isian data dan provenance | Nilai atribut dan nomor halaman | Tidak ada koreksi | Setiap nilai dicek manual ke halaman PDF; status provenance diubah dari draft-ai |
| 5 | 2026-11-08 | Shabil  | Claude | Setup repository dengan IntelliJ | Meminta panduan clone, kerangka folder, .gitignore | Langkah setup dan isi .gitignore | Disesuaikan dengan struktur proyek | Folder pdf dan .idea terabaikan oleh Git |