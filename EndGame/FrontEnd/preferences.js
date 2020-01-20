function freeze(){
    var button = document.querySelector('.button');

    button.onclick = function () {
     var red = Math.floor(Math.random() * 256);
     var blue = Math.floor(Math.random() * 256);
     var green = Math.floor(Math.random() * 256);
    
     this.style.backgroundColor = "rgb(" + red + "," + green + "," + blue + ")";
    };
    return;
}
