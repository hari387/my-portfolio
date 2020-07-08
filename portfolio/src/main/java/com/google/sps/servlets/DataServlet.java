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

import com.google.sps.data.Comment;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

  UserService userService = UserServiceFactory.getUserService();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  Gson gson = new Gson();

  // datastore keys for Comments
  String final usernameKey = "username";
  String final timestampKey = "timestamp";
  String final contentKey = "content";

  class ResponseStatus {
    boolean success;
    String errorMessage;
    ResponseStatus(boolean success,String errorMessage){
      this.success = success;
      this.errorMessage = errorMessage;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    int maxComments = Integer.parseInt(request.getParameter("maxcomments"));
    ArrayList<Comment> comments = new ArrayList<Comment>();

    // Sort the query so we will be adding newest comments first.
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    // Gets content of newest comments according to maxComments specified by request.
    for (Entity commentEntity : results.asIterable()) {
      if (comments.size() == maxComments) break;
      comments.add(new Comment(
        (String)commentEntity.getProperty(usernameKey),
        (long)commentEntity.getProperty(timestampKey),
        (String)commentEntity.getProperty(contentKey)
      ));
    }
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    if(!userService.isUserLoggedIn()){
      // If the user is not logged in, throw a 401 Unauthorized Error.
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().println(gson.toJson(new ResponseStatus(false,"Not logged in")));
      return;
    }

    String content = request.getParameter("comment-input");
    String userEmail = userService.getCurrentUser().getEmail();
    String username = userEmail.substring(0,userEmail.indexOf('@'));

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty(usernameKey, username);
    commentEntity.setProperty(timestampKey, System.currentTimeMillis());
    commentEntity.setProperty(contentKey, content);

    datastore.put(commentEntity);

    response.getWriter().println(gson.toJson(new ResponseStatus(true,null)));

  }
}
