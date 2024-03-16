import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class DataImport {
    Connection con = null;

    public Connection getConnection() {
        try {
            //private static String driver = "org.mariadb.jdbc.Driver";
            String usuario = "root";
            String password = "rubenlopez20";
            String url = "jdbc:mariadb://localhost:3306/practica1_sio";
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

        String sql;
        PreparedStatement preparedStatement;

        if(!checkIfExists(dbc,table, column,value)) {
            sql = "INSERT INTO "+table+" ("+column+") VALUES (?)";
            preparedStatement= dbc.prepareStatement(sql);
            preparedStatement.setString(1, value);
            preparedStatement.executeUpdate();
        }
        sql= "SELECT id_"+column+" FROM "+table+" WHERE "+column+" = ?";
        preparedStatement= dbc.prepareStatement(sql);
        preparedStatement.setString(1, value);
        int id;
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
            }
        }


    }

    public void control_loop(CSVRecord csvRecord, Connection dbc, String fit) throws SQLException {

        String sql = "INSERT INTO titles (id_title, title, type, description, release_year, runtime, seasons, imdb_score, imdb_votes, tmdb_popularity, tmdb_score) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement;
        String value ;
        String id_title = csvRecord.get(0);
        //n(id_title);
        if(isUniqueIdUnique(dbc,id_title)) {

            preparedStatement= dbc.prepareStatement(sql);
            preparedStatement.setString(1, id_title);
            preparedStatement.setString(2, csvRecord.get(1));
            preparedStatement.setString(3, csvRecord.get(2));
            preparedStatement.setString(4, csvRecord.get(3));
            setNullableInt(preparedStatement, 5,  csvRecord.get(4));
            setNullableInt(preparedStatement, 6, csvRecord.get(6));
            setNullableInt(preparedStatement, 7, csvRecord.get(9));
            setNullableFloat(preparedStatement, 8, csvRecord.get(11));
            setNullableInt(preparedStatement, 9, csvRecord.get(12));
            setNullableFloat(preparedStatement, 10, csvRecord.get(13));
            setNullableFloat(preparedStatement, 11, csvRecord.get(14));
            preparedStatement.executeUpdate();



            insertarNM(dbc,"age","age",csvRecord.get(5),"title_age",id_title);

            value = csvRecord.get(7);
            value = value.substring(1, value.length() - 1);
            String[] genArray = value.split(",\\s*");
            splitFunc(dbc, genArray, id_title,"Genres" );



            value = csvRecord.get(8);
            value = value.substring(1, value.length() - 1);
            genArray = value.split(",\\s*");
            splitFunc(dbc, genArray, id_title,"Country" );


            insertarNM(dbc,"provider","provider",fit,"title_provider",id_title);


        }
        else
        {
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
                if (gen.equalsIgnoreCase("United States of America")){
                    gen="USA";
                }
                if (gen.equalsIgnoreCase("Lebanon")){
                    gen="LB";
                }

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


    private boolean checkIfExists2(Connection connection, String tabla, String columnName, int columnValue, String id_title) {
        String sqlCheck = "SELECT COUNT(*) FROM " + tabla + " WHERE " + columnName + " = ? AND id_title = ?";

        try (PreparedStatement checkStatement = connection.prepareStatement(sqlCheck)) {
            checkStatement.setInt(1, columnValue);
            checkStatement.setString(2, id_title);
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

    public  void insertNMPerson(Connection dbc, String table, String column, String column2, int value, String table2, String id_title, int charactId, String role) throws SQLException {

        String sql = "INSERT INTO " + table + " ("+column+", "+column2+") VALUES (?, ?)";
        PreparedStatement preparedStatement= dbc.prepareStatement(sql);

        if(!checkIfExists2(dbc,table, column, value, id_title)) {
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, id_title);
            preparedStatement.executeUpdate();
        }
        sql= "SELECT id_info FROM "+table+" WHERE id_title = ? AND id_person = ?";
        preparedStatement= dbc.prepareStatement(sql);
        preparedStatement.setString(1, id_title);
        preparedStatement.setInt(2, value);
        int id;
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();
            id = resultSet.getInt(1);
        }
        sql = "SELECT COUNT(*) FROM " + table2 + " WHERE id_info = ? AND id_character = ? AND role = ?";
        preparedStatement= dbc.prepareStatement(sql);
        preparedStatement.setInt(1,id);
        preparedStatement.setInt(2, charactId);
        preparedStatement.setString(3, role);

        try (ResultSet resultSet2 = preparedStatement.executeQuery()) {

            resultSet2.next();
            int id2 = resultSet2.getInt(1);
            if (id2 == 0) {
                sql = "INSERT INTO " + table2 + " (id_info, id_character, role) VALUES (?, ?, ?)";
                preparedStatement= dbc.prepareStatement(sql);
                preparedStatement.setInt(1,id);
                preparedStatement.setInt(2, charactId);
                preparedStatement.setString(3, role);
                preparedStatement.executeUpdate();

            }
        }


    }


    //19066
    public void control_loop2(CSVRecord csvRecord, Connection dbc) throws SQLException {
        String sql = "INSERT INTO person (id_person, name) VALUES (?, ?)";
        String sql2 = "INSERT INTO charact (charact) VALUES (?)";
        PreparedStatement preparedStatement;

        int idPerson = Integer.parseInt(csvRecord.get(0));
        //System.out.println(idPerson);
        if (isUniqueIdUniquePerson(dbc, idPerson)) {
            preparedStatement = dbc.prepareStatement(sql);
            preparedStatement.setInt(1, idPerson);

            String name = csvRecord.get(2); // Ajusta el índice según el formato real de tu CSV
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        }

        String charact = csvRecord.get(3);
        String[] partes = charact.split("[,/]");
        String idTitle = csvRecord.get(1);
        String role = csvRecord.get(4);
        for (int i =0; i<partes.length; i++){
            partes[i]=partes[i].trim();
            if (isUniqueIdUniqueCharacter(dbc, String.valueOf(partes[i]))) {
                preparedStatement = dbc.prepareStatement(sql2);
                preparedStatement.setString(1, partes[i]);
                preparedStatement.executeUpdate();
            }
            String sql4 = "SELECT id_character FROM charact WHERE charact = ?";
            PreparedStatement statement2 = dbc.prepareStatement(sql4);
            statement2.setString(1, partes[i]);
            int idInfo ;
            try (ResultSet resultSet = statement2.executeQuery()) {
                resultSet.next();
                idInfo = resultSet.getInt(1);
            }
            insertNMPerson(dbc, "info_persons", "id_person", "id_title", idPerson, "role", idTitle, idInfo, role);

        }



    }




    private void insertData2(Connection dbc, String fit) {

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

        String csvFile = "Dataset/"+ fit +"_Credits.csv";

        try (CSVParser csvParser = new CSVParser(new FileReader(csvFile), CSVFormat.DEFAULT)) {

            int lineNumber = 0;
            for (CSVRecord csvRecord : csvParser) {

                if(lineNumber!=0) {
                    control_loop2(csvRecord,dbc);
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
        String[] stream ={"HBOMax", "Disney_Plus", "Amazon_Prime","Netflix","ParamountTV","Rakuten_ViKi", "HuluTV"};
        for(String fit: stream) {
            System.out.println(fit);
            db.insertData2(dbc, fit);
            db.insertData(dbc, fit);
        }
        db.close();
    }
}


