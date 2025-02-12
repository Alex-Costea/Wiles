# Tutorial (WIP)

## Hello World in Wiles

Here's a simple "Hello, World!" program:

```wiles
# Print "Hello, world!" to output
write_line("Hello, world!")
```

A few things to note right away:
- No need for a `main` function—just write your code and go!
- Comments start with `#` and continue until the end of the line.
- Texts (also known as strings) are enclosed in `""`.
- Functions are called in `func(args)` format.
- `write_line` prints a text followed by a newline. For no newline, use `write`.
- Statements are separated by newlines. To put multiple statements on one line, use `;`.

---

## Texts

### Multiline Text

Text can span multiple lines by default:

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

### Integer vs Rational Division

Integer division returns an integer result:

```wiles
write_line(4 / 3)  # Outputs 1
```

To get a rational result, make at least one number a decimal:

```wiles
write_line(4 / 3.0)  # Outputs 1.333333333333...
write_line(4.0 / 3)  # Outputs 1.333333333333...
```

### Exponentiation

Use `^` for exponentiation:

```wiles
write_line(2 ^ 10)  # Outputs 1024
```

Note the order of operations:

```wiles
write_line(-2 ^ 10)  # Outputs -1024
write_line((-2) ^ 10)  # Outputs 1024
```

### Concatenation

When you use the `+` operator between a text and any other type, it will result in text concatenation, meaning,
the two values will be combined into a new text:

```wiles
write_line("I have " + 100 + " dollars in cash!")
```

Output:

```
I have 100 dollars in cash!
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

Note that even when the identifier represents a variable, its type can't be changed once it's set.
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

---

## Truth Values and Operations

Truth values, also known as Booleans, represent either `true` or `false`. They are of type `truth`.

```wiles
let value1 := true
let value2 : truth := false
write_line(value1) # Outputs true
write_line(value2) # Outputs false
```

### Equality Operations

The equality operations `=` (equals) and `=/=` (not equals) return a truth value:

```wiles
write_line(1 = 1) # outputs true
write_line(1 = 2) # outputs false
write_line(1 =/= 1) # outputs false
write_line(1 =/= 2) # outputs true
```

These operations can be used with all object types, including `text` and even `truth` itself:

```wiles
write_line("abc" = "abc") # outputs true
write_line(true =/= false) # outputs true
```

### Comparison Operations

For numbers (whether `int`, `decimal`, or a mix of both),
you can also use the comparison operators `>` (greater than), `>=` (greater than or equal to),
`<` (less than), and `<=` (less than or equal to):

```wiles
write_line(1 < 1) # outputs false
write_line(1 <= 1) # outputs true
write_line(1 > 1) # outputs false
write_line(1 >= 1) # outputs true
write_line(1 < 1.5) # outputs true
write_line(1 <= 1.5) # outputs true
write_line(1 > 1.5) # outputs false
write_line(1 >= 1.5) # outputs false
```

### Truth Value Operations

You can also perform operations on truth values themselves: `and`, `or`, and `not`.

- `and` returns `true` only if both values are `true`.
- `or` returns `true` if at least one of the two values is `true`.
- `not` works on a single value and returns `true` only if that value is `false`.

```wiles
write_line(true and true) # outputs true
write_line(true and false) # outputs false
write_line(true or false) # outputs true
write_line(false or false) # outputs false
write_line(not true) # outputs false
write_line(not false) # outputs true
```

---

## Conditionals

Conditionals run a block of commands if they receive a `true` value.
The basic syntax looks like this: `if [condition] [codeblock]`:

```wiles
let name := read_line()
if name = "Alex" do write_line("Hi Alex, welcome back!")
```

We'll dive deeper into code blocks later, but the simplest format is `do [operation]`.

### Complex conditionals

The example above shows a basic conditional. But you can also have more complex conditionals, 
where the block corresponding to the first `true` value gets executed. Here's how it's written:

```wiles
let name := read_line()
if begin
    name = "Alex" do write_line("Hi Alex, welcome back!")
    name = "Cameron" do write_line("Hi Cameron, how's it going?")
end
```

The general syntax for complex conditionals is `if begin`, 
followed by a list of conditions (separated by newlines or `;`), and ending with `end`.

Note that the indentation (the spaces at the start of lines between `begin` and `end`) is optional
but recommended for readability.

### Default clause

You can also add a `default` condition at the end, which will run no matter what:

```wiles
let name := read_line()
if begin
    name = "Alex" do write_line("Hi Alex, welcome back!")
    name = "Cameron" do write_line("Hi Cameron, how's it going?")
    default do write_line("I don't know who you are, " + name)
end
```
---

## Code Blocks

As mentioned earlier, the simplest code block starts with `do`, followed by a single operation:

```wiles
let number := read_int()
if number > 100 do write_line("Your number " + number + " is big enough!")
```

In this example, `read_int` reads an integer from the input, and the code block is simply `do write_line(...)`.

### Complex Code Blocks

For more complex code blocks, you start with `begin`, followed by a series of statements separated by newlines or `;`, 
and end with the keyword `end`. Just like with complex conditionals, it's a good idea to use indentation for clarity.

```wiles
let number := read_int()
if number > 100 begin
    write_line("Your number " + number + " is big enough!")
    write_line("If it was 10 times bigger, it'd be " + number * 10)
