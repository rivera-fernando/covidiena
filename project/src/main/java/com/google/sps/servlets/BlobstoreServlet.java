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
 
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
 
@WebServlet("/blobstore-upload-url")
public class BlobstoreServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    List<String> uploadUrls = new ArrayList<>();
 
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrlPost = blobstoreService.createUploadUrl("/post-event");
    String uploadUrlEdit = blobstoreService.createUploadUrl("/edit-event");
    String uploadUrlEditUser = blobstoreService.createUploadUrl("/edit-user");
    String uploadUrlSignUp = blobstoreService.createUploadUrl("/user");



 
    uploadUrls.add(uploadUrlPost);
    uploadUrls.add(uploadUrlEdit);
    uploadUrls.add(uploadUrlEditUser);
    uploadUrls.add(uploadUrlSignUp);
 
    Gson gson = new Gson();
 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(uploadUrls));
  }
}
