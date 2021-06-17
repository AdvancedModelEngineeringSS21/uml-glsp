# UML Use Case Diagram Editor based on GLSP

Using the UML Use Case Diagram Editor is quite simple. After creating a new file you can add any element to the canvas by selecting the corresponding  tool from the tool pallet. 

## Creating a new Use Case Diagram

A new diagram can be created via:

- via menu entry `File -> New UML Class Diagram`
- via explorer context menu entry `New UML Class Diagram`
- via command palette `File: New UML Class Diagram`

Afterwards the diagram needs to be named: 

![ScreenShotNaming](figures_for_readme\ScreenShotNaming.jpeg)

and the type of the diagram needs to be specified. In the case of Use Case diagrams `usecase` needs to be entered

![ScreenShotNaming](figures_for_readme\ScreenShotTypeSelection.jpeg)

Afterwards an empty modelling canvas will be displayed. 

![ScreenShotNaming](figures_for_readme\ScreenShotCanvas.jpeg)

## Starting to model

To build your first model just click on the tool in the pallet for the element you want to add and click on the canvas. 

For creating edges between to elements, first select a relation tool, second select the source element and last select the target element. The cursor will change depending on whether creating a relationship between the two elements is allowed. 

Comments can be either added to the canvas or directly to the element to be annotated, depending on whether it is clicked on the canvas or on the element to be annotated. Comments can be added to classifiers but also to extend edges (note there is an existing bug regarding the rendering of the edge between the comment and the extend edge)

Subsystems and Packages have child compartments to which element can be added by selecting them and clicking on the parent (Subsystem or Package). Subsystems can only take use cases as children, packages can take any classifier. 