end
```

---

## Lists

A list is a data structure that holds multiple elements. Example:

```wiles
let my_list := [1, 2, 3] : int
```  

The syntax is `[elem1, elem2, ...] : type`. You can include a trailing comma if you want.  
If all elements are the same type, the type annotation can be skipped:

```wiles
let my_list := [1, 2, 3]
```  

But if the elements have different types, you **must** specify a type.  
For example, to allow any type of object:

```wiles
let my_list := [1, false, "hi!"] : anything
```  

### Accessing List Elements

To get an element from a list, use `list[index]`:

```wiles
let my_list := [1, 2, 3]
write_line(my_list[0]) # Outputs 1
```  

Lists in Wiles (like most languages) are **zero-based**, meaning the first element is at index `0`.

### Modifying Lists

By default, lists are **immutable**—once created, they can't be changed.  
But if you need a **mutable** list, use the `~` operator:

```wiles
let my_list := ~[1, 2, 3]
my_list[0] := 4
write_line(my_list) # Outputs [4, 2, 3]
my_list[3] := 1
write_line(my_list) # Outputs [4, 2, 3, 1]
```  

### List Functions

You can remove an element at a specific index like this:

```wiles
let my_list := ~[1, 2, 3]
my_list.remove(1)
write_line(my_list) # Outputs [1, 3]
```  

The syntax is `list.remove(index)`. We'll cover the `.` operator in more detail later.

To get a list's size, use `.size` (works for both mutable and immutable lists):

```wiles
let my_list := [1, 2, 3]
write_line(my_list.size) # Outputs 3
```

---

## For Statement

To loop through a list, use the `for` statement. The syntax is `for elem in list [codeblock]`. Example:

```wiles
for x in [1, 2, 3] do
    write_line("Current element: " + x)
```

### Iterating Over Ranges

You can also loop through a range of numbers using the `...` operator:

```wiles
for i in 0...100 do
    write_line("Current i: " + i) # Outputs numbers from 0 to 99
```  

The syntax is `x...y`, where `x` is the starting value (inclusive) and `y` is the ending value (exclusive).  
If `y` is smaller than `x`, the range counts **backward**:

```wiles
for i in 99...-1 do
    write_line("Current i: " + i) # Outputs numbers from 99 to 0
```

### Iterating with Index and Value

In practice, this structure is useful when you need both the index and the value:

```wiles
for i in 0 ... list.size
begin
   let elem := list[i]
   write_line("i: " + i + "; elem: " + elem)
end
```

---

## Type System

Wiles is a strongly typed language where every object has both a compile-time type and a runtime type. The compile-time 
type is determined through type inference or explicit annotations. Here are some common types:

```wiles
let a : int := 123
let b : decimal := 123.4
let c : text := "hello"
let d : truth := true
let e : list(int) := [1, 2, 3]
let f : mutable(list(int)) := ~[1, 2, 3]
let g : anything := 123 # can hold any compile-time value
```

### Type Expressions

Types in Wiles are first-class values. This means they can be assigned to variables just like any other value:

```wiles
let int_synonym : type := int
let list_of_ints := list(int)
```

Additionally, `list`, `mutable`, and similar constructs are functions that operate at compile time. Their arguments 
must also be known at compile time. This will be explored further in the `const` expression.

### Sum Types

Sum types allow a variable to hold multiple possible types, using the `|` operator:

```wiles
let a : int | decimal := 123
```

Here, `a` can be either an `int` or a `decimal`, but nothing else.

A common case is `my_type | nothing`, which has a shorthand syntax:

```wiles
let a : int? := 123
```

Note that `anything` accepts any value **except** `nothing`. For a truly universal type, use `anything?`.

### Range Types

Range types restrict values to a specific numeric range:

```wiles
let age : 0 ... 100 := 20
```

### Literal Value Types

A type can also be a specific literal value, as long as it is known at compile time:

```wiles
let status : "accepted" := "accepted"
```

This is particularly useful in combination with sum types:

```wiles
let statuses := "accepted" | "rejected"
let status : statuses := "accepted"
```

### The `literal` Function

In some cases, there may be ambiguity between whether an expression should be interpreted as a type constraint or a 
literal value. The `literal` function resolves this ambiguity:

```wiles
let a : 0 ... 100 := 17 # any value between 0 and 100
let b : literal(0 ... 100) := 0 ... 100 # stores the range itself as a value

let c : int := 123 # any integer value
let d : literal(int) := int # stores the type `int` itself as a value
```

The `literal` function ensures that the right-hand side is treated as an explicit value rather than a constraint. This 
prevents confusion when types and values have similar syntax.

---

## Compile-Time Execution with `const`

Wiles supports compile-time execution, allowing values to be computed before runtime for improved type safety. 
This is especially useful when a value needs to be known at compile time to apply a type annotation.

```wiles
let a : int := 123  # `int` is known at compile time
let my_int := int
let b : my_int := 456  # `my_int` is also known at compile time here
```

If you want to **explicitly** ensure a value is known at compile time, you can use `const`:

```wiles
let const my_int := int
```

This will cause a compile-time error if the value relies on any runtime information. 
Even a single runtime-dependent piece of data will invalidate the `const`.

Internally, Wiles tracks whether values comply with compile-time execution. 
If you use stateful functions, like `read_int`, the value will be marked as runtime-only, 
preventing it from being used in contexts that require compile-time knowledge.

Even when not working with type literals, `const` can still be useful to enforce compile-time execution 
for optimization purposes, ensuring that certain values are calculated ahead of time for better performance.