# My Portfolio
Welcome to the repository for my portfolio.

In order to build the static site, run the following from `portfolio/src/main/blog`:

```
jekyll build -d ../webapp
```

In order to run a development server, run the following from `portfolio`:

```
mvn package appengine:run
```

And in order to deploy, run the following from `portfolio`:

```
mvn package appengine:deploy
```