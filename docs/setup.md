# How to Set Up Wiles

## Method 1: Try It Online

You can experiment with Wiles right now! Just visit [this link](https://wiles.costea.in) and start coding.

## Method 2: Run Without Backend/Frontend

If you only need the core functionality, follow these steps:

1. Compile the `wiles-base` package by running the `compile_base.sh` script.
2. Execute the `main` function inside `wiles.Main`, passing your input file as an argument.

For example, if you're using the `Wiles.jar` file, run:

```sh
java -jar Wiles.jar -file example.wiles
```

Need more options? Use `-help` to see all available commands.

## Method 3: Run Backend and Frontend Locally

If you want the full experience, you can run both the backend and frontend on your machine. Choose one of these methods:

- Run only the backend (which serves a static frontend) by executing `run_dev.sh`,
  then open `localhost:8080` in your browser.
- Run both the backend and frontend servers at the same time by also running `npm run dev`,
  then open `localhost:3000`.
