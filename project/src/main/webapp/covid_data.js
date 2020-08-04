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

function loadCovidData() {
  const url = "/load-covid-data";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((countryData) => {
    const tableBodyDOM = document.getElementById('country-table-body');
    var index = 0;
    countryData.forEach((country) => {
      if (index == 0) {
        loadWorldData(country);
      } else {
        createTableRow(tableBodyDOM, country);
      }
      index++;
    })
  });
}
 
function loadWorldData(worldData) {
  const worldCasesDOM = document.getElementById('world-cases');
  const worldDeathsDOM = document.getElementById('world-deaths');
  const worldRecoveriesDOM = document.getElementById('world-recoveries');
  
  if (worldData.cases == -1) {
    worldCasesDOM.innerHTML = "No data";
  } else {
    worldCasesDOM.innerHTML = Number(worldData.cases).toLocaleString('US');
  }
  worldDeathsDOM.innerHTML = worldData.deaths;
  worldRecoveriesDOM.innerHTML = worldData.recoveries;
}
 
function createTableRow(tableBodyDOM, country) {
  const tableRowDOM = document.createElement('tr');
 
  const countryNameDOM = document.createElement('th');
  countryNameDOM.colSpan = 2;
  const casesDOM = document.createElement('td');
  const deathsDOM = document.createElement('td');
  const recoveriesDOM = document.createElement('td');
  
  countryNameDOM.innerText = country.location;
  if (country.cases == -1) {
    casesDOM.innerHTML = "No data";
  } else {
    casesDOM.innerHTML = Number(country.cases).toLocaleString('US');
  }
  deathsDOM.innerHTML = country.deaths;
  recoveriesDOM.innerHTML = country.recoveries;
 
  tableRowDOM.appendChild(countryNameDOM);
  tableRowDOM.appendChild(casesDOM);
  tableRowDOM.appendChild(deathsDOM);
  tableRowDOM.appendChild(recoveriesDOM);
  
  tableBodyDOM.appendChild(tableRowDOM);
}
/*
function loadUpdates() {
  const url = "/updates";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((updates) => {
    const updatesHistory = document.getElementById('updates');
    updatesHistory.innerHTML = '';
    if (updates[0].title === "Admin") {
      const updateForm = document.getElementById('update-form');
      updateForm.style.display = "block";
      delete updates[0];
    }
    updates.forEach((update) => {
      updatesHistory.appendChild(createUpdateElement(update));
    })
  });
}
 
function createUpdateElement(update) {
  const updateElement = document.createElement('div');
  updateElement.classList.add('card', 'pink', 'lighten-2', 'z-depth-1');
  updateElement.style.borderRadius = '5px';
 
  const container = document.createElement('div');
  container.classList.add('card-content');
 
  const title = document.createElement('p');
  title.classList.add('card-title');
  title.style.fontSize = "16px";
  const description = document.createElement('p');
  const author = document.createElement('p');
 
  title.innerText = update.title;
  description.innerText = update.description;
  author.innerText = "- " + update.author;
  
  container.appendChild(title);
  container.appendChild(description);
  container.appendChild(author);
  updateElement.appendChild(container);
 
  return updateElement;
}
*/
