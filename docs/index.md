**Wiles** is a programming language

Full tutorials on Wiles functionality can be found [here](tutorial.md).

## How to use

Try it out [online](https://wiles.costea.in)!

For other methods of using it, go [here](setup.md).

## Examples
### Hello World
```wiles
write_line("Hello, world!")
```
### FizzBuzz
```
for i in 1...101
begin
    let var text := ""
    if modulo(i, 3) = 0 do
        text := text + "Fizz"
    if modulo(i, 5) = 0 do
        text := text + "Buzz"
    if text = "" do
        text := i.as_text
    write_line(text)
end 
```
### Minimum value

TODO: update