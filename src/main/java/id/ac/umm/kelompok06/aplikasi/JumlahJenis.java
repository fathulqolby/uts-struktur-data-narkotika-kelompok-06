package id.ac.umm.kelompok06.aplikasi;

/** Pasangan jenis narkotika dan jumlah putusannya, untuk distribusi jenis. */
public class JumlahJenis {

    private final String jenis;
    private int jumlah;

    public JumlahJenis(String jenis) {
        this.jenis = jenis;
        this.jumlah = 0;
    }

    public String getJenis() { return jenis; }
    public int getJumlah() { return jumlah; }

    void tambahSatu() {
        jumlah++;
    }

    @Override
    public String toString() {
        return jenis + "=" + jumlah;
    }
}