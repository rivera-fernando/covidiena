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

/*
 * Currently, this script will run when the home page loads and display data from 
 * https://news.google.com/covid19/map?hl=en-US&gl=US&ceid=US:en in the DOM. TODO:
 * reformat into a function that loads data when different components in the DOM
 * are rendered, so that we can load different sections asynchronously.
*/

// Create the XMLHttpRequest to parse data from the page
var request = new XMLHttpRequest();

// Use a CORS proxy from herokuapp
request.open("GET", "https://cors-anywhere.herokuapp.com/https://news.google.com/covid19/map?hl=en-US&gl=US&ceid=US:en", true);  // last parameter must be true

// When the request loads, get data and display in DOM
request.responseType = "document";
request.onload = function (e) {
  if (request.readyState === 4) {
    if (request.status === 200) {
      const doc = request.responseXML;
      // The class below is the class for div containing the Cases data by nation
      const table = doc.querySelector('.dzRe8d');
      const dataContainer = document.getElementById('data-container');
      dataContainer.innerHTML = table.innerHTML;
      console.log(table);
    } else {
      console.error(request.status, request.statusText);
    }
  }
};

request.onerror = function (e) {
  console.error(request.status, request.statusText);
};

request.send(null);  // not a POST request, so don't send extra data
