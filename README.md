# Caesium
Caesium is a powerful Java bytecode obfuscator written by [sim0n](https://github.com/sim0n) for fun, and released for the public.

### Currently available mutators
* Class Folder (Turns classes into folders)
* Control Flow
* Crasher (Will crash almost every GUI based RE tool)
* Local Variable
* Line Number
* Number
* Polymorph
* Reference (invokedynamics)
* String
* Trim (Currently only trims math functions)

## Notes
You have to add every dependency your jar relies on.
Caesium is very optimised and the performance loss shouldn't be more than 5-10% (unless you're using reference mutation)

## Usage
- Run the jar.
- Select mutators in the mutators tab.
- Hit mutate. Done!
