# ADR-001: Pemilihan Struktur Data Utama dan Sekunder

- **Status:** Diterima
- **Tanggal:** 2026-11-11
- **Penulis:** M. Shabil Dwi Nur Basyah
- **Issue:** #5

## Konteks

- **Menampilkan dan menelusuri seluruh data** (tampil, filter, agregasi, ekspor):
  dilakukan setiap kali pengguna meminta laporan, selalu membaca 50 elemen berurutan.
- **Akses berdasarkan posisi**: menampilkan detail putusan ke-i, memeriksa elemen
  di posisi batas (pertama dan terakhir) pada skenario uji 2.
- **Pencarian** berdasarkan `caseId` atau nomor perkara, dengan pelaporan jumlah langkah.
- **Tambah dan hapus**: jarang dilakukan dibanding membaca; hanya saat pengguna
  menambah/menghapus record dan pada skenario uji 3–4.
- **Pengurutan berdasarkan vonis**: menghasilkan tampilan terurut tanpa mengubah
  urutan arsip asli.
- **Riwayat operasi**: setiap aksi pengguna dicatat, dan yang paling sering dilihat
  adalah aksi **terakhir**.

## Pilihan yang dipertimbangkan

### Struktur utama
1. Array dinamis
2. Singly linked list

### Struktur sekunder
1. Stack
2. Queue

## Perbandingan kompleksitas

| Operasi | Array dinamis | Singly linked list |
|---|---|---|
| `get(i)` / `set(i)` | O(1) | O(n) |
| `add` di akhir | O(1) amortized (O(n) saat resize) | O(1) jika menyimpan `tail` |
| `insert(i)` / `remove(i)` di depan atau tengah | O(n) karena menggeser elemen | O(n) mencari node, O(1) mengubah pointer |
| `search` (linear) | O(n) | O(n) |
| Tampil semua data berurutan | O(n) | O(n) |
| Memori tambahan | Slot kosong sisa kapasitas | Satu pointer `next` per node |

| Operasi | Stack (array `T[]`) | Queue (circular array) |
|---|---|---|
| push / enqueue | O(1) amortized | O(1) amortized |
| pop / dequeue, peek | O(1) | O(1) |
| searchPreserve / rotateSearch | O(n) | O(n) |

## Keputusan

- **Struktur utama: array dinamis** (`ArrayDinamis<T>`, berbasis `T[]`)
- **Struktur sekunder: Stack** (`StackRiwayat<T>`, berbasis `T[]`)

## Alasan

1. **Akses per indeks O(1).** Fitur tampil detail, pemeriksaan posisi batas, dan
   algoritma pengurutan sering mengakses elemen ke-i. Pada array dinamis ini
   langsung `data[i]`, sedangkan pada linked list harus menelusuri i node (O(n)),
   sehingga pengurutan bisa menjadi jauh lebih lambat.
2. **Biaya geser tidak signifikan untuk ukuran data ini.** Insert/remove di depan
   atau tengah memang O(n) karena elemen digeser, tetapi dengan ±50 record paling
   banyak 49 pergeseran, dan operasi ini jarang dibanding operasi membaca.
3. **Penelusuran berurutan sama-sama O(n)**, tetapi array menyimpan elemen
   bersebelahan di memori sehingga penelusuran sederhana dan tidak bergantung
   pada pointer `next`.
4. **Risiko bug lebih kecil.** Kesalahan pointer (head/tail tidak terbarui, node
   terputus) adalah sumber bug yang umum pada linked list. Dengan array, invariant
   cukup dijaga lewat `size` dan isi slot, sehingga lebih mudah diuji dan diperbaiki
   saat demo.
5. **Stack sesuai pola riwayat.** Riwayat operasi paling sering dilihat dari yang
   terbaru, dan pembatalan (undo) selalu dimulai dari operasi terakhir. Pola ini
   adalah LIFO (Stack). Queue (FIFO) lebih cocok untuk antrean pemeriksaan, yang
   tidak menjadi kebutuhan utama aplikasi ini.
6. **Implementasi seragam.** Struktur utama dan Stack sama-sama berbasis `T[]`,
   sehingga strategi resize dan pola `validateInvariant()` dapat dipakai dengan
   cara yang konsisten.

## Konsekuensi

**Yang menguntungkan**
- `get(i)` dan `set(i)` O(1); pengurutan dan pemeriksaan posisi batas efisien.
- Struktur dan invariant sederhana, mudah diuji otomatis.

**Yang harus diwaspadai dan cara menanganinya**
- **Skenario uji 3** (hapus elemen pertama, tengah, terakhir) ditunjukkan lewat
  **pergeseran elemen**: setelah `remove(i)`, elemen di indeks i+1..size-1 bergeser
  satu posisi ke kiri, `size` berkurang satu, dan slot `data[size]` dikosongkan (`null`).
  Menghapus elemen terakhir tidak memerlukan pergeseran.
- **Invariant yang dijaga `validateInvariant()`**:
    1. `0 ≤ size ≤ data.length`
    2. Semua slot indeks `0..size-1` berisi elemen (tidak `null`)
    3. Semua slot indeks `size..data.length-1` bernilai `null`
       Untuk `StackRiwayat`: `0 ≤ top ≤ data.length`, slot `0..top-1` terisi,
       dan sisanya `null`.
- **Kapasitas penuh**: saat `size == data.length`, array baru berkapasitas
  **dua kali lipat** dibuat dan elemen disalin (O(n) sekali waktu, O(1) amortized
  untuk `add`). Kapasitas awal 10 sehingga dari 10 → 20 → 40 → 80 untuk 50 record.
- **Batas indeks**: `get`, `set`, `insert`, dan `remove` dengan indeks di luar
  rentang melempar `IndexOutOfBoundsException` dengan pesan yang jelas,
  tanpa mengubah isi struktur.
- **Pengurutan berdasarkan vonis** dilakukan pada **salinan** (`ArrayDinamis` baru
  atau array mentah), sehingga urutan arsip asli dan invariant struktur utama
  tidak berubah.
- **searchPreserve pada Stack**: elemen di-pop ke stack sementara selama pencarian,
  lalu di-push kembali sehingga urutan akhir sama dengan sebelum pencarian
  (skenario uji 6).