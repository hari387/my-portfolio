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

package com.google.sps.data;

public class Comment {

  // datastore keys for Comments
  public static final String USERNAME_KEY = "username";
  public static final String TIMESTAMP_KEY = "timestamp";
  public static final String CONTENT_KEY = "content";
  public static final String TYPE = "Comment";

  public String username;
  public long timestamp;
  public String content;

  public Comment(String username, long timestamp, String content){
    this.username = username;
    this.timestamp = timestamp;
    this.content = content;
  }
}