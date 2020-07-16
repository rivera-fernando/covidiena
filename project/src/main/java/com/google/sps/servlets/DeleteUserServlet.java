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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/*deletes user using userId from datatsore*/
@WebServlet("/delete-user")
public class DeleteUserServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Boolean found = Boolean.parseBoolean(request.getParameter("found"));
        HttpSession session = request.getSession(false);

        if(found){
            Key userEntityKey = KeyFactory.createKey("User", (Long) session.getAttribute("userId"));
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.delete(userEntityKey);
            session.removeAttribute("name");  
            session.removeAttribute("email");  
            session.removeAttribute("password");  
            session.removeAttribute("birthdate");  
            session.removeAttribute("studentId");  
            session.removeAttribute("sex");
            session.removeAttribute("school");
            session.removeAttribute("phone");
            session.removeAttribute("metric"); 
            session.removeAttribute("admin");
        }
        response.sendRedirect("/login.html");

    }
}
