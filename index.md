This is a WIP programming language interpreter for my own language. The goal is to make a simple, C-style language with clean syntax inspired by Python and Pascal, that is also easier to understand for beginners. However, even more so, the goal is to learn how to make simple interpreters 😄

The informal specifications can be found [here](specifications.md).

## FAQ
### What is the state of the project right now?
The parser is functional and includes the features found in the specifications, however it's not fully tested and cannot be relied upon just yet. The interpreter hasn't been started yet.

### Why is the language called Wiles?
As it is inspired by Pascal, I decided to also name my language after a mathematician, but unlike Pascal, one that is contemporary. As such, it's named after Andrew Wiles, who proved Fermat's Last Theorem.

### Are there plans to make the language object-oriented? What about other cool features?
This is a one-man project mostly meant for myself to try out making a parser and interpreter, so honestly, it depends on my time and motivation. I would like to first finish a functional draft of the language and interpreter before looking into any advanced features.

### Will there be a formal specification?
Unless this project blows up, probably not. Languages much bigger and widely used don't have a formal specification, so it doesn't seem like a priority.

## Examples
### Hello World
```
writeline("Hello, world!")
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
let min := fun(list : list[integer]) -> integer?
begin
    let var min_value := list @ 0
    if min_value is integer do
        for x in list from 1 do
            if x < min_value do
                min_value := x
    yield min_value
end

let result := min(list := [10, 3, 55, 8])
when result is integer do
    writeline("Min found: " + result)
default do
    writeline("Error: no min found!")
```
