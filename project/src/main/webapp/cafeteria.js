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
  var elems = document.querySelectorAll('.dropdown-trigger');
  var instances = M.Dropdown.init(elems, {alignment: 'right', coverTrigger: false, hover: true, closeOnClick: false})
});

document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.timepicker');
    var options = {twelveHour: false};
    var instances = M.Timepicker.init(elems, options);
});

$(document).ready(function(){
  $('.tabs').tabs();
});

/*
 * ################# INPUT HANDLERS ###############
 */

function updateTextInput(val, id) {
  document.getElementById(id+"-preview").innerText = val; 
}

function updateTimeInput(val, id) {
  var msg = document.getElementById(id+"-msg");
  if (val.slice(4) == 0 && (val.slice(3,4) == 3 || val.slice(3,4) == 0)) {
    msg.className = "";
    msg.classList.add("green-text");
    msg.innerText = "Available";
  } else {
    msg.className = "";
    msg.classList.add("red-text");
    msg.innerText = "Please round to the nearest half hour";
  }
}

function updateTimePrefInput(val, id) {
  var msg = document.getElementById(id+"-msg");
  if (val.slice(4) == 0 && (val.slice(3,4) == 3 || val.slice(3,4) == 0)) {
    msg.className = "";
    msg.classList.add("green-text");
    msg.innerText = "Available";
  } else {
    msg.className = "";
    msg.classList.add("red-text");
    msg.innerText = "Please round to the nearest half hour";
  }
}

function updateCafeteriaSelect(cafeterias) {
  const cafeteriaSelect = document.getElementById("cafeteria-field");
  cafeterias.forEach(cafeteria => {
    const option = document.createElement("option");
    option.value = cafeteria.name;
    option.text = cafeteria.name;
    cafeteriaSelect.appendChild(option);
  });
  var elems = document.querySelectorAll('select');
  var instances = M.FormSelect.init(elems, {});
}

/*
 * ################# LOADERS #################
 */
 
async function loadPage() {
  await loadUser();
  await loadMealData();
}

async function loadMealData() {
  var message = document.getElementById("message");
  const url = '/school-meal-preferences';
  await fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((cafeterias) => {
    if (Object.keys(cafeterias).length == 0) {
      if (user.is_admin == true) {
        message.innerText = "Please submit a cafeteria for your school.";
        const form = document.getElementById("preferences-form");
        form.style.display = "none";
        loadCafeteriaForm();
      } else {
        message.innerText = "Your school's administration has not submitted any cafeterias.";
      }
    } else {
      const cafeteriaList = document.getElementById("cafeterias");
      updateCafeteriaSelect(cafeterias);
      cafeterias.forEach(cafeteria => {
        const cafeteriaElement = createCafeteriaElement(cafeteria);
        cafeteriaList.appendChild(cafeteriaElement);
      });
    }
  });
}

function loadScheduleBtn(cafeteria, container, reschedule) {
  reschedule = reschedule || false;
  if (user.is_admin) {
    const scheduleBtn = document.createElement('btn');
    if (reschedule) {
      scheduleBtn.innerText = "Reschedule";
    } else {
      scheduleBtn.innerText = "Schedule";
    }
    scheduleBtn.classList.add('btn-flat', 'center-align', 'small');
    scheduleBtn.addEventListener('click', () => {
      const params = new URLSearchParams();
      params.append("cafeteria", cafeteria.name);
      params.append("key", cafeteria.key);
      fetch('/cafeteria-scheduler', {
        method: 'POST',
        body: params
      });
    });
    container.appendChild(scheduleBtn);
  }
}

function loadCafeteriaForm() {
  const schoolPreferencesForm = document.getElementById("school-preferences-form");
  schoolPreferencesForm.action = "/school-meal-preferences";
  schoolPreferencesForm.style.display = "block";
}

function loadPreferencesForm() {
  const form = document.getElementById("preferences-form");
  form.style.display = "block";
}

