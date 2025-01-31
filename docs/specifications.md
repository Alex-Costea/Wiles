## Language specification
### NOTE: BREAKING CHANGES CAN STILL BE MADE
#### Note: [] means required, ⟨⟩ means optional, ⟪⟫ means repeating

### Literals

- `nothing`
- Integer: `12345`
- Floating: `12345.6`
- String: `"abc"`. Can be multiline. Escaped sequences:
  - `\n;` is newline
  - `\b;` is backslash
  - `\q;` is quote
  - `\d;` is dollar (reserved for future use)
- Boolean: `true` and `false`
- List literal: `[⟪value,⟫] ⟨: type⟩`
- Dict literal: `{ ⟪key -> value,⟫ } ⟨: key_type -> value_type⟩`
- Data literal: `data{ ⟪identifier := value,⟫ }`
- Functions literals: `⟨fun (⟪param1 ⟨: type⟩ ⟨:= default_value⟩,⟫)⟩ ⟨-> return_type⟩⟩ [block]`
- Type literals `type([type_info])`

### Types
- Nothing: only valid value is `nothing`
- Integers: `int` (any integral value)
- Boolean: `truth`
- String: `text`
- Floating point: `decimal` (equivalent to `BigDecimal`)
- Function type: like function literals, but no function body and put between square brackets (ex: `fun[x : int, -> int]`)
- Sum types:`type1 or type2`
- List: `list[type]`
- Dict: `dict[key_type, value_type]`
- Data: `data[⟪key : type,⟫]`
- Expressions: `expression`

### Statements
- Value: `let ⟨var⟩ name ⟨: type⟩ ⟨:= value⟩` (`var` makes it a variable, type can be inferred)
- Assignment: `name := value`
- Simple conditional: `if [condition] [block]`
- Complex conditional: `if begin; ⟪clause;⟫ end`
    - Normal clause: `[condition] [block]`
    - Default clause: `default [block]`
- Simple conditional type cast: `when [object] is [type] do [block]`
- Complex conditional type cast: `when [object] begin; ⟪clause;⟫ end`
    - Normal clause: `is [type] [block]`
    - Default clause: `default [block]`
- For loop: `for x ⟨in list⟩ ⟨from a⟩ ⟨to b⟩ [block]`
    - `from` value is inclusive, `to` value is exclusive
    - if `in` statement is included, it will iterate through the list, by default ending at last element
- While loop: `while condition [block]`
- Code block: `do [operation]` or `begin; ⟪operation;⟫ end`
- Yield: `yield [expression]` (return equivalent)
- `nothing` (no operation)
- `stop`, `skip` (`break`, `continue` equivalents)

### Symbols
- `+`, `-`, `*`, `/`, `^` (power)
- `+` (text concatenation)
- `*` (repeat text)
- `and`, `or`, `not` (not bitwise!)
- `=`, `>`, `>=`, `<`, `<=`, `=/=`
- `:=` (assign, declare or name parameters)
- `.` (`b.a⟨ ( ⟪ param ⟫ ) ⟩` is `a(b,⟨⟪ param ⟫⟩ )`, also used for field access on data)
- `:` (type annotation)
- `()` (order of operations, function access)
- `[]` (list literals, subcomponents in type definitions)
- `,` (separator between elements)
- `?` (syntactic sugar for `type? = type or nothing`)

### Named parameters
- Function calling with named parameters by default: `my_function(a := 1, b := 10)`
- Parameter can be made unnamed using the `arg` keyword: `fun(arg a : int)`
- You always have the ability to specify unnamed parameters by name
- Named parameters can be in any order. Unnamed parameters are matched in order of appearance.

### Mutable types
- Type: `mut[type]`, always subtype of `type`
- Make an immutable collection into a mutable collection with `mut` prefix operator

### Generics

WIP, To be determined.

### Standard library
- `write`, `write_line`: write object to the command line
- `panic`: print an error message and exit
- `ignore`: ignore the value
- `modulo(x,y)`: calculate the modulo
- `size`: size of list/text
- `as_list` : convert text into list
- `as_text`: convert object to text
- `read_line`, `read_truth`, `read_int`, `read_rational`: read an object from the command line
- `run`: runs the function given as argument
- `maybe` : makes a nullable type out of non-nullable object
- `collection.add(arg value, at)` : add an element to a mutable collection
- `collection.remove(arg value, at)` : remove an element from the mutable collection at the index
- `collection.update(arg value, at)` : set element of mutable collection at index
- `collection.get(arg at)` : get element of collection at index, panic if it doesn't exist
- `type_of` : get type of object at runtime
- `clone(⟨deep := [boolean]⟩)` : clone object, deeply by default
- `content` : get content of variable. panics if the value is `nothing`
- `keys` : get the keys of a dictionary

### Miscellaneous
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- `\` can be used to continue a line after a newline
- Types are not reserved keywords and can be used as variable names
- Top level expressions must be of type `nothing`
- Trailing commas are valid but not necessary
- Pass by reference to object