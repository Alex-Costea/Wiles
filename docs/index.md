**Wiles** is a programming language inspired by Kotlin, Pascal and Python. It is designed with readability and type safety as main priorities. This project contains a fully-functional interpreter written in Kotlin and Java for the language, which can also be hosted online.

Wiles supports features such as:
- `if`, `for` (including foreach functionality), `while`
- Basic operators for arithmetics, boolean logic and comparisons
- Functions as first-class objects using the `fun` keyword, including support for closures
- Sum types: `type1 or type2`
- Support for types such as: integers (`int`), decimal numbers (`decimal`), strings (`text`), booleans (`truth`), lists (`list`), hashmaps (`dict`), structs (`data`)
- Pattern matching on types using `when` statements
- Opt-in nullability using the `?` symbol
- Opt-out named arguments in function calls
- Newline as statement terminators, but no significant indentation
- Type inference in many instances
- Type definitions at compile-time
- Generics in function definitions

The informal specifications can be found [here](specifications.md).

## Changelogs

See [here](changelog.md).

## How to use

Try it out [online](https://wiles.costea.in)!

For other methods of using it, go [here](how_to_run.md).

## FAQ
### What is the state of the project right now?
The interpreter is functional and includes the features found in the specifications. While the code is thoroughly tested automatically, there is no guarantee that bugs won't occur in edge cases.

### Why is the language called Wiles?
As it is inspired by Pascal, I decided to also name my language after a mathematician, but unlike Pascal, one that is contemporary. As such, it's named after Andrew Wiles, who proved Fermat's Last Theorem.

### Are there plans to add more advanced features to the language?
This is a one-man project mostly meant for myself to try out making an interpreter, so honestly, it depends on my time and motivation.

### Will there be a formal specification?
Unless this project blows up, probably not. Languages much bigger and widely used don't have a formal specification, so it doesn't seem like a priority. However, I am working on more thorough specifications.

## Examples
### Hello World
```
write_line("Hello, world!")
```
or
```
"Hello, world!".write_line
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
    write_line(text)
end 
```
### Minimum value

```
typedef number := int or decimal

let min := fun(list : list[number as T]) -> T?
begin
    if list.size = 0 do yield nothing
    let var min_value := list.get(0)
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
        list.add(at := i, read_int())
    end
    yield list
end

let result := min(list := read_list())
when result is nothing do panic("Error: no min found!")
write_line("Min found: " + result)
```
