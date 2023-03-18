## Language specification
### NOTE: BREAKING CHANGES CAN STILL BE MADE
#### Note: [] means required, {} means optional, {@ @} means repeating

### Literals

- `nothing`
- Integer: `12345`
- Floating: `12345.6` (also `Infinity`, `-Infinity` and `NaN`)
- String: `"abc"` (escape characters HTML-style)
- Boolean: `true` and `false`
- List literal: `[{@value,@}] : type`
- Functions literals: `{fun ({@param1 {: type} {:= default_value},@})} {-> return_type}} [block]`

### Types
- Nothing: only valid value is `nothing`
- Integers: `int` (64 bits unsigned)
- Boolean: `truth`
- String: `text`
- Floating point: `rational` (equivalent to `double` in other languages)
- Function type: like function literals, but no function body and put between square brackets (ex: `fun[x : int, -> int]`)
- Sum types: `either[{@type,@}]`
- List: `list[type]`

### Statements
- Value: `let {var} name {: type} {:= value}` (`var` makes it a variable, type can be inferred)
- Assignment: `name := value`
- Simple conditional: `if [condition] [block]`
- Complex conditional: `if begin; {@clause;@} end`
    - Normal clause: `[condition] [block]`
    - Default clause: `default [block]`
- Simple conditional type cast: `when [object] is [type] do [block]`
- Complex conditional type cast: `when [object] is begin; {@clause;@} end`
    - Normal clause: `[type] [block]`
    - Default clause: `default [block]`
- For loop: `for x {in collection} {from a} {to b} [block]`
- While loop: `while condition [block]`
- Code block: `do [operation]` or `begin; {@operation;@} end`
- Yield: `yield [expression]` (return equivalent)
- `nothing` (no operation)
- `stop`, `skip` (`break`, `continue` equivalents)

### Symbols
- `+`, `-`, `*`, `/`, `^` (power)
- `+` (text and list concatenation)
- `*` (repeat text)
- `and`, `or`, `not` (not bitwise!)
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `:=` (assign, declare or name parameters)
- `.` (`b.a{ ( {@ param @} ) }` is `a(b,{ {@ param @}} )`, also used for field access)
- `:` (type annotation)
- `@` (access element in collection or text. returns nullable type)
- `+=` (append to end of mutable list)
- `()` (order of operations, function access)
- `[]` (list literals, subcomponents in type definitions)
- `,` (separator between elements)
- `?` (syntactic sugar for `type? = either[type,nothing]`)
- `import` (get a variable from the outside scope)
- `new` (make deep clone of object)

### Named parameters
- Function calling with named parameters by default: `my_function(a := 1, b := 10)`
- Parameter can be made unnamed using the `arg` keyword: `fun(arg a : int)`
- You always have the ability to specify unnamed parameters by name
- Named parameters can be in any order. Unnamed parameters are matched in order of appearance.

### Mutable types
- Type: `mut[type]`, always subtype of `type`
- Make an immutable type into a mutable type with `mut` prefix operator
- Change the value of a mutable object with `<-` infix operator
- `mut [collection]` also makes its elements mutable

### Generics in functions
- Example: `fun(x : list[anything as T]) -> T`, will return the same type as the list's components type
- Declare using the `as` keyword

### Standard library
- `write`, `writeline`: write object to the command line
- `panic`: print an error message and exit
- `ignore`: ignore the value
- `modulo(x,y)`: calculate the modulo
- `[list].size`: size of lists
- `[text].size`: size of text
- `as_list` : convert text into list
- `as_text`: convert object to text
- `read_line`, `read_truth`, `read_int`, `read_rational`: read an object from the command line

### Miscellaneous
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- `\` can be used to continue a line after a newline (including string literals and comments)
- Types are not reserved keywords and can be used as variable names
- Top level expressions must be of type `nothing`
- Trailing commas are valid but not necessary
- Pass by reference to object

### [Potential future additions](future.md)
