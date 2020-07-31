package com.google.sps.servlets;

import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.classes.User;
import com.google.sps.classes.PasswordHash;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/*user logs in with school email and their information from the datatstore is printed through json for easy access*/
@WebServlet("/login")
public class LogInServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);  
        Gson gson = new Gson();
        response.setContentType("application/json");
        // First if handles log out
        if (request.getParameter("logout") != null ) {
            session.removeAttribute("userId");
            session.removeAttribute("name");  
            session.removeAttribute("email");  
            session.removeAttribute("password");  
            session.removeAttribute("birthdate");  
            session.removeAttribute("studentId");  
            session.removeAttribute("sex");
            session.removeAttribute("school");
            session.removeAttribute("phone");
            session.removeAttribute("is_admin");      
            session.removeAttribute("imageKey"); 
            session.removeAttribute("state");
            session.removeAttribute("diagnosed");

            response.sendRedirect("/login.html");
            response.getWriter().flush();
            return;
      } else {
            String password = PasswordHash.hashPassword(request.getParameter("password").toCharArray());
            String email = request.getParameter("email").toLowerCase();
            Query query = new Query("User");

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            PreparedQuery results = datastore.prepare(query);
            ArrayList<String> users = new ArrayList<String>();
            for (Entity entity:results.asIterable()){
                if (entity.getProperty("password").equals(password) && entity.getProperty("email").equals(email)){
                    session.setAttribute("userId", (long) entity.getKey().getId());
                    session.setAttribute("name", (String) entity.getProperty("name"));  
                    session.setAttribute("email", ((String) entity.getProperty("email")).toLowerCase());  
                    session.setAttribute("password", (String) entity.getProperty("password"));  
                    session.setAttribute("birthdate", (String) entity.getProperty("birthdate"));  
                    session.setAttribute("studentId", (long) entity.getProperty("studentId"));  
                    session.setAttribute("sex", (String) entity.getProperty("sex"));
                    session.setAttribute("school", (String) entity.getProperty("school"));
                    session.setAttribute("phone", (String) entity.getProperty("phone"));
                    session.setAttribute("is_admin", (boolean) entity.getProperty("is_admin"));      
                    session.setAttribute("imageKey", (String) entity.getProperty("imageKey"));
                    session.setAttribute("state", (String) entity.getProperty("state")); 
                    session.setAttribute("diagnosed", (String) entity.getProperty("diagnosed")); 

                    response.sendRedirect("/dashboard");
                    return;
                }
          }   
          response.sendRedirect("/login.html");
      }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(false);
    List<User> users = new ArrayList<User>();
    User user = null;
    if (session.getAttribute("name") != null) {
        long id = (long) session.getAttribute("userId");
        String name = (String) session.getAttribute("name");
        String email = (String) session.getAttribute("email");
        String password = (String) session.getAttribute("password");
        String birthdate = (String) session.getAttribute("birthdate");
        long studentId = (long) session.getAttribute("studentId");
        String sex = (String) session.getAttribute("sex");
        String school = (String) session.getAttribute("school");
        String phone = (String) session.getAttribute("phone");
        boolean is_admin = (boolean) session.getAttribute("is_admin");
        String imageKey = (String) session.getAttribute("imageKey");
        String state = (String) session.getAttribute("state");
        String diagnosed = (String) session.getAttribute("diagnosed");
        user = new User(id, name, email, password, birthdate, studentId, sex, school, phone, is_admin, imageKey, state, diagnosed);
    }

    Gson gson = new Gson();
    response.setContentType("application/json");

    users.add(user);
    response.getWriter().println(gson.toJson(users));
  }
}
