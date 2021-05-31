# Tests

The following tests were performed on the commit tagged 'peer-review'.

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

- CONTEXT PACKAGE (nested inside existing PACKAGE):
    - USECASE: Creating, Renaming and Removing
    - DELETE Package and also deleting child elements at the same time only for use cases
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
    - Extend:
        - CREATE between two usecases (creates Extension Point on target)
        - CREATE from usecase directly onto Extension Point (uses existing extension point)
        - DELETE via tool
        - DELETE when source usecase is deleted
        - DELETE when target EP is deleted
        - DELETE when target usecase is deleted
    - Generalization:
        - CREATE between two usecases or two actors 
        - DELETE via tool
        - DELETE when either source or target is removed
    - Include:
        - CREATE between two usecases
        - DELETE when target usecase is deleted
        - DELETE via tool

## Functionality Issues
  - <<SubSystem>> Text in Component name can be edited and removed which should not be possible
  - DELETING source usecase of Include breaks the model and does not work
  - Dragging elements outside of their parent should remove them from the parent ( NOT YET IMPLEMENTED )
  - Deleting Actors does not work when they are connected via a Generalization edge

## Visual Issues
  - When a COMPONENT is contained inside a PACKAGE, the USECASES contained inside that COMPONENT are not rendered
  - Edges are NOT Rendered inside Package
  - Dynamic resizing does not work correctly for component
  - Dynamic resizing for packages with actors inside does not work correctly
  - Actors are at strange position when added to package
  - DOUBLE click on extend label (which is not editable) shows the "undefined" text from the transparent label behind that we wanted to use as an anchor for the comment edge
  - Edge Creation Tool snaps to parent package when creating edges between two elements inside a package

### Minor Graphic Complaints
  - Package shape should be adjusted to match UML specification template
  - Placement of Actor label needs to be adjusted
  - Palette Icons missing

