# Trace Manual Skenario 6: searchPreserve pada StackRiwayat

Pencarian di Stack tidak boleh mengubah urutan isi stack.
Kode yang di-trace (`StackRiwayat.searchPreserve` dan `restore`):

```java
while (!isEmpty()) {
    T atas = pop();
    sementara.push(atas);
    langkahTerakhir++;
    if (kriteria.test(atas)) { posisi = langkahTerakhir; break; }
}
restore(sementara);   // while (!sementara.isEmpty()) push(sementara.pop());
```

## Kondisi awal
Riwayat setelah 4 operasi (paling kanan = puncak):

`utama = [IMPOR, CARI P012, HAPUS P030, FILTER ganja <- puncak]`, top = 4

## Kasus A: cari `CARI P012` (ditemukan)

### Tahap 1: pop dari utama ke sementara

| Langkah | Aksi | utama | sementara | Cocok? |
|---|---|---|---|---|
| 1 | pop FILTER ganja → push ke sementara | [IMPOR, CARI P012, HAPUS P030] | [FILTER ganja] | tidak |
| 2 | pop HAPUS P030 → push ke sementara | [IMPOR, CARI P012] | [FILTER ganja, HAPUS P030] | tidak |
| 3 | pop CARI P012 → push ke sementara | [IMPOR] | [FILTER ganja, HAPUS P030, CARI P012] | **ya**, posisi = 3, break |

Elemen di sementara urutannya terbalik: puncak sementara adalah CARI P012.

### Tahap 2: restore (pop dari sementara, push ke utama)

| Langkah | Aksi | utama | sementara |
|---|---|---|---|
| 4 | pop CARI P012 → push ke utama | [IMPOR, CARI P012] | [FILTER ganja, HAPUS P030] |
| 5 | pop HAPUS P030 → push ke utama | [IMPOR, CARI P012, HAPUS P030] | [FILTER ganja] |
| 6 | pop FILTER ganja → push ke utama | [IMPOR, CARI P012, HAPUS P030, FILTER ganja] | [] |

Hasil: posisi **3** dari puncak, langkah pemeriksaan **3**.
Urutan akhir utama **sama** dengan kondisi awal. Dua kali pembalikan (utama → sementara → utama) mengembalikan urutan semula.

## Kasus B: cari `EKSPOR` (tidak ada)

| Langkah | Aksi | utama | sementara |
|---|---|---|---|
| 1 | pop FILTER ganja | [IMPOR, CARI P012, HAPUS P030] | [FILTER ganja] |
| 2 | pop HAPUS P030 | [IMPOR, CARI P012] | [FILTER ganja, HAPUS P030] |
| 3 | pop CARI P012 | [IMPOR] | [FILTER ganja, HAPUS P030, CARI P012] |
| 4 | pop IMPOR, utama kosong, loop berhenti | [] | [FILTER ganja, HAPUS P030, CARI P012, IMPOR] |
| 5–8 | restore 4 kali | [IMPOR, CARI P012, HAPUS P030, FILTER ganja] | [] |

Hasil: posisi **-1**, langkah **4** (semua elemen diperiksa).
Ini kasus terburuk: 4 pop + 4 push ke sementara, lalu 4 pop + 4 push saat restore. Waktu O(n), ruang tambahan O(n).

## Mengapa urutan pasti kembali?
Misal isi utama dari bawah ke atas: e1, e2, ..., ek.
Pop memindahkan ek dulu, jadi sementara berisi ek, ..., e1 (dari bawah ke atas).
Restore mem-pop e1 dulu lalu mem-push-nya ke utama, sehingga utama kembali berisi e1, ..., ek.

## Pemeriksaan invariant
Setelah kedua kasus: size = 4, slot 0..3 tidak null, slot 4..9 null → `validateInvariant()` lolos.
Test: `StackRiwayatTest#searchPreserveDitemukan`, `#searchPreserveTidakAda`, `AuditRiwayatTest#cariTidakMengubahUrutan`.

## Verifikasi dengan program
Output `gambarIsi()` dari program sementara `CobaTrace`:

```
awal          : [IMPOR, CARI P012, HAPUS P030, FILTER ganja <- puncak] size=4
cari CARI P012: posisi 3, langkah 3 -> [IMPOR, CARI P012, HAPUS P030, FILTER ganja <- puncak] size=4
cari EKSPOR   : posisi -1, langkah 4 -> [IMPOR, CARI P012, HAPUS P030, FILTER ganja <- puncak] size=4
```

Hasil trace manual sama dengan output program.