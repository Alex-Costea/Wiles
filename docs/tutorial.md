# Tutorial (WIP)

## How to set up Wiles

### Method 1

Try it out [online](https://wiles.costea.in)!

### Method 2

To use without the backend / frontend functionality:

1. Compile the `wiles-base` package by running the `compile_base.sh` script.

2. Run the function `main` within `wiles.Main`, with the input file as an argument. 
For instance, when using the JAR file `Wiles.jar`, run the command:

```sh
java -jar Wiles.jar -file example.wiles
```

Use `-help` to get all the options.

### Method 3

Using the backend and frontend locally.

You can either:
* Run only the backend, which will deploy the frontend statically, by running `run_dev.sh` and opening `localhost:8080`.
* Run the backend and frontend servers at the same time, by also running `npm run dev` and opening `localhost:3000`.

## Hello World in Wiles

Here is a basic Hello World program:

```wiles
# print "Hello World" to output
write_line("Hello, world!")
```

From here, we can see a couple features immediately:
- There is no need for a main function. You can just jump right in!
- Comments start with `#` and end on newline
- String literals are defined by writing the string between `"` characters.
- Functions are called with arguments in the format of `func(args)`
- `write_line` takes a string and prints it to output, adding a newline character. For no newline, use `write`!
- Statements are delimitated by newlines. To write two statements on one line, use `;`

## String Formatting

To start things off, note that strings can be multiline by default:

```wiles
write_line("This is line 1.
This is line 2.")
```

Also note that they support Unicode by default:

```wiles
write_line("Îmi place când aplicațiile acceptă Unicode. 😊")
```

On string escaping, note that escaping is forgiving by default.
Meaning, escaping errors are treated as just regular text.
The most basic escaped encodings are `\n` for newline, `\q` for double quotes and `\b` for backslash:

```wiles
write_line("Hello!\nMy name is \qAlex\q. Here's my favorite emoticon: \bo/")
```

Will display:

```
Hello!
My name is "Alex". Here's my favorite emoticon: \o/
```

Furthermore, Wiles supports HTML5-like character escaping, except starting with `\` instead of `&`:

```wiles
write_line("I owe my friend 100\euro;. I'll send it on Paypal&trade;")
```

Will display:

```wiles
I owe my friend €100. I'll send it on Paypal™
```

It can also be used to display Unicode characters without an entity name:

```wiles
write_line("\#x1F1F7;\#x1F1F4;")
```

Will display:

```
🇷🇴
```

## Numbers

### Integers

Integer literals are as simple as writing them out:

```wiles
write_line(123)
```

Note that there are no maximum or minimum values for integers. Internally, they are of type `BigInteger`:

```wiles
write_line(123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789)
```

To use negative integers, use the negative sign. This is technically a unary minus operator:

```wiles
write_line(-123)
```

### Rational numbers

Just use a comma:

```wiles
write_line(3.1415)
```

There's also a specific identifier for infinity, which is a valid rational value. It can be negated as usual.

```wiles
write_line(Infinity)
write_line(-Infinity)
```

Rational numbers in Wiles correspond to `BigDecimal` with **[INSERT NR HERE]** digits of significance.

### Number operations

You probably know addition, subtraction, multiplication and division from math. They work the same here.
Note the order of operation also works as expected.

```wiles
write_line(5 + 3 * 5)
```

You can use parentheses to change the default order of operations:

```wiles
write_line((5 + 3) * 5)
```

Minus and (if you need that for whatever reason) plus can also be unary operations:

```wiles
write(+10 - -10)
```

One thing to note is that division between two integers results in integer division.
In order to do rational division, make at least one a rational.

```wiles
write_line(4 / 3) #will print 1
write_line(4 / 3.0) #will print 1.333333333333333333333333333333333
write_line(4.0 / 3) #will print 1.333333333333333333333333333333333
```

Exponentiation also works as you would expect. The symbol used for it is `^`:

```wiles
write_line(2 ^ 10)
```