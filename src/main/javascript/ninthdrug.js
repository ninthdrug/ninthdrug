(function(nd) {
  nd.get = function(url, success, fail) {
    var http = new XMLHttpRequest();
    http.open("GET", url, true);
    http.onreadystatechange = function() {
      if (req.readyState == 4) {
        if (http.status == 200) {
          if (success) success(http.responseText); 
        } else {
          if (fail) fail(http); 
        }
      }
    }
  }
}(this.nd = this.nd || {}));
