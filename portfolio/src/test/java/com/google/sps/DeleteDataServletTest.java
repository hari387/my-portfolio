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
public final class DeleteDataServletTest {
  
  private LocalServiceTestHelper helper = 
    new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig(),
      new LocalUserServiceTestConfig()
    );

  private static final Gson gson = new Gson();

  private static final DataServlet dataServlet = new DataServlet();
  private static final DeleteDataServlet deleteDataServlet = new DeleteDataServlet();
  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final UserService userService = UserServiceFactory.getUserService();

  private static final String USER_1 = "wildernessfly@gmail.com";

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

  @Test
  public void deleteCommentsPostTest() {
    dataServlet.addComment(COMMENT_1);
    dataServlet.addComment(COMMENT_2);
    dataServlet.addComment(COMMENT_3);
    dataServlet.addComment(COMMENT_4);

    Query query = new Query("Comment");
    List<Entity> commentEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(4,commentEntities.size());

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    try{
      deleteDataServlet.doPost(request,response);
    } catch(IOException e){
      System.out.println("------IOException------");
      System.out.println(e);
      System.out.println("-----------------------");
    }
  
    commentEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

    Assert.assertEquals(0,commentEntities.size());
  }
}