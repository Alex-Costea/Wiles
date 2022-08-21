# Wiles

## THIS IS EXTREMELY WIP

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
- Method: `method name(param1 : type, param2 : type) : return_type`
- Variable: `let name : type`
- Conditional: `if condition then [block] otherwise [block]` (`otherwise` optional)
- For loop: `for var in elems do [block]` (elems can be list, range, dictionary)
- While loop: `while condition do [block]`

Operators:
- `+`, `-`, `*`, `/`, `modulo`
- `and`, `or`, `not`
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `from`, `to` (range operator)

## Example:

```
method factorial(x : int) : longint
begin
    if x = 0 then return 1
    otherwise begin
        let last_factorial : int = factorial(x - 1)
        return x * last_factorial
    end
end
    
method main()
begin
    for i from 1 to 10 do
        writeline(factorial(i))
end

```
