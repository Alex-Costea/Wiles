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

- `nothing`
- Integer: `12345`
- Floating: `12345.6`
- String: `"abc"`
- Boolean: `true` (1) and `false` (0)
- List literal: `list(1,2,3)`

### Types
- Nothing: only valid value is `nothing`
- Integers: `byte`, `smallint`, `int`, `bigint`
- Boolean: `bit`
- String: `text`
- Floating point: `rational` (equivalent to `double` in other languages)
- Method type: methods must be assigned to value
- Sum types: `either[type1,type2]`, either a value of `type1`, or of `type2`
- Other generic types: `list[type]`, `range[type]`

### Declaring
#### Note: {} means optional
- Method: `method({param1 : type, param2 : type}) {--> return_type}` (return assumed `nothing` if unspecified)
- Value `let {var} name {: type} := value` (`var` makes it mutable, type can be inferred)
- Conditional: `if condition [block] {otherwise [block]}`
- Conditional type casting: `when value is type [block] {otherwise block}`
- For-in loop: `for x in collection [block]`
- For-from loop: `for i from a to b` (syntactic sugar for `for i in range(from := a, to := b)`)
- While loop: `while condition [block]`
- Code block: `do nothing` (no operation), `do [operation]` or `begin [op1];[op2]; end`
- Yield: `yield [expression]` (return equivalent)
- `stop`, `skip` (`break`/`return;`, `continue` equivalents)

### Operators
- `+`, `-`, `*`, `/`, `^` (power)
- `and`, `or`, `not` (not bitwise!)
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `:=` (assign, declare or name parameters)
- `.` (method / field access)
- `:` (type annotation)
- `[]` (subtype declaration, collection access)
- `()` (order of operations, method access)
- `,` (separator between elements)

### Named parameters
- Methods calling with named parameters by default: `range(from := 1, to := 10)`
- If a method parameter's identifier starts with `arg`, it can be used without naming
- `arg` identifiers must be last
- When using one `arg` list, `my_method(a,b,c)` is the same as `my_method([a,b,c])`

### Miscellaneous
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- Returning value done with `method_name := result` or with `yield` keyword
- `\` can be used to continue a line after a newline (including string literals and comments)
- Types are not reserved keywords and can be used as variable names
- Method potentially not returning value is a compilation error
- `nothing` type is invalid in comparisons

### Potential additions (no promises!)
- `infint` (infinite precision integer)
- `decimal` (stored as fraction, not as float)
- Other generic types: `dict[type,type]`, `linkedlist[type]`, `set[type]`, `ref[type]`
- Using `method` types like first class objects 
- Classes with `class` keyword. Internally, maybe something like `dict[text,method]`?
- Declare fields `readonly` for getter with no setter, `public` for getter and setter
- Direct field access is impossible, instead it is transferred to getters/setters
- Warnings, e.g. unreachable code
- Garbage collection
- `maybe[type] = either[type,nothing]`
- `error` types
- `either` with more than 2 types

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
        text.append("Fizz")
    if modulo(i, 5) = 0 do
        text.append("Buzz")
    if text = "" do
        text := i.as_text
    writeline(text)
end 
```
### Minimum value

```
let min := method(args : list[int])  --> either[int,nothing]
begin
    if args.size = 0 do
        yield nothing
    min := args[0]
    for x in args.slice(from := 1) do
        if x < min do
            min := x
end

let result := min(10, 3, 55, 8)
when result is nothing do
    writeline("Error: no min found!")
otherwise do
    writeline("Min found: " + result)
```
