package shoppinglist;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import shoppinglist.entity.DaftarBelanja;
import shoppinglist.entity.DaftarBelanjaDetil;
import shoppinglist.repository.DaftarBelanjaRepo;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.data.domain.Example;

@SpringBootApplication
public class DemoShoppingListSpringBootApplication implements CommandLineRunner
{
    @Autowired
    private DaftarBelanjaRepo repo;

    public static void main(String[] args)
    {
        SpringApplication.run(DemoShoppingListSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        Scanner keyb = new Scanner(System.in);
        
        // Baca semua DaftarBelanja
        System.out.println("Membaca Semua Record DaftarBelanja:");
        bacaSemuaList();
        
        // Baca berdasarkan ID
        System.out.println("Masukkan ID dari objek DaftarBelanja yg ingin ditampilkan: ");
        long id = Long.parseLong(keyb.nextLine());
        
        System.out.println("Hasil pencarian: ");
        bacaBerdasarkanId(id);
        
        // Mencari DaftarBelanja berdasarkan kemiripan judul
        System.out.println("Masukkan judul DaftarBelanja yang ingin dicari: ");
        String judul = keyb.nextLine();
        
        System.out.println("Hasil pencarian: ");
        cariBerdasarkanJudul(judul);
        
        // Menyimpan DaftarBelanja
        System.out.println("Masukkan judul DaftarBelanja baru: ");
        judul = keyb.nextLine();
        
        DaftarBelanja db = new DaftarBelanja();
        db.setJudul(judul);
        db.setTanggal(LocalDateTime.now());
        
        System.out.println("Masukkan jumlah barang dalam DaftarBelanja baru: ");
        int jmlh = Integer.parseInt(keyb.nextLine());
        
        DaftarBelanjaDetil listDetil[] = new DaftarBelanjaDetil[jmlh];
        for (int i = 0; i < jmlh; i++) {
            System.out.println("\nMasukkan nama barang " + (i+1) +": ");
            String nama = keyb.nextLine();
            
            System.out.println("Masukkan banyak barang " + (i+1) +": ");
            float bnyk = Float.parseFloat(keyb.nextLine());
            
            System.out.println("Masukkan satuan banyak barang " + (i+1) +": ");
            String satuan = keyb.nextLine();
            
            System.out.println("Tulis memo barang " + (i+1) +": ");
            String memo = keyb.nextLine();
            
            listDetil[i] = new DaftarBelanjaDetil();
            listDetil[i].setNamaBarang(nama);
            listDetil[i].setByk(bnyk);
            listDetil[i].setSatuan(satuan);
            listDetil[i].setMemo(memo);
        }
        simpanDaftarBelanja(db, listDetil);
        
        // Update DaftarBelanja
        System.out.println("Masukkan id DaftarBelanja yang ingin diupdate: ");
        id = Long.parseLong(keyb.nextLine());
        db = getDb(id);
        
        System.out.println("(Langsung tekan Enter jika tidak ingin diganti)");
        System.out.println("Masukkan judul baru: ");
        judul = keyb.nextLine();
        if(!judul.isBlank())
            db.setJudul(judul);
        
        System.out.println("Masukkan tanggal baru (tulis 'now' untuk saat ini): [yyyy-mm-dd H:i:s]");
        String strTanggal = keyb.nextLine();
        if(!strTanggal.isBlank()) {
            if(strTanggal.equalsIgnoreCase("now"))
                db.setTanggal(LocalDateTime.now());
            else
                db.setTanggal(LocalDateTime.parse(strTanggal, DateTimeFormatter.ISO_DATE_TIME));
        }
        
        int i = 1;
        for (DaftarBelanjaDetil barang : db.getDaftarBarang()) {
            System.out.println("Barang " + (i++) + ": ");
            System.out.println("Nama Barang: ");
            String namaBrg = keyb.nextLine();
            if(!namaBrg.isBlank())
                barang.setNamaBarang(namaBrg);
            
            System.out.println("Banyak: ");
            String bnyk = keyb.nextLine();
            if(!bnyk.isBlank() && bnyk.matches("[-+]?[0-9]*\\.?[0-9]+"))
                barang.setByk(Float.parseFloat(bnyk));
            
            System.out.println("Satuan: ");
            String satuan = keyb.nextLine();
            if(!satuan.isBlank())
                barang.setSatuan(satuan);
            
            System.out.println("Memo: ");
            String memo = keyb.nextLine();
            if(!memo.isBlank())
                barang.setMemo(memo);
        }
        
        updateDaftarBelanja(db);
        
        // Hapus DaftarBelanja
        System.out.println("Masukkan id DaftarBelanja yang ingin dihapus: ");
        id = Long.parseLong(keyb.nextLine());
        
        hapusDaftarBelanja(id);
        
    }
    
    private void bacaSemuaList() {
        List<DaftarBelanja> all = repo.findAll();
        
        for(DaftarBelanja db : all) {
            System.out.println(db.toString());
        }
    }
    
    private void cariBerdasarkanJudul(String judul) {
        List<DaftarBelanja> listDB = repo.findByJudulLike(judul + "%");
        
        if (listDB.size() > 0) {
            for (DaftarBelanja db : listDB) {
                System.out.println(db.toString());
            }
        } else {
            System.out.println("\tTIDAK DITEMUKAN.\n");
        }
    }
    
    private void bacaBerdasarkanId(long id) {
        DaftarBelanja db = getDb(id);
        if (db != null)
            System.out.println(db.toString());
        else
            System.out.println("\tTIDAK DITEMUKAN.\n");
    }
    
    private void simpanDaftarBelanja(DaftarBelanja db, DaftarBelanjaDetil listDetil[]) {
        try {
            repo.save(db);
            
            int noUrut = 1;
            for (DaftarBelanjaDetil detil : listDetil) {
                detil.setId(db.getId(), noUrut++);
                db.addDaftarBarang(detil);
            }
            
            repo.save(db);

            System.out.println("\nTersimpan dengan id " + db.getId());
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    private void updateDaftarBelanja(DaftarBelanja db) {
        try {
            repo.save(db);
            
            System.out.println("DaftarBelanja berhasil diupdate");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    private void hapusDaftarBelanja(Long id) {
        try {
            repo.deleteById(id);
            
            System.out.println("\nDaftarBelanja dengan id " + id + " sudah dihapus");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    
    private DaftarBelanja getDb(long id){
        Optional<DaftarBelanja> optDb = repo.findById(id);
        if(optDb.isPresent())
            return optDb.get();
        else
            return null;
    }
}