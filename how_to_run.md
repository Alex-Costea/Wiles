
## Method 1

Try it out [online](https://wiles.costea.in)!

## Method 2

To use without the backend / frontend functionality:

1. Download one of the [releases](https://github.com/Alex-Costea/Wiles/releases/), or compile the code yourself.

2. Run the function `main` within `wiles.Main`, with the input file as an argument. For instance, when using the JAR file `Wiles.jar`, run the command:

```
java -jar Wiles.jar example.wiles
```
You can also compile the source code by adding `--compile` as an argument, and run a compiled file using `--run`.

## Method 3

Using the backend and frontend locally.

To use `Wiles Web Backend`, you have to run `setup_server.sh` and add the `Wiles.jar` file manually to the following address before running Maven:

```
 /Users/[username]/.m2/repository/costea/Wiles/[version]/Wiles-[version].jar
```

(or equivalent for Windows)

To run in production mode, using HTTPS and CSRF protection, add `-Dspring.profiles.active=prod` when running the backend. Also, make sure to update the links to the SSL certificates accordingly.

You can either:
* Run only the backend, which will deploy the frontend statically
* Run the backend and frontend servers at the same time, by doing `npm run dev` and going to `localhost:3000`.