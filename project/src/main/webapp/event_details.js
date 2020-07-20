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

document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.modal');
    var instances = M.Modal.init(elems, {});
});

/*
 * ################# LOADERS #################
 */

async function loadPage() {
  await loadUser();
  fetchEvent();
}

async function loadUser() {
  await fetch('/login').then(response => response.json()).then((users) => {
    user = users[0]; 
  });
}

async function fetchEvent() {
  await fetch('/event-details')
    .then(response => response.json())
    .then(events => {
      createEventElement(events[0]);
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
  const name = document.getElementById('event-name');
  name.innerText = event.name;
  
  const location = document.getElementById('event-location');
  location.innerHTML = "<b>Location:</b> " + event.location;
  
  const info = document.getElementById('event-info');
  info.innerText = event.date + " at " + event.time;
  
  const description = document.getElementById('event-description');
  description.innerHTML = "<b>Description:</b> " + event.description;
  
  const type = document.createElement('span');
  type.innerText = event.type;
  type.style.backgroundColor = '#42a5f5';
  type.style.borderRadius = '8px';
  type.style.padding = '3px 8px 3px 8px';
  type.style.marginLeft = '10px';
  type.style.color = 'white';
  
  const attendance = document.createElement('span');
  attendance.innerText = event.attendance;
  attendance.style.backgroundColor = '#42a5f5';
  attendance.style.borderRadius = '8px';
  attendance.style.padding = '3px 8px 3px 8px';
  attendance.style.marginLeft = '10px';
  attendance.style.color = 'white';

  info.appendChild(type);
  info.appendChild(attendance);
  
  const changes = document.getElementById('event-changes');
  event.changes.forEach(change => {
    const changeElement = document.createElement('li');
    changeElement.innerText = change;
    changes.appendChild(changeElement);
  });
  
  const attendees = document.getElementById('event-attendees');
  attendees.innerHTML = "<b>Capacity:</b> " + event.numAttendees + "/" + event.maxCapacity;

  serveBlob(event);

  const comments = document.getElementById('event-comments');
  if (event.comments != undefined) {
    event.comments.forEach(comment => {
      const commentElement = document.createElement('li');
      commentElement.innerText = comment;
      comments.appendChild(commentElement);
    });
  }
}

/*
 * ################# BUTTONS #################
 */

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

function addEditBtn(eventElement, event) {
  const editBtn = document.createElement('li');

  editBtn.classList.add('waves-light', 'btn-small', 'btn-flat', 'modal-trigger');
  editBtn.innerHTML = "EDIT";
  editBtn.dataset.target = "";
 
  editBtn.addEventListener('click', async () => {
    const editForm = document.getElementById("edit-events-form");
    loadEditForm(event);
  });
 
  var dropdownList = eventElement.getElementsByTagName('ul')[0];
  dropdownList.appendChild(editBtn);
}

/*
 * ################# BLOB METHODS #################
 */

function serveBlob(event) {
  const imageElement = document.getElementById('event-image');
  imageElement.style.maxHeight = "100%";
  imageElement.style.maxWidth = "100%";
  if (event.imageKey != null){
    fetch('/serve-blob?imageKey='+event.imageKey).then((image)=>{
      imageElement.src = image.url;
    });
  } else {
    imageElement.src = '/images/noImage.jpg';
  }
  var elems = document.querySelectorAll('.materialboxed');
  var instances = M.Materialbox.init(elems, {});
}

/*
 * ################# VALIDATORS #################
 */
 
function validateComment() {
  return true;
}
