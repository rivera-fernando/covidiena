// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.Locale;
import java.text.NumberFormat;
import java.text.ParseException;

/** Servlet that loads pending events*/
@WebServlet("/update-covid-data")
public class CovidDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    cleanDatastore(datastore);

    WebClient client = new WebClient();
    client.getOptions().setCssEnabled(false);

    try {
      String searchUrl = "https://en.wikipedia.org/wiki/Template:COVID-19_pandemic_data";
      HtmlPage page = client.getPage(searchUrl);

      // Scrape data from wikipedia page
      HtmlElement table = (HtmlElement) page.getHtmlElementById("thetable");
      DomElement tableBody = (DomElement) table.getLastElementChild();
      DomNodeList<HtmlElement> tableData = (DomNodeList<HtmlElement>) tableBody.getElementsByTagName("tr");

      updateWorldData(datastore, tableData);

      // Parse data from table
      for (int row = 2; row < 230; row++) {
        updateCountryData(datastore, tableData, row);
      }

      response.getWriter().println("Datastore updated");
      response.setStatus(200);
      
    } catch(Exception e){
      e.printStackTrace();
      // Change this so that it forces cron to retry
      response.setStatus(200);
    }
  }

  private void cleanDatastore(DatastoreService datastore) {
    // Set up and clean datastore
    Query query = new Query("CovidData");
    PreparedQuery results = datastore.prepare(query);

    // Delete entities so we can replace them
    // Note: Updating entities instead of deleting would require looping through
    // the datastore to find the entity's key for every country
    for (Entity entity: results.asIterable()) {
      Key key = entity.getKey();
      datastore.delete(key);
    }
  }

  private void updateWorldData(DatastoreService datastore, DomNodeList<HtmlElement> tableData) {
    // Handle world data
    HtmlElement worldElement = (HtmlElement) tableData.get(1);
    DomNodeList<HtmlElement> worldData = (DomNodeList<HtmlElement>) worldElement.getElementsByTagName("th");

    int worldCases = 0;
    try {
      worldCases = NumberFormat.getNumberInstance(Locale.US).parse(((HtmlElement) worldData.get(2)).getTextContent()).intValue();
    } catch(ParseException e) {
      // If there is no data, we will handle the -1 in the loading servlet
      worldCases = -1;
    }
    String worldDeaths = ((HtmlElement) worldData.get(3)).getTextContent();;
    String worldRecoveries = ((HtmlElement) worldData.get(4)).getTextContent();;

    Entity worldEntity = new Entity("CovidData");
    worldEntity.setProperty("location", "World");
    worldEntity.setProperty("cases", worldCases);
    worldEntity.setProperty("deaths", worldDeaths);
    worldEntity.setProperty("recoveries", worldRecoveries);
    datastore.put(worldEntity);
  }

  private void updateCountryData(DatastoreService datastore, DomNodeList<HtmlElement> tableData, int row) {
    HtmlElement countryElement = (HtmlElement) tableData.get(row);
    String location = (((DomNodeList<HtmlElement>) countryElement.getElementsByTagName("th")).get(1)).getChildNodes().item(0).getTextContent();
    DomNodeList<HtmlElement> countryData = (DomNodeList<HtmlElement>) countryElement.getElementsByTagName("td");
    
    int cases = 0;
    try {
      cases = NumberFormat.getNumberInstance(Locale.US).parse(((HtmlElement) countryData.get(0)).getTextContent()).intValue();
    } catch(ParseException e) {
      // If there is no data, we will handle the -1 in the loading servlet
      cases = -1;
    }
    String deaths = ((HtmlElement) countryData.get(1)).getTextContent();;
    String recoveries = ((HtmlElement) countryData.get(2)).getTextContent();;

    Entity countryEntity = new Entity("CovidData");
    countryEntity.setProperty("location", location);
    countryEntity.setProperty("cases", cases);
    countryEntity.setProperty("deaths", deaths);
    countryEntity.setProperty("recoveries", recoveries);
    datastore.put(countryEntity);
  }
}
