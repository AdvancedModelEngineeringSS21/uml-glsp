# Tests

The following tests were performed on the commit tagged 'peer-review'.

- [ ] Marks open Points, also mentioned in the known issues parts

## Working

- CONTEXT DIAGRAM (without nesting in other elements):
    - Actor: Creating, Renaming and Removing
    - UseCase: Creating, Renaming and Removing
    - Comment: Creating, Renaming and Removing, + Creating Comment on any object directly creates a CommentEdge
    - Component = Subsystem: Creating, Renaming and Removing
    - Package: Creating, Renaming and Removing

- CONTEXT COMPONENT (nested inside existing COMPONENT):
    - UseCase: Creating, Renaming and Removing
    - Other nodes are not supposed to be inside a component (according to the UML documentation) and thus are not supported
    - [ ] Dynamic resizing does not work correctly for component
    - [ ] Deleting Component when it holds at least one usecase does remove it from the UML file, but it it still drawn => check Compound Command.

- CONTEXT PACKAGE (nested inside existing PACKAGE):
    - USECASE: Creating, Renaming and Removing
    - DELETE Package and also deleting child elements at the same time only for use cases
    - [ ] DELETE Package fails when also actors or components are children
- EDGES:
    - Association:
        - CREATE From usecase To Actor and vice versa and between two usecases
        - CREATING between two Actors does nothing as intended
        - DELETE via tool
        - DELETE when either of the connected nodes is deleted
        - Adjusting cardinality works if the correct format is used ("[x..x]" or "[x]") 
    - CommentEdge:
        - Adding from Comment (source) to Extend (target)
        - DELETE when source comment or target extend are removed
        - [ ] DELETE via tool
    - Extend:
        - CREATE between two usecases (creates Extension Point on target)
        - CREATE from usecase directly onto Extension Point (uses existing extension point)
        - DELETE via tool
        - DELETE when source usecase is deleted
        - DELETE when target EP is deleted
        - DELETE when target usecase is deleted
    - Generalization:
        - CREATE between two usecases
        - DELETE via tool
        - DELETE when either source or target is removed
    - Include:
        - CREATE between two usecases
        - DELETE when target usecase is deleted
        - DELETE via tool

## Functionality Issues
  - <<SubSystem>> Text in Component name can be edited and removed which should not be possible
  - DELETING source usecase of Include breaks the model and does not work
  - DELETING CommentEdge via tool not working
  - When a Comment is linked to an element that is not an Edge and this element is removed, the annotatedElement attribute is not set to null which causes a question mark to appear on the comment
  - Deleting Component when it hold at least one usecase does remove it from the UML file, but it it still drawn => check Compound Command.
  - DELETE Package fails when also actors or components are children

## Visual Issues
  - Palette Icons missing
  - When a COMPONENT is contained inside a PACKAGE, the USECASES contained inside that COMPONENT are not rendered
  - Edges are NOT Rendered inside Package
  - Dynamic resizing does not work correctly for component
  - DOUBLE click on extend label (which is not editable) shows the "undefined" text from the transparent label behind that we wanted to use as an anchor for the comment edge
  - Edge Creation Tool snaps to parent package when creating edges between two elements inside a package