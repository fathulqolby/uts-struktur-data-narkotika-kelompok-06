package id.ac.umm.kelompok06.struktur;

import java.util.function.Predicate;

/**
 * Struktur sekunder: Stack generik berbasis T[] untuk riwayat (audit) operasi pengguna.
 * Elemen teratas adalah operasi terbaru (LIFO). Lihat ADR-001.
 *
 * Invariant (diperiksa oleh validateInvariant()):
 *  1. 0 <= top <= data.length          (top = jumlah elemen = indeks slot kosong berikutnya)
 *  2. slot 0..top-1 berisi elemen (tidak null)
 *  3. slot top..data.length-1 bernilai null
 *
 * Tidak memakai koleksi java.util (Stack, Deque, ArrayList, dsb.).
 */
public class StackRiwayat<T> {

    private static final int KAPASITAS_AWAL = 10;

    private T[] data;
    private int top;

    /** Jumlah elemen yang diperiksa pada searchPreserve terakhir. */
    private int langkahTerakhir;

    public StackRiwayat() {
        this(KAPASITAS_AWAL);
    }

    @SuppressWarnings("unchecked")
    public StackRiwayat(int kapasitasAwal) {
        if (kapasitasAwal < 1) {
            throw new IllegalArgumentException("Kapasitas awal minimal 1, diberikan: " + kapasitasAwal);
        }
        data = (T[]) new Object[kapasitasAwal];
        top = 0;
    }

    /** Menaruh elemen di puncak. O(1) amortized, resize x2 saat penuh. */
    public void push(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Elemen null tidak boleh disimpan");
        }
        if (top == data.length) {
            resize(data.length * 2);
        }
        data[top] = value;
        top++;
    }

    /** Mengambil dan menghapus elemen puncak. O(1). */
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack kosong, tidak ada yang bisa di-pop");
        }
        top--;
        T nilai = data[top];
        data[top] = null; // jaga invariant 3
        return nilai;
    }

    /** Melihat elemen puncak tanpa menghapus. O(1). */
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack kosong, tidak ada yang bisa di-peek");
        }
        return data[top - 1];
    }

    public int size() {
        return top;
    }

    public boolean isEmpty() {
        return top == 0;
    }

    /**
     * Pencarian non-destruktif (skenario uji 6).
     * Elemen di-pop satu per satu ke stack sementara sampai yang dicari ditemukan,
     * lalu semua dikembalikan dengan restore() sehingga urutan akhir sama seperti semula.
     *
     * @return posisi dari puncak (1 = elemen teratas), atau -1 jika tidak ada. O(n).
     */
    public int searchPreserve(Predicate<T> kriteria) {
        StackRiwayat<T> sementara = new StackRiwayat<>(Math.max(1, top));
        int posisi = -1;
        langkahTerakhir = 0;

        while (!isEmpty()) {
            T atas = pop();
            sementara.push(atas);
            langkahTerakhir++;
            if (kriteria.test(atas)) {
                posisi = langkahTerakhir;
                break;
            }
        }
        restore(sementara);
        return posisi;
    }

    /** Versi searchPreserve berdasarkan equals. */
    public int searchPreserve(T value) {
        return searchPreserve(e -> e.equals(value));
    }

    /**
     * Mengembalikan elemen dari stack sementara ke stack ini.
     * Karena sementara berisi elemen dalam urutan terbalik, pop-lalu-push
     * membalikkannya lagi sehingga urutan kembali seperti semula. O(k).
     */
    public void restore(StackRiwayat<T> sementara) {
        while (!sementara.isEmpty()) {
            push(sementara.pop());
        }
    }

    /** Jumlah elemen yang diperiksa pada searchPreserve terakhir. */
    public int getLangkahTerakhir() {
        return langkahTerakhir;
    }

    /**
     * Melihat elemen ke-n dari puncak tanpa mengubah stack (1 = teratas).
     * Dipakai untuk menampilkan riwayat dari yang terbaru. O(1).
     */
    public T lihatDariAtas(int urutan) {
        if (urutan < 1 || urutan > top) {
            throw new IndexOutOfBoundsException("Urutan " + urutan + " di luar 1.." + top);
        }
        return data[top - urutan];
    }

    /** Memeriksa invariant. Melempar IllegalStateException jika dilanggar. */
    public void validateInvariant() {
        if (top < 0 || top > data.length) {
            throw new IllegalStateException("top " + top + " di luar 0.." + data.length);
        }
        for (int i = 0; i < top; i++) {
            if (data[i] == null) {
                throw new IllegalStateException("slot aktif " + i + " bernilai null");
            }
        }
        for (int i = top; i < data.length; i++) {
            if (data[i] != null) {
                throw new IllegalStateException("slot kosong " + i + " masih berisi elemen");
            }
        }
    }

    /** Isi stack dari bawah ke atas, misalnya [A, B, C <- puncak]. */
    public String gambarIsi() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < top; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(data[i]);
        }
        if (top > 0) {
            sb.append(" <- puncak");
        }
        return sb.append("] size=").append(top).toString();
    }

    @SuppressWarnings("unchecked")
    private void resize(int kapasitasBaru) {
        T[] baru = (T[]) new Object[kapasitasBaru];
        for (int i = 0; i < top; i++) {
            baru[i] = data[i];
        }
        data = baru;
    }
}