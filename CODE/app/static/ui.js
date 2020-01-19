/**
* Adds a  draggable marker to the map..
*
* @param {H.Map} map                      A HERE Map instance within the
*                                         application
* @param {H.mapevents.Behavior} behavior  Behavior implements
*                                         default interactions for pan/zoom
// jaipur lat: 26.8787545,75.689723
// riet   lat: 26.8787545, lng:75.689723
*/
/*function calculateRouteFromAtoB (platform) {
  var router = platform.getRoutingService(),
    routeRequestParams = {
      mode: 'fastest;car',
      representation: 'display',
      routeattributes : 'waypoints,summary,shape,legs',
      maneuverattributes: 'direction,action',
      waypoint0: '26.9258,75.8066', // jaipur
      waypoint1: '26.8729,75.6953' //bhankrota
    };


  router.calculateRoute(
    routeRequestParams,
    onSuccess,
    onError
  );
} */



/**
 * Calculates and displays a car route from the Brandenburg Gate in the centre of Berlin
 * to Friedrichstraße Railway Station.
 *
 * A full list of available request parameters can be found in the Routing API documentation.
 * see:  http://developer.here.com/rest-apis/documentation/routing/topics/resource-calculate-route.html
 *
 * @param   {H.service.Platform} platform    A stub class to access HERE services
 */

function calculateRouteFromAtoB(platform, origin, destination) {
    var router = platform.getRoutingService(),
        routeRequestParams = {
            mode: 'fastest;car',
            representation: 'display',
            routeattributes: 'waypoints,summary,shape,legs',
            maneuverattributes: 'direction,action',
            waypoint0: origin, // Brandenburg Gate
            waypoint1: destination
        };


    router.calculateRoute(
        routeRequestParams,
        onSuccess,
        onError
    );
}
/**
 * This function will be called once the Routing REST API provides a response
 * @param  {Object} result          A JSONP object representing the calculated route
 *
 * see: http://developer.here.com/rest-apis/documentation/routing/topics/resource-type-calculate-route.html
 */
function onSuccess(result) {
    var route = result.response.route[0];
    /*
     * The styling of the route response on the map is entirely under the developer's control.
     * A representitive styling can be found the full JS + HTML code of this example
     * in the functions below:
     */
    addRouteShapeToMap(route);
    addManueversToMap(route);

}

/**
 * This function will be called if a communication error occurs during the JSON-P request
 * @param  {Object} error  The error message received.
 */
function onError(error) {
    alert('Can\'t reach the remote server');
}

/**
 * Boilerplate map initialization code starts below:
 */

// set up containers for the map  + panel
var mapContainer = document.getElementById('map'),
    routeInstructionsContainer = document.getElementById('panel');

//Step 1: initialize communication with the platform
// In your own code, replace variable window.apikey with your own apikey
var platform = new H.service.Platform({
    apikey: 'A7nlGT30Isgik-8tSUCn-F7kjVe2o0Fpgl1gL1EHHqw'
});

var defaultLayers = platform.createDefaultLayers();

//Step 2: initialize a map - this map is centered over Berlin
var map = new H.Map(mapContainer,
    defaultLayers.vector.normal.map, {
    center: { lat: 51.529412, lng: -0.172691 },
    zoom: 9,
    pixelRatio: window.devicePixelRatio || 1
});
// add a resize listener to make sure that the map occupies the whole container
window.addEventListener('resize', () => map.getViewPort().resize());

//Step 3: make the map interactive
// MapEvents enables the event system
// Behavior implements default interactions for pan/zoom (also on mobile touch environments)
var behavior = new H.mapevents.Behavior(new H.mapevents.MapEvents(map));

// Create the default UI components
var ui = H.ui.UI.createDefault(map, defaultLayers);

// Hold a reference to any infobubble opened
var bubble;

/**
 * Opens/Closes a infobubble
 * @param  {H.geo.Point} position     The location on the map.
 * @param  {String} text              The contents of the infobubble.
 */
function openBubble(position, text) {
    if (!bubble) {
        bubble = new H.ui.InfoBubble(
            position,
            // The FO property holds the province name.
            { content: text });
        ui.addBubble(bubble);
    } else {
        bubble.setPosition(position);
        bubble.setContent(text);
        bubble.open();
    }
}


/**
 * Creates a H.map.Polyline from the shape of the route and adds it to the map.
 * @param {Object} route A route as received from the H.service.RoutingService
 */
function addRouteShapeToMap(route) {
    var lineString = new H.geo.LineString(),
        routeShape = route.shape,
        polyline;

    routeShape.forEach(function (point) {
        var parts = point.split(',');
        lineString.pushLatLngAlt(parts[0], parts[1]);
    });

    polyline = new H.map.Polyline(lineString, {
        style: {
            lineWidth: 4,
            strokeColor: 'rgba(0, 128, 255, 0.7)'
        }
    });
    // Add the polyline to the map
    map.addObject(polyline);
    // And zoom to its bounding rectangle
    map.getViewModel().setLookAtData({
        bounds: polyline.getBoundingBox()
    });
}


