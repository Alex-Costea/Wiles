# Open Tickets

## 9 - `require` function

- `require(elem : truth)`, panics when false
- Maybe also have an `assert` function that can be turned on or off?

## 10 - `stop`, `skip` and `yield` improvements

- Should be allowed inside expressions
- Should yield `UNIVERSAL_SUBTYPE`, so that they can be used anywhere

## 11 - `panic` should be allowed anywhere

- Make `panic` yield the type `UNIVERSAL_SUBTYPE`.
- The type checker that checks for existence of `panic` functions should check for it everywhere inside expressions.

## 14 - Integer ranges

- Possible syntax: `int[from 0 to 99]`

## 20 - Functions for bitwise operation equivalents

- `b_and`
- `b_or`
- `b_xor`
- `b_left`
- `b_right`
- `b_comp`

## 23 - Collections syntactic sugar

- `collection[key] := value` - equivalent to `add`
- `collection[key]` - equivalent to `get` otherwise

## 30 - `Infinity` and `MinusInfinity` as singleton types

It would allow things like `either[int,Infinity]`

## 39 - Improvements to Generics

Generics need to be reworked, they are too complex and the code is very unclear and potentially buggy. Breaking backwards compatibility is probably going to have to be done.

This is a broad issue that will require some thinking.

## 44 Improve specification

Instead of the current specifications file, there should be in detail explanations for each of the statement types.

## 51 - String interpolation

Syntax:
```
write_line("2 + 3 = $2 + 3;")
```

## 59 - Display errors inline in Wiles Web

No description provided.

## 60 - Types such as `"accepted" or "rejected"`

No description provided.

## 64 - Allow string concat with `anything` + fix tests

No description provided.

## 65 - Command line option to create a standalone executable

No description provided.

## 67 - String and char functions

Get a character out of a number.

## 68 - Standard Library improvements: make standalone file

No description provided.

## 73 - Avoid splash of white by making background dark before image load.

No description provided.

# Accepted and Resolved Tickets

## 6 - Remove `@` operator

The `@` operator should be removed, for the following reasons:

- Now the `get` function exists, and as such it is mostly redundant.
- For a `list[type?]`, it cannot differentiate between an element not existing and an element being `nothing`.
- In general, error handling should not be handled by yielding `nothing`, but by a real exception handling system.
- The syntax is relatively unclear and unusual compared to other languages.
- If it is decided to still use `@` after all, it should exist as syntactic sugar only.

The main difficulty would occur with adapting all the tests.

## 7 - Add dictionaries

- Corresponding to LinkedHashMap in JVM
- Use the same CRUD functions as lists
- Proposed syntax example: `{key -> value, key -> value} : type -> type`

## 8 - New syntactic sugar for `either` types

- Instead of just `either[type1, type2]`, also allow `type1 or type2`.

## 16 - Mutable list issue

The following code breaks the type checking, as it is allowed to run despite failing at run time:

```
let func := fun(arg list : list[int])
begin
    let object : anything := list
    when object is mut[list[text]]
    begin
        object.add(at := 0, "hi!")
        yield nothing
    end
    panic("oops!")
end

let list := mut [] : int
func(list)
let a := list.get(0)
writeline(a > 0)
```

## 18 - Improve CannotCallMethodException

Instead of just stating that the function cannot be called with the provided arguments, a reason should be provided for why that is the case (such as unmatching types, too many arguments, not enough arguments etc)

## 21 - Expand `=` to all types

Internally this functionality already exists due to `dict`s, so it should be expanded to language users as well. Equalities that are impossible at runtime (e.g. int to string) should not be allowed.

Example of falling test:

```
let a := [1,2,3]
let b := [1,2,3]
writeline(a=b)
```

## 22 - Test `dict`s more

No description provided.

## 24 - `dict.keys`

Add function `keys` that will return a dictionary's list of keys. Will make it possible to iterate over a dictionary.

## 25 - Dynamic types for dictionaries at runtime

`ObjectDetails.getType()` should return the type based on the elements of the dictionary if the dict is mutable, similar to how it works for lists.

## 26 - Clearer error messages when CannotCallMethodException deals with generics

Example:

```
let list := [1,2,3]
writeline(list.add(at := "hi", 4))
```

Error message displayed:

```
>>> COMPILATION FAILED
>>>
>>> Line 2: The function cannot be called with these arguments. Reason: The type definition TYPE MUTABLE; (TYPE COLLECTION; (TYPE GENERIC; (!T|add_key; TYPE EITHER; (TYPE ANYTHING; TYPE !nothing); DECLARE); TYPE GENERIC; (!T|add_value; TYPE EITHER; (TYPE ANYTHING; TYPE !nothing); DECLARE))) conflicts with the inferred type TYPE LIST; (TYPE INT64) for identifier "collection".
>>> writeline(list.add(at := "hi", 4))
>>>               ^
>>>
```

This is not very helpful.

## 27 - `dict.as_text` not in the proper format

```
let b := {1 -> 5, 2 -> 6} : int -> int
writeline(b)
```

expected:
`{1 -> 5, 2 -> 6}`

actual:
`{1=5, 2=6}`

## 28 - Empty `dict` doesn't have type inferred properly

E.g.:
```
let b := {} : int -> int
writeline(b.type)
```

## 29 - `int` types should maybe be BigInteger, not Long?

This is an option to consider. The language will be more user-friendly, at the cost of performance.

## 35 - Bug: interpreter error

```
let f := fun(x : int) do nothing

when f is fun[x : int] begin
    f(x := 10)
end
```

## 36 - Bug: out of bounds issue

```
let f := do nothing
when f is fun[] begin
    writeline("hi")
end
```
It works fine like this:

```
let f := do nothing
when f is fun[->nothing] begin
    writeline("hi")
end
```

## 37 - Data structs

Example

```
typedef person := data[name : text, age : int]
```

`data` is the keyword here. Subtype of `dict`.

With closures, things like "private fields" should be possible to implement.

## 41 - Support for emojis in identifiers

No description provided.

## 42 - `writeline` should be `write_line`

For better consistency with `read_line`

## 43 - Mutable types revamp

- Stop autounboxing mut
- Only allow mut operation on collections
- Remove `set` function for everything

Why?

- Bugs with mut unboxing
- `mut` shouldn't be used for regular values, instead `var` is preferred

## 45 - Remove support for `either` in favor of `or`?

Remove support for `either` in favor of `or`?

## 46 - Make `rational` into an exact base 10 rational type

No description provided.

## 47 - Remove `import` operator: make variables from the outside scope accessible by default

No description provided.

## 49 - Remove list concat operator `+`

Too prone to abuse.

## 50 - Remove `\` for string literals and comments

No description provided.

## 52 - Make strings multiline by default

No description provided.

## 53 - Revamp string escaping

New string escaping mechanism with the format `\code;`, to replace the HTML-based string escaping.

## 57 - Create Token information mode for Wiles Base API, to be used for syntax highlighting

No description provided.

## 61 - No description provided.

No description provided.

## 70 - Provide more precision with rational division. E.g.: `write(1.0 / 6)`

No description provided.
