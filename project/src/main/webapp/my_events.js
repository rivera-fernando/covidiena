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

var user = null;

$(document).ready(function(){
  $('.tabs').tabs();
});

document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.modal');
    var instances = M.Modal.init(elems, {});
});

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

/*
 * ################# LOADERS #################
 */

async function loadMyEvents() {
  await loadUser();
  loadPending();
  loadApproved();
  loadUpdates();
  fetchBlobstoreUrlAndShowForm();
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
      pendingEvents.style.color = "white";
    } else if (user.is_admin == true) {
      events.forEach((event) => {
        var eventElement = createEventElement(event);
        addApprovalBtn(eventElement);
        addRejectionBtn(eventElement);
        addViewDetailsBtn(eventElement, event);
        pendingEvents.appendChild(eventElement);
        loadDropdowns();
      })
    } else {
      events.forEach((event) => {
        var eventElement = createEventElement(event);
        addViewDetailsBtn(eventElement, event);
        pendingEvents.appendChild(eventElement);
        loadDropdowns();
      })
    }
  });
}

function loadApproved() {
  const url = "/load-approved";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((events) => {
    const approvedEvents = document.getElementById('approved-events');
    approvedEvents.innerHTML = '';
    // Right now this if statement won't work for admins
    if (Object.keys(events).length == 0) {
      approvedEvents.innerText = "You have no approved events";
      approvedEvents.style.color = "white";
    } else {
      events.forEach((event) => {
        var eventElement = createEventElement(event);
        addViewDetailsBtn(eventElement, event);
        approvedEvents.appendChild(eventElement);
        loadDropdowns();
      })
    }
  });
}

function loadEditForm(event) {
  const editName = document.getElementsByName("edit-name")[0];
  const editLocation = document.getElementsByName("edit-location")[0];
  const editDate = document.getElementsByName("edit-date")[0];
  const editTime = document.getElementsByName("edit-time")[0];
  const editDescription = document.getElementsByName("edit-description")[0];
  const preview = document.getElementById("edit-output-image");
 
  editName.value = event.name;
  editLocation.value = event.location;
  editDate.value = event.date;
  editTime.value = event.time;
  editDescription.value = event.description;
  if (event.imageKey != undefined) {
    fetch('/serve-blob?imageKey='+event.imageKey).then((image)=>{
      preview.src = image.url;
    });
  }
 
  document.getElementById(event.type).checked = true;
  document.getElementById(event.attendance).checked = true;
 
  const entityType = document.getElementsByName("event-status")[0];
  entityType.value = event.entityType;
  const entityId = document.getElementsByName("event-id")[0];
  entityId.value = event.id;
}

async function loadUser() {
  await fetch('/login').then(response => response.json()).then((users) => {
    user = users[0]; 
    var helloMsg = document.getElementById("helloMsg");
    helloMsg.innerText = user.name + "'s Events";
  });
}

function loadUpdates() {
  const url = "/load-event-updates";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((updates) => {
    var approvalUpdates = document.getElementById("approval-updates");
    var changesUpdates = document.getElementById("changes-updates");

    if (Object.keys(updates).length == 0) {
      approvalUpdates.innerText = "You have no updates";
      changesUpdates.innerText = "You have no updates";
    } else {
      updates.forEach(update => {
        createUpdateElement(update);
      });
    }
    if (!approvalUpdates.hasChildNodes()) {
      approvalUpdates.innerText = "You have no updates";
    } else if (!changesUpdates.hasChildNodes()) {
      changesUpdates.innerText = "You have no updates";
    }
  });
}

/*
 * ################# DROPDOWN METHODS #################
 */

async function loadDropdowns() {
  var elems = document.querySelectorAll('.dropdown-trigger');
  var instances = M.Dropdown.init(elems, {alignment: 'right'})
}

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
 
  if (event.isMine) {
    addEditBtn(eventElement, event);
    addDeleteBtn(eventElement, event.entityType);
  }
}

/*
 * ################# EVENT ELEMS #################
 */

