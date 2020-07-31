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
import java.util.Random;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.sps.classes.EmailSender;
import com.google.sps.classes.PasswordHash;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;



/*user logs in with school email and their information from the datatstore is printed through json for easy access*/
@WebServlet("/search-account")
public class SearchAccountServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);  
        Gson gson = new Gson();
        response.setContentType("application/json");
        String email = request.getParameter("searchEmail");

        Query query = new Query("User");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        session.setAttribute("accountExists", false);

        for(Entity entity:results.asIterable()){
            if(email.equals(((String)entity.getProperty("email")).toLowerCase())){
                Entity user = null;
                long userId = entity.getKey().getId();
                try {
                    Key userKey = KeyFactory.createKey("User", userId);
                    user = datastore.get(userKey);
                }
                catch ( Exception EntityNotFoundException){
                    return;
                }
                user.setProperty("email", (String) entity.getProperty("email"));
                user.setProperty("name", (String) entity.getProperty("name"));
                user.setProperty("birthdate", (String) entity.getProperty("birthdate"));
                user.setProperty("studentId", (long) entity.getProperty("studentId"));
                user.setProperty("sex", (String) entity.getProperty("sex"));
                user.setProperty("school", (String) entity.getProperty("school"));
                user.setProperty("phone", (String) entity.getProperty("phone"));
                user.setProperty("is_admin", (boolean) entity.getProperty("is_admin"));
                user.setProperty("imageKey", (String) entity.getProperty("imageKey"));
                user.setProperty("state", (String) entity.getProperty("state"));

                String password = getRandomString();

                user.setProperty("password", PasswordHash.hashPassword((password).toCharArray()));

                datastore.put(user);

                EmailSender.sendEmail(email, password);
                session.setAttribute("accountExists", true);
                response.sendRedirect("/login.html");
                return;
            }
        }
        session.setAttribute("accountExists", false);
        response.sendRedirect("/signup.html");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);  
        Gson gson = new Gson();
        response.setContentType("application/json");
        Boolean accountExists = (boolean) session.getAttribute("accountExists");

        response.getWriter().println(gson.toJson(accountExists));
    }

    public String getRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
    
        String generatedString = random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    
        return generatedString;
    }
}
