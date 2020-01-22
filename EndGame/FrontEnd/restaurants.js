window.onload = () => {
  var searchInput = document.getElementById("query");
  var form = document.getElementById("form_t");
  form.addEventListener("submit", e => {
    e.preventDefault();
  });
  searchInput.addEventListener("keydown", async event => {
    if (event.keyCode === 13) {
      const loc = event.target.value;
      console.log(loc);
      let response = await fetch(
        `https://herebackend.herokuapp.com/api/location/${loc}`,
        {
          method: "GET",
          mode: "cors"
        }
      );
      response = await response.json();

      let url = `https://herebackend.herokuapp.com/api/restaurants`;
      let response2 = await fetch(url, {
        method: "POST",
        mode: "cors",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          Latitude: response.Latitude,
          Longitude: response.Longitude
        })
      });
      response2 = await response2.json();
      console.log(response2);

      let boxes = document.getElementsByClassName("box");
      for (let i = 0; i < boxes.length; i++) {
        boxes[i].getElementsByClassName(
          "inner"
        )[0].innerHTML = `<h3>${response2[i].title}</h3>`;
      }
    }
  });
};

async function getGeocodeData(name) {
  let apiKey = "J5KRMOi97DHlFyKZUQa9L_9p8V3_V7zm9IEQgKpjj50";
  let url = `https://geocoder.ls.hereapi.com/6.2/geocode.json?apiKey=${apiKey}&searchtext=${name}`;
  let response = await fetch(url, {
    method: "GET",
    mode: "cors"
  });
  response = await response.json();
  //console.log(response);
  let data = {};
  let location = response.Response.View[0].Result[0].Location;
  data.coordinates = {
    lat: location.DisplayPosition.Latitude,
    lng: location.DisplayPosition.Longitude
  };
  data.bounds = {
    tl: location.MapView.TopLeft,
    br: location.MapView.BottomRight
  };
  (data.bounds.tr = {
    Latitude: data.bounds.br.Latitude,
    Longitude: data.bounds.tl.Longitude
  }),
    (data.bounds.bl = {
      Latitude: data.bounds.tl.Latitude,
      Longitude: data.bounds.br.Longitude
    });
  return data;
}

function generateCoordinates(bounds, number) {
  let coordinates = [];

  for (let i = 0; i < number; i++) {
    let maxx = Math.max(bounds.tr.Latitude, bounds.tl.Latitude);
    let minx = Math.min(bounds.tr.Latitude, bounds.tl.Latitude);
    let maxy = Math.max(bounds.tl.Longitude, bounds.bl.Longitude);
    let miny = Math.min(bounds.tl.Longitude, bounds.bl.Longitude);
    let lat = Math.random() * (maxx - minx) + minx;
    let long = Math.random() * (maxy - miny) + miny;
    coordinates.push({ lat, long });
  }
  //console.log(coordinates);
  return coordinates;
}

async function getCityScore(cityName) {
  let data = await getGeocodeData(cityName);
  //console.log(data);
  let coordinates = generateCoordinates(data.bounds, 1);
  let restaurants = coordinates.map(coordinate =>
    (async coordinate => {
      let url = `https://herebackend.herokuapp.com/api/restaurants`;
      let response = await fetch(url, {
        method: "POST",
        mode: "cors",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          Latitude: coordinate.lat,
          Longitude: coordinate.long
        })
      });
      response = await response.json();
      //console.log(response);
      return response;
    })(coordinate)
  );
  restaurants = await Promise.all(restaurants);
  //console.log(restaurants);
  // coordinates.forEach(async coordinate => {
  //     let url = `https://herebackend.herokuapp.com/api/restaurants`
  //     let response = await fetch(url, {
  //         method: 'POST',
  //         mode: 'cors',
  //         headers: {
  //             'Content-Type': 'application/json'
  //         },
  //         body: JSON.stringify({
  //             Latitude: coordinate.lat,
  //             Longitude: coordinate.long
  //         })
  //     })
  //     response = await response.json()
  //     //console.log(response);
  // })

  // let apiKey = "1CoH1sBcYBYTdr74jH9T9SfCd7MNiW7NjF5Jjzq-fc4"
  //console.log(JSON.stringify(data));
  // let trafficUrl = `https://traffic.ls.hereapi.com/traffic/6.2/flow.xml?bbox=${data.bounds.tl.Latitude},${data.bounds.tl.Longitude};${data.bounds.tl.Latitude+4},${data.bounds.tl.Longitude+4}&apiKey=${apiKey}`
  let trafficUrl = `https://herebackend.herokuapp.com/api/traffic`;
  // trafficUrl = encodeURIComponent(trafficUrl);
  let traffic = await fetch(trafficUrl, {
    method: "POST",
    mode: "cors",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ data })
  });
  traffic = await traffic.json();
  //Use jam factor
  // //console.log(traffic.RWS[0].RW[0].FIS[0].FI[0].CF[0].CN)

  traffic = traffic.RWS.reduce((pValue, cValue, currentIndex) => {
    toAdd = cValue.RW.reduce((pValue, cValue, currentIndex) => {
      toAdd = cValue.FIS.reduce((pValue, cValue, currentIndex) => {
        toAdd = cValue.FI.reduce((pValue, cValue, currentIndex) => {
          toAdd =
            (cValue.CF[0].CN * cValue.CF[0].FF) / (cValue.CF[0].JF + 1e-5);
          return pValue + toAdd;
        }, 0);
        return pValue + toAdd;
      }, 0);
      return pValue + toAdd;
    }, 0);
    return pValue + toAdd;
  }, 0);

  let literacy = await fetch("https://herebackend.herokuapp.com/api/literacy", {
    method: "POST",
    mode: "cors",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      Latitude: data.coordinates.lat,
      Longitude: data.coordinates.lng
    })
  });

  literacy = await literacy.json();
  // //console.log(literacy);
  crime = crime_set[cityName];
  crime = crime.safety_index;
  //console.log(crime);
  // let restaurants = await fetch("https://herebackend.herokuapp.com/api/restaurants", {
  //     method: 'POST',
  //     mode: 'cors',
  //     headers: {
  //         'Content-Type': 'application/json'
  //     },
  //     body: JSON.stringify({
  //         Latitude: data.coordinates.lat,
  //         Longitude: data.coordinates.lng
  //     })
  // });
  restaurants = restaurants.reduce((p, c, i) => {
    return p + c.length;
  }, 0);
  let touristAttractions = await fetch(
    "https://herebackend.herokuapp.com/api/touristAttractions",
    {
      method: "POST",
      mode: "cors",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        Latitude: data.coordinates.lat,
        Longitude: data.coordinates.lng
      })
    }
  );
  touristAttractions = await touristAttractions.json();
  // //console.log(touristAttractions);
  return (
    parseInt(traffic) +
    parseInt(crime) +
    parseInt(literacy.Literacy) +
    restaurants +
    touristAttractions.length
  );
  // let CF = traffic.RWS[0].RW[0].FIS[0].FI[0].CF
  //console.log(Math.log(traffic));
}
// getGeocodeData("California")
getCityScore("San Francisco").then(score => {
  console.log("SF: " + score);
});
getCityScore("Bangalore").then(score => {
  console.log("BG: " + score);
});

getCityScore("London").then(score => {
    console.log("LN: " + score);
  });

  getCityScore("Hyderabad").then(score => {
    console.log("HD: " + score);
  });

  getCityScore("Coimbatore").then(score => {
    console.log("CN: " + score);
  });
// //console.log(getCityScore("Bangalore"))
