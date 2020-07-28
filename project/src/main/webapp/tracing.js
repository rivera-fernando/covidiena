// This example requires the Places library. Include the libraries=places
// parameter when you first load the API. For example:
// <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">

var map;

async function heatmap() {
    const heatmapData = [];
    await fetch("/logLocation").then(function(response) {
        return response.json();
    }).then(function(locations) {
        locations.forEach(location => {
            var place = new google.maps.LatLng(location[1], location[0]);
            heatmapData.push(place);
        });
    });
    var center = new google.maps.LatLng(40.774546, -97.433523);

    map = new google.maps.Map(document.getElementById('map'), {
        center: center,
        zoom: 4,
    });
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
        var pos = {
            lat: position.coords.latitude,
            lng: position.coords.longitude
        };
        map.setCenter(pos);
        map.setZoom(9);
        });}

    var heatmap = new google.maps.visualization.HeatmapLayer({
        data: heatmapData,
        radius: 15,
    });
    heatmap.setMap(map);
}

function createMap() {
    map = new google.maps.Map(document.getElementById('map'),
    {center: {lng: 50, lat: 57},
    zoom: 11
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
        const input = document.getElementById("pac-input");
        const searchBox = new google.maps.places.SearchBox(input);


        // Bias the SearchBox results towards current map's viewport.
        map.addListener("bounds_changed", () => {
            searchBox.setBounds(map.getBounds());
        });
        let markers = [];
        // Listen for the event fired when the user selects a prediction and retrieve
        // more details for that place.
        searchBox.addListener("places_changed", () => {
            const places = searchBox.getPlaces();

            if (places.length == 0) {
            return;
            }
            // Clear out the old markers.
            markers.forEach(marker => {
            marker.setMap(null);
            });
            markers = [];
            // For each place, get the icon, name and location.
            const bounds = new google.maps.LatLngBounds();
            places.forEach(place => {
            if (!place.geometry) {
                console.log("Returned place contains no geometry");
                return;
            }
            // Create a marker for each place.
            markers.push(
                new google.maps.Marker({
                map,
                icon: { url: "http://maps.google.com/mapfiles/ms/icons/purple-dot.png"},
                title: place.name,
                position: place.geometry.location
                })
            );

            document.getElementById("logLocationButton").style.display = "";
            document.getElementById("log-location-form").style.display = "";
            document.getElementById("longitude").value = place.geometry.location.lat();
            document.getElementById("latitude").value = place.geometry.location.lng();
            document.getElementById("location").value = place.name;

            if (place.geometry.viewport) {
                bounds.union(place.geometry.viewport);
            } else {
                bounds.extend(place.geometry.location);
            }
            });
            for (let mark of markers){
                mark.addListener('click', function() {
                    console.log("{" + mark.getPosition().lat() + " , " + mark.getPosition().lng() + "}" );
                    document.getElementById("logLocationButton").style.display = "";
                    document.getElementById("log-location-form").style.display = "";
                    document.getElementById("longitude").value = mark.getPosition().lat();
                    document.getElementById("latitude").value = mark.getPosition().lng();
                    document.getElementById("location").value = mark.getTitle();
                });
            }
            map.fitBounds(bounds);
        });
    }, function() {
      handleLocationError(true, infoWindow, map.getCenter());
    });
    } else {
        // Browser doesn't support Geolocation
        handleLocationError(false, infoWindow, map.getCenter());
    }
}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
  infoWindow.setPosition(pos);
  infoWindow.setContent(browserHasGeolocation ?
    'Error: The Geolocation service failed.' :
    'Error: Your browser doesn\'t support geolocation.');
  infoWindow.open(map);
}

function showLocationForm(){
    document.getElementById("logLocationCard").style.display = "";
}

function hideLocationForm(){
    document.getElementById("logLocationCard").style.display = "none";
}

function getPastLocations(){
    fetch('/load-location').then(response => response.json()).then((locations) => {
        const pastLocations = document.getElementById('oldLocations');
        locations.forEach((location) => {
            pastLocations.appendChild(createLocationElement(location));
        });
    });
}

function createLocationElement(location){
    const locationElement = document.createElement('tr');
    const locationInfo = document.createElement('td');
    locationInfo.innerText = location.name + " on " + location.date;
    const deleteButton = document.createElement('i');
    deleteButton.innerText = 'delete';
    deleteButton.classList.add('material-icons');
    deleteButton.classList.add('right');
    deleteButton.addEventListener('click', () => {
        deleteLocation(location);
        locationElement.remove();
    });

    locationElement.appendChild(locationInfo);
    locationElement.appendChild(deleteButton);
    return locationElement;
}

async function deleteLocation(location){
    (async () =>{
        const params = new URLSearchParams();
        params.append('locationId', location.locationId);
        fetch('/delete-location', {method: 'POST', body:params});
    })();
}
