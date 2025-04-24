/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_Status;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        //JsonObject requestJson= gson.fromJson(request.getReader(),JsonObject.class);
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        String firstname = request.getParameter("firstName");
        String lastname = request.getParameter("lastName");
        String mobile = request.getParameter("mobile");
        String password = request.getParameter("password");
      
        
        if (firstname.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Your First Name");
        } else if (lastname.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Your Last Name");
        } else if (mobile.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Your Mobile Number");
        } else if (!Validations.isMobileNumberValid(mobile)) {
            responseJson.addProperty("message", "Invalid Mobile Number");
        } else if (password.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Your Password");
        } else if (!Validations.isPasswordValid(password)) {
            responseJson.addProperty("message", "Invalid Password");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            //search mobile number
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("mobile", mobile));

            if (!criteria.list().isEmpty()) {
                //mobile number alreasy use
                responseJson.addProperty("message", "Mobile Number Already Used");
            } else {

                User user = new User();
                user.setFirst_name(firstname);
                user.setLast_name(lastname);
                user.setMobile(mobile);
                user.setPassword(password);
                user.setRegistered_date_time(new Date());


                session.save(user);
                session.beginTransaction().commit();


                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "Registration Complete");

                session.close();

            }

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
