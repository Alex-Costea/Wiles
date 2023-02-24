## Potential future additions

- Integer ranges: `int[from 0 to 99]`
- `decimal` (stored as fraction, not as float)
- Range types: `range[{int,} from 0, to 100]
- Other types: `dict[type,type]`, `set[type]`, `ref[type]`
- Generic types
- Classes with `class` keyword. Interfaces with `type` keyword.
- Declare fields `readonly` for getter with no setter, `public` for getter and setter
- Warnings, e.g. unreachable code
- `error` types. syntax: `int? io_error, illegal_state_error`, internally also an `either`
- Type declarations? `let type a = fun[a : int, -> int]`
- `func(a := a)` can also be written as `func(a)` (if the identifier names match)
