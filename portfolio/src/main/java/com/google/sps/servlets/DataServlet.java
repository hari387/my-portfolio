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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    DatastoreService datastore;

    public DataServlet() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    private String convertToJsonUsingGson(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        System.out.println(json);
        return json;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int maxComments = Integer.parseInt(request.getParameter("maxcomments"));
        System.out.println(maxComments);
        ArrayList<String> comments = new ArrayList<String>();
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
        System.out.println("comments query starting");
        PreparedQuery results = datastore.prepare(query);
        System.out.println("comments queried");
        for (Entity commentEntity : results.asIterable()) {
            comments.add((String) commentEntity.getProperty("content"));
            if(comments.size() == maxComments)
                break;
        }
        response.setContentType("application/json;");
        response.getWriter().println(convertToJsonUsingGson(comments));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String content = request.getParameter("comment-input");

        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("content", content);
        commentEntity.setProperty("timestamp", System.currentTimeMillis());

        datastore.put(commentEntity);
    }

}
