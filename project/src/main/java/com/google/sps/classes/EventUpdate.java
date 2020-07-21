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
 
package com.google.sps.classes;

import java.util.List;
 
/**
 * Class representing an individual event.
 *
 */
public final class EventUpdate {
 
  private final long id;
  private final String name;
  private final String owner;
  private final String adminEmail;
  private final boolean isRejected;
  private final String changeRequested;

  public EventUpdate(long id, String name, String owner, String adminEmail, 
    boolean isRejected, String changeRequested) {

    this.id = id;
    this.name = name;
    this.owner = owner;
    this.adminEmail = adminEmail;
    this.isRejected = isRejected;
    this.changeRequested = changeRequested;
  }
}
