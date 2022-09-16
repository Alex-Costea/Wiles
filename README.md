# Wiles

This is a WIP programming language compiler for my own language. The goal is to make a simple, C-style language with clean syntax inspired by Python and Pascal, that is also easier to understand for beginners. However, even more so, the goal is to learn how to make simple compilers 😄

## FAQ
### Why is the language called Wiles?
As it is inspired by Pascal, I decided to also name my language after a mathematician, but unlike Pascal, one that is contemporary. As such, it's named after Andrew Wiles, who proved Fermat's Last Theorem.

### Are there plans to make the language object-oriented? What about other cool features like lambda expressions etc.?
This is a one-man project mostly meant for myself to try out making a compiler, so honestly, it depends on my time and motivation. I would like to first finish a functional draft of the language before looking into any advanced features.

## Language specification
### NOTE: WORK IN PROGRESS, SUBJECT TO CHANGE

Literals

- Integer: `12345`
- Floating: `12345.6`
- String: `"abc"`
- Boolean: `true` and `false`
- Optional: `nothing` (null equivalent)

Types
- Integers: `byte`, `smallint`, `int`, `bigint`, `infint` (infinite precision)
- Boolean: `bit`
- String: `text`
- Floating point: `decimal` (equivalent to double in other languages), `exactdec` (equivalent to decimal)
- Generic types: `list[type]`, `linkedlist[type]`, `optional[type]`, `dict[type,type]`, `range[type]`, `set[type]`

Declaring:
- Method: `method name(param1 : type, param2 : type) : return_type` (parameters and `return_type` optional)
- Variable: `var name : type` (type can be inferred)
- Constant: `let name : type`
- Conditional: `if condition then [block] otherwise [block]` (`otherwise` optional)
- For loop: `for var in collection do [block]` (`in` keyword skipped when using `from to` construct)
- While loop: `while condition do [block]`

Operators:
- `+`, `-`, `*`, `/`, `mod`, `^` (power)
- `and`, `or`, `not`
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `from`, `to`, `by` (range operators, `from 1 to 10 by 3`)
- `in` (element in collection)
- `:=`, `+=`, `-=`, `*=`, `/=`, `^=`
- `[]`, `()`, `,`, `.`, `:`

Other keywords:
- `stop`, `skip` (break, continue equivalents)
- `begin`, `end` (code blocks)

Miscellaneous:
- Declaring `main` method optional when using no other methods
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- Returning value done with `method_name := result`
- Garbage collection

## Examples
### Hello World
```
writeline("Hello, world!")
```
### FizzBuzz
```
for i from 1 to 100 do begin
    var my_text := ""
    if i mod 3 = 0 then
        my_text += "Fizz"
    if i mod 5 = 0 then
        my_text += "Buzz"
    if my_text = "" then writeline(i)
        otherwise writeline(my_text)
end
    
```
### Factorial

```
method factorial(x : int) : infint
begin
    if x = 0 then return 1
    otherwise return x * factorial(x - 1)
end

method main()
begin
    for i from 1 to 30 do
        writeline(factorial(i))
end

```
