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
import org.apache.commons.codec.binary.Hex;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


@WebServlet("/login")
public class LogInServlet extends HttpServlet {

    public User user = null;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = hashPassword(request.getParameter("password").toCharArray());
        String email = request.getParameter("email").toLowerCase();
        Gson gson = new Gson();
        response.setContentType("text/html");

        Query query = new Query("User");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<String> users = new ArrayList<String>();
        for(Entity entity:results.asIterable()){
            if(entity.getProperty("password").equals(password) && entity.getProperty("email").equals(email)){
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
                response.getWriter().println(gson.toJson(user));
                response.sendRedirect("/dashboard.html");
                return;
            }
        }
        response.sendRedirect("/login.html");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(user));
    }

    private String hashPassword(final char[] password){
      try {
            String salt = "@*1!";
            int iterations = 500;
            int keyLength = 412;
            byte[] saltBytes = salt.getBytes();
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, saltBytes, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return Hex.encodeHexString(res);
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }
}
