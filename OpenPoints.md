# Open Points regarding our implementation

## ToDos:
- [x] Add Extensions directly to extension points
- [x] Comments relation edge (extra edge via palette)
- [x] Make Package / Component render according to elements in it
- [ ] Actors behave a bit stupidly
- [x] Adding Use Cases to Components is only possible if component is outside of package (only rendendering, adding to uml file works)
- [ ] Abgabe Paper

## Nice to have: 
- [x] multiline comments
- [ ] Rendering of text from actors where to place it
- [x] Palette Icons

## If time allows (i.e. never)
- [ ] Clean up code, e.g. generalize methods like set name or create shape
- [ ] Improve pattern matcher for multiplicity regex
- [ ] It is a very nice idea to have multiline possibility for comments and to make a new line using `CTRL`+`ENTER`. Thus, afterwards it is still rendered in a single line.
- [ ] There is a type hint for the creation of an association between a usecase and a comment, but when the user tries to create such an association, nothing happens (which is correct, as it should not be possible). So I'd recommend to just remove this type hint. ---- COULD NOT FIX
- [ ] Package Name Label has a big indent after adding subsystems / components ---- HAS SOMETHING TO DO WITH THE 0-Point POSITION
- [ ] If a element with a comment is inside a container and the container is moved, the comment stays at the same position. You could just render the comment as well inside the container, so it moves with it.

------------

## Questions
- why do we need to have new java classes for every element even if they all do exactly the same
- ```yarn start``` always tries to start the glsp server even if it is already running from eclipse

## Open Issues
- Saving not always consistent / consequent
- Palette is not always rendered
- (0,0) point of the container elements Package and Component are NOT in the top left corner which is odd and results in a number of usability issues

## Annoying Stuff
- yarn is to picky
- rendering of edges is terrible and makes the diagrams unreadable

