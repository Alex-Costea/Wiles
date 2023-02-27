## Language specification
### NOTE: WORK IN PROGRESS, SUBJECT TO CHANGE
#### Note: [] means required, {} means optional, {@ @} means repeating

### Literals

- `nothing`
- Integer: `12345`
- Floating: `12345.6`
- String: `"abc"`
- Boolean: `true` and `false`
- List literal: `[{@value,@}] : type`
- Functions literals: `{fun ({@param1 {: type} {:= default_value},@})} {-> return_type}} [block]`

### Types
- Nothing: only valid value is `nothing`
- Integers: `int` (64 bits unsigned)
- Boolean: `truth`
- String: `text`
- Floating point: `rational` (equivalent to `double` in other languages)
- Function type: like function literals, but no function body
- Sum types: `either[type1,type2,type3]`
- List: `list[type]`

### Statements
- Value: `let {var} name {: type} {:= value}` (`var` makes it mutable, type can be inferred)
- Assignment: `name := value`
- Simple conditional: `if [condition] [block]`
- Complex conditional: `when [first clause]; {@other clauses;@} [default clause]`
    - First clause: `{case} [condition] [block]`
    - Other clauses: `case [condition] [block]`
    - Default clause: `default [block]`
      - Default clause with case: `default case [condition] [block]`
- Conditional type cast: `with [object] as [type] do [block]` (only executed when of correct type)
- For loop: `for x {in collection} {from a} {to b} [block]`
- While loop: `while condition [block]`
- Code block: `do [operation]` or `begin; {@operation;@} end`
- Yield: `yield [expression]` (return equivalent)
- `nothing` (no operation)
- `stop`, `skip` (`break`, `continue` equivalents)

### Symbols
- `+`, `-`, `*`, `/`, `^` (power)
- `and`, `or`, `not` (not bitwise!)
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `:=` (assign, declare or name parameters)
- `.` (function / field access)
- `:` (type annotation)
- `@` (access element in collection. returns nullable type)
- `()` (order of operations, function access)
- `[]` (used in list literals and type definitions)
- `,` (separator between elements)
- `?` (syntactic sugar for `type? = either[type,nothing]`)
- `import` (get a variable from an outside context)

### Named parameters
- Function calling with named parameters by default: `my_function(a := 1, b := 10)`
- Parameter can be made unnamed using the `arg` keyword: `fun(arg a : int)`
- You can always specify unnamed parameters by name
- Named parameters can be in any order. Unnamed parameters are matched in order of appearance.

### Mutable types
- Type: `mut[type]`, always subtype of `type`
- Make an immutable type into a mutable type with `mut` prefix operator
- Change the value of a mutable object with `<-` infix operator
- `mut [collection]` also makes its elements mutable

### Miscellaneous
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- `\` can be used to continue a line after a newline (including string literals and comments)
- Types are not reserved keywords and can be used as variable names
- Top level expressions must be of type `nothing`
- Trailing commas are valid but not necessary
- Pass by reference to object, immutable by default 

### [Potential future additions](future.md)
