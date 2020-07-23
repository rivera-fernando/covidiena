package com.google.sps.servlets;

import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.classes.User;
import com.google.sps.classes.PasswordHash;
import com.google.sps.classes.EmailSender;
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
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;



/*creates a user entity in datastore with user information so they can update it whenever they log in*/
@WebServlet("/user")
public class SignUpServlet extends HttpServlet {

    User user = null;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();  
        String name = request.getParameter("fname") + " " + request.getParameter("lname");
        String email = request.getParameter("email").toLowerCase();
        String birthdate = request.getParameter("birthdate");
        long studentId = Long.parseLong(request.getParameter("studentID"));
        String sex = request.getParameter("sex");
        String school = request.getParameter("school");
        String phone = request.getParameter("phone");
        String password = PasswordHash.hashPassword(request.getParameter("password").toCharArray());
        String imageKey = getUploadedFileUrl(request, "image");

        Query query = new Query("User");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        ArrayList<String> users = new ArrayList<String>();
        for (Entity entity:results.asIterable()){
            if (entity.getProperty("email").equals(email)){
                response.sendRedirect("/login.html");
                return;
            }
        }

        Entity userEntity = new Entity("User");
        userEntity.setProperty("name", name);
        userEntity.setProperty("email", email);
        userEntity.setProperty("birthdate", birthdate);
        userEntity.setProperty("studentId", studentId);
        userEntity.setProperty("sex", sex);
        userEntity.setProperty("school", school);
        userEntity.setProperty("phone", phone);
        userEntity.setProperty("metric", "fahrenheit");
        userEntity.setProperty("is_admin", false);
        userEntity.setProperty("password", password);
        userEntity.setProperty("imageKey", imageKey);

        datastore.put(userEntity);  

        response.sendRedirect("/login.html");
    }


    private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);
 
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }
 
    BlobKey blobKey = blobKeys.get(0);
 
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    } else {
      return blobKey.getKeyString();
    }
  }
}
