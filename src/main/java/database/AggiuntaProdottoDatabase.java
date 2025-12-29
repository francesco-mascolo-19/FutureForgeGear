package database;

import java.io.*;
import java.sql.*;

public class AggiuntaProdottoDatabase {

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        //Class.forName("com.mysql.cj.jdbc.Driver");
        //String jdbcURL = "jdbc:mysql://localhost:3306/ingrosso";
        String jdbcURL = "jdbc:mysql://localhost:3306/ingrosso?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String dbPassword = "root";

        Connection connection = null;
        PreparedStatement statement = null;
        FileInputStream fis = null;
        File imageFile=null;

        try {
            connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
            String sql = "INSERT INTO Prodotto (idProdotto, Quantita, Prezzo, Nome, Descrizione, Categoria, Sconto, Foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 1);
            statement.setInt(2, 3);
            statement.setDouble(3, 1299.99);
            statement.setString(4, "Computer Gaming Ryzen 7 - RTX 4060");
            statement.setString(5, "PC da gaming ad alte prestazioni con processore Ryzen 7 di ultima generazione, ideale per giochi AAA in Full HD e 2K. Raffreddamento silenzioso e case RGB.");
            statement.setString(6, "Fissi");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/pcfisso/pcfisso3.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();



            statement = connection.prepareStatement(sql);
            statement.setInt(1, 2);
            statement.setInt(2, 5);
            statement.setDouble(3, 649.99);
            statement.setString(4, "Computer da Ufficio Intel i5");
            statement.setString(5, "Desktop affidabile, silenzioso e a basso consumo, perfetto per studio, smart working e software da ufficio. Avvio rapido e massima stabilità.");
            statement.setString(6, "Fissi");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/pcfisso/pcfisso5.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();


            statement = connection.prepareStatement(sql);
            statement.setInt(1, 3);
            statement.setInt(2, 2);
            statement.setDouble(3, 1799.99);
            statement.setString(4, "Workstation Creativa Ryzen 9");
            statement.setString(5, "Potente workstation progettata per editing video, rendering 3D e grafica professionale. Elevate prestazioni multi-core e memoria ad alta velocità.");
            statement.setString(6, "Fissi");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/pcfisso/pcfisso4.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();


            statement = connection.prepareStatement(sql);
            statement.setInt(1, 4);
            statement.setInt(2, 4);
            statement.setDouble(3, 499.99);
            statement.setString(4, "Computer Compatto Mini-ITX");
            statement.setString(5, "PC compatto adatto a casa e ufficio, veloce e pratico, con consumi ridotti. Perfetto per navigazione, streaming e applicazioni leggere.");
            statement.setString(6, "Fissi");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/pcfisso/pcfisso6.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            //----------------------------------

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 5);
            statement.setInt(2, 12);
            statement.setDouble(3, 1099.99);
            statement.setString(4, "Laptop Ultrabook");
            statement.setString(5, "Ultrabook leggero e super portatile, ideale per università, lavoro in mobilità e multitasking veloce. Batteria a lunga durata e display Full HD.");
            statement.setString(6, "Portatili");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/pcportatili/pcportatile3.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();


            statement = connection.prepareStatement(sql);
            statement.setInt(1, 6);
            statement.setInt(2, 7);
            statement.setDouble(3, 899.99);
            statement.setString(4, "Notebook Gaming Ryzen 5");
            statement.setString(5, "Laptop da gaming entry-level perfetto per giochi competitivi e utilizzo misto. Ottimo rapporto qualità-prezzo.");
            statement.setString(6, "Portatili");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/pcportatili/pcportatile4.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();


