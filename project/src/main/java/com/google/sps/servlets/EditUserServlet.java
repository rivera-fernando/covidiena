package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.classes.User;
import com.google.sps.classes.PasswordHash;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Hex;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/*gets the user's userId and edits their entity in the datastore and then redirects them to login again*/
@WebServlet("/edit-user")
public class EditUserServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Query query = new Query("User");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        String userEmail = request.getParameter("userEmail");
        User oldInfo = null;

        long userId = -1;
        for(Entity entity : results.asIterable()){
            if(userEmail.equals((String)entity.getProperty("email"))){
                userId = entity.getKey().getId();
                oldInfo = new User(
                    userId,
                    (String)entity.getProperty("name"),
                    userEmail,
                    (String)entity.getProperty("password"),
                    (String)entity.getProperty("birthdate"),
                    Long.parseLong(String.valueOf(entity.getProperty("studentId"))),
                    (String)entity.getProperty("sex"),
                    (String)entity.getProperty("school"),
                    (String)entity.getProperty("phone"),
                    (String)entity.getProperty("metric"),
                    Boolean.parseBoolean(String.valueOf(entity.getProperty("admin"))));
                break;
            }
        }

        Entity user = null;
        try{
            Key userKey = KeyFactory.createKey("User", userId);
            user = datastore.get(userKey);
        }
        catch( Exception EntityNotFoundException){
            return;
        }

        if(!request.getParameter("name").isEmpty()){
            user.setProperty("name", request.getParameter("name"));
        }else{
            user.setProperty("name", oldInfo.getName());
        }
        if(request.getParameter("metric")!= null){
            if(request.getParameter("metric").equals("on")){
                user.setProperty("metric", "celsius");
            }else{
                user.setProperty("metric", "fahrenheit");
            }
        }
        if(!request.getParameter("phone").isEmpty()){
            user.setProperty("phone", request.getParameter("phone"));
        }else{
            user.setProperty("phone", oldInfo.getPhone());
        }
        if(!request.getParameter("password").isEmpty()){
            user.setProperty("password", PasswordHash.hashPassword((request.getParameter("password")).toCharArray()));
        }else{
            user.setProperty("password", oldInfo.getPassword());
        }
    
        user.setProperty("birthdate", oldInfo.getBirthdate());
        user.setProperty("studentId", oldInfo.getStudentId());
        user.setProperty("school", oldInfo.getSchool());
        user.setProperty("admin", oldInfo.getAdmin());
        user.setProperty("sex", oldInfo.getSex());
        user.setProperty("email", oldInfo.getEmail());

        datastore.put(user);
        response.sendRedirect("/login.html");
    }
}
