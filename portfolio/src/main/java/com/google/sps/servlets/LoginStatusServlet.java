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

import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {

  UserService userService = UserServiceFactory.getUserService();
  Gson gson = new Gson();

  // This is the return page after logging in or out.
  final String homeUrl = "/";

  class LoginInfo {
    boolean loggedIn;
    String loginUrl;
    String logoutUrl;
    LoginInfo(boolean loggedIn, String loginUrl, String logoutUrl){
      this.loggedIn = loggedIn;
      this.loginUrl = loginUrl;
      this.logoutUrl = logoutUrl;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    LoginInfo loginInfo;

    if (userService.isUserLoggedIn()) {
      loginInfo = new LoginInfo(true, null, userService.createLogoutURL(homeUrl));
    } else {
      loginInfo = new LoginInfo(false, userService.createLoginURL(homeUrl), null);
    }

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(loginInfo));

  }
}
