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
import javax.servlet.http.HttpSession;


/*user requests admin access which creates a new entity pending approval*/
@WebServlet("/request-admin")
public class RequestAdminServlet extends HttpServlet {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession(false); 

        String name = (String) session.getAttribute("name");
        String email = (String) session.getAttribute("email");
        boolean isAdmin = (boolean) session.getAttribute("admin");
        if (!isAdmin) {
        Query query = new Query("RequestAdmin");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<String> users = new ArrayList<String>();
        for (Entity entity:results.asIterable()){
            if (entity.getProperty("email").equals(email)){
                return;
            }
        }

        Entity requestAdminEntity = new Entity("RequestAdmin");
        requestAdminEntity.setProperty("name", name);
        requestAdminEntity.setProperty("email", email);
        requestAdminEntity.setProperty("status", "pending");

        datastore.put(requestAdminEntity);
        response.sendRedirect("/login.html");
        }
        else {
        String pending_email = request.getParameter("email");
        String action = request.getParameter("action");
        Query query = new Query("RequestAdmin");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<String> users = new ArrayList<String>();
        for (Entity entity:results.asIterable()){
            if (entity.getProperty("email").equals(pending_email)){
                datastore.delete(entity.getKey());
            }
        }
        if (action.equals("approve")) {
            query = new Query("User");
            results = datastore.prepare(query);
            for (Entity entity:results.asIterable()){
                if (entity.getProperty("email").equals(pending_email)){
                    entity.setProperty("admin", true);
                    datastore.put(entity);
                }
            }
        }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Query query = new Query("RequestAdmin");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<Object> users = new ArrayList<Object>();
        for (Entity entity:results.asIterable()){
            ArrayList<String> user = new ArrayList<String>();
            if (entity.getProperty("status").equals("pending")){
                user.add((String) entity.getProperty("name"));
                user.add((String) entity.getProperty("email"));
            }
            users.add(user);
        }
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(users));
    }
}
