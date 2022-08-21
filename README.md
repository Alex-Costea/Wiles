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
- Generic types: `optional<type>`, `list<type>`, `dictionary<type,type>`

Declaring:
- Method: `method name(param1 : type, param2 : type) : return_type`
- Variable: `let name : type`

## Example:

```
method factorial(x : int) : longint
begin
    if x=0 then return x
    else begin
        let last_factorial = factorial(x - 1) //type is inferred
        return x * last_factorial
    end
end
    
method main()
begin
    writeln(factorial(10))
end

```
