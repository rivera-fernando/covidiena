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
var cafeteriasArray;
var currLunch;
var currDinner;
var currCafeteria;

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

$('.dropdown-trigger').dropdown();

document.addEventListener('DOMContentLoaded', function() {
  var elems = document.querySelectorAll('.modal');
  var instances = M.Modal.init(elems, {});
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
  loadMealData();
}

async function loadUser() {
  await fetch('/login').then(response => response.json()).then((users) => {
    user = users[0]; 
    const header = document.getElementById("header");
    const name = document.createElement("h4");
    name.innerText = "Welcome " + user.name;
    const school = document.createElement("h5");
    school.innerText = user.school;
    header.appendChild(name);
    header.appendChild(school);
  });
}

async function loadMealData() {
  var message = document.getElementById("message");
  const url = '/school-meal-preferences';
  await fetch(url, {
    method: 'GET'
  }).then(response => response.json()).then((cafeterias) => {
    cafeteriasArray = cafeterias;
    if (Object.keys(cafeterias).length == 0) {
      if (user.is_admin == true) {
        message.innerText = "Please submit a cafeteria for your school!";
        loadCafeteriaForm();
      } else {
        message.innerText = "Your school's administration has not submitted any cafeterias.";
      }
    } else {
      const cafeteriaList = document.getElementById("cafeterias");
      cafeteriaList.style.display = "block";
      updateCafeteriaSelect(cafeterias);
      cafeterias.forEach(cafeteria => {
        addToDropDown(cafeteria);
      });
      if (user.is_admin) {
        addCafeteriaOption();
      }
      var elems = document.querySelectorAll('select');
      var instances = M.FormSelect.init(elems, {});
      loadPreferencesData();
    }
  });
}

async function loadPreferencesData() {
  await fetch('cafeteria-preferences').then(response => response.json()).then((student) => {
    var message = document.getElementById("message");
    if (Object.keys(student).length == 0) {
      message.innerText = "Please indicate your preferences.";
      loadPreferencesForm();
    } else {
      createPreferences(message, student[0]);
    }
  });
}

function loadScheduleBtn(cafeteria, container, reschedule) {
  reschedule = reschedule || false;
  if (user.is_admin) {
    const scheduleBtn = document.createElement('btn');
    scheduleBtn.style.paddingLeft = "0";
    if (reschedule) {
      scheduleBtn.innerText = "Reschedule";
    } else {
      scheduleBtn.innerText = "Schedule";
    }
    scheduleBtn.classList.add('btn-flat', 'center-align', 'small', 'white-text');
    scheduleBtn.addEventListener('click', () => {
      const params = new URLSearchParams();
      params.append("cafeteria", cafeteria.name);
      params.append("key", cafeteria.key);
      fetch('/cafeteria-scheduler', {
        method: 'POST',
        body: params
      }).then(location.reload());
    });
    container.appendChild(scheduleBtn);
  }
}

function loadCafeteriaForm() {
  const schoolPreferencesForm = document.getElementById("school-preferences-card");
  if (schoolPreferencesForm.style.display == "none") {
    schoolPreferencesForm.style.display = "block";
  } else {
    schoolPreferencesForm.style.display = "none";
  }
}

function loadPreferencesForm() {
  const form = document.getElementById("preferences-trigger");
  form.style.display = "inline-block";
}

function loadSchedule(cafeteria) {
  const addCafeteriaForm = document.getElementById("school-preferences-card");;
  addCafeteriaForm.style.display = "none";
  const lunchElem = document.getElementById("lunch");
  lunchElem.innerHTML = "<h5>Lunch Schedule</h5>";
  lunchElem.classList.add("center-align");
  const dinnerElem = document.getElementById("dinner");
  dinnerElem.innerHTML = "<h5>Dinner Schedule</h5>";
  dinnerElem.classList.add("center-align");
  const scheduleElem = document.getElementById("schedule");
  if (cafeteria.is_scheduled) {
    scheduleElem.style.display = "block";
    const url = "/cafeteria-scheduler?cafeteria="+cafeteria.key;
    fetch(url).then(response => response.json()).then((schedule) => {
      createMealBlocks(lunchElem, schedule[0], cafeteria, "lunch");
      createMealBlocks(dinnerElem, schedule[1], cafeteria, "dinner");
    });
  } else {
    scheduleElem.style.display = "none";
  }
}

/*
 * ################# DOM ELEMS ############
 */

function createCafeteriaElement(cafeteria) {
  const cafeteriaElement = document.createElement('div');
  cafeteriaElement.style.width = "fit-content";
  cafeteriaElement.style.borderRadius = "3px";
  cafeteriaElement.classList.add('card', 'blue', 'darken-4', 'white-text', 'col');
  const container = document.createElement('div');
  container.classList.add('card-content');
  const name = document.createElement('p');
  name.classList.add('card-title', 'center');
  const meal_time = document.createElement('p');
  const max_capacity = document.createElement('p');
  const lunch = document.createElement('p');
  const dinner = document.createElement('p');

  name.innerText = cafeteria.name;
  meal_time.innerHTML = "<b>Meal Time:</b> " + cafeteria.meal_time;
  max_capacity.innerHTML = "<b>Dining Hall Capacity:</b> " + cafeteria.max_capacity;
  lunch.innerHTML = "<b>Lunch:</b> " + timeConverter(cafeteria.lunch_start) + " to " + timeConverter(cafeteria.lunch_end);
  dinner.innerHTML = "<b>Dinner:</b> " + timeConverter(cafeteria.dinner_start) + " to " + timeConverter(cafeteria.dinner_end);

  container.appendChild(name);
  container.appendChild(max_capacity);
  container.appendChild(meal_time);
  container.appendChild(lunch);
  container.appendChild(dinner);

  loadScheduleBtn(cafeteria, container, cafeteria.is_scheduled);

  cafeteriaElement.appendChild(container);

  const preview = document.getElementById("cafeteria-preview");
  preview.style.display = "block";
  preview.innerHTML = "";
  preview.appendChild(cafeteriaElement);
}

function addToDropDown(cafeteria) {
  const dropdownElem = document.createElement('option');
  dropdownElem.innerText = cafeteria.name;
  dropdownElem.value = cafeteria.name;
  const dropdownMenu = document.getElementById("cafeteria-select");
  dropdownMenu.appendChild(dropdownElem);
}

function addCafeteriaOption() {
  const dropdownMenu = document.getElementById("cafeteria-select");
  const dropdownElem = document.createElement('option');
  dropdownElem.innerText = "Add Cafeteria";
  dropdownElem.value = "Add Cafeteria";
  dropdownMenu.appendChild(dropdownElem);
}

function changeCafeteria() {
  const dropdownMenu = document.getElementById("cafeteria-select");
  const value = dropdownMenu.value;
  if (value === "Add Cafeteria") {
    loadCafeteriaForm();
    const schedule = document.getElementById("schedule");
    schedule.style.display = "none";
    const cafeteriaCard = document.getElementById("cafeteria-preview");
    cafeteriaCard.style.display = "none";
  }
  cafeteriasArray.forEach(cafeteria => {
    if (value === cafeteria.name) {
      createCafeteriaElement(cafeteria);
      loadSchedule(cafeteria);
    }
  });
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

function createMealBlocks(elem, blocks, cafeteria, meal) {
  const table = document.createElement('table');
  table.classList.add("striped");
  const thead = document.createElement('thead');
  const trow = document.createElement('tr');

  var blockIndex = 1;
  blocks.forEach(block => {
    const blockElem = document.createElement('td');
    blockElem.style.paddingTop = 0;
    blockElem.classList.add("vgrid", "center", "center-align");
    const blockTitle = document.createElement('p');
    blockTitle.classList.add("center-align", "center");
    blockTitle.innerHTML = "<b>Block " + blockIndex + "</b>";
    const time = document.createElement('p');
    time.innerHTML = "<b>Time: </b>" + timeConverter(block.time.start) + " - " + timeConverter(block.time.start+block.time.duration);
    //const is_mine = block.students.includes(user.name);
    const students = document.createElement('p');
    students.innerHTML = "<b>Capacity: </b>" + Object.keys(block.students).length + "/" + cafeteria.max_capacity;
    const flag = document.createElement('i');
    flag.classList.add("material-icons");
    flag.innerText = "brightness_1";
    
    if (Object.keys(block.students).length / cafeteria.max_capacity < 0.8) {
      flag.classList.add('tiny', 'green-text');
    } else if (Object.keys(block.students).length / cafeteria.max_capacity < 1) {
      flag.classList.add('tiny', 'yellow-text');
    } else {
      flag.classList.add('tiny', 'red-text');
    }

    blockTitle.appendChild(flag);
    blockElem.appendChild(blockTitle);
    blockElem.appendChild(time);
    blockElem.appendChild(students);

    var found = false;
    block.students.forEach(student => {
      if (student.name === user.name) {
        blockElem.classList.add("blue", "lighten-5");
        found = true;
      }
    });

    const addBtn = document.createElement('button');
    addBtn.innerText = "Join";
    addBtn.classList.add('btn');
    addBtn.addEventListener('click', () => {
    const params = new URLSearchParams();
    params.append("cafeteriaKey", cafeteria.key);
    params.append("targetCafeteriaName", cafeteria.name);
    params.append("blockStart", block.time.start);
    params.append("mealDuration", cafeteria.meal_time);
    params.append("currCafeteria", currCafeteria);
    if (meal === "lunch") {
        params.append("currTimeRangeStart", currLunch.start);
        params.append("currTimeRangeEnd", currLunch.start + currLunch.duration);
    } else {
        params.append("currTimeRangeStart", currDinner.start);
        params.append("currTimeRangeEnd", currDinner.start + currDinner.duration);
    }
    params.append("meal", meal);
    fetch('/change-cafeteria', {
        method: 'POST',
        body: params
    }).then(location.reload());
    });
    blockElem.appendChild(addBtn);
    
    if (found == true) {
      addBtn.disable = true;
    }

    trow.appendChild(blockElem);
    blockIndex++;
  })

  thead.appendChild(trow);
  table.appendChild(thead);
  elem.appendChild(table);
}

function createPreferences(message, student) {
  message.classList.add('row');

  const preferences = document.createElement('div');
  preferences.classList.add('col', 's6');
  preferences.style.padding = 0;
  preferences.innerText = "You indicated the following preferences:";

  const lunchPref = document.createElement("p");
  lunchPref.innerHTML = "<b>Lunch:</b> " + timeConverter(student.lunchPref.start) + " - " + timeConverter(student.lunchPref.start+student.lunchPref.duration);
  const dinnerPref = document.createElement("p");
  dinnerPref.innerHTML = "<b>Dinner:</b> " + timeConverter(student.dinnerPref.start) + " - " + timeConverter(student.dinnerPref.start+student.dinnerPref.duration);
  const cafeteriaPref = document.createElement("p");
  cafeteriaPref.innerHTML = "<b>Cafeteria:</b> " + student.cafeteria;

  preferences.appendChild(cafeteriaPref);
  preferences.appendChild(lunchPref);
  preferences.appendChild(dinnerPref);
  message.appendChild(preferences);

  // If student was assigned
  if (student.hasOwnProperty("lunchReceived")) {
    currLunch = student.lunchReceived;
    currDinner = student.dinnerReceived;
    currCafeteria = student.cafeteriaReceived;
    const received = document.createElement('div');
    received.classList.add('col', 's6');
    received.style.padding = 0;
    received.innerText = "You were assigned the following slots:";

    // Implement proper functionality to actually find student
    const lunchReceived = document.createElement("p");
    lunchReceived.innerHTML = "<b>Lunch:</b> " + timeConverter(student.lunchReceived.start) + " - " + timeConverter(student.lunchReceived.start+student.lunchReceived.duration);
    const dinnerReceived = document.createElement("p");
    dinnerReceived.innerHTML = "<b>Dinner:</b> " + timeConverter(student.dinnerReceived.start) + " - " + timeConverter(student.dinnerReceived.start+student.dinnerReceived.duration);
    const cafeteriaReceived = document.createElement("p");
    cafeteriaReceived.innerHTML = "<b>Cafeteria:</b> " + student.cafeteriaReceived;

    received.appendChild(cafeteriaReceived);
    received.appendChild(lunchReceived);
    received.appendChild(dinnerReceived);
    message.appendChild(received);
  }
} 

/*
 * ################# VALIDATORS #################
 */

function validation() {
  return true;
}