async function loadUser() {
  await fetch('/login').then(response => response.json()).then((users) => {
    user = users[0]; 
  });
  await fetch('cafeteria-preferences').then(response => response.json()).then((student) => {
    var message = document.getElementById("message");
    if (Object.keys(student).length == 0) {
      message.innerText = "Please indicate your preferences.";
      loadPreferencesForm();
    } else {
      const card = document.getElementById("card-for-forms");
      card.style.display = "none";
      message.innerText = "You indicated the following preferences:";
      const lunchPref = document.createElement("p");
      lunchPref.innerHTML = "<b>Lunch:</b> " + timeConverter(student[0].lunchPref.start) + " - " + timeConverter(student[0].lunchPref.start+student[0].lunchPref.duration);
      const dinnerPref = document.createElement("p");
      dinnerPref.innerHTML = "<b>Dinner:</b> " + timeConverter(student[0].dinnerPref.start) + " - " + timeConverter(student[0].dinnerPref.start+student[0].dinnerPref.duration);
      message.appendChild(lunchPref);
      message.appendChild(dinnerPref);
    }
  });
}

function loadSchedule(cafeteria) {
  const url = "/cafeteria-scheduler?cafeteria="+cafeteria.key;
  fetch(url).then(response => response.json()).then((schedule) => {
    const scheduleElem = document.getElementById("schedule");
    scheduleElem.style.display = "block";
    const lunchElem = document.getElementById("lunch");
    console.log(schedule);
    createMealBlocks(lunchElem, schedule[0]);
    const dinnerElem = document.getElementById("dinner");
    createMealBlocks(dinnerElem, schedule[1]);
  });
}

/*
 * ################# CAFETERIA ELEMS ############
 */

function createCafeteriaElement(cafeteria) {
  const cafeteriaElement = document.createElement('div');
  cafeteriaElement.style.width = "fit-content";
  cafeteriaElement.classList.add('card', 'indigo', 'lighten-5');
  const container = document.createElement('div');
  container.classList.add('card-content');
  const name = document.createElement('p');
  name.classList.add('card-title', 'center');
  const mealTime = document.createElement('p');
  const maxCapacity = document.createElement('p');
  const lunch = document.createElement('p');
  const dinner = document.createElement('p');

  name.innerText = cafeteria.name;
  mealTime.innerHTML = "<b>Meal Time:</b> " + cafeteria.mealTime;
  maxCapacity.innerHTML = "<b>Dining Hall Capacity:</b> " + cafeteria.maxCapacity;
  lunch.innerHTML = "<b>Lunch:</b> " + timeConverter(cafeteria.lunch_start) + " to " + timeConverter(cafeteria.lunch_end);
  dinner.innerHTML = "<b>Dinner:</b> " + timeConverter(cafeteria.dinner_start) + " to " + timeConverter(cafeteria.dinner_end);

  container.appendChild(name);
  container.appendChild(maxCapacity);
  container.appendChild(mealTime);
  container.appendChild(lunch);
  container.appendChild(dinner);

  loadScheduleBtn(cafeteria, container, cafeteria.is_scheduled);

  if (cafeteria.is_scheduled) {
    loadSchedule(cafeteria);
  }

  cafeteriaElement.appendChild(container);

  return cafeteriaElement
}

function timeConverter(time) {
  var hours = time / 60;
  var rhours = Math.floor(hours);
  var minutes = (hours - rhours) * 60;
  var rminutes = Math.round(minutes);
  if (rminutes === 30) {
    return rhours + ":" + rminutes;
  } else {
    return rhours + ":" + rminutes + "0";
  }
}

function createMealBlocks(elem, blocks) {
  var blockIndex = 1;
  blocks.forEach(block => {
    const blockElem = document.createElement('li');
    blockElem.innerHTML = "<b>Block" + blockIndex + "</b>";
    const time = document.createElement('p');
    time.innerText = timeConverter(block.time.start) + " : " + timeConverter(block.time.start+block.time.duration);
    //const is_mine = block.students.includes(user.name);
    const students = document.createElement('ul');
    if (Object.keys(block.students).length == 0) {
      students.innerHTML = "This block is empty";
    } else {
      block.students.forEach(student => {
        const studentElem = document.createElement('li');
        studentElem.innerText = student.name;
        students.appendChild(studentElem);
      })
    }

    blockElem.appendChild(time);
    blockElem.appendChild(students);
    elem.appendChild(blockElem);
    blockIndex++;
  })
}

/*
 * ################# VALIDATORS #################
 */

function validation() {
  return true;
}
