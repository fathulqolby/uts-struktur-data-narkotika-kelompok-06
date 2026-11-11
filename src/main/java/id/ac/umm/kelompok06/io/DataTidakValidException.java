package id.ac.umm.kelompok06.io;

/**
 * Dilempar ketika satu baris CSV tidak memenuhi aturan data.
 * Menyimpan nomor baris agar pesan kesalahan mudah dilacak.
 */
public class DataTidakValidException extends Exception {

    private final int nomorBaris;

    public DataTidakValidException(int nomorBaris, String pesan) {
        super("Baris " + nomorBaris + ": " + pesan);
        this.nomorBaris = nomorBaris;
    }

    public int getNomorBaris() {
        return nomorBaris;
    }
}