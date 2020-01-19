class HereMap {

    constructor(appId, appCode, mapElement) {   
    this.appId = appId;
    this.appCode = appCode;
    this.platform = new H.service.Platform({
        "app_id": this.appId,
        "app_code": this.appCode
    });
    this.map = new H.Map(
        mapElement,
        this.platform.createDefaultLayers().normal.map,
        {
            zoom: 10,
            center: { lat: 37, lng: -121 }
        }
    );
    const mapEvent = new H.mapevents.MapEvents(this.map);
    const behavior = new H.mapevents.Behavior(mapEvent);
    this.geofencing = this.platform.getGeofencingService();
    this.currentPosition = new H.map.Marker({ lat: 37.21, lng: -121.21 });
    this.map.addEventListener("tap", (ev) => {
    var target = ev.target;
    this.map.removeObject(this.currentPosition);
    this.currentPosition = new H.map.Marker(this.map.screenToGeo(ev.currentPointer.viewportX, ev.currentPointer.viewportY));
    this.map.addObject(this.currentPosition);
    this.fenceRequest(["1234"], this.currentPosition.getPosition()).then(result => {
        if(result.geometries.length > 0) {
            alert("You are within a geofence!")
        } else {
            console.log("Not within a geofence!");
        }
    });
}, false);
    this.map.addObject(this.currentPosition);}
    draw(mapObject) { this.map.addObject(mapObject);}
    polygonToWKT(polygon) {
    const geometry = polygon.getGeometry();
    return geometry.toString(); 
}
    uploadGeofence(layerId, name, geometry) {
      const zip = new JSZip();
    zip.file("data.wkt", "NAME\tWKT\n" + name + "\t" + geometry);
    return zip.generateAsync({ type:"blob" }).then(content => {
        var formData = new FormData();
        formData.append("zipfile", content);
        return axios.post("https://gfe.api.here.com/2/layers/upload.json", formData, {
            headers: {
                "content-type": "multipart/form-data"
            },
            params: {
                "app_id": this.appId,
                "app_code": this.appCode,
                "layer_id": layerId
            }
        });
    });
    }
    fenceRequest(layerIds, position) {
    return new Promise((resolve, reject) => {
        this.geofencing.request(
            H.service.extension.geofencing.Service.EntryPoint.SEARCH_PROXIMITY,
            {
                'layer_ids': layerIds,
                'proximity': position.lat + "," + position.lng,
                'key_attributes': ['NAME']
            },
            result => {
                resolve(result);
            }, error => {
                reject(error);
            }
        );
    });
}
var platform = new H.service.Platform({
  apikey: 'ueqwdUG7FR6Dtxz4ojDyjPl7xPSMEKuzDEPJRqElZQA'
});

function calculateRouteFromAtoB (platform) {
  var router = platform.getRoutingService(),
    routeRequestParams = {
      mode: 'fastest;car',
      representation: 'display',
      routeattributes : 'waypoints,summary,shape,legs',
      maneuverattributes: 'direction,action',
      waypoint0: '52.5160,13.3779', // Brandenburg Gate
      waypoint1: '52.5206,13.3862'  // Friedrichstraße Railway Station
    };
  router.calculateRoute(
    routeRequestParams,
    onSuccess,
    onError
  );
}

function onSuccess(result) {
  var route = result.response.route[0];
//  addRouteShapeToMap(route);
  //addManueversToMap(route);
  addWaypointsToPanel(route.waypoint);
  //addManueversToPanel(route);
  //addSummaryToPanel(route.summary);
}
function addWaypointsToPanel(waypoints){
  var nodeH3 = document.createElement('h3'),
  waypointLabels = [],
  i;
  const map = new HereMap("D4tGRktFDkctrxbb6rLN", "ueqwdUG7FR6Dtxz4ojDyjPl7xPSMEKuzDEPJRqElZQA ", document.getElementById("map"));
  const lineString = new H.geo.LineString();
      
  for (i = 0;  i < waypoints.length; i += 1) {
  waypointLabels.push(waypoints[i].label)
  lineString.pushPoint({ lat: waypoints[i].mappedPosition.latitude, lng: waypoints[i].mappedPosition.longitude});
  }
  const start = async () => {
        
        // lineString.pushPoint({ lat: 37.2, lng: -121.002 });
        // lineString.pushPoint({ lat: 37.2, lng: -121.2 });
        // //lineString.pushPoint({ lat: 37, lng: -121 });
        const polygon = new H.map.Polyline(lineString);
        console.log(map.polygonToWKT(polygon));
        map.draw(polygon);
        const geofenceResponse = await map.uploadGeofence("1234", "Nic Secret Layer", map.polygonToWKT(polygon));
    };

    start();
  nodeH3.textContent = waypointLabels.join(' - ');
  routeInstructionsContainer.innerHTML = '';
  routeInstructionsContainer.appendChild(nodeH3);
}

};