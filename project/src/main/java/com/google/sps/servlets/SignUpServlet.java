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

/*
creates a user entity in datastore with user information so they can update it whenever they log in
*/

@WebServlet("/user")
public class SignUpServlet extends HttpServlet {

//   @Override
//   public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//       UserService userService = UserServiceFactory.getUserService();
//       String name = request.getParameter("fname") + " " + request.getParameter("lname");
//       String email = userService.getCurrentUser().getEmail().toLowerCase();
//       String birthdate = request.getParameter("birthdate");
//       long studentId = Long.parseLong(request.getParameter("studentID"));
//       String sex = request.getParameter("sex");
//       String school = request.getParameter("school");
//       String phone = request.getParameter("phone");

//       Entity userEntity = new Entity("User");
//       userEntity.setProperty("name", name);
//       userEntity.setProperty("email", email);
//       userEntity.setProperty("birthdate", birthdate);
//       userEntity.setProperty("studentId", studentId);
//       userEntity.setProperty("sex", sex);
//       userEntity.setProperty("school", school);
//       userEntity.setProperty("phone", phone);
//       userEntity.setProperty("metric", "fahrenheit");
//       userEntity.setProperty("admin", false);


//       DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//       datastore.put(userEntity);

//       User user = new User(userEntity.getKey().getId(), name, email, birthdate, studentId, sex, school, phone, "fahrenheit", false);
//       Gson gson = new Gson();
//       response.setContentType("application/json;");
//       response.getWriter().println(gson.toJson(user));

//       response.sendRedirect("/dashboard.html");
//   }

@Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String name = request.getParameter("fname") + " " + request.getParameter("lname");
      String email = request.getParameter("email").toLowerCase();
      String birthdate = request.getParameter("birthdate");
      long studentId = Long.parseLong(request.getParameter("studentID"));
      String sex = request.getParameter("sex");
      String school = request.getParameter("school");
      String phone = request.getParameter("phone");
      String password = hashPassword(request.getParameter("password").toCharArray());

      Entity userEntity = new Entity("User");
      userEntity.setProperty("name", name);
      userEntity.setProperty("email", email);
      userEntity.setProperty("birthdate", birthdate);
      userEntity.setProperty("studentId", studentId);
      userEntity.setProperty("sex", sex);
      userEntity.setProperty("school", school);
      userEntity.setProperty("phone", phone);
      userEntity.setProperty("metric", "fahrenheit");
      userEntity.setProperty("admin", false);
      userEntity.setProperty("password", password);


      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(userEntity);

      User user = new User(userEntity.getKey().getId(), name, email, password, birthdate, studentId, sex, school, phone, "fahrenheit", false);
      Gson gson = new Gson();
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(user));

      response.sendRedirect("/dashboard.html");
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
