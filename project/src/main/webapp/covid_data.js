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
request.open("GET", "https://cors-anywhere.herokuapp.com/https://en.wikipedia.org/wiki/Template:COVID-19_pandemic_data", true);  // last parameter must be true

// When the request loads, get data and display in DOM
request.responseType = "document";
request.onload = function (e) {
  if (request.readyState === 4) {
    if (request.status === 200) {
      const doc = request.responseXML;
      // The class below is the class for div containing the Cases data by nation
      const table = doc.getElementById('thetable');
      const tableBody = table.getElementsByTagName('tbody')[0];
      const tableData = tableBody.getElementsByTagName('tr');
      const worldData = tableData[1].getElementsByTagName('th');
      console.log(worldData);
      getWorldData(worldData);
      console.log(tableData[0]);
      createTableHead(tableData[0]);
      for (row = 2; row < 230; row++) {
        createTableRow(tableData[row]);
      }
    } else {
      console.error(request.status, request.statusText);
    }
  }
};

request.onerror = function (e) {
  console.error(request.status, request.statusText);
};

request.send(null);  // not a POST request, so don't send extra data

function getWorldData(worldData) {
    const worldCases = worldData[2];
    const worldDeaths = worldData[3];
    const worldRecoveries = worldData[4];

    const worldCasesDOM = document.getElementById('world-cases');
    const worldDeathsDOM = document.getElementById('world-deaths');
    const worldRecoveriesDOM = document.getElementById('world-recoveries');
    
    worldCasesDOM.innerHTML = worldCases.innerHTML;
    worldDeathsDOM.innerHTML = worldDeaths.innerHTML;
    worldRecoveriesDOM.innerHTML = worldRecoveries.innerHTML;
}

function createTableHead(tableDataHead) {
    const tableHeadDOM = document.getElementById('country-table-head');
    const tableRowDOM = document.createElement('tr');

    const LocationDOM = document.createElement('th');
    LocationDOM.colSpan = 2;
    LocationDOM.innerText = "Location";
    tableRowDOM.appendChild(LocationDOM);
    
    const CasesDOM = document.createElement('th');
    CasesDOM.innerText = "Cases";
    tableRowDOM.appendChild(CasesDOM);
    
    const DeathsDOM = document.createElement('th');
    DeathsDOM.innerText = "Deaths";
    tableRowDOM.appendChild(DeathsDOM);
    
    const RecoveriesDOM = document.createElement('th');
    RecoveriesDOM.innerText = "Recoveries";
    tableRowDOM.appendChild(RecoveriesDOM);
    
    tableHeadDOM.appendChild(tableRowDOM);
}

function createTableRow(tableDataRow) {
    const tableBodyDOM = document.getElementById('country-table-body');
    const tableRowDOM = document.createElement('tr');
    
    extractData(tableDataRow, tableRowDOM);
    tableBodyDOM.appendChild(tableRowDOM);
}

function extractData(tableDataRow, tableRowDOM) {

    const headerContent = tableDataRow.getElementsByTagName('th');
    const bodyContent = tableDataRow.getElementsByTagName('td');

    const flag = headerContent[0];
    const countryName = headerContent[1];
    const cases = bodyContent[0];
    const deaths = bodyContent[1];
    const recoveries = bodyContent[2];

    const flagDOM = document.createElement('th');
    const countryNameDOM = document.createElement('th');
    const casesDOM = document.createElement('td');
    const deathsDOM = document.createElement('td');
    const recoveriesDOM = document.createElement('td');
    
    flagDOM.innerHTML = flag.innerHTML;
    countryNameDOM.innerText = countryName.childNodes[0].innerText;
    casesDOM.innerHTML = cases.innerHTML;
    deathsDOM.innerHTML = deaths.innerHTML;
    recoveriesDOM.innerHTML = recoveries.innerHTML;

    tableRowDOM.appendChild(flagDOM);
    tableRowDOM.appendChild(countryNameDOM);
    tableRowDOM.appendChild(casesDOM);
    tableRowDOM.appendChild(deathsDOM);
    tableRowDOM.appendChild(recoveriesDOM);
}

/*
 * News web scraping section
 */

// Create the XMLHttpRequest to parse data from Google News page
var request2 = new XMLHttpRequest();

// Use a CORS proxy from herokuapp
request2.open("GET", "https://cors-anywhere.herokuapp.com/https://news.google.com/covid19/map?hl=en-US&gl=US&ceid=US:en", true);  // last parameter must be true

// When the request loads, get data and display in DOM
request2.responseType = "document";
request2.onload = function (e) {
  if (request2.readyState === 4) {
    if (request2.status === 200) {
      const doc2 = request2.responseXML;
      // The class below is the class for div containing the Cases data by nation
      const news = doc2.querySelectorAll('.lxmZnf, .pym81b')[6];
      const newsDOM = document.getElementById('news');
      newsDOM.innerHTML = news.innerHTML;
    } else {
      console.error(request2.status, request2.statusText);
    }
  }
};

request2.onerror = function (e) {
  console.error(request2.status, request2.statusText);
};

request2.send(null);  // not a POST request, so don't send extra data
