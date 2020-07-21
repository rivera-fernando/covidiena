// This example requires the Places library. Include the libraries=places
// parameter when you first load the API. For example:
// <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">

var map;

function createMap() {
    map = new google.maps.Map(document.getElementById('map'),
    {center: {lng: 39.8283, lat: 98.5795},
    zoom: 8
    });
  infoWindow = new google.maps.InfoWindow;

  // Try HTML5 geolocation.
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      var pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };

      infoWindow.setPosition(pos);
      infoWindow.setContent('Location found.');
      infoWindow.open(map);
      map.setCenter(pos);
      
//   Create the places service.
  var service = new google.maps.places.PlacesService(map);
  var getNextPage = null;
  var moreButton = document.getElementById('more-tracing');
  moreButton.onclick = function() {
    moreButton.disabled = true;
    if (getNextPage) getNextPage();
  };

  // Perform a nearby search.
  service.nearbySearch(
      {location: pos, radius: 9700,
        type: [ 
            'liquor_store', 'bakery', 'bar', 'meal_takeaway',
            'meal_delivery','cafe', 'pharmacy','clothing_store',
            'convenience_store', 'restaurant', 'store'
            ]},
      function(results, status, pagination) {
        if (status !== 'OK') return;

        createMarkers(results);
        moreButton.disabled = !pagination.hasNextPage;
        getNextPage = pagination.hasNextPage && function() {
          pagination.nextPage();
        };
      });
    }, function() {
      handleLocationError(true, infoWindow, map.getCenter());
    });
  } else {
    // Browser doesn't support Geolocation
    handleLocationError(false, infoWindow, map.getCenter());
  }

}

function createMarkers(places) {
  var bounds = new google.maps.LatLngBounds();
  var placesList = document.getElementById('places-tracing');
  var markers = [];
  for (var i = 0, place; place = places[i]; i++) {
    var marker = new google.maps.Marker({
      map: map,
      icon: { url: "http://maps.google.com/mapfiles/ms/icons/purple-dot.png"},
      title: place.name,
      position: place.geometry.location
    });
    markers.push(marker);
    // console.log("{" + marker.getPosition().lat() + " , " + marker.getPosition().lng() + "}");
    // console.log(marker.getTitle());

    var li = document.createElement('li');
    li.textContent = place.name;
    placesList.appendChild(li);

    bounds.extend(place.geometry.location);
  }

  for (let mark of markers){
      mark.addListener('click', function() {
        console.log("{" + mark.getPosition().lat() + " , " + marker.getPosition().lng() + "}");
        console.log(mark.getTitle());
        document.getElementById("logLocationButton").style.display = "";
        document.getElementById("log-location-form").style.display = "";
        document.getElementById("longitude").value = mark.getPosition().lat();
        document.getElementById("latitude").value = mark.getPosition().lng();
        document.getElementById("location").value = mark.getTitle();
    });
  }

  map.fitBounds(bounds);
}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
  infoWindow.setPosition(pos);
  infoWindow.setContent(browserHasGeolocation ?
                        'Error: The Geolocation service failed.' :
                        'Error: Your browser doesn\'t support geolocation.');
  infoWindow.open(map);
}


