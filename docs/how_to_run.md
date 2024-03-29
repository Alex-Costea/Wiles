
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

To use `wiles-web-backend`, add the `Wiles.jar` file manually to the following address before running Maven:

```
 /Users/[username]/.m2/repository/costea/Wiles/[version]/Wiles-[version].jar
```

(or equivalent for Windows)

Afterwards, you have to run `setup_server.sh` before running the backend.

You can either:
* Run only the backend, which will deploy the frontend statically
* Run the backend and frontend servers at the same time, by doing `npm run dev` and going to `localhost:3000`.

## Method 4

Run the backend in production mode.

Pull the repository from GitHub, add the `Wiles.jar` file as described above, and then do:

`sh reboot.sh`