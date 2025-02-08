# Tutorial (WIP)

## How to Set Up Wiles

### Method 1: Try It Online

You can experiment with Wiles right now! Just visit [this link](https://wiles.costea.in) and start coding.

### Method 2: Run Without Backend/Frontend

If you only need the core functionality, follow these steps:

1. Compile the `wiles-base` package by running the `compile_base.sh` script.
2. Execute the `main` function inside `wiles.Main`, passing your input file as an argument.

For example, if you're using the `Wiles.jar` file, run:

```sh
java -jar Wiles.jar -file example.wiles
```

Need more options? Use `-help` to see all available commands.

### Method 3: Run Backend and Frontend Locally

If you want the full experience, you can run both the backend and frontend on your machine. Choose one of these methods:

- Run only the backend (which serves a static frontend) by executing `run_dev.sh`,
then open `localhost:8080` in your browser.
- Run both the backend and frontend servers at the same time by also running `npm run dev`,
then open `localhost:3000`.

---

## Hello World in Wiles

Here's a simple "Hello, World!" program:

```wiles
# Print "Hello, world!" to output
write_line("Hello, world!")
```

A few things to note right away:
- No need for a `main` function—just write your code and go!
- Comments start with `#` and continue until the end of the line.
- Strings are enclosed in `""`.
- Functions are called in `func(args)` format.
- `write_line` prints a string followed by a newline. For no newline, use `write`.
- Statements are separated by newlines. To put multiple statements on one line, use `;`.

---

## Strings

### Multiline Strings

Strings can span multiple lines by default:

```wiles
write_line("This is line 1.
This is line 2.")
```

### Unicode Support

Unicode works out of the box:

```wiles
write_line("Îmi place când aplicațiile acceptă Unicode. 😊")
```

### Escaping Characters

Escaping is forgiving—if something isn't recognized, it just stays as-is. Here are the most common escapes:

- `\n` → Newline
- `\q` → Double quotes `"`
- `\b` → Backslash `\`

Example:

```wiles
write_line("Hello!\nMy name is \qAlex\q. Here's my favorite emoticon: \bo/")
```

Output:

```
Hello!
My name is "Alex". Here's my favorite emoticon: \o/
```

### HTML5-Like Escapes

Wiles supports HTML5-style character escaping, but with `\` instead of `&`:

```wiles
write_line("I owe my friend 100\euro;. I'll send it on Paypal\trade;")
```

Output:

```
I owe my friend €100. I'll send it on Paypal™
```

You can also use Unicode character codes:

```wiles
write_line("\#x1F1F7;\#x1F1F4;")
```

Output:

```
🇷🇴
```

---

## Numbers

### Integers

Just write them as-is:

```wiles
write_line(123)
```

No need to worry about limits—integers in Wiles internally use `BigInteger`, so they can be arbitrarily large:

```wiles
write_line(123456789123456789123456789123456789)
```

For negative numbers, use `-` (which is technically a unary minus operator):

```wiles
write_line(-123)
```

### Rational Numbers

Just use a decimal point:

```wiles
write_line(3.1415)
```

Wiles also supports `Infinity`, which can be negated as usual:

```wiles
write_line(Infinity)
write_line(-Infinity)
```

Under the hood, rational numbers internally use `BigDecimal` in `DECIMAL128` mode.

---

## Basic Operations

Basic arithmetic works like you'd expect:

```wiles
write_line(5 + 3 * 5)  # Outputs 20
```

Use parentheses to control order of operations:

```wiles
write_line((5 + 3) * 5)  # Outputs 40
```

Unary operators (`-` and, if you need it for whatever reason, `+`) work too:

```wiles
write(+10 - -10)  # Outputs 20
```

#### Integer vs Rational Division

Integer division returns an integer result:

```wiles
write_line(4 / 3)  # Outputs 1
```

To get a rational result, make at least one number a decimal:

```wiles
write_line(4 / 3.0)  # Outputs 1.333333333333...
write_line(4.0 / 3)  # Outputs 1.333333333333...
```

#### Exponentiation

Use `^` for exponentiation:

```wiles
write_line(2 ^ 10)  # Outputs 1024
```

Note the order of operations:

```wiles
write_line(-2 ^ 10)  # Outputs -1024
write_line((-2) ^ 10)  # Outputs -1024
```

### Concatenation

When you use the `+` operator between a string and any other type, it will result in string concatenation:

```wiles
write_line("I have " + 100 + " dollars in cash!")
```

---

## Declarations and Assignments

To declare a value, use the following syntax:

```wiles
let value := 123
```

In this case, `value` is constant, meaning it can't be reassigned.  
If you want to make it a variable, use the keyword `var`:

```wiles
let var value := 123
write_line(value) # Outputs 123
value := 456
write_line(value) # Outputs 456
```

In this example, `value` is first assigned to be `123`, and then reassigned to `456`, which is done by using the syntax `value := new_value`.

### Type Inference and Explicit Types

Wiles is a strongly typed language. In the examples above, the type of `value` is inferred 
automatically. To explicitly declare the type, use `:` followed by the type (in this case, `int`):

```wiles
let value : int := 123
```

We will go into more detail about types later.

### Initialization

Note that the identifier doesn't have to be immediately initialized. However, it must be initialized before being used. 
This also means the type cannot be inferred in such cases:

```wiles
let value : int
write_line("I don't know what value is!")
value := 123
write_line("Now I do! It is " + value)
```

### Storing and Manipulating Strings

You can also store strings in an identifier:

```wiles
let name := read_line()
write_line("Hello, " + name)
```

Here, `read_line()` reads a line of input, stores it in `name` (which is automatically inferred to be of type `text`),
and then concatenates and displays it.

### Type Safety and Flexibility

Note that even when the identifier represents a variable, its type can't be changed once it’s set.
For example, this won't compile:

```wiles
let var value := 123 # value is of type int
value := "Alex" # Compile Error! value is int, not text
```

To make this code compile, you need to specify that `value` can hold any type of value. 
You can do this by annotating its type as `anything`:

```wiles
let var value : anything := 123
value := "Alex"
write_line("The value is:" + value)
```