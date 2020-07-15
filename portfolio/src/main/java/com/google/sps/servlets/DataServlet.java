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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

  UserService userService = UserServiceFactory.getUserService();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  Gson gson = new Gson();

  protected class ResponseStatus {
    static final String NOT_LOGGED_IN = "Not logged in";
    boolean success;
    long keyId;
    String errorMessage;
    ResponseStatus(boolean success,long keyId,String errorMessage){
      this.success = success;
      this.keyId = keyId;
      this.errorMessage = errorMessage;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    int maxComments = Integer.parseInt(request.getParameter("maxcomments"));
    ArrayList<Comment> comments = new ArrayList<Comment>();

    // Sort the query so we will be adding newest comments first,
    // and set the query limit to maxComments using FetchOptions.
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(maxComments));

    // Convert Entities from query into Comments.
    for (Entity commentEntity : results) {
      comments.add(getCommentFromEntity(commentEntity));
    }
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  public Comment getCommentFromEntity(Entity commentEntity) {
    Comment comment = new Comment(
        (String)commentEntity.getProperty(Comment.USERNAME_KEY),
        (long)commentEntity.getProperty(Comment.TIMESTAMP_KEY),
        (String)commentEntity.getProperty(Comment.CONTENT_KEY)
    );

    return comment;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    if(!userService.isUserLoggedIn()){
      // If the user is not logged in, throw a 401 Unauthorized Error.
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().println(gson.toJson(new ResponseStatus(false,0,ResponseStatus.NOT_LOGGED_IN)));
      return;
    }

    final String userEmail = userService.getCurrentUser().getEmail();
    final String username = userEmail.substring(0,userEmail.indexOf('@'));
    final long timestamp = System.currentTimeMillis();
    final String content = request.getParameter("comment-input");

    final Comment comment = new Comment(username,timestamp,content);

    final long keyId = addComment(comment).getId();

    response.getWriter().println(gson.toJson(new ResponseStatus(true,keyId,null)));
  }

  public Key addComment(Comment comment) {
    final Entity commentEntity = new Entity("Comment");
    
    commentEntity.setProperty(Comment.USERNAME_KEY,comment.username);
    commentEntity.setProperty(Comment.TIMESTAMP_KEY,comment.timestamp);
    commentEntity.setProperty(Comment.CONTENT_KEY,comment.content);

    return datastore.put(commentEntity);
  }
}
