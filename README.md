# Wiles

## THIS IS A VERY WIP SPECIFICATION OF THE LANGUAGE

Literals

- Integer: `12345`
- Floating: `12345.6`
- String: `"abc"`
- Boolean: `true`

Types
- Integers: `byte`, `shortint`, `int`, `longint`
- Boolean: `bit`
- String: `text`
- Floating point: `decimal` (eqivalent to double in other languages)
- Generic types: `list<type>`, `optional<type>`, `dict<type,type>`, `range<type>`

Declaring:
- Method: `method name(param1 : type, param2 : type) : return_type` (`return_type` optional)
- Variable: `let name : type` (type can be inferred)
- Constant: `const name : type`
- Conditional: `if condition then [block] otherwise [block]` (`otherwise` optional)
- For loop: `for var in elems do [block]` (`elems` can be list, range, dictionary)
- While loop: `while condition do [block]`

Operators:
- `+`, `-`, `*`, `/`, `mod`, `^` (power)
- `and`, `or`, `not`
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `to`, `step` (range operators)
- `:=`, `+=`, `-=`, `*=`, `/=`

Miscellaneous:
- declaring `main` method optional when using no other methods
- `;` can be specified or inferred from newline

## Examples
### Hello World
```
writeline("Hello, world!")
```
### FizzBuzz
```
for i in 1 to 100 do begin
    let my_text := ""
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
method factorial(x : int) : longint
begin
    if x = 0 then return 1
    otherwise return x * factorial(x - 1)
end

method main()
begin
    for i in 1 to 10 do
        writeline(factorial(i))
end

```
