package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.model.Putusan;
import id.ac.umm.kelompok06.struktur.ArrayDinamis;

/**
 * Fitur Filter: menelusuri struktur utama dan menampung hasil yang cocok
 * di ArrayDinamis baru. Arsip asli tidak diubah. O(n).
 */
public class FilterPutusan {

    private final ArsipPutusan aplikasi;

    public FilterPutusan(ArsipPutusan aplikasi) {
        this.aplikasi = aplikasi;
    }

    /** Filter umum (tunggal maupun gabungan). */
    public ArrayDinamis<Putusan> filter(KriteriaFilter kriteria) {
        ArrayDinamis<Putusan> arsip = aplikasi.getArsip();
        ArrayDinamis<Putusan> hasil = new ArrayDinamis<>();
        for (int i = 0; i < arsip.size(); i++) {
            Putusan p = arsip.get(i);
            if (kriteria.cocok(p)) {
                hasil.add(p);
            }
        }
        aplikasi.catat("FILTER", kriteria + " -> " + hasil.size() + " putusan");
        return hasil;
    }

    public ArrayDinamis<Putusan> berdasarkanJenis(String jenis) {
        return filter(new KriteriaFilter().jenis(jenis));
    }

    public ArrayDinamis<Putusan> berdasarkanBerat(double min, double max) {
        return filter(new KriteriaFilter().berat(min, max));
    }

    public ArrayDinamis<Putusan> berdasarkanPasalTerbukti(String pasal) {
        return filter(new KriteriaFilter().pasalTerbukti(pasal));
    }

    public ArrayDinamis<Putusan> berdasarkanVonis(int min, int max) {
        return filter(new KriteriaFilter().vonis(min, max));
    }

    /** Teks hasil filter untuk ditampilkan atau diekspor. */
    public static String format(ArrayDinamis<Putusan> hasil) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d putusan cocok%n", hasil.size()));
        for (int i = 0; i < hasil.size(); i++) {
            sb.append(String.format("%3d. %s%n", i + 1, hasil.get(i)));
        }
        return sb.toString();
    }
}