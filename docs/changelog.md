# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

**NOTE:** Only the latest version with a tag at any given time is considered maintained. Bug fixes will **not** be back-ported. 
 
The versions exist as part of the git repository under the form of tags.

These changelogs only apply to the `wiles-base` package. The frontend / backend are not currently versioned.

## [v2.2.1] - 2024-03-25

- Bug fix (#70): rational division doesn't have enough precision
- Bug fix with debug mode
    
## [v2.2] - 2024-03-23

Implemented the following:

- Create Token information mode for Wiles Base API, to be used for syntax highlighting (#57)
- Generics: stop adding the vertical bars and numbers. E.g.: `T` instead of `T|1` (#61)
    
## [v2.1] - 2024-03-01

- The function `getOutput` now returns `OutputData` instead of `Pair<String, String>` inside `Wiles.jar` 
- Errors will no longer be forced to be displayed in red
- Better `tokenLocation`: now it includes information on where the token ends
- Relatedly, better compilation error messages in console. The entire token, both the start and the end, will be highlighted.
    
## [v2.0] - 2024-02-28

- Removed support for `either[type1,type2]` syntax (#45). Please use `type1 or type2`.
- The `writeline` function is now `write_line` (#42).
- Strings are now multiline by default. (#52) The `\` character is not used to make a string multiline (#50).
-  `\` no longer makes comments multiline either (#50).
- New string escaping mechanism with the format `\code;`, to replace the HTML-based string escaping. (#53)
- Dollar signs in strings are now reserved, to be used in the future for string interpolation. (#51)
- Remove `import` operator: make variables from the outside scope accessible by default (#47)
- Remove list concat operator `+` (#49)
- `mut` operator now only applies to collections, and there's no `set` function (#43)
- Make `rational` into an exact base 10 rational type (#46)

    
## [v1.1.5] - 2024-02-24

- Added support for data objects (#37). Syntax `data{field1 := value1, field2 := value2`
- Added support for data object types. Syntax: `data[field1 : type1, field2 : type2]`
- Added support for accessing fields of data objects. Syntax: `object.field`
- Added support for emoji identifiers (#41). Sometimes you just gotta have fun.
- Various bug fixes, refactoring and improvements.
    
## [v1.1.4] - 2024-02-03

- Bug fixes + tests, such as #35 and #36
- Refactoring
    
## [v1.1.3] - 2023-07-24

- Refactoring of existing code, such as moving the compiler into the `Wiles Base` folder
- Improvements to the Wiles interpreter, such as that a different JVM code can safely run the compilation process multiple times at the same time without any risk of contamination 
- Created an alternative way of calling the interpreter, using the `WilesCompiler.getOutput` function, that will return a `Pair<String,String>` containing the interpreter output and the error output, respectively
- Start work on web app project backend
    
## [v1.1.2] - 2023-07-11

- Implemented ticket #8: alternative syntax for `either` types, i.e. `type1 or type2`
- Updated specs
- Tests
    
## [v1.1.1] - 2023-07-09

- Bug fix in `for` and spec clarification: the `to` statement's value is always exclusive
- Adjust tests
    
## [v1.1] - 2023-07-09

- Extended the range for `int`, from using the JVM `long` to using `BigInteger`, as per #29
- Most behaviour should work the same, except for overflowing and underflowing which will no longer happen
- Due to longer range, operations between two `int`s, such as `+`, `-`, `*`,  `-` and `^` will yield the exact result
- Internal representation change, from `INT64` to just `INT`
- Minor bug fixes, such as making `for`s without a `to` never stop instead of stopping at `Long.MAX_VALUE`
- Additional tests
    
## [v1.0.3] - 2023-07-09

- Fixes for issues #27 and #28
- Tests
    
## [v1.0.2] - 2023-07-09

- Further improvements to error messages, specifically when dealing with generics in function calls, as per issue #26
- Tests for the improvements
    
## [v1.0.1] - 2023-07-08

- Implemented #18 : now the compiler will explain why a function cannot be called
- Bug fixes, fixing issues with possible non-determinism
- Updated existing tests and added more tests
    
## [v1.0] - 2023-07-04

First official release! No breaking changes except in internal representation as compared to `0.7.1`.

Note: while this is the first official release, this is far from guaranteed to be bug-free, especially in edge cases.

Additions:

- Expanded `=` and `=/=` to all types as per #21 
- Added `dict.keys` as per #24 
- Fix issue #25 and other issues discovered in the meantime
- More tests for dict stuff as per #22 
    
## [v0.7.1] - 2023-05-06

- Added support for `dict`, representing hash maps, as per issue #7
- Added support for CRUD functions for `dict`
- Tests for `dict`
- Updated specs
- Other minor non-breaking changes
    
## [v0.7] - 2023-04-20

- Remove `@` operator, as per issue #6. Please use the `get` function.
- Rewrote tests to reflect the removal of the `@` operator
- Update specifications
- Refactor the JSONService class
    
## [v0.6.4] - 2023-04-16

- Fix issue #16
- Test for issue
    
## [v0.6.3] - 2023-04-16

- Added `get` function for collections. Unlike `@`, it panics on the index not being found.
- Improvements for type checker. `let func := fun() -> int do panic()` now does not generate error.
- Bug fixes.
- Tests.
    
## [v0.6.2] - 2023-04-14

- Significant performance improvements in start-up / compilation
  - Due to dropping Jackson dependency and replacing it with `minimal-json` for the handling of JSON files
  - For instance, running all tests went from around 0.5s - 0.55s to around 0.2s - 0.25s
  - Size of JAR file also dropped from 7.84 MB to 2.86 MB
- No changes in functionality
    
## [v0.6.1] - 2023-04-14

- Added `collection` type, mostly for internal use for CRUD functions
- Made CRUD functions use `collection`. This will facilitate adding dictionaries later on
- Minor bug fixes
- Tests
    
## [v0.6] - 2023-04-08

**Note: breaking changes**

- Changes in the names of list functions, for standardisation.
  - `list.add(arg value, at)` : add an element to a mutable list
  - `list.remove(arg value, at)` : remove an element from the list at the index
  - `list.update(arg value, at)` : set element at index.
- Various bug fixes, both minor and major
- Update for the minimum value example on the front page
- Added and updated tests for everything here
    
## [v0.5.4] - 2023-04-02

- Improvements to type checking algorithm. To put it simply,  now this compiles (b is safely int):
```
let a := maybe(10)
when a is nothing do panic()
let b := a + 10
```
- Test for this
- Bug fixes
    
## [v0.5.3] - 2023-04-02

- Added `content` function, which returns content of variable and panics on `nothing`
- Tests for `content`
- Update dependencies
    
## [v0.5.2] - 2023-04-01

- Bug fixes in generic functions
- Tests for the fixes
    
## [v0.5.1] - 2023-04-01

- Bug fixes in run-time type checking for lists
- Fix in incorrect compilation error message
- More tests
    
## [v0.5] - 2023-04-01

**Note: breaking changes**

- `mut [list_literal]` will no longer make its elements also mutable
- remove a dependency
- changes in error messages
- the scope of function literal parameters no longer includes the outside scope. use the `import` keyword for this.
- changes to `when` syntax
- `set_at` no longer has a `mutable` paramater
- bug fixes
    
## [v0.4.2] - 2023-03-29

- Add `remove` and `set_at` functions for lists
- Bug fixes
- Tests
    
## [v0.4.1] - 2023-03-29

- Various bug fixes
- Allow statements of the form `import a := 10`
- Additional tests
    
## [v0.4] - 2023-03-28

**Note: breaking changes**

- Remove `new` operator, replaced with `clone` function
- `elem.clone()` makes a deep clone by default. Make a shallow clone using `elem.clone(deep := false)`
- Write tests for deep and shallow cloning
    
## [v0.3.7] - 2023-03-27

- Support for first-class types. To declare type definition of type, use e.g. `type[int]`
- Generic types are also now of `type` type. This should be safe.
- Type definitions at compile time: `typedef name := type`
- Get type of object at runtime: `object.type`
- Bug fixes
    
## [v0.3.6] - 2023-03-26

**Note: breaking change in compiled file syntax. No breaking change when not using `--compile` and `-run`**

- Various bug fixes, including major fixes
- Various refactoring and changes in internal representation
- Now inside `fun(x : anything? as T)`, T is an accessible value of type `GENERIC_TYPE`. This **can't** be used as a regular type because the actual type is not known at compile type.
    
## [v0.3.5] - 2023-03-25

- Added `add` function for mutable lists, including tests
- Bug fix: disallowed repeating the generic name for functions inside other functions
- Bug fix that didn't check generic types correctly
- Refactoring / code cleanup
    
## [v0.3.4] - 2023-03-23

- Improvements to generics type checking. The return type of `maybe(5)` is now un-generified.
- Bug fix in compilation exception handling
    
## [v0.3.3] - 2023-03-22

- Non-breaking change in compiled code representation
- Improvements to the `when` type checking
- Bug fix where function params referencing other params was allowed
    
## [v0.3.2] - 2023-03-19

- Finishing the bug fixing from the previous version
    
## [v0.3.1] - 2023-03-19

- Added helper functions with generics
  - `maybe`: makes non-nullable type nullable
  - `run`: runs a function and yields its result (alternative to `func()`)
- Bug fixes
    
## [v0.3] - 2023-03-19

**Note: breaking changes from v0.2.***

- Removed the `<-` symbol, for changing a mutable value
- Added `set` function with the same functionality using generics. Instead of `a <- b`, do `a.set(b)`
    
## [v0.2.16] - 2023-03-19

- Added generics and tests. **Bugs could still occur for generics!**
- Various bug fixes, including major ones!
    
## [v0.2.15] - 2023-03-17

- Add support for `writeline` without any arguments
- Add support for `=/=` with nothing, since `=` is already supported
    
## [v0.2.14] - 2023-03-17

- Allow operations such as `2 = nothing`. As `when` casts the value, this is necessary
- Change tests to reflect this change
- Fixed a bug where the internal type after `=` operation was incorrect (not boolean)
    
## [v0.2.13] - 2023-03-16

- Major bug fix: `when` statement should not create a copy of the object.
  - This made it impossible to modify a variable using `<-` after checking its type
- Test for the bug fix
    
## [v0.2.12] - 2023-03-16

- Refactoring the error messages into one class, and improvements in the messages themselves
- Throw a `panic` exception instead of a Java error when trying to multiply a text with a negative number
- In debug mode, add a newline before displaying the variables.
    
## [v0.2.11] - 2023-03-14

- Added functionality for right side of `.` (access operator) to be anything, not just a simple identifier.
- Added support for a subtype of a function type to have additional parameters, as long as they have default values and, in case of unnamed arguments, at the end of the parameter list. This does not break the Liskov Substitution Principle
 
## [v0.2.10] - 2023-03-13

- Fixed bug which made expressions like `[1,2,3].size` not work
- Wrote test for the bug fix
 
## [v0.2.9] - 2023-03-12

- Additional functionality for the `.` operator: `elem.func(params)` is now equivalent to `func(elem,params)`
- Bug fixes for `text.size` and `list.size`
 
## [v0.2.8] - 2023-03-12

**Note: breaking change in compiled file syntax. No breaking change when not using `--compile` and `-run`**

- Refactoring `true` / `false` / `nothing` into normal tokens
- Refactor all standard functions into one `StandardLibrary` static class
- Added `Infinity` and `NaN` identifiers
 
## [v0.2.7] - 2023-03-12

**Note: breaking change in compiled file syntax. No breaking change when not using `--compile` and `-run`**

- Throw `panic` on stack overflow, instead of the default Java error
- Add `@` (elem access) for text
- Text times int operation, for repeating text
- `as_list` function to convert text into list
- Additional tests for new functionality
 
## [v0.2.6] - 2023-03-11

- Make `as_text(elem)` work as well, not just `elem.as_text`
- Add `text.size` support
- Make the `size` function handle lists of nullable subtype, e.g. `list[int?]`
 
## [v0.2.5] - 2023-03-11

- Allow to use `write` and `writeline` with any object, except nothing types
- Fix bug that made `int?` a subtype of `anything`
- Write test to check the bug
 
## [v0.2.4] - 2023-03-11

Fixed issue where:

    let a := fun() -> nothing do nothing
    let b := do if true do yield nothing

would get rejected as incorrect by the checker, despite always yielding nothing.
 
## [v0.2.3] - 2023-03-11

- Panic on arithmetic exceptions and input reading errors, instead of throwing Java exception
 
## [v0.2.2] - 2023-03-11

**Note: breaking change in compiled file syntax. No breaking change when not using `--compile` and `-run`**
- Bug fix in getting the type of function literals in interpreter
- Fix tests and create new test for function literal types
 
## [v0.2.1] - 2023-03-11

- Bug fix in type checker for list addition
 
## [v0.2] - 2023-03-11

**Note: breaking changes from v0.1**

- Support for list concatenation using plus operator. E.g.: `[1,2]` + `[3]` = `[1,2,3]`
- Remove append to list `+=` operator as duplicate. Should be `add` function once support for generics is added.

## [v0.1] - 2023-03-09

First official release, with the interpreter being functional. Read the readme for details.

[v2.2.1]: https://github.com/Alex-Costea/Wiles/compare/v2.2...v2.2.1
[v2.2]: https://github.com/Alex-Costea/Wiles/compare/v2.1...v2.2
[v2.1]: https://github.com/Alex-Costea/Wiles/compare/v2.0...v2.1
[v2.0]: https://github.com/Alex-Costea/Wiles/compare/v1.1.5...v2.0
[v1.1.5]: https://github.com/Alex-Costea/Wiles/compare/v1.1.4...v1.1.5
[v1.1.4]: https://github.com/Alex-Costea/Wiles/compare/v1.1.3...v1.1.4
[v1.1.3]: https://github.com/Alex-Costea/Wiles/compare/v1.1.2...v1.1.3
[v1.1.2]: https://github.com/Alex-Costea/Wiles/compare/v1.1.1...v1.1.2
[v1.1.1]: https://github.com/Alex-Costea/Wiles/compare/v1.1...v1.1.1
[v1.1]: https://github.com/Alex-Costea/Wiles/compare/v1.0.3...v1.1
[v1.0.3]: https://github.com/Alex-Costea/Wiles/compare/v1.0.2...v1.0.3
[v1.0.2]: https://github.com/Alex-Costea/Wiles/compare/v1.0.1...v1.0.2
[v1.0.1]: https://github.com/Alex-Costea/Wiles/compare/v1.0...v1.0.1
[v1.0]: https://github.com/Alex-Costea/Wiles/compare/v0.7.1...v1.0
[v0.7.1]: https://github.com/Alex-Costea/Wiles/compare/v0.7...v0.7.1
[v0.7]: https://github.com/Alex-Costea/Wiles/compare/v0.6.4...v0.7
[v0.6.4]: https://github.com/Alex-Costea/Wiles/compare/v0.6.3...v0.6.4
[v0.6.3]: https://github.com/Alex-Costea/Wiles/compare/v0.6.2...v0.6.3
[v0.6.2]: https://github.com/Alex-Costea/Wiles/compare/v0.6.1...v0.6.2
[v0.6.1]: https://github.com/Alex-Costea/Wiles/compare/v0.6...v0.6.1
[v0.6]: https://github.com/Alex-Costea/Wiles/compare/v0.5.4...v0.6
[v0.5.4]: https://github.com/Alex-Costea/Wiles/compare/v0.5.3...v0.5.4
[v0.5.3]: https://github.com/Alex-Costea/Wiles/compare/v0.5.2...v0.5.3
[v0.5.2]: https://github.com/Alex-Costea/Wiles/compare/v0.5.1...v0.5.2
[v0.5.1]: https://github.com/Alex-Costea/Wiles/compare/v0.5...v0.5.1
[v0.5]: https://github.com/Alex-Costea/Wiles/compare/v0.4.2...v0.5
[v0.4.2]: https://github.com/Alex-Costea/Wiles/compare/v0.4.1...v0.4.2
[v0.4.1]: https://github.com/Alex-Costea/Wiles/compare/v0.4...v0.4.1
[v0.4]: https://github.com/Alex-Costea/Wiles/compare/v0.3.7...v0.4
[v0.3.7]: https://github.com/Alex-Costea/Wiles/compare/v0.3.6...v0.3.7
[v0.3.6]: https://github.com/Alex-Costea/Wiles/compare/v0.3.5...v0.3.6
[v0.3.5]: https://github.com/Alex-Costea/Wiles/compare/v0.3.4...v0.3.5
[v0.3.4]: https://github.com/Alex-Costea/Wiles/compare/v0.3.3...v0.3.4
[v0.3.3]: https://github.com/Alex-Costea/Wiles/compare/v0.3.2...v0.3.3
[v0.3.2]: https://github.com/Alex-Costea/Wiles/compare/v0.3.1...v0.3.2
[v0.3.1]: https://github.com/Alex-Costea/Wiles/compare/v0.3...v0.3.1
[v0.3]: https://github.com/Alex-Costea/Wiles/compare/v0.2.16...v0.3
[v0.2.16]: https://github.com/Alex-Costea/Wiles/compare/v0.2.15...v0.2.16
[v0.2.15]: https://github.com/Alex-Costea/Wiles/compare/v0.2.14...v0.2.15
[v0.2.14]: https://github.com/Alex-Costea/Wiles/compare/v0.2.13...v0.2.14
[v0.2.13]: https://github.com/Alex-Costea/Wiles/compare/v0.2.12...v0.2.13
[v0.2.12]: https://github.com/Alex-Costea/Wiles/compare/v0.2.11...v0.2.12
[v0.2.11]: https://github.com/Alex-Costea/Wiles/compare/v0.2.10...v0.2.11
[v0.2.10]: https://github.com/Alex-Costea/Wiles/compare/v0.2.9...v0.2.10
[v0.2.9]: https://github.com/Alex-Costea/Wiles/compare/v0.2.8...v0.2.9
[v0.2.8]: https://github.com/Alex-Costea/Wiles/compare/v0.2.7...v0.2.8
[v0.2.7]: https://github.com/Alex-Costea/Wiles/compare/v0.2.6...v0.2.7
[v0.2.6]: https://github.com/Alex-Costea/Wiles/compare/v0.2.5...v0.2.6
[v0.2.5]: https://github.com/Alex-Costea/Wiles/compare/v0.2.4...v0.2.5
[v0.2.4]: https://github.com/Alex-Costea/Wiles/compare/v0.2.3...v0.2.4
[v0.2.3]: https://github.com/Alex-Costea/Wiles/compare/v0.2.2...v0.2.3
[v0.2.2]: https://github.com/Alex-Costea/Wiles/compare/v0.2.1...v0.2.2
[v0.2.1]: https://github.com/Alex-Costea/Wiles/compare/v0.2...v0.2.1
[v0.2]: https://github.com/Alex-Costea/Wiles/compare/v0.1...v0.2
[v0.1]: https://github.com/Alex-Costea/Wiles/tree/v0.1
