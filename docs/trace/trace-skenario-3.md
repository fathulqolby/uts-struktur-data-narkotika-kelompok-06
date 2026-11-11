# Trace Manual Skenario 3: Hapus Awal, Tengah, Akhir pada ArrayDinamis

Contoh memakai 6 record pertama (P001–P006) supaya slot mudah digambar.
Kapasitas awal ArrayDinamis = 10. Slot kosong ditulis `_`.

Kode yang di-trace (`ArrayDinamis.remove`):

```java
T dihapus = data[position];
for (int i = position; i < size - 1; i++) {
    data[i] = data[i + 1];          // geser satu langkah ke kiri
}
data[size - 1] = null;              // invariant: slot bekas dikosongkan
size--;
```

## Kondisi awal

| Indeks | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
|---|---|---|---|---|---|---|---|---|---|---|
| data | P001 | P002 | P003 | P004 | P005 | P006 | _ | _ | _ | _ |

size = 6, kapasitas = 10

## 1. Hapus awal: `remove(0)` (P001)

| Langkah | i | Aksi | Isi data[0..5] |
|---|---|---|---|
| 0 | - | dihapus = data[0] = P001 | P001, P002, P003, P004, P005, P006 |
| 1 | 0 | data[0] = data[1] | **P002**, P002, P003, P004, P005, P006 |
| 2 | 1 | data[1] = data[2] | P002, **P003**, P003, P004, P005, P006 |
| 3 | 2 | data[2] = data[3] | P002, P003, **P004**, P004, P005, P006 |
| 4 | 3 | data[3] = data[4] | P002, P003, P004, **P005**, P005, P006 |
| 5 | 4 | data[4] = data[5] | P002, P003, P004, P005, **P006**, P006 |
| 6 | - | data[5] = null, size = 5 | P002, P003, P004, P005, P006, **_** |

Hasil: `[P002, P003, P004, P005, P006, _, _, _, _, _] size=5`
Jumlah pergeseran = size − 1 − position = 6 − 1 − 0 = **5** (kasus terburuk, O(n)).

## 2. Hapus tengah: `remove(2)` (P004)

| Langkah | i | Aksi | Isi data[0..4] |
|---|---|---|---|
| 0 | - | dihapus = data[2] = P004 | P002, P003, P004, P005, P006 |
| 1 | 2 | data[2] = data[3] | P002, P003, **P005**, P005, P006 |
| 2 | 3 | data[3] = data[4] | P002, P003, P005, **P006**, P006 |
| 3 | - | data[4] = null, size = 4 | P002, P003, P005, P006, **_** |

Hasil: `[P002, P003, P005, P006, _, _, _, _, _, _] size=4`
Jumlah pergeseran = 5 − 1 − 2 = **2**.

## 3. Hapus akhir: `remove(3)` (P006)

| Langkah | i | Aksi | Isi data[0..3] |
|---|---|---|---|
| 0 | - | dihapus = data[3] = P006 | P002, P003, P005, P006 |
| 1 | 3 | syarat `3 < 3` salah, loop tidak berjalan | P002, P003, P005, P006 |
| 2 | - | data[3] = null, size = 3 | P002, P003, P005, **_** |

Hasil: `[P002, P003, P005, _, _, _, _, _, _, _] size=3`
Jumlah pergeseran = 4 − 1 − 3 = **0** (kasus terbaik, O(1)).

## Pemeriksaan invariant setelah setiap hapus
1. `0 ≤ size ≤ kapasitas`: 5, 4, 3 ≤ 10 ✓
2. Slot `0..size-1` tidak null ✓
3. Slot `size..kapasitas-1` bernilai null ✓ (karena `data[size-1] = null`)

## Hubungan dengan data asli (50 record)
Test `TambahHapusTest#hapusPertamaTengahTerakhir` melakukan hal yang sama pada 50 putusan:
hapus P001 (49 pergeseran), hapus P026 di indeks 24 (24 pergeseran), dan hapus P050 (0 pergeseran). Hasil akhirnya size = 47.

## Verifikasi dengan program
Output `gambarSlot()` dari program sementara `CobaTrace`:

```
awal        : [P001, P002, P003, P004, P005, P006, _, _, _, _] size=6 kapasitas=10
remove(0)   : [P002, P003, P004, P005, P006, _, _, _, _, _] size=5 kapasitas=10
remove(2)   : [P002, P003, P005, P006, _, _, _, _, _, _] size=4 kapasitas=10
remove(last): [P002, P003, P005, _, _, _, _, _, _, _] size=3 kapasitas=10
```

Hasil trace manual sama dengan output program.