## Potential future additions

- `infint` (infinite precision integer)
- `decimal` (stored as fraction, not as float)
- Other types: `dict[type,type]`, `linked_list[type]`, `set[type]`, `ref[type]`
- Generic types, e.g.: `fun(a : *b) -> *b`
- Classes with `class` keyword. Internally, maybe something like `dict[text,fun]`?
- Declare fields `readonly` for getter with no setter, `public` for getter and setter
- Warnings, e.g. unreachable code
- `error` types. syntax: `int? io_error, illegal_state_error`, internally also an `either`
