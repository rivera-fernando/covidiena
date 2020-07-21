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
 * ################# INITIALIZERS #################
 */

var currWeek = 0;
var typeInput = [];
var attendanceInput = [];
var user = null;
 
document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.datepicker');
    // Can't select date before today
    var options = {minDate : new Date(), format: 'mmm dd, yyyy'};
    var instances = M.Datepicker.init(elems, options);
});
 
document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.timepicker');
    var options = {twelveHour: false};
    var instances = M.Timepicker.init(elems, options);
});

document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.modal');
    var instances = M.Modal.init(elems, {});
});

document.addEventListener('DOMContentLoaded', function() {
  var elems = document.querySelectorAll('.dropdown-trigger');
  var instances = M.Dropdown.init(elems, {alignment: 'right', coverTrigger: false, hover: true, closeOnClick: false})
});
 
$(document).ready(function(){
  $('.tabs').tabs();
});
 
function preview_image(event) {
  let elem = event.target.getAttribute("name");
  var reader = new FileReader();
  reader.onload = function() {
    if (elem === "image") {
      var output = document.getElementById('output-image');
      output.src = reader.result;
    }
  }
  reader.readAsDataURL(event.target.files[0]);
}

function closeForm() {
  const postForm = document.getElementById("events-form");
  postForm.style.display = "none";
}
 
/*
 * ################# LOADERS #################
 */
 
async function loadPage() {
  await loadUser();
  await loadEvents();
}
 
async function loadEvents() {
  fetchBlobstoreUrlAndShowForm();
  loadExplore();
  loadSearchExplore();
}

async function loadUser() {
  await fetch('/login').then(response => response.json()).then((users) => {
    user = users[0]; 
    console.log(user);
  });
}
 
function loadExplore(week, filter, type, attendance) {
  week = week || "0";
  filter = filter || false;
  type = type || [];
  attendance = attendance || [];
  const url = "/load-explore?week="+week;
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((week) => {
    const tableBody = document.getElementById("explore-body");
    tableBody.innerHTML = '';
    var isFull = 0;
    const today = (new Date()).getDay();
    var dayIndex = -1;
    // Load dates
    week.forEach((day) => {
      const date = document.getElementById(day.name+"-date");
      const theDay = day.date.slice(0,2);
      const rest = day.date.slice(2, day.date.length);
      if (today == dayIndex && currWeek == 0) {
        date.innerHTML = 
          "<span style=\"font-size: 20px; border-radius: 50%; background-color: black; color: white; padding: 5px\">"
          + theDay + "</span>" + "<br>" + "<span>" + rest + "</span>";
      } else {
        date.innerHTML = "<span style=\"font-size: 20px;\">"
          + theDay + "</span>" + "<br>" + "<span>" + rest + "</span>";
      }
      dayIndex++;
    });
    // While the week is not empty
    while (week[0].events.length != 0 || week[1].events.length != 0 || week[2].events.length != 0
        || week[3].events.length != 0 || week[4].events.length != 0 || week[5].events.length != 0
        || week[6].events.length != 0) {
      // Create a new row
      const row = document.createElement('tr');
      week.forEach((day) => {
        // For each day create a new table data cell
        const cell = document.createElement('td');
        cell.classList.add("vgrid");
        if (day.events === undefined || day.events.length != 0) {
          // If there are still events this day, create an event elem and append it
          const event = day.events[0];
          const eventElement = createEventPreview(event);
          if (filter) {
            if (type.includes(event.type) || type.length == 0) {
              if (attendance.includes(event.attendance) || attendance.length == 0) {
                isFull++;
                cell.appendChild(eventElement);
              }
            }
          } else {
            isFull++;
            cell.appendChild(eventElement);
          }
          day.events.shift();
        }
        row.appendChild(cell);
      });
      tableBody.appendChild(row);
    }
    if (isFull == 0) {
      tableBody.innerHTML = '';
      const row = document.createElement('tr');
      const cell = document.createElement('td');
      cell.style.textAlign = "center";
      cell.innerText = 'There are no events';
      cell.colSpan = "7";
      row.appendChild(cell);
      tableBody.appendChild(row);
    }
  });
}

/*
 * ################# CAL ELEMS #################
 */

function nextWeek() {
  currWeek++;
  loadExplore(currWeek, true, typeInput, attendanceInput);
}

function previousWeek() {
  currWeek--;
  loadExplore(currWeek, true, typeInput, attendanceInput);
}
 
function originalWeek() {
  currWeek = 0;
  loadExplore(currWeek, true, typeInput, attendanceInput);
}

