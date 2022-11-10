## Language specification
### NOTE: WORK IN PROGRESS, SUBJECT TO CHANGE
#### Note: [] means required, {} means optional, {@ @} means repeating

### Literals

- `nothing`
- Integer: `12345`
- Floating: `12345.6`
- String: `"abc"`
- Boolean: `true` (1) and `false` (0)
- List literal: `[{@value,@}] : type`
- Functions literals: `{fun} {({@param1 : type := value,@}) {-> return_type}} [block]` (no return type implies `nothing`)

### Types
- Nothing: only valid value is `nothing`
- Integers: `byte`, `smallint`, `int`, `bigint`
- Boolean: `bit`
- String: `text`
- Floating point: `rational` (equivalent to `double` in other languages)
- Function type: like function literals, but no function body
- Sum types: `either[type1,type2,type3]`
- List: `list[type]`

### Statements
- Value: `let {var} name {: type} {:= value}` (`var` makes it mutable, type can be inferred)
- Assignment: `name := value`
- Simple conditional: `if [condition] {then} [block]`
- Complex conditional: `when [first clause]; {@other clauses;@} [default clause]`
    - Type casting: `case value is type {then} [block]`
    - First clause: `{case} [condition] {then} [block]`
    - Other clauses: `case [condition] {then} [block]`
    - Default cases: `otherwise [block]`
    - `when` can also be an expression
- For loop: `for x {in collection} {from a} {to b} [block]`
- While loop: `while condition [block]`
- Code block: `do [operation]` or `begin; {@operation;@} end`
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
- `[]` (used in list literals and type definitions)
- `,` (separator between elements)
- `?` (syntactic sugar for `type? = either[type,nothing]`)

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
- Top level expressions must be of type `nothing`
- `nothing` type is invalid in comparisons
- Garbage collection
- Trailing commas are valid but not necessary

### [Potential future additions](future.md)
