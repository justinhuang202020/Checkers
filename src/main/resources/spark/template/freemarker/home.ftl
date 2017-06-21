<!DOCTYPE html>
<html>

<!-- Head contains meta data and imports -->
<head>
    <link rel="stylesheet" href="css/home.css">

    <!-- define character set in use -->
    <meta charset="utf-8">
    <script src="js/jquery-2.1.1.js"></script>
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/html5bp.css">
    <script src="js/ways.js"></script>
    <script src="js/tiles.js"></script>
    <script src="js/maps.js"></script>
    <title>${title}</title>

</head>

<!-- Body contains the page content -->
<body>
<h1>Welcome to Maps!</h1>
<div id="canvas">
    <canvas id="map" height=500 width=500></canvas>
</div>
<div id="form">
        <p>Enter either lat, long, lat, long coordinates or 2 sets of streets to find shortest path!  </p>
        <textarea name="street1" value="" id="street1"
                          placeholder="Enter Street 1"></textarea>
            <textarea name="street2" value="" id="street2"
                      placeholder="Enter Street 2"></textarea>
             <textarea name="street3" value="" id="street3"
                          placeholder="Enter Street 3"></textarea>
            <textarea name="street4" value="" id="street4"
                      placeholder="Enter Street 4"></textarea><br>
             <br><p id = "result"></p>
             <p>Check box to toggle street suggestions!</p>
            <div id = "checkbox">
            <br><input type = "checkbox" id = "box" class = "acCheckBox">
            </div>
            <div id = wrapper>
            <div id = list>
            <ul id="suggestion1" class = "suggestions"></ul>
             <ul id="suggestion2" class = "suggestions"></ul>
              <ul id="suggestion3" class = "suggestions"></ul>
            <ul id="suggestion4" class = "suggestions"></ul><br>
            </div>
            <div id = buttons>
            <center><br><br><input type="submit" id = "submit" value="submit form!"></center>
           <center> <br> <input type = "submit" id = "clear" value = "clear path"></input><center>
           </div>
</div>
</body>

<!-- Make sure to close all your tags! -->
</html>