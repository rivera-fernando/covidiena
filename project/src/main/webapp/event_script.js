
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


document.addEventListener('DOMContentLoaded', function(){
    var elems = document.querySelectorAll('.datepicker');
    var instances = M.Datepicker.init(elems, {
        format: 'ddd mmm dd, yyyy'
    });
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
      events.forEach((event) => {
        var eventElement = createEventElement(event);
        eventElement = addApprovalBtn(eventElement);
        pendingEvents.appendChild(eventElement);
      })
    } else {
      events.forEach((event) => {
        pendingEvents.appendChild(createEventElement(event));
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
      })
    }
  });
}

function createEventElement(event) {
  const eventElement = document.createElement('div');
  eventElement.classList.add('card', 'white');
  eventElement.style.borderRadius = '5px';
  eventElement.id = event.id;

  const container = document.createElement('div');
  container.classList.add('card-content');

  const name = document.createElement('p');
  name.classList.add('card-title');
  const date = document.createElement('p');
  const description = document.createElement('p');
  const type = document.createElement('p');
  const attendance = document.createElement('p');

  name.innerText = event.name;
  date.innerText = event.date;
  description.innerText = event.description;
  type.innerText = event.type;
  attendance.innerText = event.attendance;

  container.appendChild(name);
  container.appendChild(date);
  container.appendChild(description);
  container.appendChild(type);
  container.appendChild(attendance);
  eventElement.appendChild(container);

  return eventElement;
}

function addApprovalBtn(eventElement) {
  const approvalBtn = document.createElement('button');
  approvalBtn.className = "waves-effect waves-light btn";
  approvalBtn.innerText = "Approve";

  approvalBtn.addEventListener('click', async () => {
    const eventID = eventElement.id;
    const params = new URLSearchParams();
    params.append('id', eventID);
    await fetch('/approve-event', {
      method: 'POST',
      body: params
    });
    loadPending();
    loadUpcoming();
    loadExplore();
  });

  eventElement.appendChild(approvalBtn);
  return eventElement;
}

function addRemovalBtn(eventElement) {
  const removalBtn = document.createElement('button');
  removalBtn.className = "waves-effect waves-light btn";
  removalBtn.innerText = "Remove";
  
  removalBtn.addEventListener('click', async () => {
    const eventID = eventElement.id;
    const params = new URLSearchParams();
    params.append('id', eventID);
    await fetch('/remove-event-rsvp', {
      method: 'POST',
      body: params
    });
    loadUpcoming();
  })

  eventElement.appendChild(removalBtn);
  return eventElement;
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
    })
  });
}

function addRSVPBtn(eventElement) {
  const RSVPBtn = document.createElement('button');
  RSVPBtn.className = "waves-effect waves-light btn";
  RSVPBtn.innerText = "RSVP";

  RSVPBtn.addEventListener('click', async () => {
    const eventID = eventElement.id;
    const params = new URLSearchParams();
    params.append('id', eventID);
    await fetch('/RSVP-event', {
      method: 'POST',
      body: params
    });
    loadUpcoming();
  });

  eventElement.appendChild(RSVPBtn);
  return eventElement;
}

function loadEventInfo() {

}

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
