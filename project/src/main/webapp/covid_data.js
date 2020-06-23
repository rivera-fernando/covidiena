var request = new XMLHttpRequest();
request.open("GET", "https://cors-anywhere.herokuapp.com/https://en.wikipedia.org/wiki/Template:COVID-19_pandemic_data", true);  // last parameter must be true
request.responseType = "document";
request.onload = function (e) {
  if (request.readyState === 4) {
    if (request.status === 200) {
      const doc = request.responseXML;
      const text = doc.getElementById('firstHeading');
      console.log(text.innerText);
    } else {
      console.error(request.status, request.statusText);
    }
  }
};

request.onerror = function (e) {
  console.error(request.status, request.statusText);
};

request.send(null);  // not a POST request, so don't send extra data