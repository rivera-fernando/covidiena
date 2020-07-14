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


/*user logs in with school email and their information from the datatstore is printed through json for easy access*/
@WebServlet("/login")
public class LogInServlet extends HttpServlet {

    public User user = null;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();  
        Gson gson = new Gson();
        response.setContentType("application/json");
        if (request.getParameter("logout") != null){
            user = null;
            response.getWriter().println(gson.toJson(user));
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
                    user = new User(
                        (long) entity.getKey().getId(),
                        (String) entity.getProperty("name"),
                        (String) entity.getProperty("email"),
                        (String) entity.getProperty("password"),
                        (String) entity.getProperty("birthdate"),
                        (long) entity.getProperty("studentId"),
                        (String) entity.getProperty("sex"),
                        (String) entity.getProperty("school"),
                        (String) entity.getProperty("phone"),
                        (String) entity.getProperty("metric"), 
                        (Boolean) entity.getProperty("admin")
                    );
                    session.setAttribute("name", user.getName());  
                    session.setAttribute("email", user.getEmail());  
                    session.setAttribute("password", user.getPassword());  
                    session.setAttribute("birthdate", user.getBirthdate());  
                    session.setAttribute("studentId", user.getStudentId());  
                    session.setAttribute("sex", user.getSex());
                    session.setAttribute("school", user.getSchool());
                    session.setAttribute("phone", user.getPhone());
                    session.setAttribute("metric", user.getMetric()); 
                    session.setAttribute("admin", user.getAdmin());        

                    response.getWriter().println(session.getAttribute("person") instanceof User);
                    response.sendRedirect("../dashboard");
                    return;
                }
            }   
            response.sendRedirect("/login.html");
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Gson gson = new Gson();
        HttpSession session = request.getSession();
        response.setContentType("html/text");
        response.getWriter().println(session.getAttribute("person") instanceof User);
    }
}
