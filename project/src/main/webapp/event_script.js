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


document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.datepicker');
    // Can't select date before today
    var options = {minDate : new Date(), format: 'ddd mmm dd, yyyy'};
    var instances = M.Datepicker.init(elems, options);
});
 
document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.timepicker');
    var options = {twelveHour: false};
    var instances = M.Timepicker.init(elems, options);
});
 
$(document).ready(function(){
  $('.tabs').tabs();
});
 
function preview_image(event) {
  var reader = new FileReader();
  reader.onload = function() {
    var output = document.getElementById('output-image');
    output.src = reader.result;
  }
  reader.readAsDataURL(event.target.files[0]);
}
 
function cancelEdit() {
  
}
 
/*
 * ################# LOADERS #################
 */
 
async function loadPage() {
  await loadEvents();
}
 
async function loadDropdowns() {
  var elems = document.querySelectorAll('.dropdown-trigger');
  var instances = M.Dropdown.init(elems, {alignment: 'right'})
}
 
async function loadEvents() {
  loadUpcoming();
  loadPending();
  loadPast();
  loadExplore();
}
 
function loadPending() {
  const url = "/load-pending";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((events) => {
    const pendingEvents = document.getElementById('pending-events');
    pendingEvents.innerHTML = '';
    // Right now this if statement won't work for admins
    if (Object.keys(events).length == 0) {
      pendingEvents.innerText = "You have no pending events";
    } else if (events[0].name === "Admin") {
      delete events[0];
      if (Object.keys(events).length == 0) {
        pendingEvents.innerText = "You have no pending events";
      }
      events.forEach((event) => {
        var eventElement = createEventElement(event);
        eventElement = addApprovalBtn(eventElement);
        pendingEvents.appendChild(eventElement);
        loadDropdowns();
      })
    } else {
      events.forEach((event) => {
        var eventElement = createEventElement(event);
        eventElement = addApprovalBtn(eventElement);
        pendingEvents.appendChild(eventElement);
        loadDropdowns();
      })
    }
  });
}
 
function loadUpcoming() {
  const url ="/load-upcoming";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((events) => {
    const upcomingEvents = document.getElementById('upcoming-events');
    upcomingEvents.innerHTML = '';
    if (Object.keys(events).length == 0) {
      upcomingEvents.innerText = "You have no upcoming events";
    } else {
      events.forEach((event) => {
        var eventElement = createEventElement(event);
        eventElement = addRemovalBtn(eventElement);
        upcomingEvents.appendChild(eventElement);
        loadDropdowns();
      })
    }
  });
}
 
function loadPast() {
  const url="/load-past";
  fetch(url, {
    method: 'GET'
  }).then((response) => response.json()).then((events) => {
    const pastEvents = document.getElementById('past-events');
    if (Object.keys(events).length == 0) {
      pastEvents.innerText = "You have no past events";
    } else {
      events.forEach((event) => {
        var eventElement = createEventElement(event);
        pastEvents.appendChild(eventElement);
        loadDropdowns();
      })
    }
  });
}
 
function loadExplore() {
  const url = "/load-explore";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((events) => {
    const exploreEvents = document.getElementById('explore-events');
    exploreEvents.innerHTML = '';
    events.forEach((event) => {
      var eventElement = createEventElement(event);
      eventElement = addRSVPBtn(eventElement);
      exploreEvents.appendChild(eventElement);
      loadDropdowns();
    })
  });
}
 
function loadEventInfo() {
 
}
 
function loadEditForm(event) {
  const editName = document.getElementsByName("edit-name")[0];
  const editLocation = document.getElementsByName("edit-location")[0];
  const editDate = document.getElementsByName("edit-date")[0];
  const editTime = document.getElementsByName("edit-time")[0];
  const editDescription = document.getElementsByName("edit-description")[0];
  const editType = document.getElementsByName("edit-event-type")[0];
  const editAttendance = document.getElementsByName("edit-event-attendance")[0];
 
  editName.value = event.name;
  editLocation.value = event.location;
  editDate.value = event.date;
  editTime.value = event.time;
  editDescription.value = event.description;
 
  document.getElementById(event.type).checked = true;
  document.getElementById(event.attendance).checked = true;
 
  const entityType = document.getElementsByName("event-status")[0];
  entityType.value = event.entityType;
  const entityId = document.getElementsByName("event-id")[0];
  entityId.value = event.id;
}
 
/*
 * ################# EVENT ELEMS #################
 */
 
