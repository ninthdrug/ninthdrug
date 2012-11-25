INSERT INTO websites(websiteid, name, description, hostname, port) 
VALUES (1, "testcms", "The it-do cmstest", "localhost", "8181");

INSERT INTO pages(websiteid, url, name, description, layoutid) 
VALUES (1, "http://www.cmstest.nl/index.html", "index", "indexpage", 1);

INSERT INTO layouts(layoutid, html, name, csslink) 
VALUES (1, "<html><head><title>dit is een test</title></head><body><h1>hello!!</h1></body></html>", "Indexpage", null);
