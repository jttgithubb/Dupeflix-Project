import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movies"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:/comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage. 
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();
            
            // Query accessing the  top 20 rated movies
            String query = "select m.id, m.title, m.year, m.director, r.rating " + 
                            "from movies as m join ratings as r on m.id = r.movieId " +
                            "order by r.rating DESC " +
                            "limit 20";
            // Query accessing the genres of each movie
            String query2 = "select gm.movieId, gm.genreId, g.name " +
                            "from movies as m join genres_in_movies as gm on m.id = gm.movieId " +
                            "join genres as g on gm.genreId = g.id " +
                            "where m.id = ?";
            // Query accessing the stars of each movie
            String query3 = "select sm.starId, sm.movieId, s.name " +
                            "from stars_in_movies as sm join stars as s on sm.starId = s.id " +
                            "where sm.movieId = ?";
            
            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            // Create a JsonArray object containing the data of each movie
            JsonArray jsonArray = new JsonArray();  

            // Iterate through each row of rs which is a movie
            while (rs.next()) {
                JsonObject movieObject = new JsonObject();

                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                int movie_year = rs.getInt("year");
                String director = rs.getString("director"); 
                float rating = rs.getFloat("rating");

                PreparedStatement genres_statement = conn.prepareStatement(query2);
                genres_statement.setString(1, movie_id);
                ResultSet rs2 = genres_statement.executeQuery();
                JsonArray genres = new JsonArray();
                while (rs2.next()) {
                    String genre_name = rs2.getString("name");
                    genres.add(genre_name);
                }
                rs2.close();
                genres_statement.close();

                PreparedStatement stars_statement = conn.prepareStatement(query3);
                stars_statement.setString(1, movie_id); 
                ResultSet rs3 = stars_statement.executeQuery();
                JsonArray stars = new JsonArray();
                JsonArray stars_id = new JsonArray();
                while (rs3.next()) {
                    String star_name = rs3.getString("name");
                    stars.add(star_name);
                    String star_id = rs3.getString("starId");
                    stars_id.add(star_id);
                }
                rs3.close();
                stars_statement.close();

                movieObject.addProperty("movie_id", movie_id);
                movieObject.addProperty("movie_title", movie_title);
                movieObject.addProperty("movie_year", movie_year);
                movieObject.addProperty("movie_director", director);
                movieObject.addProperty("movie_rating", rating);
                movieObject.add("movie_genres", genres);
                movieObject.add("movie_stars", stars);
                movieObject.add("stars_id", stars_id);

                jsonArray.add(movieObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log    
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);    

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally { 
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources
        
    }
}
