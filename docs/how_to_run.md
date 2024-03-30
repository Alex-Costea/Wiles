
## Method 1

Try it out [online](https://wiles.costea.in)!

## Method 2

To use without the backend / frontend functionality:

1. Compile the `wiles-base` package by running the `compile_base.sh` script.

2. Run the function `main` within `wiles.Main`, with the input file as an argument. For instance, when using the JAR file `Wiles.jar`, run the command:

```
java -jar Wiles.jar example.wiles
```
You can also compile the source code by adding `--compile` as an argument, and run a compiled file using `--run`.

## Method 3

Using the backend and frontend locally.

Run `setup_server.sh` to set up the backend before running it.

You can either:
* Run only the backend, which will deploy the frontend statically, by running `run_dev.sh` and opening `localhost:8080`.
* Run the backend and frontend servers at the same time, by also running `npm run dev` and opening `localhost:3000`.

## Method 4

Deploy the backend online, in production mode.

Pull the repository from GitHub, add the `Wiles.jar` file as described above, and then do:

`sh reboot.sh`

Note that you'll have to configure the `wiles-web-backend` TLS credentials to use your own keys.