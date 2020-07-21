package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.classes.User;
import com.google.sps.classes.PasswordHash;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

/*gets the user's userId and edits their entity in the datastore and then redirects them to login again*/
@WebServlet("/edit-user")
public class EditUserServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession(false);  
        Query query = new Query("User");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        String userEmail = request.getParameter("userEmail");
        User oldInfo = null;

        long userId = -1;
        for (Entity entity : results.asIterable()){
            if (userEmail.equals((String)entity.getProperty("email"))){
                userId = entity.getKey().getId();
                oldInfo = new User(
                    userId,
                    (String)entity.getProperty("name"),
                    userEmail,
                    (String)entity.getProperty("password"),
                    (String)entity.getProperty("birthdate"),
                    (long) entity.getProperty("studentId"),
                    (String)entity.getProperty("sex"),
                    (String)entity.getProperty("school"),
                    (String)entity.getProperty("phone"),
                    (String)entity.getProperty("metric"),
                    (boolean) entity.getProperty("is_admin"),
                    (String) entity.getProperty("imageKey"));
                break;
            }
        }

        Entity user = null;
        try {
            Key userKey = KeyFactory.createKey("User", userId);
            user = datastore.get(userKey);
        }
        catch ( Exception EntityNotFoundException){
            return;
        }

        if (!request.getParameter("name").isEmpty()){
            user.setProperty("name", request.getParameter("name"));
            session.setAttribute("name", request.getParameter("name"));
        } else {
            user.setProperty("name", oldInfo.getName());
        }

        if (request.getParameter("metric")!= null){
            if (request.getParameter("metric").equals("celsius")){
                user.setProperty("metric", "celsius");
                session.setAttribute("metric", "celsius");
            } else {
                user.setProperty("metric", "fahrenheit");
                session.setAttribute("metric", "fahrenheit");
            }
        }
        if (!request.getParameter("phone").isEmpty()){
            user.setProperty("phone", request.getParameter("phone"));
            session.setAttribute("phone", request.getParameter("phone"));
        } else {
            user.setProperty("phone", oldInfo.getPhone());
        }
        if (!request.getParameter("password").isEmpty()){
            user.setProperty("password", PasswordHash.hashPassword((request.getParameter("password")).toCharArray()));
            session.setAttribute("password", PasswordHash.hashPassword((request.getParameter("password")).toCharArray()));
        } else {
            user.setProperty("password", oldInfo.getPassword());
        }
        System.out.println(request.getParameter("edit-image"));
        if(request.getParameter("edit-image") != null){
            user.setProperty("imageKey", getUploadedFileUrl(request, "edit-image"));
            session.setAttribute("imageKey", getUploadedFileUrl(request, "edit-image"));
        }else{
            user.setProperty("imageKey", oldInfo.getImageKey());
        }
    
        user.setProperty("birthdate", oldInfo.getBirthdate());
        user.setProperty("studentId", oldInfo.getStudentId());
        user.setProperty("school", oldInfo.getSchool());
        user.setProperty("is_admin", oldInfo.getIsAdmin());
        user.setProperty("sex", oldInfo.getSex());
        user.setProperty("email", oldInfo.getEmail());


        datastore.put(user);
        response.sendRedirect("/settings.html");
    }

    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Gson gson = new Gson();
        response.setContentType("application/json;");
        HttpSession session = request.getSession(false);
        User user = new User(
            (long) session.getAttribute("userId"),
            (String) session.getAttribute("name"),
            (String) session.getAttribute("email"),
            (String) session.getAttribute("password"),
            (String) session.getAttribute("birthdate"),
            (long) session.getAttribute("studentId"),
            (String) session.getAttribute("sex"),
            (String) session.getAttribute("school"),
            (String) session.getAttribute("phone"),
            (String) session.getAttribute("metric"),
            (boolean) session.getAttribute("is_admin"),
            (String) session.getAttribute("imageKey"));

        response.getWriter().println(gson.toJson(user));

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
