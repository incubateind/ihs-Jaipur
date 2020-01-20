function myfunc()
{
  var lo=document.getElementById("longitude").value;
  var la=document.getElementById("latitude").value;

// function moveMapToBerlin(map){
//     map.setCenter({lat:la, lng:lo});
//     map.setZoom(14);
//   }
  
  /**
   * Boilerplate map initialization code starts below:
   */
  
  //Step 1: initialize communication with the platform
  // In your own code, replace variable window.apikey with your own apikey
  var platform = new H.service.Platform({
    'apikey': '{rqcFGLBcSJotmv5HTdiSXLuVpP7VT3bBS_pR8kOB9D4}'
  });
  var defaultLayers = platform.createDefaultLayers();
  
  //Step 2: initialize a map - this map is centered over Europe
  var map = new H.Map(document.getElementById('MAP'),
    defaultLayers.vector.normal.map,{
        zoom: 4,
        center: {lat:la, lng:lo},
    
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
  
       
  // Now use the map as required...
  // window.onload = function () {
  //   moveMapToBerlin(map);
  // }
}