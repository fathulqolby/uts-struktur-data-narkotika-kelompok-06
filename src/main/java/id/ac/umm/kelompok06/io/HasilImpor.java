package id.ac.umm.kelompok06.io;

/**
 * Ringkasan hasil membaca file CSV: berapa record diterima,
 * berapa ditolak, dan pesan kesalahan untuk setiap record yang ditolak.
 */
public class HasilImpor {

    private final int jumlahDiterima;
    private final int jumlahDitolak;
    private final String laporanKesalahan;

    public HasilImpor(int jumlahDiterima, int jumlahDitolak, String laporanKesalahan) {
        this.jumlahDiterima = jumlahDiterima;
        this.jumlahDitolak = jumlahDitolak;
        this.laporanKesalahan = laporanKesalahan;
    }

    public int getJumlahDiterima() { return jumlahDiterima; }
    public int getJumlahDitolak() { return jumlahDitolak; }
    public String getLaporanKesalahan() { return laporanKesalahan; }

    @Override
    public String toString() {
        String ringkas = "Diterima: " + jumlahDiterima + ", ditolak: " + jumlahDitolak;
        return jumlahDitolak == 0 ? ringkas : ringkas + System.lineSeparator() + laporanKesalahan;
    }
}