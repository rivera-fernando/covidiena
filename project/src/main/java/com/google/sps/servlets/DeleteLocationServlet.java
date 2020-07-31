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

@WebServlet("/delete-location")
public class DeleteLocationServlet extends HttpServlet{
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
      long locationId = Long.parseLong(request.getParameter("locationId"));

      Key locationEntityKey = KeyFactory.createKey("LocationLog", locationId);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.delete(locationEntityKey);
  }
}
