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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.gson.Gson;
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
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(JUnit4.class)
public final class DataServletTest {
  
  private LocalServiceTestHelper helper = 
    new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig(),
      new LocalUserServiceTestConfig()
    );

  private static final Gson gson = new Gson();

  private static final DataServlet dataServlet = new DataServlet();
  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final UserService userService = UserServiceFactory.getUserService();

  private static final String USER_1 = "wildernessfly@gmail.com";

  private static Entity COMMENT_ENTITY_1;

  private static final Comment COMMENT_1 = new Comment("wildernessfly",24898942,"This is wildernessfly commenting");
  private static final Comment COMMENT_2 = new Comment("neptunedory",25898942,"This is neptunedory commenting");
  private static final Comment COMMENT_3 = new Comment("thegeneralcat",26898942,"This is thegeneralcat commenting");
  private static final Comment COMMENT_4 = new Comment("puddingbagel",27898942,"This is puddingbagel commenting");
  private static final Comment COMMENT_5 = new Comment("netappledog",28898942,"This is netappledog commenting");
  private static final Comment COMMENT_6 = new Comment("floralmirnix",29898942,"This is floralmirnix commenting");
  private static final Comment COMMENT_7 = new Comment("pigwafflesant",30898942,"This is pigwafflesant commenting");

  @Before
  public void setUp() {
    helper.setUp();

  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private void login(String userEmail, boolean isAdmin) {
    helper.setEnvEmail(userEmail);
    helper.setEnvIsLoggedIn(true);
    helper.setEnvIsAdmin(isAdmin);
    helper.setEnvAuthDomain("gmail");
  }

  private void logout() {
    helper.setEnvEmail("");
    helper.setEnvIsLoggedIn(false);
    helper.setEnvIsAdmin(false);
    helper.setEnvAuthDomain("");
  }

  @Test
  public void getCommentFromEntityTest() {
    COMMENT_ENTITY_1 = new Entity(Comment.TYPE);
    COMMENT_ENTITY_1.setProperty(Comment.USERNAME_KEY,COMMENT_1.username);
    COMMENT_ENTITY_1.setProperty(Comment.TIMESTAMP_KEY,COMMENT_1.timestamp);
    COMMENT_ENTITY_1.setProperty(Comment.CONTENT_KEY,COMMENT_1.content);
    
    Comment comment = dataServlet.getCommentFromEntity(COMMENT_ENTITY_1);

    Assert.assertEquals(COMMENT_1.username,comment.username);
    Assert.assertEquals(COMMENT_1.timestamp,comment.timestamp);
    Assert.assertEquals(COMMENT_1.content,comment.content);
  }

  @Test
  public void addCommentTest() throws IOException {
    Key commentKey = dataServlet.addComment(COMMENT_1);
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(results.size(),1);

    Entity commentEntity = results.get(0);
    Comment comment = dataServlet.getCommentFromEntity(results.get(0));

    Assert.assertEquals(commentKey,commentEntity.getKey());
    Assert.assertEquals(COMMENT_1.username,comment.username);
    Assert.assertEquals(COMMENT_1.timestamp,comment.timestamp);
    Assert.assertEquals(COMMENT_1.content,comment.content);

  }

  @Test
  public void getMaxLessThanExistingTest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter actualStringWriter = new StringWriter();
    PrintWriter actualPrintWriter = new PrintWriter(actualStringWriter);

    when(request.getParameter("maxcomments")).thenReturn("3");
    
    dataServlet.addComment(COMMENT_3);
    dataServlet.addComment(COMMENT_4);
    dataServlet.addComment(COMMENT_5);
    dataServlet.addComment(COMMENT_6);
    dataServlet.addComment(COMMENT_7);


    ArrayList<Comment> expectedComments = 
      new ArrayList(Arrays.asList(COMMENT_7,COMMENT_6,COMMENT_5));

    StringWriter expectedStringWriter = new StringWriter();
    PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);

    expectedPrintWriter.println(gson.toJson(expectedComments));

    when(response.getWriter()).thenReturn(actualPrintWriter);
    dataServlet.doGet(request,response);

    Assert.assertEquals(expectedStringWriter.toString(),actualStringWriter.toString());
  }

  @Test
  public void getMaxMoreThanExistingTest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter actualStringWriter = new StringWriter();
    PrintWriter actualPrintWriter = new PrintWriter(actualStringWriter);

    when(request.getParameter("maxcomments")).thenReturn("10");
    
    dataServlet.addComment(COMMENT_3);
    dataServlet.addComment(COMMENT_4);
    dataServlet.addComment(COMMENT_5);
    dataServlet.addComment(COMMENT_6);
    dataServlet.addComment(COMMENT_7);


    ArrayList<Comment> expectedComments = 
      new ArrayList(Arrays.asList(COMMENT_7,COMMENT_6,COMMENT_5,COMMENT_4,COMMENT_3));

    StringWriter expectedStringWriter = new StringWriter();
    PrintWriter expectedPrintWriter = new PrintWriter(expectedStringWriter);

    expectedPrintWriter.println(gson.toJson(expectedComments));

    when(response.getWriter()).thenReturn(actualPrintWriter);
    dataServlet.doGet(request,response);

    Assert.assertEquals(expectedStringWriter.toString(),actualStringWriter.toString());
  }

  @Test
  public void postCommentWhileLoggedInTest() throws IOException {
    login(USER_1,false);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter actualStringWriter = new StringWriter();
    PrintWriter actualPrintWriter = new PrintWriter(actualStringWriter);

    when(request.getParameter("comment-input")).thenReturn(COMMENT_1.content);
    when(response.getWriter()).thenReturn(actualPrintWriter);
    dataServlet.doPost(request,response);

    String responseStatusJson = actualStringWriter.toString();
    DataServlet.ResponseStatus responseStatus = gson.fromJson(responseStatusJson,DataServlet.ResponseStatus.class);

    Assert.assertTrue(responseStatus.success);
    Assert.assertEquals(null,responseStatus.errorMessage);

    Query query = new Query("Comment");
    Entity commentEntity = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults()).get(0);

    Assert.assertEquals(COMMENT_1.username,commentEntity.getProperty(Comment.USERNAME_KEY));
    Assert.assertEquals(COMMENT_1.content,commentEntity.getProperty(Comment.CONTENT_KEY));
    logout();
  }

  @Test
  public void postCommentWhileLoggedOutTest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    StringWriter actualStringWriter = new StringWriter();
    PrintWriter actualPrintWriter = new PrintWriter(actualStringWriter);

    when(request.getParameter("comment-input")).thenReturn(COMMENT_1.content);
    when(response.getWriter()).thenReturn(actualPrintWriter);
    dataServlet.doPost(request,response);

    String responseStatusJson = actualStringWriter.toString();
    DataServlet.ResponseStatus responseStatus = gson.fromJson(responseStatusJson,DataServlet.ResponseStatus.class);

    Assert.assertFalse(responseStatus.success);
    Assert.assertEquals(DataServlet.ResponseStatus.NOT_LOGGED_IN,responseStatus.errorMessage);

    Query query = new Query("Comment");
    List<Entity> commentEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(0,commentEntities.size());

  }
}

