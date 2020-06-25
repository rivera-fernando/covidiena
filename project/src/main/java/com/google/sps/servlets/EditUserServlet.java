package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/edit-user")
public class EditUserServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        long userId = Long.parseLong(request.getParameter("userId"));
        String name = request.getParameter("name");
        String metric = request.getParameter("metric");
        String phone = request.getParameter("phone");
        System.out.println(userId);
        System.out.println(name);
        System.out.println(metric);
        System.out.println(phone);

        Key userKey = KeyFactory.createKey("User", userId);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity user;
        try{
            user = datastore.get(userKey);
        }
        catch( Exception EntityNotFoundException){
            return;
        }

        if(name != null){
            user.setProperty("name", name);
        }
        if(metric!= null){
            if(metric.equals("°C")){
                user.setProperty("metric", "c");
            }else{
                user.setProperty("metric", "f");
            }
        }
        if(phone!=null){
            user.setProperty("phone", phone);
        }

        datastore.put(user);
        response.sendRedirect("/settings.html");
    }
}
