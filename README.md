# Wiles

This is a WIP programming language interpreter for my own language. The goal is to make a simple, C-style language with clean syntax inspired by Python and Pascal, that is also easier to understand for beginners. However, even more so, the goal is to learn how to make simple interpreters 😄

## FAQ
### Why is the language called Wiles?
As it is inspired by Pascal, I decided to also name my language after a mathematician, but unlike Pascal, one that is contemporary. As such, it's named after Andrew Wiles, who proved Fermat's Last Theorem.

### Are there plans to make the language object-oriented? What about other cool features, such as lambda expressions?
This is a one-man project mostly meant for myself to try out making an interpreter, so honestly, it depends on my time and motivation. I would like to first finish a functional draft of the language and interpreter before looking into any advanced features.

## Language specification
### NOTE: WORK IN PROGRESS, SUBJECT TO CHANGE

### Literals

- Integer: `12345`
- Floating: `12345.6`
- String: `"abc"`
- Boolean: `true` and `false`
- `nothing`

### Types
- Nothing: only valid value is `nothing`
- Integers: `byte`, `smallint`, `int`, `bigint`
- Boolean: `bit`
- String: `text`
- Floating point: `decimal` (equivalent to double in other languages)
- `Optional[type]` either a value of `type`, or `nothing`
- Other generic types: `list[type]`, `dict[type,type]`, `range[type]`

### Potential additions to types
- Integers: `infint` (infinite precision)
- Floating point: `exactdec` (equivalent to decimal)
- Generic types: `linkedlist[type]`, `set[type]`, `method[param1,param2][return_type]`

### Declaring
- Method: `method name(param1 : type, param2 : type) : return_type` (parameters and `return_type` optional)
- Immutable variable: `let name : type` (type can be inferred)
- Mutable variable: `let var name : type`
- Conditional: `if condition then [block] otherwise [block]` (`otherwise` optional)
- For loop: `for x in collection do [block]` (`in` keyword skipped when using `from to` construct)
- While loop: `while condition do [block]`

### Operators
- `+`, `-`, `*`, `/`, `^` (power)
- `and`, `or`, `not` (not bitwise!)
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `from`, `to`, `by` (range operators, `from 1 to 10 by 3`)
- `in` (element in collection)
- `:=` (assign or declare)
- `.` (method/field access)
- `:` (type annotation)
- `[]`, `()`, `,`

### Other keywords
- `stop`, `skip` (break, continue equivalents)
- `begin`, `end` (code blocks)
- `result` (return equivalent)

### Miscellaneous
- Declaring `main` method optional when using no other methods
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- Returning value done with `method_name := result` or with `result` keyword
- Garbage collection
- `nothing` can also be used to mean "no operation"
- `\` can be used to continue a line after a newline (including string literals)

### Other potential additions
- Classes (or at least structs) with `class` keyword
- Declare fields `readyonly` for getter with no setter, `public` for getter and setter

## Examples
### Hello World
```
writeline("Hello, world!")
```
### FizzBuzz
```
for i from 1 to 100 do begin
    let var my_text := ""
    if modulo(i, 3) = 0 then
        my_text.append("Fizz")
    if modulo(i, 5) = 0 then
        my_text.append("Buzz")
    if my_text = "" then 
        my_text := i.as_text
    writeline(my_text)
end 
```
### Minimum value

```
method min(my_list: list[int]) : int
begin
    if my_list.size = 0 then
        result -1
    min := my_list.get(0)
    for x in my_list do
        if x < min then
            min := x
end
```
