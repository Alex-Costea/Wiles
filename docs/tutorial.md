# Tutorial

## Table Of Contents

- [Tutorial](#tutorial)
    * [Table Of Contents](#table-of-contents)
    * [Hello World in Wiles](#hello-world-in-wiles)
    * [Texts](#texts)
        + [Multiline Text](#multiline-text)
        + [Unicode Support](#unicode-support)
        + [Escaping Characters](#escaping-characters)
        + [HTML5-Like Escapes](#html5-like-escapes)
    * [Numbers](#numbers)
        + [Integers](#integers)
        + [Rational Numbers](#rational-numbers)
    * [Basic Operations](#basic-operations)
        + [Integer vs Rational Division](#integer-vs-rational-division)
        + [Exponentiation](#exponentiation)
        + [Concatenation](#concatenation)
    * [Declarations and Assignments](#declarations-and-assignments)
        + [Type Inference and Explicit Types](#type-inference-and-explicit-types)
        + [Initialization](#initialization)
        + [Storing and Manipulating Strings](#storing-and-manipulating-strings)
        + [Type Safety and Flexibility](#type-safety-and-flexibility)
    * [Truth Values and Operations](#truth-values-and-operations)
        + [Equality Operations](#equality-operations)
        + [Comparison Operations](#comparison-operations)
        + [Truth Value Operations](#truth-value-operations)
    * [Conditionals](#conditionals)
        + [Complex conditionals](#complex-conditionals)
        + [Default clause](#default-clause)
    * [Code Blocks](#code-blocks)
        + [Complex Code Blocks](#complex-code-blocks)
    * [Lists](#lists)
        + [Accessing List Elements](#accessing-list-elements)
        + [Modifying Lists](#modifying-lists)
        + [List Functions](#list-functions)
    * [For Statement](#for-statement)
        + [Iterating Over Ranges](#iterating-over-ranges)
        + [Iterating with Index and Value](#iterating-with-index-and-value)
    * [While Statement](#while-statement)
    * [Type System](#type-system)
        + [Type Expressions](#type-expressions)
        + [Sum Types](#sum-types)
        + [Range Types](#range-types)
        + [Literal Value Types](#literal-value-types)
        + [The `literal` Function](#the-literal-function)
        + [The `type` type](#the-type-type)
        + [Subtyping operator](#subtyping-operator)
    * [Compile-Time Execution with `const`](#compile-time-execution-with-const)
    * [Functions](#functions)
        + [Named vs. Unnamed Parameters](#named-vs-unnamed-parameters)
        + [Default Values](#default-values)
        + [Returning Values](#returning-values)
        + [Function Types](#function-types)
        + [No-Parameter Shorthand](#no-parameter-shorthand)
    * [Level Scope](#level-scope)
        + [Recursive Functions](#recursive-functions)
        + [Data subtyping rules](#data-subtyping-rules)
        + [Function types rules](#function-types-rules)
        + [Compile-time Arguments in Functions](#compile-time-arguments-in-functions)
    * [Dictionaries](#dictionaries)
        + [Mutable Dictionaries](#mutable-dictionaries)
    * [Data Objects and Types](#data-objects-and-types)
        + [Data Types](#data-types)
        + [Recursive Data Types](#recursive-data-types)
    * [Standard Library](#standard-library)
        + [Infinity](#infinity)
        + [Truth values](#truth-values)
        + [Nothing](#nothing)
        + [Write to output](#write-to-output)
        + [Panic](#panic)
        + [Modulo](#modulo)
        + [Input read functions](#input-read-functions)
        + [Members of every object](#members-of-every-object)
        + [Size function](#size-function)
        + [Remove function](#remove-function)
        + [Keys function](#keys-function)
        + [Type definition functions](#type-definition-functions)
    * [Miscellaneous](#miscellaneous)

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

The syntax is `[elem1, elem2, ...] : type`. If all elements are the same type, the type annotation can be skipped:

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

## While Statement

The `while` statement executes a code block as long as a truth value remains true.

```wiles
let my_list := [1, 2, 3, 4, 5]
let var i := 0
let value_to_find := 4
while my_list[i] =/= value_to_find do
    i := i + 1
write_line("Value found at index " + i)
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

### The `type` type

All type expressions are themselves of type `type()`. 
You can also use `type(subtype)` to hold all expressions which are a subtype of something. For instance:

```wiles
let my_type : type(int | text) := int
```

### Subtyping operator

You can check whether a type is a subtype of another type using `of`.

```wiles
let x : int | text := 123
if begin
    # .type checks a value's runtime type
    x.type of int do write_line("x is int")
    x.type of text do write_line("x is text")
end
```

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

The keyword `const` cannot be used with variables for obvious reasons.
It also can't be applied to mutable values, since they allow changes at runtime, 
conflicting with the compile-time constant requirement.

---

## Functions

In Wiles, functions are first-class values. Let's start with the simplest case, a function that doesn't yield a value:

```wiles
let greet := fun(name : text)
    do write_line("Hello, " + name)
greet(name := "Alex")
```

The general structure for defining a function is `fun(param1 : type1, param2 : type2, ...) [codeblock]`.

To call a function, use the syntax `func_name(param1 := value1, param2 := value2, ...)`.

### Named vs. Unnamed Parameters

In Wiles, function parameters **must** be explicitly named in the function call. 
This makes function calls more readable and independent of argument order:

```wiles
greet(name := "Alex")
```

However, when argument order is intuitive, you can opt into unnamed parameters using the `arg` keyword:

```wiles
let greet := fun(arg name : text)
    do write_line("Hello, " + name)
greet("Alex")
```

Even when using `arg`, you can still explicitly name parameters:

```wiles
greet(name := "Alex")
```

Unlike named parameters, unnamed parameters are always matched in order.

### Default Values

Function parameters can have default values, allowing them to be omitted in function calls:

```wiles
let greet := fun(arg name := "Alex")
    do write_line("Hello, " + name)
greet() # Greets Alex
greet("Cameron") # Greets Cameron
```

Since the type can be inferred from the default value, an explicit type declaration is not required.

### Returning Values

Functions can also return values using `yield`:

```wiles
let add := fun(arg x : int, arg y : int) -> int
    do yield x + y
write_line(add(5, 5)) # 10
```

The `-> type` syntax specifies the return type. If the return type is clear at compile-time, it can be omitted:

```wiles
let add := fun(arg x : int, arg y : int)
    do yield x + y # Can only be `int`
write_line(add(5, 5)) # 10
```

If a function does not explicitly return a value, it yields `nothing` by default:

```wiles
let my_func := fun() do nothing
let x := my_func()
write_line(x) # Outputs: nothing
```

### Function Types

To define a function type, write the function signature without a body:

```wiles
let my_func : fun(name : text) := fun(name : text)
    do write_line("Hello, " + name)
```

Function types also support unnamed parameters, default values, and yielding type definitions.

### No-Parameter Shorthand

If a function takes no parameters, you can define it using only a code block:

```wiles
let greet := do write_line("Hello, world!")
greet() # Outputs: Hello, world!
```

### Const parameters

You can define a parameter as `const`, thus ensuring that it is known at compile time. This can be used for 
generics-like constructs:


```wiles
let add(const T : type(int | rational), x : T, y : T) -> T 
    do yield x + y
    
let a := add(int, 1, 2)
let b := add(rational, 1.0, 2.0)
```

---

## Level Scope

Values defined with **level scope** can be accessed from anywhere within the current level or any levels below it,
but **not** above it. This is achieved by using `def` instead of `let`.

```wiles
def a := 123
if 2 > 1 begin
    def b := 456
    # both a and b are accessible here
    write_line(a)
    write_line(b)
end
# only a is accessible here
write_line(a)
```

Note that this applies even when the declaration appears **after** its usage. `def` values are evaluated first.

```wiles
write_line(a) # Outputs 123
def a := 123
```

### Recursive Functions

The `def` keyword can also be used to create **recursive functions**. However, when using level scoping, the 
function's yielded type must be explicitly annotated, including if it's `nothing`:

```wiles
write_line(factorial(10)) # Outputs 3628800

def factorial(arg x : int) -> int # Explicit annotation required
begin
    if x <= 0 do yield 1
    # Wiles knows the factorial function will yield an int
    yield x * factorial(x - 1)
end
```

### Data subtyping rules

In order for a data type to be a subtype of another data type:
- The subtype can add new members, but can't remove them
- Existing values can get a default definition
- Existing default definitions can be overriden, but not removed

### Function types rules

In order for a function type to be considered a subtype of another, it must adhere to the usual 
Liskov substitution principles. This means that parameter types can be **broader**, 
while the yielding type can be **more specific**.

Additionally, new parameters can be added, 
and existing parameters can be given a default value or have their default value changed. 
Named parameters can be made unnamed by adding `arg`, but not the other way around.

### Compile-time Arguments in Functions

Function parameters can be marked as `const`, meaning they can only accept values known at compile-time:

```wiles
def const mutable_list := fun(const t : type()) -> type()
    do yield mutable(list(t))

let a : mutable_list(int) := ~[1, 2, 3]
let b : mutable_list(text) := ~["hi", "bye"]
```  

---

## Dictionaries

Dictionaries are used to map keys to values. 
Internally, they work like linked hash maps, and as such, the order is guaranteed to remain the same.
Here's how you define one:

```wiles
let my_dict := { 1 : "one", 2 : "two" } : int -> text
write_line(my_dict)
```

The syntax is `{key1 : value1, key2 : value2, ...} : key_type -> value_type`. 
Note that the key type, value type, or both can be inferred, like so:

```wiles
let my_dict := { 1 : "one", 2 : "two" }
write_line(my_dict)
```

To access a specific element, use `dict[key]`, just like with lists:

```wiles
let my_dict := { 1 : "one", 2 : "two" }
write_line(my_dict[1]) # Outputs: one
```

You can also grab a dictionary's keys with `dict.keys`. 
This returns a list of keys, which you can use to iterate over the dictionary:

```wiles
let my_dict := { 1 : "one", 2 : "two" }
for key in my_dict.keys do
    write_line(my_dict[key])
```

### Mutable Dictionaries

By default, dictionaries are immutable, but you can make them mutable with `~`, similar to how lists work.

```wiles
let my_dict := ~ { 1 : "one", 2 : "two" }
my_dict[1] := "one!!!"
my_dict[3] := "three"
my_dict.remove(2)
write_line(my_dict) # Outputs: { 1 : "one!!!", 3 : "three" }
```

---

## Data Objects and Types

Data objects are immutable values that hold multiple members, which are represented as identifiers.
You can access these members using the `.` operator. For example:

```wiles
let steve := << name := "Steve", age := 25 >>
write_line(steve.name) # Outputs: Steve
write_line(steve.age) # Outputs: 25
```

The syntax is `<< declaration1, declaration2, ... >>`. You can also add type definitions to these declarations:

```wiles
let steve := << name : text? := "Steve", age := 25 >>
write_line(steve.name) # Outputs: Steve
write_line(steve.age) # Outputs: 25
```

### Data Types

The syntax for defining data types is the same. When an identifier is used as a data type,
the values associated with it are considered **default values**, which can be overridden.

```wiles
let person_type := <<name : text? := nothing, age : int>>

let greet_person := fun(person : person_type)
begin
    if person.name =/= nothing do
        write("Hi, " + person.name + ". ")
    write_line("Your age is " + person.age)
end
    
greet_person(<<name := "Steve", age := 25>>)
greet_person(<<age := 40>>)
```

If any value is missing, it is treated as a type definition only and not as a complete data object.

### Recursive Data Types

Data types can be recursive using the `def` keyword. For example:

```wiles
def tree := <<
    value : int,
    left : tree?,
    right: tree?
>>
```

This allows the creation of complex structures like trees 
where each node contains references to other nodes of the same type.

---

## Standard Library

Here are the functions that come from the standard library, imported by default

### Infinity

The value `Infinity` represents infinity.

### Truth values

`true` represents true and `false` represents false.

### Nothing

`nothing` represents a lack of a value.

### Write to output

The `write` and `write_line` function are of type `fun(arg value : text)`. They write a value to output.
`write_line` also outputs a newline character at the end.

### Panic

The `panic` function is of type `fun(arg text := "Interpreter error")`. 
It displays an error message and then ends execution.

### Modulo

The `modulo` function is of type `fun(arg x : int, arg y : int) -> int`. It returns the modulo result.

### Run

The `run` function executes another function that takes no parameters, and returns its value.
Practically speaking, `run(func)` is the same as `func()`.

### Input read functions

These functions read a value from the input. They are: 

- `read_line`, of type `fun() -> text`
- `read_int`, of type `fun() -> int`
- `read_decimal`, of type `fun() -> decimal`

### Members of every object

All objects, including `nothing`, have these members, which can be accessed using the `.` operator

- `.type` gets an object's **runtime** type
- `.as_text` gets a textual representation of an object

### Size function

For lists, dictionaries and texts, `.size` will yield their size as an integer.

### Remove function

For lists and dictionaries, `.remove(at)` will remove an element at a specific index/key.

### Keys function

For dictionaries, `.keys` will return a list of the keys

### Type definition functions

The following are standalone: `int`, `string`, `decimal`, `anything`, `truth`

The following takes either 0 or 1 types as parameters: `type()`, `type(subtype)`

The following take 1 type as parameter: `list(type)`,  `mutable(type)`, `literal(type)`

The following takes 2 types as parameters: `dict(type1, type2)`

---

## Miscellaneous

- Trailing commas are always allowed.
- `\` can be used to continue an expression after a newline. 
This is usually unnecessary, but it can disambiguate some situations.
- All top level expressions must be of type `nothing`.
- Function closures are fully supported.