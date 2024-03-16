import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class Neo {
    private final Driver driverNeo;
    public Neo(String uri, String user, String password) {
        this.driverNeo = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void close() {
        this.driverNeo.close();
    }

    // Mètode per executar consultes Cypher

    public void runQuery(String cypherQuery) {
        try (org.neo4j.driver.Session session = driverNeo.session()) {
            session.writeTransaction(tx -> {
                tx.run(cypherQuery);
                return null;
            });
        }
    }

    // Mètodo per importar dades de la base de dades MariaDB a Neo4j
    public void importDataFromMariaDB(Connection connection) {

        try {
            // Consulta SQL per obtenir informació de les persones i les pel·lícules/sèries en què han participat
            String query = "SELECT p.id_person, p.name, t.id_title, t.title, t.type, t.release_year, r.role, c.charact, GROUP_CONCAT(DISTINCT g.genres) AS genres, GROUP_CONCAT(DISTINCT co.country) AS countries " +
                    "FROM person p " +
                    "JOIN info_persons ip ON p.id_person = ip.id_person " +
                    "JOIN titles t ON ip.id_title = t.id_title " +
                    "JOIN role r ON ip.id_info = r.id_info " +
                    "JOIN charact c ON r.id_character = c.id_character " +
                    "JOIN title_genres tg ON t.id_title = tg.id_title " +
                    "JOIN genres g ON tg.id_genres = g.id_genres " +
                    "JOIN title_country tc ON t.id_title = tc.id_title " +
                    "JOIN country co ON tc.id_country = co.id_country " +
                    "GROUP BY t.id_title";


            // Executar la consulta SQL
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Recórrer el resultat i crear nodes per a persones i pel·lícules/sèries a Neo4j
            while (resultSet.next()) {
                String personId = resultSet.getString("id_person");
                String personName = resultSet.getString("name");
                String titleId = resultSet.getString("id_title");
                String title = resultSet.getString("title");
                String type = resultSet.getString("type");
                int releaseYear = resultSet.getInt("release_year");
                String role = resultSet.getString("role");
                String charact = resultSet.getString("charact");
                String[] genresArray = resultSet.getString("genres").split(",");
                List<String> genresList = Arrays.asList(genresArray);
                String[] countriesArray = resultSet.getString("countries").split(",");
                List<String> countriesList = Arrays.asList(countriesArray);

                // Crear node de persona en Neo4j
                runQuery(String.format("MERGE (p:Person {id: '%s', name: '%s'})", personId, personName.replace("'", "\\'")));

                // Crear node de pel·lícula/sèries en Neo4j
                runQuery(String.format("MERGE (t:%s {id: '%s', title: '%s', release_year: %d, genres: '%s', countries: '%s'})", type, titleId, title.replace("'", "\\'"), releaseYear, genresList, countriesList));


                // Determinar el tipus de relació segons el paper de la persona en la pel·lícula/serie
                String relationshipType = getRelationshipType(role);

                // Crear relació entre persona y pel·lícula/serie en Neo4j amb el personatge
                runQuery(String.format("MATCH (p:Person {id: '%s'}), (t:%s {id: '%s'}) MERGE (p)-[:%s {character: '%s'}]->(t)", personId, type, titleId, relationshipType, charact.replace("'", "\\'")));
            }



            System.out.println("Datos importados correctamente desde la base de datos MariaDB a Neo4j.");
        } catch (SQLException e) {
            System.err.println("Error al importar datos desde la base de datos MariaDB a Neo4j: " + e.getMessage());
        }
    }

    // Mètode per determinar el tipus de relació segons el paper de la persona a la pel·lícula/sèrie
    private String getRelationshipType(String role) {
        if (role.equalsIgnoreCase("ACTOR")) {
            return "ACTED_IN";
        } else if (role.equalsIgnoreCase("DIRECTOR")) {
            return "DIRECTED";
        } else {
            return "";
        }
    }


    public static void main(String[] args) {
        // URL de connexió a la base de dades Neo4j
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "rubenlopez20";

        String mariadbUrl = "jdbc:mariadb://localhost:3306/practica1_sio";
        String mariadbUser = "root";
        String mariadbPassword = "rubenlopez20";


        Connection mariadbConnection = null;

        // Crear una instància de Neo4jConnector amb la URL de connexió i les credencials
        Neo connector = new Neo(uri, user, password);

        // Crear una connexió a la base de dades MariaDB
        try{
            mariadbConnection = DriverManager.getConnection(mariadbUrl, mariadbUser, mariadbPassword);

            // Importar dades des de la base de dades MariaDB a Neo4j
            connector.importDataFromMariaDB(mariadbConnection);
        } catch (Exception e) {
            System.err.println("Error al conectar a la base de datos Neo4j: " + e.getMessage());
        } finally {
            // Tanca la connexió
            if (mariadbConnection != null) {
                try {
                    mariadbConnection.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión a la base de datos MariaDB: " + e.getMessage());
                }
            }
            connector.close();
        }
    }
}