function createEventElement(event) {
  var eventElement = document.createElement('div');
  eventElement.classList.add('card', 'white');
  eventElement.style.borderRadius = '5px';
  eventElement.id = event.id;
 
  const container = document.createElement('div');
  container.classList.add('card-content');
 
  const name = document.createElement('span');
  name.classList.add('card-title', 'activator');
  const when = document.createElement('span');
  const description = document.createElement('p');
  const attendance = document.createElement('span');
  attendance.style.backgroundColor = '#42a5f5';
  attendance.style.borderRadius = '8px';
  attendance.style.padding = '4px 8px 4px 8px';
  attendance.style.color = 'white';
  const type = document.createElement('span');
  type.style.backgroundColor = '#42a5f5';
  type.style.borderRadius = '8px';
  type.style.padding = '4px 8px 4px 8px';
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
 
  container.appendChild(name);
  container.appendChild(when);
  container.appendChild(type);
  container.appendChild(space);
  container.appendChild(attendance);
  container.appendChild(description);
  container.appendChild(location);
  eventElement.appendChild(container);
 
  eventElement = addDropdownMenu(eventElement, event);
 
  return eventElement;
}
 
function addDropdownMenu(eventElement, event) {
  const dropdown = document.createElement('a');
  dropdown.classList.add('dropdown-trigger', 'flat-btn', 'right');
  dropdown.href = '#';
  dropdown.dataset.target = 'dropdown-' + event.id;
  dropdown.innerHTML = '<i class="material-icons">more_vert</i>';
  
  const dropdownList = document.createElement('ul');
  dropdownList.id = 'dropdown-' + event.id;
  dropdownList.classList.add('dropdown-content');
 
  var name = eventElement.querySelector('.card-title');
 
  name.appendChild(dropdown);
  name.appendChild(dropdownList);
 
  if (event.isMine) {
    eventElement = addEditBtn(eventElement, event);
    eventElement = addDeleteBtn(eventElement, event.entityType);
  }
  
  return eventElement;
}
 
/*
 * ################# BUTTONS #################
 */
 
function addApprovalBtn(eventElement) {
  const approvalBtn = document.createElement('li');
 
  approvalBtn.classList.add('waves-light', 'btn-flat', 'btn-small');
  approvalBtn.innerText = "Approve";
 
  approvalBtn.addEventListener('click', async () => {
    const eventID = eventElement.id;
    const params = new URLSearchParams();
    params.append('id', eventID);
    await fetch('/approve-event', {
      method: 'POST',
      body: params
    });
    location.reload();
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(approvalBtn);
  return eventElement;
}
 
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
  })
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(removalBtn);
  return eventElement;
}
 
function addRSVPBtn(eventElement) {
  const RSVPBtn = document.createElement('li');
 
  RSVPBtn.classList.add('waves-light', 'btn-small', 'btn-flat');
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
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(RSVPBtn);
  return eventElement;
}
 
function addEditBtn(eventElement, event) {
  const editBtn = document.createElement('li');
 
  editBtn.classList.add('waves-light', 'btn-small', 'btn-flat');
  editBtn.innerText = "EDIT";
 
  editBtn.addEventListener('click', async () => {
    const editForm = document.getElementById("edit-form");
    editForm.style.display = "block";
    loadEditForm(event);
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(editBtn);
  return eventElement;
}
 
function addDeleteBtn(eventElement, entityType) {
  const deleteBtn = document.createElement('li');
 
  deleteBtn.classList.add('waves-light', 'btn-small', 'btn-flat', 'red-text');
  deleteBtn.innerText = "DELETE";
 
  deleteBtn.addEventListener('click', async () => {
    const params = new URLSearchParams();
    params.append('id', eventElement.id);
    params.append('entityType', entityType);
    await fetch('/delete-event', {
      method: 'POST',
      body: params
    });
    location.reload();
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(deleteBtn);
  return eventElement;
}
 
/*
 * ################# VALIDATORS #################
 */
 
function validateEvent() {
  var name = document.getElementsByName('name')[0].value;
  var date = document.getElementsByName('date')[0].value;
  var description = document.getElementsByName('description')[0].value;
 
  const nameError = document.getElementById('name-error');
  const dateError = document.getElementById('date-error');
  const descriptionError = document.getElementById('description-error');
  const injectionError = document.getElementById('injection-error');
 
  // If comment contains any html of javascript don't submit the form
  if ((description.includes("<html>")) || (description.includes("<script>")) || (name.includes("<html>")) || (name.includes("<script>"))) {
      injectionError.style.display = "block";
      return false;
  } else {
      injectionError.style.display = "none";
 
  }
  // If name, description, or both are empty don't submit the form
  if ((description === "") || (name === "") || (date === "")) {
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
      return false;
  } else {
      descriptionError.style.display = "none";
      nameError.style.display = "none";
      dateError.style.display = "none";
      injectionError.style.display = "none";
  }
  return true;
}
