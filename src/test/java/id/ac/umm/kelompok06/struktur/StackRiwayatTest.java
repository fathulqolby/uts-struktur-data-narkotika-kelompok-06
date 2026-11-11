package id.ac.umm.kelompok06.struktur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StackRiwayatTest {

    private StackRiwayat<String> stack;

    @BeforeEach
    void siapkan() {
        stack = new StackRiwayat<>(3);
        stack.push("impor");
        stack.push("cari P012");
        stack.push("hapus P030");
    }

    @Test
    @DisplayName("pop mengembalikan elemen terakhir yang di-push (LIFO)")
    void lifo() {
        assertEquals("hapus P030", stack.pop());
        assertEquals("cari P012", stack.pop());
        assertEquals("impor", stack.pop());
        assertTrue(stack.isEmpty());
        stack.validateInvariant();
    }

    @Test
    @DisplayName("peek melihat puncak tanpa menghapus")
    void peekTidakMenghapus() {
        assertEquals("hapus P030", stack.peek());
        assertEquals(3, stack.size());
        stack.validateInvariant();
    }

    @Test
    @DisplayName("push saat penuh memperbesar kapasitas dan urutan tetap")
    void resizeSaatPenuh() {
        stack.push("filter sabu"); // kapasitas 3 penuh -> resize
        assertEquals(4, stack.size());
        assertEquals("filter sabu", stack.peek());
        assertEquals("impor", stack.lihatDariAtas(4));
        stack.validateInvariant();
    }

    @Test
    @DisplayName("pop dan peek pada stack kosong ditolak")
    void kosongDitolak() {
        StackRiwayat<String> kosong = new StackRiwayat<>();
        assertThrows(IllegalStateException.class, kosong::pop);
        assertThrows(IllegalStateException.class, kosong::peek);
        kosong.validateInvariant();
    }

    @Test
    @DisplayName("Skenario 6: searchPreserve menemukan elemen tanpa mengubah urutan")
    void searchPreserveDitemukan() {
        String sebelum = stack.gambarIsi();

        assertEquals(2, stack.searchPreserve("cari P012"));
        assertEquals(2, stack.getLangkahTerakhir());

        assertEquals(sebelum, stack.gambarIsi());
        assertEquals("hapus P030", stack.peek());
        stack.validateInvariant();
    }

    @Test
    @DisplayName("Skenario 6: searchPreserve elemen yang tidak ada tetap mengembalikan semua elemen")
    void searchPreserveTidakAda() {
        String sebelum = stack.gambarIsi();

        assertEquals(-1, stack.searchPreserve("ekspor"));
        assertEquals(3, stack.getLangkahTerakhir());

        assertEquals(sebelum, stack.gambarIsi());
        assertEquals(3, stack.size());
        stack.validateInvariant();
    }

    @Test
    @DisplayName("searchPreserve dengan kriteria bebas")
    void searchPreserveKriteria() {
        assertEquals(1, stack.searchPreserve(s -> s.startsWith("hapus")));
        assertEquals(3, stack.searchPreserve(s -> s.startsWith("impor")));
        assertEquals(3, stack.size());
    }

    @Test
    @DisplayName("restore memindahkan elemen sementara kembali dengan urutan semula")
    void restoreMengembalikanUrutan() {
        StackRiwayat<String> sementara = new StackRiwayat<>();
        sementara.push(stack.pop()); // hapus P030
        sementara.push(stack.pop()); // cari P012
        assertEquals(1, stack.size());

        stack.restore(sementara);

        assertTrue(sementara.isEmpty());
        assertEquals("hapus P030", stack.lihatDariAtas(1));
        assertEquals("cari P012", stack.lihatDariAtas(2));
        assertEquals("impor", stack.lihatDariAtas(3));
        stack.validateInvariant();
    }

    @Test
    @DisplayName("lihatDariAtas di luar rentang ditolak")
    void lihatDariAtasBatas() {
        assertThrows(IndexOutOfBoundsException.class, () -> stack.lihatDariAtas(0));
        assertThrows(IndexOutOfBoundsException.class, () -> stack.lihatDariAtas(4));
    }

    @Test
    @DisplayName("Elemen null ditolak")
    void nullDitolak() {
        assertThrows(IllegalArgumentException.class, () -> stack.push(null));
        assertEquals(3, stack.size());
    }
}