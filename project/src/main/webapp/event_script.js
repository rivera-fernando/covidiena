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

$(document).ready(function(){
  $('.datepicker').datepicker();
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
    events.forEach((event) => {
        pendingEvents.appendChild(createEventElement(event));
    })
  });
}

function loadUpcoming() {
  const url ="/load-pending";
  fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((events) => {
    const pendingEvents = document.getElementById('pending-events');
    pendingEvents.innerHTML = '';
    events.forEach((event) => {
      pendingEvents.appendChild(createEventElement(event));
    })
  });
}

function createEventElement(event) {
  const eventElement = document.createElement('div');
  eventElement.classList.add('card', 'white');

  const name = document.createElement('p');
  name.classList.add('card-title');
  const content = document.createElement('div');
  content.classList.add('card-content')
  const date = document.createElement('p');
  const description = document.createElement('p');
  const type = document.createElement('p');
  const attendance = document.createElement('p');

  name.innerText = event.name;
  date.innerText = event.date;
  description.innerText = event.description;
  type.innerText = event.type;
  attendance.innerText = event.attendance;

  eventElement.appendChild(name);
  content.appendChild(date);
  content.appendChild(description);
  content.appendChild(type);
  content.appendChild(attendance);
  eventElement.appendChild(content);

  return eventElement;
}

function loadExplore() {

}

function loadEventInfo() {

}
