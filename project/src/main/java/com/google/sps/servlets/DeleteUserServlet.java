package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-user")
public class DeleteUserServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        long userId = Long.parseLong(request.getParameter("userId"));

        Key userEntityKey = KeyFactory.createKey("User", userId);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.delete(userEntityKey);
    }
}
