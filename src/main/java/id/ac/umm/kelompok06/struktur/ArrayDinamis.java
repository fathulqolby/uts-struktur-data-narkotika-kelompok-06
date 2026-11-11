package id.ac.umm.kelompok06.struktur;

import java.util.function.Predicate;

/**
 * Struktur data utama: array dinamis generik berbasis T[] (lihat ADR-001).
 *
 * Invariant (diperiksa oleh validateInvariant()):
 *  1. 0 <= size <= data.length
 *  2. slot 0..size-1 berisi elemen (tidak null)
 *  3. slot size..data.length-1 bernilai null
 *
 * Tidak memakai koleksi java.util untuk penyimpanan.
 */
public class ArrayDinamis<T> implements StrukturData<T> {

    private static final int KAPASITAS_AWAL = 10;

    private T[] data;
    private int size;

    /** Jumlah perbandingan pada pencarian terakhir (untuk fitur cari). */
    private int langkahTerakhir;

    public ArrayDinamis() {
        this(KAPASITAS_AWAL);
    }

    @SuppressWarnings("unchecked")
    public ArrayDinamis(int kapasitasAwal) {
        if (kapasitasAwal < 1) {
            throw new IllegalArgumentException("Kapasitas awal minimal 1, diberikan: " + kapasitasAwal);
        }
        // Java tidak bisa membuat new T[n], jadi dibuat Object[] lalu di-cast.
        data = (T[]) new Object[kapasitasAwal];
        size = 0;
    }

    // ================= Operasi kontrak StrukturData =================

    /** Menambah elemen di akhir. O(1) amortized, O(n) saat resize. */
    @Override
    public void add(T value) {
        cekTidakNull(value);
        if (size == data.length) {
            resize(data.length * 2);
        }
        data[size] = value;
        size++;
    }

    /** Mengambil elemen pada indeks. O(1). */
    @Override
    public T get(int position) {
        cekIndeks(position, size - 1);
        return data[position];
    }

    /**
     * Menghapus elemen pada indeks dan mengembalikannya. O(n):
     * elemen di kanan posisi digeser satu langkah ke kiri.
     */
    @Override
    public T remove(int position) {
        cekIndeks(position, size - 1);
        T dihapus = data[position];
        for (int i = position; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null; // jaga invariant 3: slot bekas tidak menyimpan referensi lama
        size--;
        return dihapus;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(T value) {
        return search(value) != -1;
    }

    /** Menghapus semua elemen; kapasitas tetap. O(n). */
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    // ================= Operasi tambahan array dinamis =================

    /**
     * Menyisipkan elemen pada indeks (0..size). O(n):
     * elemen dari posisi itu digeser satu langkah ke kanan.
     * insert(size, x) sama dengan add(x).
     */
    public void insert(int position, T value) {
        cekTidakNull(value);
        cekIndeks(position, size); // boleh sama dengan size (sisip di akhir)
        if (size == data.length) {
            resize(data.length * 2);
        }
        for (int i = size; i > position; i--) {
            data[i] = data[i - 1];
        }
        data[position] = value;
        size++;
    }

    /** Mengganti elemen pada indeks dan mengembalikan elemen lama. O(1). */
    public T set(int position, T value) {
        cekTidakNull(value);
        cekIndeks(position, size - 1);
        T lama = data[position];
        data[position] = value;
        return lama;
    }

    /**
     * Pencarian linear berdasarkan equals. Mengembalikan indeks pertama, atau -1.
     * Jumlah perbandingan disimpan di getLangkahTerakhir(). O(n).
     */
    public int search(T value) {
        langkahTerakhir = 0;
        if (value == null) {
            return -1;
        }
        for (int i = 0; i < size; i++) {
            langkahTerakhir++;
            if (data[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Pencarian linear dengan kriteria bebas, misalnya
     * cari(p -> p.getNomorPerkara().equals(nomor)). O(n).
     */
    public int cari(Predicate<T> kriteria) {
        langkahTerakhir = 0;
        for (int i = 0; i < size; i++) {
            langkahTerakhir++;
            if (kriteria.test(data[i])) {
                return i;
            }
        }
        return -1;
    }

    /** Jumlah perbandingan pada search/cari terakhir. */
    public int getLangkahTerakhir() {
        return langkahTerakhir;
    }

    /** Kapasitas array saat ini (panjang data[]). */
    public int kapasitas() {
        return data.length;
    }

    /** Salinan dangkal dengan urutan sama; dipakai untuk tampilan terurut tanpa mengubah arsip. O(n). */
    public ArrayDinamis<T> salin() {
        ArrayDinamis<T> hasil = new ArrayDinamis<>(Math.max(KAPASITAS_AWAL, size));
        for (int i = 0; i < size; i++) {
            hasil.add(data[i]);
        }
        return hasil;
    }

    // ================= Invariant dan bantuan =================

    /**
     * Memeriksa invariant struktur. Melempar IllegalStateException jika dilanggar.
     * Dipanggil di unit test setelah setiap operasi yang mengubah isi.
     */
    public void validateInvariant() {
        if (size < 0 || size > data.length) {
            throw new IllegalStateException("size " + size + " di luar 0.." + data.length);
        }
        for (int i = 0; i < size; i++) {
            if (data[i] == null) {
                throw new IllegalStateException("slot aktif " + i + " bernilai null");
            }
        }
        for (int i = size; i < data.length; i++) {
            if (data[i] != null) {
                throw new IllegalStateException("slot kosong " + i + " masih berisi elemen");
            }
        }
    }

    /**
     * Gambaran isi slot, termasuk slot kosong. Dipakai untuk menunjukkan
     * pergeseran elemen pada skenario uji 3, misalnya [A, B, C, _, _].
     */
    public String gambarSlot() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(data[i] == null ? "_" : data[i]);
        }
        return sb.append("] size=").append(size).append(" kapasitas=").append(data.length).toString();
    }

    @SuppressWarnings("unchecked")
    private void resize(int kapasitasBaru) {
        T[] baru = (T[]) new Object[kapasitasBaru];
        for (int i = 0; i < size; i++) {
            baru[i] = data[i];
        }
        data = baru;
    }

    private void cekIndeks(int position, int maks) {
        if (position < 0 || position > maks) {
            throw new IndexOutOfBoundsException(
                    "Indeks " + position + " di luar rentang 0.." + maks + " (size=" + size + ")");
        }
    }

    private void cekTidakNull(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Elemen null tidak boleh disimpan");
        }
    }
}