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
- Boolean: `true` (1) and `false` (0)
- List: `[1,2,3]`
- `nothing`

### Types
- Nothing: only valid value is `nothing`
- Integers: `byte`, `smallint`, `int`, `bigint`
- Boolean: `bit`
- String: `text`
- Floating point: `decimal` (equivalent to double in other languages)
- `Optional[type]` either a value of `type`, or `nothing`
- Other generic types: `list[type]`, `range[type]`, `dict[type,type]`

### Declaring
#### Note: {} means optional
- Method: `method name({param1 : type, param2 : type}) {: return_type}` (return assumed `nothing` if unspecified)
- Value `let {var} name {: type} := value` (`var` makes it mutable, type can be inferred)
- Conditional: `if condition then [block] {otherwise [block]}`
- For-in loop: `for x in collection do [block]`
- For-from loop: `for i from a to b` (syntactic sugar for `for i in range(a,b)`)
- While loop: `while condition do [block]`

### Operators
- `+`, `-`, `*`, `/`, `^` (power)
- `and`, `or`, `not` (not bitwise!)
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `:=` (assign or declare)
- `.` (method / field access)
- `:` (type annotation)
- `[]`, `()`, `,`

### Other keywords
- `stop`, `skip` (`break`/`return;`, `continue` equivalents)
- `yield` (return equivalent)
- `begin`, `end` (code blocks)

### Miscellaneous
- Declaring `main` method optional when using no other methods
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- Returning value done with `method_name := result` or with `yield` keyword
- `nothing` can also be used to mean "no operation"
- `\` can be used to continue a line after a newline (including string literals)
- Types are not reserved keywords and can be used as variable names
- Method potentially not returning value and potentially unreachable code are compilation errors 

### Potential additions (no promises!)
- `infint` (infinite precision integer)
- `exactdec` (stored as fraction, not as float)
- Other generic types:  `linkedlist[type]`, `set[type]`
- Method type: `method[param1,param2][return_type]`
- Classes with `class` keyword. Internally, maybe something like `dict[text,method]`?
- Declare fields `readonly` for getter with no setter, `public` for getter and setter
- Direct field access is impossible, instead it is trasferred to getters/setters
- Methods calling with named parameters: `range(1,10)` or `range(from: 1, to: 10)`
- Garbage collection

## Examples
### Hello World
```
writeline("Hello, world!")
```
### FizzBuzz
```
for i from 1 to 100 do
begin
    let var text := ""
    if modulo(i, 3) = 0 then
        text.append("Fizz")
    if modulo(i, 5) = 0 then
        text.append("Buzz")
    if my_text = "" then 
        text := i.as_text
    writeline(text)
end 
```
### Minimum value

```
method min(list: list[int]) : int
begin
    min := -1
    for x in list do
        if x < min then
            yield x
end

method main()
begin
    let list := [10, 4, 8]
    writeline(min(list))
end
```
