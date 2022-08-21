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
- Generic types: `list<type>`, `optional<type>`, `dictionary<type,type>`
- Range: `range` (integer types only)

Declaring:
- Method: `method name(param1 : type, param2 : type) : return_type` (`return_type` optional)
- Variable: `let name : type` (type can be inferred)
- Conditional: `if condition then [block] otherwise [block]` (`otherwise` optional)
- For loop: `for var in elems do [block]` (`elems` can be list, range, dictionary)
- While loop: `while condition do [block]`

Operators:
- `+`, `-`, `*`, `/`, `modulo`
- `and`, `or`, `not`
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `from`, `to` (range operator)
- `:=` (assignment)

Miscellaneous:
- declaring `main` method optional when using no other methods
- `;` inferred from newline

## Examples
### Hello World
```
writeline("Hello, world!")
```
### FizzBuzz
```
for i from 1 to 100 do begin
    let my_text := ""
    if i modulo 3 = 0 then
        my_text := text + "Fizz"
    if i modulo 5 = 0 then
        my_text := text + "Buzz"
    if my_text = "" then writeline(i)
        otherwise writeline(my_text)
    
```
### Factorial

```
method factorial(x : int) : longint
begin
    if x = 0 then return 1
    otherwise begin
        let last_factorial := factorial(x - 1)
        return x * last_factorial
    end
end

method main()
begin
    for i from 1 to 10 do
        writeline(factorial(i))
end

```
