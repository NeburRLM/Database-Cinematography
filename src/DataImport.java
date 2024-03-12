import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
public class DataImport {
    private static String driver = "org.mariadb.jdbc.Driver";
    private static String usuario = "root";
    private static String password = "rubenlopez20";
    private static String url = "jdbc:mariadb://localhost:3306/practica1_sio";


    Connection con = null;

    public Connection getConnection() {
        try {
            con = DriverManager.getConnection(url, usuario, password);
            System.out.println("Conectado a MariaDB");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error de conexión");
        }
        return con;
    }

    public void close() {
        try {
            if (con != null) {
                con.close();
                System.out.println("Se cerró la conexión exitosamente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cerrar la conexión");
        }
    }



    // Método para verificar si el registro ya existe
    private boolean checkIfExists(Connection connection, String tabla, String columnName, String columnValue) {
        String sqlCheck = "SELECT COUNT(*) FROM " + tabla + " WHERE " + columnName + " = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(sqlCheck)) {
            checkStatement.setString(1, columnValue);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setNullableInt(PreparedStatement preparedStatement, int index, String value) throws SQLException {
        if (value != null && !value.isEmpty()) {
            preparedStatement.setInt(index, (int) Float.parseFloat(value));
        } else {
            preparedStatement.setNull(index, Types.INTEGER);
        }
    }
    private void setNullableFloat(PreparedStatement preparedStatement, int index, String value) throws SQLException {
        if (value != null && !value.isEmpty()) {
            preparedStatement.setFloat(index,  Float.parseFloat(value));
        } else {
            preparedStatement.setNull(index, Types.FLOAT);
        }
    }
    private void setNullableString(PreparedStatement preparedStatement, int index, String value) throws SQLException {
        if (value != null && !value.isEmpty()) {
            preparedStatement.setString(index, value);
        } else {
            preparedStatement.setNull(index, Types.VARCHAR);
        }
    }
    public boolean isUniqueIdUnique(Connection dbc, String id ) throws SQLException {
            String sql = "SELECT COUNT(*) FROM titles WHERE id_title = ?";
            PreparedStatement preparedStatement= dbc.prepareStatement(sql);
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);
                return count == 0;
            }
    }

    public  void insertarNM(Connection dbc, String table, String column, String value, String table2, String id_title ) throws SQLException {

        String sql = "INSERT INTO "+table+" ("+column+") VALUES (?)";
        PreparedStatement preparedStatement= dbc.prepareStatement(sql);

        if(!checkIfExists(dbc,table, column,value)) {
            preparedStatement.setString(1, value);
            preparedStatement.executeUpdate();
        }
        sql= "SELECT id_"+column+" FROM "+table+" WHERE "+column+" = ?";
        preparedStatement= dbc.prepareStatement(sql);
        preparedStatement.setString(1, value);
        int id=0;
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();
           id = resultSet.getInt(1);
        }
        sql = "SELECT COUNT(*) FROM " + table2 + " WHERE id_title = ? AND id_" + column + " = ?";
        preparedStatement= dbc.prepareStatement(sql);
        preparedStatement.setString(1,id_title);
        preparedStatement.setInt(2,id);