function createEventElement(event) {
  // Containers, rows, and columns
  var eventElement = document.createElement('div');
  eventElement.classList.add('card', 'white');
  eventElement.style.borderRadius = '5px';
  eventElement.style.overflow = "scroll";
  eventElement.id = event.id;
 
  const container = document.createElement('div');
  container.classList.add('card-content');
 
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
  const name = document.createElement('span');
  name.classList.add('card-title', 'activator');
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
        + event.adminEmail + '</p>';
      var index = 1;
      event.changes.forEach(change => {
        var requestedChange = document.createElement('p');
        requestedChange.innerText = "Change #" + index + ": " + change;
        viewChanges.appendChild(requestedChange);
        index++;
      })
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

function createUpdateElement(update) {
  const updateElement = document.createElement('div');
  const name = document.createElement('h6');
  name.style.fontWeight = "bold";
  name.innerText = update.name;
  const adminEmail = document.createElement('span');
  adminEmail.innerHTML = "<b>Change requested by: </b>" + update.adminEmail;
  const changeRequested = document.createElement('p');
  changeRequested.innerHTML = "<b>Description: </b>" + update.changeRequested;

  updateElement.appendChild(name);
  updateElement.appendChild(adminEmail);
  updateElement.appendChild(changeRequested);
  
  if (update.isRejected) {
    const changesUpdates = document.getElementById("changes-updates");
    changesUpdates.appendChild(updateElement);
    if (!$(updateElement).is(':last-child')) {
      const divider = document.createElement('div');
      divider.classList.add('divider');
      changesUpdates.appendChild(divider);
    }
  } else {
    const approvalUpdates = document.getElementById("approval-updates");
    approvalUpdates.appendChild(updateElement);
    if (!$(updateElement).is(':last-child')) {
      const divider = document.createElement('div');
      divider.classList.add('divider');
      approvalUpdates.appendChild(divider);
    }
  }
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
    params.append('event-changes-id', eventID);
    params.append('approved', true);
    await fetch('/approve-event', {
      method: 'POST',
      body: params
    });
    location.reload();
    loadPage();
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(approvalBtn);
}

function addRejectionBtn(eventElement) {
  const rejectionBtn = document.createElement('li');
 
  rejectionBtn.classList.add('waves-light', 'btn-flat', 'btn-small', 'modal-trigger');
  rejectionBtn.innerText = "Reject";
  rejectionBtn.dataset.target = "request-changes-container";
 
  rejectionBtn.addEventListener('click', async () => {
    var changesContainerInput = document.getElementById('changes-input');
    //changesContainerInput.value = '';
    var approved = document.getElementById('approved');
    approved.value = false;
    var eventId = document.getElementById('event-changes-id');
    eventId.value = eventElement.id;
  });

  var triggers = document.querySelectorAll('.modal');
  var instances = M.Modal.init(triggers, {});
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(rejectionBtn);
}

function addDeleteBtn(eventElement, entityType) {
  const deleteBtn = document.createElement('li');
 
  deleteBtn.classList.add('waves-light', 'btn-small', 'btn-flat', 'red-text', 'modal-close');
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
    loadPage();
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(deleteBtn);
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

function addEditBtn(eventElement, event) {
  const editBtn = document.createElement('li');

  editBtn.classList.add('waves-light', 'btn-small', 'btn-flat', 'modal-trigger');
  editBtn.innerHTML = "EDIT & RESUBMIT";
  editBtn.dataset.target = "edit-events-form-container";
 
  editBtn.addEventListener('click', async () => {
    const editForm = document.getElementById("edit-events-form");
    loadEditForm(event);
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(editBtn);
}

function cancelEdit() {
  const editForm = document.getElementById("edit-events-form");
  editForm.style.display = "none";
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
    imageElement.style.maxHeight = "90%";
    imageElement.style.maxWidth = "90%";
    eventElement.appendChild(imageElement);
  }
}

function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-upload-url').then((response) => response.json())
  .then((imageUploadUrls) => {
    const editEventsForm = document.getElementById('edit-events-form');
    editEventsForm.action = imageUploadUrls[1];
  });
}

function preview_image(event) {
  let elem = event.target.getAttribute("name");
  var reader = new FileReader();
  reader.onload = function() {
    if (elem === "image") {
      var output = document.getElementById('output-image');
      output.src = reader.result;
    } else if (elem === "edit-image") {
      var output = document.getElementById('edit-output-image');
      output.src = reader.result;
    }
  }
  reader.readAsDataURL(event.target.files[0]);
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
