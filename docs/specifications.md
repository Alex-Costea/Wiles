## Language specification
### NOTE: BREAKING CHANGES CAN STILL BE MADE
#### Note: [] means required, ⟨⟩ means optional, ⟪⟫ means repeating

### Literals

- `nothing`
- Integer: `12345`
- Floating: `12345.6`
- String: `"abc"`. Can be multiline, supports full unicode, includes escape sequences.
  - Escaping is forgiving: if it can't be escaped, it's taken literally.
  - `\[content];` is equivalent to HTML `&[content];`
    - Includes support for entities (e.g. `\beta;`) and code points (e.g. `\#x1F600;`)
  - Support for one-letter escape sequences:
    - `\q` for `"`
    - `\n` for newline
    - `\b` for `\`
    - Take optional ending semicolon, `\n;` equivalent to `\n`
- Boolean: `true` and `false`
- List literal: `[⟪value,⟫] ⟨: type⟩`
- Dict literal: `{ ⟪key -> value,⟫ } ⟨: key_type -> value_type⟩`
- Data literal: `data<< ⟪[declaration],⟫ >>` (identifier can also be const string)
- Functions literals: `⟨fun (⟪param1 ⟨: type⟩ ⟨:= default_value⟩,⟫)⟩ ⟨-> return_type⟩⟩ [block]`

### Types
- Nothing: only valid value is `nothing`
- Integers: `int` (any integral value)
- Boolean: `truth`
- String: `text`
- Floating point: `decimal` (equivalent to `BigDecimal`)
- Function type: like function literals, but with no function body
- Sum types: `type1 | type2`
- List: `list(type)`
- Constant: `constant(type)`
- Dict: `dict(key_type, value_type)`
- Mutable type: `mutable(type)`
- Expressions (any const expression is a valid type)

### Statements
- Declaration: `let ⟨def⟩ ⟨var⟩ ⟨const⟩ name ⟨: type⟩ ⟨:= value⟩`
  - `let` can be skipped if it starts with `def`
  - `var` makes it a variable
  - `def` makes it global scope
  - `const` makes it a compile time constant
- Assignment: `name := value`
- Simple conditional: `if [condition] [block]`
- Complex conditional: `if begin; ⟪clause;⟫ end`
    - Normal clause: `[condition] [block]`
    - Default clause: `default [block]`
- For loop: `for x in [list/range] [block]`
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
- `?` (makes types and values nullable)
- `|` (`type1 | type2` is the sum type of `type1` and `type2`)
- `~` (make an immutable collection mutable)


### Named parameters
- Function calling with named parameters by default: `my_function(a := 1, b := 10)`
- Parameter can be made unnamed using the `arg` keyword: `fun(arg a : int)`
- You always have the ability to specify unnamed parameters by name
- Named parameters can be in any order. Unnamed parameters are matched in order of appearance.

### Compile time execution

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
- `collection.add(arg value, at)` : add an element to a mutable collection
- `collection.remove(arg value, at)` : remove an element from the mutable collection at the index
- `collection.update(arg value, at)` : set element of mutable collection at index
- `collection.get(arg at)` : get element of collection at index, panic if it doesn't exist
- `type` : get type of object at runtime
- `clone(⟨deep := [boolean]⟩)` : clone object, deeply by default
- `content` : get content of variable. panics if the value is `nothing`
- `keys` : get the keys of a dictionary
- `range(⟨from := [value],⟩ ⟨to := [value],⟩ ⟨step := [value]⟩)`

### Miscellaneous
- `;` can be specified or inferred from newline
- Language is statically, strongly typed with some type inference
- Comment using `#`
- `\` can be used to continue a line after a newline
- Top level expressions must be of type `nothing`
- Trailing commas are valid but not necessary
- Pass by reference to object