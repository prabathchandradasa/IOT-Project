package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Tdata;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDateTime;
import java.sql.Timestamp;

@WebServlet(name = "DataSave", urlPatterns = {"/DataSave"})
public class DataSave extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Initialize Gson to parse JSON
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject(); // Response JSON object
        response.setContentType("application/json"); // Set response type to JSON
        response.setCharacterEncoding("UTF-8");

        // Open a new Hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null; // Transaction variable to handle commit/rollback

        try {
            // Read JSON payload from the request body
            JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);

            // Extract values from the JSON object
            String temperature = requestJson.get("temperature").getAsString();
            String humidity = requestJson.get("humidity").getAsString();
            String userId = requestJson.get("userId").getAsString();

            // Print extracted data for debugging
            System.out.println("Temperature: " + temperature);
            System.out.println("Humidity: " + humidity);
            System.out.println("User ID: " + userId);

            // Start a new Hibernate transaction
            transaction = session.beginTransaction();

            // Create a new Tdata object and set its properties
            Tdata tdata = new Tdata();
            tdata.setTemperature(Double.parseDouble(temperature));
            tdata.setHumidity(Double.parseDouble(humidity));
            // දැක්කාම කලාවේ LocalDateTime ලබා ගන්නවා
            LocalDateTime currentDateTime = LocalDateTime.now();

// LocalDateTime එක Timestamp එකකට පරිවර්තනය කරන්න
            Timestamp timestamp = Timestamp.valueOf(currentDateTime);
            tdata.setDate(timestamp); // Setting the current date and time

            // Fetch the user based on the userId received in the request
            User user = (User) session.get(User.class, Integer.parseInt(userId));
            if (user != null) {
                tdata.setUser(user); // Set the user in Tdata
            } else {
                throw new Exception("User not found with ID: " + userId);
            }

            // Save the Tdata object to the database
            session.save(tdata);

            // Commit the transaction to persist the data
            transaction.commit();

            // Respond back with a success message
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Data saved successfully!");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback the transaction if an error occurs
            }
            e.printStackTrace(); // Print stack trace for debugging

            // Respond back with an error message
            responseJson.addProperty("success", false);
            responseJson.addProperty("message", "Error saving data: " + e.getMessage());
        } finally {
            session.close(); // Close the Hibernate session
        }

        // Send the response as a JSON object
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(responseJson));
        out.flush(); // Ensure the response is sent
    }
}
