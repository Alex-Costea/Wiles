**Wiles** is a programming language inspired by Kotlin, Pascal and Python. It is designed with readability and type safety as main priorities. This project contains a fully-functional interpreter written in Kotlin and Java for the language.

Wiles supports features such as:
- `if`, `for` (including foreach functionality), `while`
- Basic operators for arithmetics, boolean logic and comparisons
- Functions as first-class objects using the `fun` keyword, including support for closures
- Sum types using the `either` keyword
- Support for types such as: integers (`int`), floating point numbers (`rational`), strings (`text`), booleans (`truth`), lists (`list`)
- Conditional type casting using `when` statements
- Opt-in nullability using the `?` symbol
- Opt-out named arguments as mandatory in function calls
- Newline as statement terminators, but no significant indentation
- Type inference in many cases
- First class types and type definitions at compile-time
- Generics

The informal specifications can be found [here](specifications.md).

## How to use

Download from one of the [releases](https://github.com/Alex-Costea/Wiles/releases/), or compile the code yourself.

Run the function `main` within `wiles.Main`, with the input file as an argument. For instance, when using the JAR file `Wiles.jar`, run the command:

```
java -jar Wiles.jar example.wiles
```
You can also compile the source code by adding `--compile` as an argument, and run a compiled file using `--run`.  

## FAQ
### What is the state of the project right now?
The interpreter is functional and includes the features found in the specifications. While the code is thoroughly tested automatically, there is no guarantee that bugs won't occur in edge cases.

### Why is the language called Wiles?
As it is inspired by Pascal, I decided to also name my language after a mathematician, but unlike Pascal, one that is contemporary. As such, it's named after Andrew Wiles, who proved Fermat's Last Theorem.

### Are there plans to make the language object-oriented? What about generics, first-class types or other cool features?
This is a one-man project mostly meant for myself to try out making an interpreter, so honestly, it depends on my time and motivation. However, I'd like to implement these features.

### Will there be a formal specification?
Unless this project blows up, probably not. Languages much bigger and widely used don't have a formal specification, so it doesn't seem like a priority. However, I am working on more thorough specifications.

## Examples
### Hello World
```
writeline("Hello, world!")
```
or
```
"Hello, world!".writeline
```
### FizzBuzz
```
for i from 1 to 100
begin
    let var text := ""
    if modulo(i, 3) = 0 do
        text := text + "Fizz"
    if modulo(i, 5) = 0 do
        text := text + "Buzz"
    if text = "" do
        text := i.as_text
    writeline(text)
end 
```
### Minimum value

```
let min := fun(list : list[int]) -> int?
begin
    let var min_value := list @ 0
    when min_value is int do
        for x in list from 1 do
            if x < min_value do
                min_value := x
    yield min_value
end

let read_list := begin
    let list := mut [] : int
    write("list size: ")
    let list_size := read_int()
    for i from 0 to list_size
    begin
        write("Element " + i + ": ")
        list.add(read_int())
    end
    yield list
end

let result := min(list := read_list())
when result is begin
    int do writeline("Min found: " + result)
    default do panic("Error: no min found!")
end
```
