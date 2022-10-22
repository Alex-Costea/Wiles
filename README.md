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
- List literal: `list_of(1,2,3)`

### Types
- Nothing: only valid value is `nothing`
- Integers: `byte`, `smallint`, `int`, `bigint`
- Boolean: `bit`
- String: `text`
- Floating point: `rational` (equivalent to `double` in other languages)
- Method type: methods must be assigned to value
- Sum types: `either[type1,type2]`, either a value of `type1`, or of `type2`
- List: `list[type]`

### Commands
#### Note: {} means optional
- Method: `method({param1 : type, param2 : type}) {-> return_type}` (return assumed `nothing` if unspecified)
- Value: `let {var} name {: type} := value` (`var` makes it mutable, type can be inferred)
- Assignment: `name := value`
- Conditional: `if condition [block] {otherwise [block]}`
- Conditional type casting: `when value is type [block] {otherwise block}`
- For loop: `for x {in collection} {from a} {to b} [block]`
- While loop: `while condition [block]`
- Code block: `do [operation]` or `begin [op1];[op2]; end`
- Yield: `yield [expression]` (return equivalent)
- `nothing` (no operation)
- `stop`, `skip` (`break`/`return;`, `continue` equivalents)

### Operators
- `+`, `-`, `*`, `/`, `^` (power)
- `and`, `or`, `not` (not bitwise!)
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `:=` (assign, declare or name parameters)
- `+=`, `-=`, `*=`, `/=`, `^=` (syntactic sugar for `a +=` = `a := a +`)
- `.` (method / field access)
- `:` (type annotation)
- `[]` (subtypes, also `a[b]` = `a.get(b)`,`a[b] := c` = `a.set(to := c, a)`)
- `()` (order of operations, method access)
- `,` (separator between elements)
- `?` (syntactic sugar for `type? = either[type,nothing]`)

### Named parameters
- Methods calling with named parameters by default: `my_method(a := 1, b := 10)`
- If a method parameter's identifier starts with `arg`, it can be used without naming
- `arg` identifiers must be last
- When using one `arg` list, `my_method(a,b,c)` is the same as `my_method([a,b,c])`

### Miscellaneous
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- `\` can be used to continue a line after a newline (including string literals and comments)
- Types are not reserved keywords and can be used as variable names
- Method potentially not returning value is a compilation error
- `nothing` type is invalid in comparisons

### Potential additions (no promises!)
- `infint` (infinite precision integer)
- `decimal` (stored as fraction, not as float)
- Other generic types: `dict[type,type]`, `linkedlist[type]`, `set[type]`, `ref[type]`, `range[type]`
- Using `method` types like first class objects 
- Classes with `class` keyword. Internally, maybe something like `dict[text,method]`?
- Declare fields `readonly` for getter with no setter, `public` for getter and setter
- Direct field access is impossible, instead it is transferred to getters/setters
- Warnings, e.g. unreachable code
- Garbage collection
- `anything`, `anything?` types
- `either` with more than 2 types
- `error` types. syntax: `int? io_error, illegal_state_error`, internally also an `either`

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
        text += "Fizz"
    if modulo(i, 5) = 0 do
        text += "Buzz"
    if text = "" do
        text := i.as_text
    writeline(text)
end 
```
### Minimum value

```
let min := method(args : list[int]) -> int?
begin
    if args.size = 0 do
        yield nothing
    let var min_value := args[0]
    for x in args from 1 do
        if x < min_value do
            min_value := x
    yield min_value
end

let result := min(10, 3, 55, 8)
when result is nothing do
    writeline("Error: no min found!")
otherwise do
    writeline("Min found: " + result)
```
