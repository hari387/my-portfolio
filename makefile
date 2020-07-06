

webapp:
	jekyll build -s portfolio/src/main/blog -d portfolio/src/main/webapp

run: webapp
	cd portfolio && mvn package appengine:run && cd ..

deploy: webapp
	cd portfolio && mvn package appengine:deploy && cd ..
