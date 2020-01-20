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

      let boxes = document.getElementsByClassName("thumbnails")[0];
      console.log(boxes);
      for (let i = 0; i < boxes.childElementCount; i++) {
        for (let j = 0; j < boxes.children[i].childElementCount; j++) {
          for (
            let k = 0;
            k < boxes.children[i].children[j].childElementCount;
            k++
          )
            boxes.children[i].children[j].children[k].innerText = `${
              response2[i + j + k].title
            }`;
        }
      }
    }
  });
};
