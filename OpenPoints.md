# Open Points regarding our implementation

## ToDos Heute:
- [x] Remove Generalization Impl
- [x] Make use case visible when placed initially 
- [ ] Make Package have body to show stuff in it
- [ ] Strichlierte Lines mit Pfeilspitze

## Questions
- Package vs Component for Subsystem?
- With which other elements can actors be linked up? 
    - Especially Associations? Can they link two actors? According to the OMG UML doku not, but in papyrus this is possible
- Association -> Class vs. Classifier
- ownedUseCase vs packagedElement
- why do we need to have new java classes for every element even if they all do exactly the same
- ```yarn start``` always tries to start the glsp server even if it is already running from eclipse


## Open Issues
- Saving not always consistent / consequent
- Palette is not always rendered


## ToDos: 
### Must Have
- Visual feedback for association creation (Currently it seems okay, if hovering over second actor)
- Implememt Subsystem as Component instead of as Class
- Extend: 
    - How handle creation of extension points?
    - Add extension to existing Extension point if exisitng extions point is clicked, else add new extension point
    - Validate Extension points
    - disallow extension loops (extending the extended usecase  with the extending usecase)
    - 
### Nice To Have
- Improve pattern matcher for multiplicity regex