            statement = connection.prepareStatement(sql);
            statement.setInt(1, 7);
            statement.setInt(2, 8);
            statement.setDouble(3, 1499.99);
            statement.setString(4, "Laptop Professionale Intel i9");
            statement.setString(5, "Notebook ad alte prestazioni per professionisti creativi. Ottimo per editing video, Photoshop, modeling 3D e rendering leggero.");
            statement.setString(6, "Portatili");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/pcportatili/pcportatile5.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 8);
            statement.setInt(2, 3);
            statement.setDouble(3, 349.99);
            statement.setString(4, "Chromebook Student");
            statement.setString(5, "Portatile economico e veloce per studenti, navigazione web, streaming e studio online. Avvio istantaneo e batteria molto lunga.");
            statement.setString(6, "Portatili");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/pcportatili/pcportatile6.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            //----------------------------------

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 9);
            statement.setInt(2, 5);
            statement.setDouble(3, 379.99);
            statement.setString(4, "Scheda Video NVIDIA RTX 4060 8GB");
            statement.setString(5, "GPU moderna ed efficiente, ideale per gaming 1080p/1440p, editing video e rendering 3D leggero. Consumi ridotti e ottimo rapporto qualità-prezzo.");
            statement.setString(6, "Componenti");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/componenti/componenti1.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 10);
            statement.setInt(2, 7);
            statement.setDouble(3, 259.99);
            statement.setString(4, "Processore AMD Ryzen 7 5800X");
            statement.setString(5, "CPU 8 core / 16 thread perfetta per gaming competitivo, streaming e produttività. Compatibile con piattaforma AM4.");
            statement.setString(6, "Componenti");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/componenti/componenti2.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 11);
            statement.setInt(2, 3);
            statement.setDouble(3, 89.99);
            statement.setString(4, "SSD NVMe 1TB PCIe 4.0");
            statement.setString(5, "Unità NVMe ultraveloce con letture fino a 5000 MB/s. Ideale per Windows, programmi pesanti e caricamenti rapidissimi.");
            statement.setString(6, "Componenti");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/componenti/componenti3.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 12);
            statement.setInt(2, 1);
            statement.setDouble(3, 59.99);
            statement.setString(4, "RAM DDR4 16GB (2×8GB) 3200MHz");
            statement.setString(5, "Kit prestante e compatibile con la maggior parte delle schede madri. Migliora notevolmente reattività e multitasking.");
            statement.setString(6, "Componenti");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/componenti/componenti4.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            //--------------------

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 13);
            statement.setInt(2, 10);
            statement.setDouble(3, 29.99);
            statement.setString(4, "Mouse Gaming RGB");
            statement.setString(5, "Mouse ergonomico con sensore da 12.000 DPI regolabili, illuminazione RGB e 6 tasti programmabili. Perfetto per gaming e lavoro.");
            statement.setString(6, "Periferiche");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/periferiche/periferiche3.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 14);
            statement.setInt(2, 2);
            statement.setDouble(3, 59.99);
            statement.setString(4, "Tastiera Meccanica");
            statement.setString(5, "Tastiera meccanica compatta con switch rossi silenziosi, costruita in alluminio e retroilluminazione LED a più livelli.");
            statement.setString(6, "Periferiche");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/periferiche/periferiche4.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 15);
            statement.setInt(2, 5);
            statement.setDouble(3, 69.99);
            statement.setString(4, "Cuffie Wireless");
            statement.setString(5, "Cuffie over-ear con audio surround virtuale, microfono removibile e batteria fino a 20 ore. Compatibili PC/Console.");
            statement.setString(6, "Periferiche");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/periferiche/periferiche5.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            statement = connection.prepareStatement(sql);
            statement.setInt(1, 16);
            statement.setInt(2, 12);
            statement.setDouble(3, 159.99);
            statement.setString(4, "Monitor 24'' FullHD");
            statement.setString(5, "Monitor a 144Hz con pannello IPS, colori vividi e bassa latenza. Ideale per gaming fluido e produttività.");
            statement.setString(6, "Periferiche");
            statement.setDouble(7, 0);
            imageFile = new File("src/main/resources/images/periferiche/periferiche6.jpg");
            fis = new FileInputStream(imageFile);
            statement.setBinaryStream(8, fis, (int) imageFile.length());
            statement.executeUpdate();
            fis.close();

            connection.close();
            System.out.println("Prodotti aggiunti");

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}