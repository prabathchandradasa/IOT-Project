package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Tdata;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import model.HibernateUtil;

@WebServlet(name = "DataLoad", urlPatterns = {"/DataLoad"})
public class DataLoad extends HttpServlet {
     private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
        // Set the response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
 //String uid= request.getParameter("user");
        // Create a session to interact with the database
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            // Create a Criteria object
            Criteria criteria = session.createCriteria(Tdata.class); 
            
            // Fetch all rows where user_id = 1
            criteria.add(Restrictions.eq("user.id", 1)); // Ensure this fetches rows for user with id = 1

            // Fetch the data
            List<Tdata> tdataList = criteria.list();

            // Create a list to store the JSON objects
            JsonArray jsonArray = new JsonArray();

            // Loop through the data and convert to JSON
            for (Tdata tdata : tdataList) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", tdata.getId());
                jsonObject.addProperty("temperature", tdata.getTemperature());
                jsonObject.addProperty("humidity", tdata.getHumidity());
                jsonObject.addProperty("date", tdata.getDate().toString());

                // Add to the JSON array
                jsonArray.add(jsonObject);
            }

            // Write the JSON response
            PrintWriter out = response.getWriter();
            out.print(jsonArray.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            session.close();
        }
    }
}