function filterExploreEvents() {
  typeInput = [];
  attendanceInput = [];
  if(document.getElementById("academic").checked){
    typeInput.push("Academic");
  }
  if(document.getElementById("social").checked){
    typeInput.push("Social");
  }
  if(document.getElementById("optional").checked){
    attendanceInput.push("Optional");
  }
  if(document.getElementById("mandatory").checked){
    attendanceInput.push("Mandatory");
  }
  loadExplore(currWeek, true, typeInput, attendanceInput);
}

function showSearch() {
  const search = document.getElementById("search");
  if (search.style.display == "none") {
    search.style.display = "block";
  } else {
    search.style.display = "none";
  }
}

function loadSearchExplore() {
  const url = "/load-search";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((events) => {
    const searchBody = document.getElementById("explore-search-body");
    events.forEach((event) => {
      searchBody.appendChild(createSearchEvent(event));
    });
  });
}

function createSearchEvent(event) {
  const eventElement = document.createElement('tr');

  const eventTitle = document.createElement('td');
  eventTitle.innerText = event.name;

  const eventDate = document.createElement('td');
  eventDate.innerText = event.date + " at " + event.time;

  const seeMore = document.createElement('a');
  seeMore.classList.add('modal-trigger');
  seeMore.href = "#modal-container";
  seeMore.onclick = function() {
    const preview = document.getElementById("preview-event-info");
    preview.innerHTML = '';
    const previewElem = createEventElement(event);
    if (event.category === "Explore") {
      addRSVPBtn(previewElem);
    } else if (event.category === "Upcoming") {
      addRemovalBtn(previewElem);
    }
    addViewDetailsBtn(previewElem, event);
    preview.appendChild(previewElem);
    loadDropdowns();
  }
  seeMore.classList.add('blue-text', 'text-darken-3')
  var triggers = document.querySelectorAll('.modal');
  var instances = M.Modal.init(triggers, {});
  seeMore.innerText = 'See More';

  eventElement.appendChild(eventTitle);
  eventElement.appendChild(eventDate);
  eventElement.appendChild(seeMore);

  return eventElement;
}

