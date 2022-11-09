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
- List literal: `[a, b, c] : type`

### Types
- Nothing: only valid value is `nothing`
- Integers: `byte`, `smallint`, `int`, `bigint`
- Boolean: `bit`
- String: `text`
- Floating point: `rational` (equivalent to `double` in other languages)
- Function type: functions must be assigned to value
- Sum types: `either(type1,type2)`, either a value of `type1`, or of `type2`
- List: `list(type)`

### Statements
#### Note: [] means required, {} means optional
- Function: `{fun} {(param1 : type, param2 : type) {-> return_type}} [block]` (no return type implies `nothing`)
- Value: `let {var} name {: type} {:= value}` (`var` makes it mutable, type can be inferred)
- Assignment: `name := value`
- Conditional: `when [first clause] {other clauses}`
  - Default clause: `[condition]`
  - Type casting: `value is type [block]`
  - Other cases: `case [condition] [block]`
  - Else/default cases: `otherwise [block]`
- For loop: `for x {in collection} {from a} {to b} [block]`
- While loop: `while condition [block]`
- Code block: `do [operation]` or `begin; [op1];[op2]; end`
- Yield: `yield [expression]` (return equivalent)
- `nothing` (no operation)
- `stop`, `skip` (`break`/`return;`, `continue` equivalents)

### Symbols
- `+`, `-`, `*`, `/`, `^` (power)
- `and`, `or`, `not` (not bitwise!)
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `:=` (assign, declare or name parameters)
- `.` (function / field access)
- `:` (type annotation)
- `@` (access element in collection)
- `()` (order of operations, function access)
- `[]` (used in list literals)
- `,` (separator between elements)
- `?` (syntactic sugar for `type? = either(type,nothing)`)

### Named parameters
- Function calling with named parameters by default: `my_function(a := 1, b := 10)`
  - Parameter can be made unnamed using the `arg` keyword: `fun(arg a : int)`
- `func(a := a)` can also be written as `func(a)` (if the identifier names match)

### Miscellaneous
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- `\` can be used to continue a line after a newline (including string literals and comments)
- Types are not reserved keywords and can be used as variable names
- Function potentially not returning value is a compilation error
- `nothing` type is invalid in comparisons
- Garbage collection

### Potential additions (no promises!)
- `infint` (infinite precision integer)
- `decimal` (stored as fraction, not as float)
- Other types: `dict(type,type)`, `linked_list(type)`, `set(type)`, `ref(type)`
- Generic types, e.g.: `fun(a : *b) -> *b`
- Classes with `class` keyword. Internally, maybe something like `dict(text,fun)`?
- Declare fields `readonly` for getter with no setter, `public` for getter and setter
- Warnings, e.g. unreachable code
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
    when modulo(i, 3) = 0 do
        text := text + "Fizz"
    when modulo(i, 5) = 0 do
        text := text + "Buzz"
    when text = "" do
        text := i.as_text
    writeline(text)
end 
```
### Minimum value

```
let min := fun(list : list(int)) -> int?
begin
    when list.size = 0 do
        yield nothing
    let var min_value := list @ 0
    for x in list from 1 do
        when x < min_value do
            min_value := x
    yield min_value
end

let list := [10, 3, 55, 8] : int
let result := min(list)
when result is int do
    writeline("Min found: " + result)
otherwise do
    writeline("Error: no min found!")
```
