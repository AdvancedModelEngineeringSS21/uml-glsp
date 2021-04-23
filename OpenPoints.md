# Open Points regarding our implementation

## Questions
- Package vs Component for Subsystem?
- With which other elements can actors be linked up? 
    - Especially Associations? Can they link two actors? According to the OMG UML doku not, but in papyrus this is possible
- Association -> Class vs. Classifer
- ownedUseCase vs packagedElement
- why do we need to have new java classes for every element even if they all do exactly the same


## Open Issues
- Use Case not rendered when placed initially
- Deleting elements connected via a relationships destroys everything 
  - -> Hypothesis: Changing notation
  - [ ] Remove Commands need to be implemented
- Saving not always consistent / consequent
- Palette is not always rendered


## ToDos: 
### Must Have
- Visual feedback for association creation (Currently it seems okay, if hovering over second actor)
- Implememt Subsystem as Component instead of as Class
- Extend: 
    - How handle creation of extension points?
    - Add extension to existing Extension point if exisitng extions point is clicked, else add new extension point
    - Validate Extension points?
### Nice To Have
- Improve pattern matcher for multiplicity regex