/**
 * Creates a series of H.map.Marker points from the route and adds them to the map.
 * @param {Object} route  A route as received from the H.service.RoutingService
 */
function addManueversToMap(route) {
    var svgMarkup = '<svg width="18" height="18" ' +
        'xmlns="http://www.w3.org/2000/svg">' +
        '<circle cx="8" cy="8" r="8" ' +
        'fill="#1b468d" stroke="white" stroke-width="1"  />' +
        '</svg>',
        dotIcon = new H.map.Icon(svgMarkup, { anchor: { x: 8, y: 8 } }),
        group = new H.map.Group(),
        i,
        j;

    // Add a marker for each maneuver
    for (i = 0; i < route.leg.length; i += 1) {
        for (j = 0; j < route.leg[i].maneuver.length; j += 1) {
            // Get the next maneuver.
            maneuver = route.leg[i].maneuver[j];
            // Add a marker to the maneuvers group
            var marker = new H.map.Marker({
                lat: maneuver.position.latitude,
                lng: maneuver.position.longitude
            },
                { icon: dotIcon });
            marker.instruction = maneuver.instruction;
            group.addObject(marker);
        }
    }

    group.addEventListener('tap', function (evt) {
        map.setCenter(evt.target.getGeometry());
        openBubble(
            evt.target.getGeometry(), evt.target.instruction);
    }, false);

    // Add the maneuvers group to the map
    map.addObject(group);
}





Number.prototype.toMMSS = function () {
    return Math.floor(this / 60) + ' minutes ' + (this % 60) + ' seconds.';
}


//customized js

var orign;
var destination;
function updatemap() {
    orign = document.getElementById('origin-input').value;
    destination = document.getElementById('destination-input').value;
    console.log('start'+ orign, 'Desti'+ destination);
    // Now use the map as required...
    calculateRouteFromAtoB(platform, orign, destination );
    callToApi();
    
    //startClustering(map, airports);
}

// 26.9258,75.8066
//26.8729,75.6953
//show markers to map
function addMarkersToMap(map) {
    var parisMarker = new H.map.Marker({lat:26.9258, lng:75.8066});
    var parisMarker2 = new H.map.Marker({lat:26.8729,lng:75.6953});
    var dt = [parisMarker, parisMarker2];
    dt.map(function (item){
        map.addObject(item);
        console.log(item);
    })
    
    
}
addMarkersToMap(map);




//call api 

function callToApi(){
    var me = this;
        // Sending and receiving data in JSON format using POST method
        var xhr = new XMLHttpRequest();
        var url = "http://127.0.0.1:5000/prediction";
        console.log(url);
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var json = JSON.parse(xhr.responseText);
                //if no accident, alert user, else display icons
                if(json == null ) {window.alert('Hooray! No accidents predicted in your route. Click "Next Prediction" to reset map.');}
                else {
                    showAccidents2(json);
                    console.log('thisjsoncalled'+json);
                }
            }
        };
        var data = JSON.stringify({"origin": document.getElementById("origin-input").value,
                                    "destination": document.getElementById("destination-input").value,
                                    "datetime": document.getElementById("datetime-input").value});
                                    //console.log(data);
        xhr.send(data);
         
}
//adding markers to map after prediction
function showAccidents2(accident_json) {
		console.log('functioncalled');

        var count = Object.keys(accident_json.accidents).length;
        console.log(count);
        window.alert(count + ' accidents predicted. Zoom in and hover over icon to view probability. Click "Next Prediction" to reset map.');
        var markers = [];
    		for(var i = 0; i < count; i++)
        		{
        			//accident_json.accidents[i].lat, accident_json.accidents[i].lng);
					markers.push(new H.map.Marker({lat:accident_json.accidents[i].lat, lng:accident_json.accidents[i].lng}));
					
					
					markers.map(function (item){
					map.addObject(item);
					//console.log(item);
    })
        			
        		}
        
    }




//add clusters to the map 
/*
function startClustering(map, data) {
    // First we need to create an array of DataPoint objects,
    // for the ClusterProvider
    var dataPoints = data.map(function (item) {
      return new H.clustering.DataPoint(item.latitude, item.longitude);
    });
  
    // Create a clustering provider with custom options for clusterizing the input
    var clusteredDataProvider = new H.clustering.Provider(dataPoints, {
      clusteringOptions: {
        // Maximum radius of the neighbourhood
        eps: 32,
        // minimum weight of points required to form a cluster
        minWeight: 2
      }
    });
  
    // Create a layer tha will consume objects from our clustering provider
    var clusteringLayer = new H.map.layer.ObjectLayer(clusteredDataProvider);
  
    // To make objects from clustering provder visible,
    // we need to add our layer to the map
    map.addLayer(clusteringLayer);
  }
*/