package com.google.sps.servlets;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import javax.servlet.http.HttpSession;


/*user requests admin access which creates a new entity pending approval*/
@WebServlet("/request-admin")
public class RequestAdminServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Boolean found = Boolean.parseBoolean(request.getParameter("found"));
        HttpSession session = request.getSession(false);
        Query query = new Query("RequestAdmin");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity:results.asIterable()){
            if (entity.getProperty("email").equals(session.getAttribute("email"))){
                return;
            }
        }

        String name = (String)session.getAttribute("name");
        String email = (String)session.getAttribute("email");
        
        Entity requestAdminEntity = new Entity("RequestAdmin");
        requestAdminEntity.setProperty("name", name);
        requestAdminEntity.setProperty("email", email);
        requestAdminEntity.setProperty("status", "pending");

        datastore.put(requestAdminEntity);
        response.sendRedirect("/login.html");
    }
}