        try (ResultSet resultSet2 = preparedStatement.executeQuery()) {

            resultSet2.next();
            int id2 = resultSet2.getInt(1);
            if (id2 == 0) {
                sql = "INSERT INTO " + table2 + " (id_title, id_" + column + ") VALUES (?, ?)";
                preparedStatement = dbc.prepareStatement(sql);
                preparedStatement.setString(1, id_title);
                preparedStatement.setInt(2, id);
                preparedStatement.executeUpdate();
            }else{
                System.out.println(id_title+" "+column);
            }
        }


    }

     public void control_loop(CSVRecord csvRecord, Connection dbc, String fit) throws SQLException {
         String sql;
         sql = "INSERT INTO titles (id_title, title, type, description, release_year, runtime, seasons, imdb_score, imdb_votes, tmdb_popularity, tmdb_score) "
                 + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
         PreparedStatement preparedStatement;
         String value ;
         String id_title       = csvRecord.get(0);
         if(isUniqueIdUnique(dbc,id_title)) {
             preparedStatement= dbc.prepareStatement(sql);
             preparedStatement.setString(1, id_title);
             value = csvRecord.get(1);

             preparedStatement.setString(2, value);
             value = csvRecord.get(2);
             preparedStatement.setString(3, value);
             value = csvRecord.get(3);
             preparedStatement.setString(4, value);
             value = csvRecord.get(4);

             setNullableInt(preparedStatement, 5, value);

             /*value = csvRecord.get(5);
             preparedStatement.setString(6, value);
             */
             value = csvRecord.get(6);
             setNullableInt(preparedStatement, 6, value);

             /*value = csvRecord.get(7);
             preparedStatement.setString(8, value);

             value = csvRecord.get(8);
             preparedStatement.setString(9, value);
             */
             value = csvRecord.get(9);
             setNullableInt(preparedStatement, 7, value);
             /*
             value = csvRecord.get(10);
             setNullableString(preparedStatement, 11, value);

              */
             value = csvRecord.get(11);
             setNullableFloat(preparedStatement, 8, value);
             value = csvRecord.get(12);
             setNullableInt(preparedStatement, 9, value);
             value = csvRecord.get(13);
             setNullableFloat(preparedStatement, 10, value);
             value = csvRecord.get(14);
             setNullableFloat(preparedStatement, 11, value);

             preparedStatement.executeUpdate();

             value = csvRecord.get(5);
             insertarNM(dbc,"age","age",value,"title_age",id_title);

             value = csvRecord.get(7);
             // Eliminar corchetes al principio y al final
             value = value.substring(1, value.length() - 1);

             // Dividir la cadena en elementos usando comas y espacios como delimitadores
             String[] genArray = value.split(",\\s*");
             String type = "Genres";
             splitFunc(dbc, genArray, id_title, type);



             value = csvRecord.get(8);
             value = value.substring(1, value.length() - 1);
             genArray = value.split(",\\s*");
             type = "Country";
             splitFunc(dbc, genArray, id_title, type);

             insertarNM(dbc,"provider","provider",fit,"title_provider",id_title);


         }
         else
         {
             //System.out.println(id_title+" "+fit);
             insertarNM(dbc,"provider","provider",fit,"title_provider",id_title);
         }
    }


    private void splitFunc(Connection dbc, String[] genArray, String id_title, String type) throws SQLException {
        if(type.equalsIgnoreCase("Genres")){
            for (String gen : genArray) {
                // Eliminar comillas simples al principio y al final de cada elemento
                gen = gen.replaceAll("^\\s*'(.*)'\\s*$", "$1");
                insertarNM(dbc, "genres", "genres", gen, "title_genres", id_title);
            }
        }

        if(type.equalsIgnoreCase("Country")){
            for (String gen : genArray) {
                // Eliminar comillas simples al principio y al final de cada elemento
                gen = gen.replaceAll("^\\s*'(.*)'\\s*$", "$1");
                insertarNM(dbc, "country", "country", gen, "title_country", id_title);
            }
        }

    }

    public boolean isUniqueIdUniquePerson(Connection dbc, int id ) throws SQLException {
        String sql = "SELECT COUNT(*) FROM person WHERE id_person = ?";
        PreparedStatement preparedStatement= dbc.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();
            int count = resultSet.getInt(1);
            return count == 0;
        }
    }

    public boolean isUniqueIdUniqueCharacter(Connection dbc, String charact) throws SQLException {
        String sql = "SELECT COUNT(*) FROM charact WHERE charact = ?";
        PreparedStatement preparedStatement = dbc.prepareStatement(sql);
        preparedStatement.setString(1, charact);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();
            int count = resultSet.getInt(1);
            return count == 0;
        }
    }



    public void control_loop2(CSVRecord csvRecord, Connection dbc, String fit) throws SQLException {
        String sql = "INSERT INTO person (id_person, name) VALUES (?, ?)";
        String sql2 = "INSERT INTO charact (charact) VALUES (?)";
        PreparedStatement preparedStatement;

        int idPerson = Integer.parseInt(csvRecord.get(0));
        System.out.println(idPerson);
        if (isUniqueIdUniquePerson(dbc, idPerson)) {
            preparedStatement = dbc.prepareStatement(sql);
            preparedStatement.setInt(1, idPerson);

            String name = csvRecord.get(2); // Ajusta el índice según el formato real de tu CSV
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        }

        String charact = csvRecord.get(3);
        System.out.println("Character from CSV: " + charact);
        if (isUniqueIdUniqueCharacter(dbc, String.valueOf(charact))) {
            preparedStatement = dbc.prepareStatement(sql2);
            preparedStatement.setString(1, charact);
            preparedStatement.executeUpdate();
        }
    }




    private void insertData2(Connection dbc, String fit) {



            Connection connection = dbc;


                //PreparedStatement preparedStatement = connection.prepareStatement(sql)


            String csvFile = "Dataset/"+ fit +"_Titles.csv";

            try (CSVParser csvParser = new CSVParser(new FileReader(csvFile), CSVFormat.DEFAULT)) {
                int lineNumber = 0;
                for (CSVRecord csvRecord : csvParser) {

                    if(lineNumber!=0) {

                       control_loop(csvRecord,dbc, fit);

                    }

                    lineNumber++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


    }
    private void insertData(Connection dbc, String fit) {

        Connection connection = dbc;


        //PreparedStatement preparedStatement = connection.prepareStatement(sql)


        String csvFile = "Dataset/"+ fit +"_Credits.csv";

        try (CSVParser csvParser = new CSVParser(new FileReader(csvFile), CSVFormat.DEFAULT)) {
            int lineNumber = 0;
            for (CSVRecord csvRecord : csvParser) {

                if(lineNumber!=0) {

                    control_loop2(csvRecord,dbc, fit);

                }

                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }

    public static void main(String[] args) {
        DataImport db = new DataImport();
        Connection dbc = db.getConnection();
        String stream[]={"Amazon_Prime", "Disney_Plus", "HBOMax","Netflix","ParamountTV","Rakuten_ViKi"};
        for(String fit: stream) {
            System.out.println(fit);
            //db.insertData2(dbc, fit);
            db.insertData(dbc, fit);
        }
        // Insertar datos para Titles y Credits de cada proveedor
       /* db.insertData("Amazon_Prime", "Titles", "resources/Dataset/Amazon_Prime_Titles.csv");
        db.insertData("Amazon_Prime", "Credits", "resources/Dataset/Amazon_Prime_Credits.csv");

        db.insertData("Disney_Plus", "Titles", "resources/Dataset/Disney_Plus_Titles.csv");
        db.insertData("Disney_Plus", "Credits", "resources/Dataset/Disney_Plus_Credits.csv");

        db.insertData("HBOMax", "Titles", "resources/Dataset/HBOMax_Titles.csv");
        db.insertData("HBOMax", "Credits", "resources/Dataset/HBOMax_Credits.csv");

        db.insertData("HuluTV", "Titles", "resources/Dataset/HuluTV_Titles.csv");
        db.insertData("HuluTV", "Credits", "resources/Dataset/HuluTV_Credits.csv");

        db.insertData("Netflix", "Titles", "resources/Dataset/Netflix_Titles.csv");
        db.insertData("Netflix", "Credits", "resources/Dataset/Netflix_Credits.csv");

        db.insertData("ParamountTV", "Titles", "resources/Dataset/ParamountTV_Titles.csv");
        db.insertData("ParamountTV", "Credits", "resources/Dataset/ParamountTV_Credits.csv");

        db.insertData("Rakuten_Viki", "Titles", "resources/Dataset/Rakuten_Viki_Titles.csv");
        db.insertData("Rakuten_Viki", "Credits", "resources/Dataset/Rakuten_Viki_Credits.csv");
        */
        db.close();
    }
}
