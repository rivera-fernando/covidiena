package com.google.sps.servlets;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
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
import com.google.sps.data.User;
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


@WebServlet("/login")
public class LogInServlet extends HttpServlet {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();

    /*
    directs user to log in with gmail and prints with gson the email, admin permission, and log out/in url
    */

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");
        UserService userService = UserServiceFactory.getUserService();
        Gson gson = new Gson();
        ArrayList<String> userInfo = new ArrayList<String>();

        Query query = new Query("User");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<String> users = new ArrayList<String>();
        for(Entity entity:results.asIterable()){
            users.add(((String) entity.getProperty("email")).toLowerCase());
        }

        if (userService.isUserLoggedIn()) {
            String userEmail = userService.getCurrentUser().getEmail().toLowerCase();
            String urlToRedirectToAfterUserLogsOut = "/";
            String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
            userInfo.add(userEmail);
            userInfo.add(logoutUrl);
            if(users.contains(userEmail)){
                userInfo.add("true");
            }else{
                userInfo.add(null);
            }
            response.getWriter().println(gson.toJson(userInfo));

        } else {
            String urlToRedirectToAfterUserLogsIn = "/dashboard";
            String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
            userInfo.add(null);
            userInfo.add(loginUrl);
            userInfo.add(null);
            response.getWriter().println(gson.toJson(userInfo));
        }
    }
}
