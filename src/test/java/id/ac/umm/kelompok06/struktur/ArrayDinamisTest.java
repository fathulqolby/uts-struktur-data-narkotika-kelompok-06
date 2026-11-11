package id.ac.umm.kelompok06.struktur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayDinamisTest {

    private ArrayDinamis<String> arr;

    @BeforeEach
    void siapkan() {
        arr = new ArrayDinamis<>(4);
        arr.add("A");
        arr.add("B");
        arr.add("C");
    }

    @Test
    @DisplayName("add menambah di akhir dan get mengambil sesuai indeks")
    void addDanGet() {
        assertEquals(3, arr.size());
        assertEquals("A", arr.get(0));
        assertEquals("C", arr.get(2));
        arr.validateInvariant();
    }

    @Test
    @DisplayName("Kapasitas berlipat dua saat penuh dan urutan tetap")
    void resizeSaatPenuh() {
        arr.add("D");                 // kapasitas 4 penuh
        assertEquals(4, arr.kapasitas());
        arr.add("E");                 // memicu resize
        assertEquals(8, arr.kapasitas());
        assertEquals(5, arr.size());
        assertEquals("A", arr.get(0));
        assertEquals("E", arr.get(4));
        arr.validateInvariant();
    }

    @Test
    @DisplayName("Menampung 50 elemen dari kapasitas awal default")
    void menampung50() {
        ArrayDinamis<Integer> angka = new ArrayDinamis<>();
        for (int i = 1; i <= 50; i++) {
            angka.add(i);
        }
        assertEquals(50, angka.size());
        assertEquals(80, angka.kapasitas()); // 10 -> 20 -> 40 -> 80
        assertEquals(50, (int) angka.get(49));
        angka.validateInvariant();
    }

    @Test
    @DisplayName("Skenario 3: hapus elemen pertama menggeser sisanya ke kiri")
    void hapusPertama() {
        assertEquals("A", arr.remove(0));
        assertEquals("B", arr.get(0));
        assertEquals("C", arr.get(1));
        assertEquals(2, arr.size());
        arr.validateInvariant();
    }

    @Test
    @DisplayName("Skenario 3: hapus elemen tengah")
    void hapusTengah() {
        assertEquals("B", arr.remove(1));
        assertEquals("A", arr.get(0));
        assertEquals("C", arr.get(1));
        arr.validateInvariant();
    }

    @Test
    @DisplayName("Skenario 3: hapus elemen terakhir tanpa pergeseran")
    void hapusTerakhir() {
        assertEquals("C", arr.remove(2));
        assertEquals(2, arr.size());
        assertEquals("B", arr.get(1));
        arr.validateInvariant();
    }

    @Test
    @DisplayName("Skenario 4: sisip di depan, tengah, dan akhir menjaga urutan")
    void sisipKembali() {
        arr.insert(0, "X");          // X A B C
        arr.insert(2, "Y");          // X A Y B C  (memicu resize 4 -> 8)
        arr.insert(arr.size(), "Z"); // X A Y B C Z
        String[] harapan = {"X", "A", "Y", "B", "C", "Z"};
        assertEquals(harapan.length, arr.size());
        for (int i = 0; i < harapan.length; i++) {
            assertEquals(harapan[i], arr.get(i));
        }
        arr.validateInvariant();
    }

    @Test
    @DisplayName("set mengganti elemen dan mengembalikan yang lama")
    void setElemen() {
        assertEquals("B", arr.set(1, "Q"));
        assertEquals("Q", arr.get(1));
        arr.validateInvariant();
    }

    @Test
    @DisplayName("Indeks di luar batas ditolak tanpa mengubah isi")
    void batasIndeks() {
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.remove(3));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.set(5, "Z"));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.insert(4, "Z"));
        assertEquals(3, arr.size());
        arr.validateInvariant();
    }

    @Test
    @DisplayName("Elemen null ditolak")
    void nullDitolak() {
        assertThrows(IllegalArgumentException.class, () -> arr.add(null));
        assertEquals(3, arr.size());
    }

    @Test
    @DisplayName("Skenario 2: search menemukan elemen dan menghitung langkah")
    void searchDitemukan() {
        assertEquals(2, arr.search("C"));
        assertEquals(3, arr.getLangkahTerakhir());
        assertEquals(0, arr.search("A"));
        assertEquals(1, arr.getLangkahTerakhir());
    }

    @Test
    @DisplayName("Skenario 2: search elemen yang tidak ada mengembalikan -1 setelah n langkah")
    void searchTidakAda() {
        assertEquals(-1, arr.search("Z"));
        assertEquals(3, arr.getLangkahTerakhir());
        assertTrue(arr.contains("B"));
        assertFalse(arr.contains("Z"));
    }

    @Test
    @DisplayName("cari dengan kriteria bebas")
    void cariDenganKriteria() {
        assertEquals(1, arr.cari(s -> s.equals("B")));
        assertEquals(2, arr.getLangkahTerakhir());
    }

    @Test
    @DisplayName("clear mengosongkan isi dan invariant tetap valid")
    void clearIsi() {
        arr.clear();
        assertTrue(arr.isEmpty());
        assertEquals(0, arr.size());
        arr.validateInvariant();
    }

    @Test
    @DisplayName("salin menghasilkan struktur terpisah dengan urutan sama")
    void salinTerpisah() {
        ArrayDinamis<String> kopi = arr.salin();
        kopi.set(0, "Z");
        assertEquals("A", arr.get(0));
        assertEquals("Z", kopi.get(0));
        assertEquals(3, kopi.size());
    }
}