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

    public User user = null;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();  
        Gson gson = new Gson();
        response.setContentType("application/json");
        // First if handles log out
        if (request.getParameter("logout") != null ) {
          session.removeAttribute("name");  
          session.removeAttribute("email");  
          session.removeAttribute("password");  
          session.removeAttribute("birthdate");  
          session.removeAttribute("studentId");  
          session.removeAttribute("sex");
          session.removeAttribute("school");
          session.removeAttribute("phone");
          session.removeAttribute("metric"); 
          session.removeAttribute("is_admin");    
          session.removeAttribute("imageKey");

          response.sendRedirect("/index.html");
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
                    session.setAttribute("metric", (String) entity.getProperty("metric")); 
                    session.setAttribute("is_admin", (boolean) entity.getProperty("is_admin"));      
                    session.setAttribute("imageKey", (String) entity.getProperty("imageKey"));  

                    response.sendRedirect("/dashboard");
                    return;
                }
            }   
            response.sendRedirect("/login.html");
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Gson gson = new Gson();
        HttpSession session = request.getSession(false);
        Boolean found = false;
        if (session.getAttribute("name") != null) {
            found = true;
        }
        List<Boolean> log = new ArrayList<Boolean>();
        log.add(found);

        response.setContentType("application/loggedIn");
        response.getWriter().println(log);
    }
}
