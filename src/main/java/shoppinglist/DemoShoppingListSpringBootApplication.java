package shoppinglist;

import java.time.LocalDateTime;
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
        System.out.println("Membaca Semua Record DaftarBelanja:");
        List<DaftarBelanja> all = repo.findAll();
        for (DaftarBelanja db : all) {
            System.out.println("[" + db.getId() + "] " + db.getJudul());

            List<DaftarBelanjaDetil> listBarang = db.getDaftarBarang();
            for (DaftarBelanjaDetil barang : listBarang) {
                System.out.println("\t" + barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
            }
        }
        
        Scanner keyb = new Scanner(System.in);
        
        // Baca berdasarkan ID
        System.out.print("Masukkan ID dari objek DaftarBelanja yg ingin ditampilkan: ");
        long id = Long.parseLong(keyb.nextLine());
        System.out.println("Hasil pencarian: ");
        bacaBerdasarkanId(id);
        
        // Mencari DaftarBelanja berdasarkan kemiripan judul
        System.out.print("Masukkan judul DaftarBelanja yang ingin dicari: ");
        String judul = keyb.nextLine();
        DaftarBelanja db = new DaftarBelanja();
        db.setJudul(judul);
//        optDB = repo.findOne();
        
        System.out.println("Hasil");
        
        // Menyimpan DaftarBelanja
        System.out.print("Masukkan judul DaftarBelanja: ");
        judul = keyb.nextLine();
        
        db.setJudul(judul);
        db.setTanggal(LocalDateTime.now());
        
        System.out.print("Masukkan jumlah barang dalam list: ");
        int jmlh = Integer.parseInt(keyb.nextLine());
        
        for (int i = 1; i <= jmlh; i++) {
            System.out.print("Masukkan nama barang " + i +": ");
            String nama = keyb.nextLine();
            
            System.out.print("Masukkan banyak barang " + i +": ");
            int bnyk = Integer.parseInt(keyb.nextLine());
            
            System.out.print("Masukkan satuan banyak barang " + i +": ");
            String satuan = keyb.nextLine();
            
            System.out.print("Tulis memo barang " + i +": ");
            String memo = keyb.nextLine();
            
            DaftarBelanjaDetil dbd = new DaftarBelanjaDetil();
            dbd.setNoUrut(i);
            dbd.setNamaBarang(nama);
            dbd.setByk(bnyk);
            dbd.setSatuan(satuan);
            dbd.setMemo(memo);
            
            db.getDaftarBarang().add(dbd);
        }
    }
    
    private void bacaBerdasarkanId(long id){
        Optional<DaftarBelanja> optDB = repo.findById(id);
        if (optDB.isPresent()) {
            DaftarBelanja db = optDB.get();
            System.out.println("\tJudul: " + db.getJudul());
        } else {
            System.out.println("\tTIDAK DITEMUKAN.");
        }
    }
}