function searchExploreEvents() {
  var input, filter, table, tr, td, eventIndex, txtValue;
  input = document.getElementById("searchInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("searchTable");
  tr = table.getElementsByTagName("tr");
  for (eventIndex = 0; eventIndex < tr.length; eventIndex++) {
    td = tr[eventIndex].getElementsByTagName("td")[0];
    if (td) {
      txtValue = td.textContent || td.innerText;
      if (txtValue.toUpperCase().indexOf(filter) > -1) {
        tr[eventIndex].style.display = "";
      } else {
        tr[eventIndex].style.display = "none";
      }
    }       
  }
}

/*
 * ################# EVENT ELEMS #################
 */

function createEventPreview(event) {
  // Containers, rows, and columns
  var eventElement = document.createElement('div');
  eventElement.classList.add('card', 'left-align', 'white-text');
  eventElement.style.padding = 0;
  if (event.category === "Past") {
    eventElement.classList.add('yellow', 'darken-3');
  } else if (event.category === "Explore") {
    eventElement.classList.add('deep-purple');
  } else if (event.category === "Upcoming") {
    eventElement.classList.add('green', 'darken-2');
  }
  eventElement.style.borderRadius = '3px';
  eventElement.style.boxShadow = 'none';
  eventElement.id = event.id;
 
  const container = document.createElement('div');
  container.classList.add('card-content');
  container.style.padding = "10px 10px 10px 10px";
 
  // Text elements
  const name = document.createElement('p');
  name.style.fontSize = "14px";
  const date = document.createElement('p');
  const time = document.createElement('p');
  const attendance = document.createElement('p');
  const type = document.createElement('p');
  const seeMore = document.createElement('a');
  seeMore.classList.add('modal-trigger');
  seeMore.href = "#modal-container";
  seeMore.onclick = function() {
    const preview = document.getElementById("preview-event-info");
    preview.innerHTML = '';
    const previewElem = createEventElement(event);
    if (event.category === "Explore") {
      addRSVPBtn(previewElem);
    } else if (event.category === "Upcoming") {
      addRemovalBtn(previewElem);
    }
    addViewDetailsBtn(previewElem, event);
    preview.appendChild(previewElem);
    loadDropdowns();
  }
  var triggers = document.querySelectorAll('.modal');
  var instances = M.Modal.init(triggers, {});
 
  name.innerHTML = "<b>" + event.name + "</b>";
  date.innerText = event.date;
  time.innerText = "At " + event.time;
  attendance.innerHTML = "<b>" + event.attendance + "</b>";
  type.innerHTML = "<b>" + event.type + "</b>";
  seeMore.innerHTML = "<b>See More</b>";
  seeMore.classList.add('cyan-text', 'text-accent-1');
 
  container.appendChild(name);
  container.appendChild(date);
  container.appendChild(time);
  container.appendChild(type);
  container.appendChild(attendance);
  container.appendChild(seeMore);
  container.style.fontSize = "12px";
  eventElement.appendChild(container);

  return eventElement;
}

function createEventElement(event) {
  // Containers, rows, and columns
  var eventElement = document.createElement('div');
  eventElement.style.borderRadius = '5px';
  eventElement.style.overflow = "scroll";
  eventElement.id = event.id;
 
  const container = document.createElement('div');
 
  const headerRow = document.createElement('div');
  headerRow.classList.add('row');
 
  const headerColumn = document.createElement('div');
  headerColumn.classList.add('col', 's11');
  
  const row = document.createElement('div');
  row.classList.add('row');
 
  const textColumn = document.createElement('div');
  textColumn.classList.add('col', 's9');
 
  const imageColumn = document.createElement('div');
  imageColumn.classList.add('col', 's3', 'valign-wrapper', 'center');
 
  const dropdownColumn = document.createElement('div');
  dropdownColumn.classList.add('col', 's1');
 
  // Text elements
  const name = document.createElement('h5');
  const when = document.createElement('span');
  const description = document.createElement('p');
  const attendance = document.createElement('span');
  attendance.style.backgroundColor = '#42a5f5';
  attendance.style.borderRadius = '8px';
  attendance.style.padding = '3px 8px 3px 8px';
  attendance.style.color = 'white';
  const type = document.createElement('span');
  type.style.backgroundColor = '#42a5f5';
  type.style.borderRadius = '8px';
  type.style.padding = '3px 8px 3px 8px';
  type.style.color = 'white';
  const space = document.createElement('span');
  const location = document.createElement('p');
 
  name.innerText = event.name;
  when.innerText = event.date + " at " + event.time + " ";
  when.classList.add('grey-text', 'text-darken-2');
  attendance.innerText = event.attendance;
  type.innerText = event.type;
  space.innerText = " ";
  description.innerHTML = "<b>About: </b>" + event.description;
  location.innerHTML = "<b>At: </b>" + event.location;
 
  headerColumn.appendChild(name);

  if (event.rejected) {
    const rejected = document.createElement('p');
    const changes = document.createElement('a');
    changes.classList.add("modal-trigger");
    changes.href = "#view-requested-changes";
    changes.innerText = "View Requested Changes";
    changes.onclick = function() {
      const viewChanges = document.getElementById("requested-changes");
      viewChanges.innerHTML = '<h5>' + event.name + '</h5><p>Changes requested by: ' 
        + event.adminEmail + '</p><p>' + event.changes + '</p>';
    }
    rejected.innerHTML = "Rejected - ";
    rejected.appendChild(changes);
    var triggers = document.querySelectorAll('.modal');
    var instances = M.Modal.init(triggers, {});
    rejected.style.color = "red";
    headerColumn.appendChild(rejected);
  }

  if (event.edited) {
    const edited = document.createElement('p');
    edited.innerText = 'Edited';
    edited.style.color = "red";
    headerColumn.appendChild(edited);
  }

  headerRow.appendChild(headerColumn);
  container.appendChild(headerRow);
  textColumn.appendChild(when);
  textColumn.appendChild(type);
  textColumn.appendChild(space);
  textColumn.appendChild(attendance);
  textColumn.appendChild(location);
  textColumn.appendChild(description);
  row.appendChild(textColumn);
 
  // Image element
  serveBlob(event, imageColumn);
  row.appendChild(imageColumn);
 
  headerRow.appendChild(dropdownColumn);
  container.appendChild(row);
  eventElement.appendChild(container);
  addDropdownMenu(dropdownColumn, event, eventElement);
 
  return eventElement;
}

/*
 * ################# DROPDOWN METHODS #################
 */

function addDropdownMenu(dropdownColumn, event, eventElement) {
  const dropdown = document.createElement('a');
  dropdown.classList.add('dropdown-trigger', 'flat-btn', 'right', 'right-align');
  dropdown.dataset.target = 'dropdown-' + event.id;
  dropdown.innerHTML = '<i class="material-icons">more_vert</i>';
  
  const dropdownList = document.createElement('ul');
  dropdownList.id = 'dropdown-' + event.id;
  dropdownList.classList.add('dropdown-content');
 
  dropdownColumn.appendChild(dropdown);
  dropdownColumn.appendChild(dropdownList);
}

async function loadDropdowns() {
  var elems = document.querySelectorAll('.dropdown-trigger');
  var instances = M.Dropdown.init(elems, {alignment: 'right'})
}
 
/*
 * ################# BUTTONS #################
 */
 
function addRemovalBtn(eventElement) {
  const removalBtn = document.createElement('li');
 
  removalBtn.classList.add('waves-light', 'btn-small', 'btn-flat');
  removalBtn.innerText = "REMOVE";
  
  removalBtn.addEventListener('click', async () => {
    const eventID = eventElement.id;
    const params = new URLSearchParams();
    params.append('id', eventID);
    await fetch('/remove-event-rsvp', {
      method: 'POST',
      body: params
    });
    location.reload();
    loadPage();
  })
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(removalBtn);
}
 
function addRSVPBtn(eventElement) {
  const RSVPBtn = document.createElement('li');
 
  RSVPBtn.classList.add('waves-light', 'btn-small', 'btn-flat', 'modal-close');
  RSVPBtn.innerText = "RSVP";
 
  RSVPBtn.addEventListener('click', async () => {
    const eventID = eventElement.id;
    const params = new URLSearchParams();
    params.append('id', eventID);
    await fetch('/RSVP-event', {
      method: 'POST',
      body: params
    });
    location.reload();
    loadPage();
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(RSVPBtn);
}

function addViewDetailsBtn(eventElement, event) {
  const viewDetails = document.createElement('li');

  viewDetails.innerText = "Event Details";
  viewDetails.classList.add('waves-light', 'btn-flat', 'btn-small');

  viewDetails.addEventListener('click', () => {
    const params = new URLSearchParams();
    params.append('eventId', eventElement.id);
    params.append('eventType', event.entityType);
    fetch ('event-details', {
      method: 'POST',
      body: params
    });
    location.replace('/event-details.html');
  });

  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(viewDetails);
}


/*
 * ################# BLOB METHODS #################
 */

function serveBlob(event, eventElement) {
  if(event.imageKey != null){
    const imageElement = document.createElement('img');
    fetch('/serve-blob?imageKey='+event.imageKey).then((image)=>{
      imageElement.src = image.url;
    });
    imageElement.style.maxHeight = '90%';
    imageElement.style.maxWidth = '90%';
    eventElement.appendChild(imageElement);
  }
}

function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-upload-url').then((response) => response.json())
  .then((imageUploadUrls) => {
    const eventsForm = document.getElementById('events-form');
    eventsForm.action = imageUploadUrls[0];
  });
}

/*
 * ################# VALIDATORS #################
 */
 
function validateEvent() {
  var name = document.getElementsByName('name')[0].value;
  var location = document.getElementsByName('location')[0].value;
  var date = document.getElementsByName('date')[0].value;
  var time = document.getElementsByName('time')[0].value;
  var capacity = document.getElementsByName('capacity')[0].value;
  var description = document.getElementsByName('description')[0].value;
 
  const nameError = document.getElementById('name-error');
  const locationError = document.getElementById('location-error');
  const dateError = document.getElementById('date-error');
  const timeError = document.getElementById('time-error');
  const capacityError = document.getElementById('capacity-error');
  const descriptionError = document.getElementById('description-error');
  const injectionError = document.getElementById('injection-error');
 
  // If comment contains any html of javascript don't submit the form
  if ((description.includes("<html>")) || (description.includes("<script>")) || (name.includes("<html>")) 
    || (name.includes("<script>"))) {

      injectionError.style.display = "block";
      return false;
  } else {
      injectionError.style.display = "none";
 
  }
  // If name, description, or both are empty don't submit the form
  if ((description === "") || (name === "") || (date === "") || (time === "") || (capacity === "") 
    || (location === "")) {

      if (description === "") {
        descriptionError.style.display = "block";
      } else {
        descriptionError.style.display = "none";
      }
      if (name === "") {
        nameError.style.display = "block";
      } else {
        nameError.style.display = "none";
      }
      if (date === "") {
        dateError.style.display = "block";
      } else {
        dateError.style.display = "none";
      }
      if (time === "") {
        timeError.style.display = "block";
      } else {
        timeError.style.display = "none";
      }
      if (capacity === "") {
        capacityError.style.display = "block";
      } else {
        capacityError.style.display = "none";
      }
      if (location === "") {
        locationError.style.display = "block";
      } else {
        locationError.style.display = "none";
      }
      return false;
  } else {
      descriptionError.style.display = "none";
      nameError.style.display = "none";
      dateError.style.display = "none";
      timeError.style.display = "none";
      capacityError.style.display = "none";
      locationError.style.display = "none";
      injectionError.style.display = "none";
  }
  return true;
}
 