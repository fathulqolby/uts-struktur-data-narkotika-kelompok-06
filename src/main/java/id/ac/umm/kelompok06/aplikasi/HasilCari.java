package id.ac.umm.kelompok06.aplikasi;

import id.ac.umm.kelompok06.model.Putusan;

/** Hasil satu pencarian: putusan yang ditemukan (atau null), posisinya, dan jumlah langkah. */
public class HasilCari {

    private final String kataKunci;
    private final Putusan putusan;
    private final int indeks;
    private final int langkah;

    public HasilCari(String kataKunci, Putusan putusan, int indeks, int langkah) {
        this.kataKunci = kataKunci;
        this.putusan = putusan;
        this.indeks = indeks;
        this.langkah = langkah;
    }

    public boolean ditemukan() { return putusan != null; }
    public String getKataKunci() { return kataKunci; }
    public Putusan getPutusan() { return putusan; }
    public int getIndeks() { return indeks; }
    public int getLangkah() { return langkah; }

    @Override
    public String toString() {
        if (!ditemukan()) {
            return "'" + kataKunci + "' tidak ditemukan (" + langkah + " langkah perbandingan)";
        }
        return "'" + kataKunci + "' ditemukan di indeks " + indeks
                + " (" + langkah + " langkah perbandingan)" + System.lineSeparator() + putusan;
    }
}