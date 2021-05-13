/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package com.eclipsesource.uml.glsp.gmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GEdge;
import org.eclipse.glsp.graph.GLabel;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GEdgeBuilder;
import org.eclipse.glsp.graph.builder.impl.GEdgePlacementBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.util.UmlConfig.CSS;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.eclipsesource.uml.glsp.util.UmlIDUtil;
import com.eclipsesource.uml.modelserver.unotation.Shape;

public class ClassifierNodeFactory extends AbstractGModelFactory<Classifier, GNode> {

   private final LabelFactory labelFactory;

   public ClassifierNodeFactory(final UmlModelState modelState, final LabelFactory labelFactory) {
      super(modelState);
      this.labelFactory = labelFactory;
   }

   @Override
   public GNode create(final Classifier classifier) {
      if (classifier instanceof Class) {
         return createClassNode((Class) classifier);
      } else if (classifier instanceof Package) {
         return create((Package) classifier);
      } else if (classifier instanceof Component) {
         return create(classifier);
      } else if (classifier instanceof Actor) {
         return create((Actor) classifier);
      } else if (classifier instanceof UseCase) {
         return create((UseCase) classifier);
      } else if (classifier instanceof Comment) {
         return create((Comment) classifier);
      }

      return null;
   }

   protected void applyShapeData(final Element element, final GNodeBuilder builder) {
      modelState.getIndex().getNotation(element, Shape.class).ifPresent(shape -> {
         if (shape.getPosition() != null) {
            builder.position(GraphUtil.copy(shape.getPosition()));
         } else if (shape.getSize() != null) {
            builder.size(GraphUtil.copy(shape.getSize()));
         }
      });
   }

   protected GNode createClassNode(final Class umClass) {
      GNodeBuilder classNodeBuilder = new GNodeBuilder(Types.CLASS)
         .id(toId(umClass))
         .layout(GConstants.Layout.VBOX)
         .addCssClass(CSS.NODE);

      applyShapeData(umClass, classNodeBuilder);

      GCompartment classHeader = buildHeader(umClass);
      classNodeBuilder.add(classHeader);

      GCompartment classPropertiesCompartment = buildClassPropertiesCompartment(umClass.getAttributes(), umClass);
      classNodeBuilder.add(classPropertiesCompartment);

      return classNodeBuilder.build();
   }

   // region Use Case Diagram

   protected void applyShapeData(final Package classifier, final GNodeBuilder builder) {
      modelState.getIndex().getNotation(classifier, Shape.class).ifPresent(shape -> {
         if (shape.getPosition() != null) {
            builder.position(GraphUtil.copy(shape.getPosition()));
         } else if (shape.getSize() != null) {
            double minX = shape.getPosition().getX();
            double maxX = 0;
            double minY = shape.getPosition().getY();
            double maxY = 0;
            for (PackageableElement e : classifier.getPackagedElements()) {
               Optional<Shape> cso = modelState.getIndex().getNotation(e, Shape.class);
               if (cso.isPresent()) {
                  Shape cs = cso.get();
                  double currentX = cs.getPosition().getX() + cs.getSize().getWidth();
                  double currentY = cs.getPosition().getY() + cs.getSize().getHeight();

                  if (currentX > maxX) {
                     maxX = currentX;
                  }
                  if (currentY > maxY) {
                     maxY = currentY;
                  }
               }
            }
            builder.size(GraphUtil.dimension(maxX - minX, maxY - minY));
            // builder.size(GraphUtil.copy(shape.getSize()));
         }
      });
   }

   protected GNode create(final Component umlComponent) {
      GNodeBuilder b = new GNodeBuilder(Types.COMPONENT)
         .id(toId(umlComponent))
         .layout(GConstants.Layout.VBOX)
         .addCssClass(CSS.NODE);

      applyShapeData(umlComponent, b);

      GCompartment classHeader = buildHeaderWithOutIcon(umlComponent);
      b.add(classHeader);

      ArrayList<Classifier> childELements = new ArrayList<>();

      childELements.addAll(umlComponent.getPackagedElements().stream()
         .filter(pe -> (pe instanceof UseCase))
         .map(Classifier.class::cast)
         .collect(Collectors.toList()));

      GCompartment componentChildCompartment = buildPackageOrComponentChildCompartment(childELements, umlComponent);
      b.add(componentChildCompartment);

      return b.build();
   }

   protected GNode create(final Package umlPackage) {
      GNodeBuilder b = new GNodeBuilder(Types.PACKAGE)
         .id(toId(umlPackage))
         .layout(GConstants.Layout.VBOX)
         .addCssClass(CSS.NODE);

      applyShapeData(umlPackage, b);

      GCompartment classHeader = buildHeader(umlPackage);
      b.add(classHeader);

      ArrayList<Classifier> childELements = new ArrayList<>();

      childELements.addAll(umlPackage.getPackagedElements().stream()
         .filter(pe -> (pe instanceof Actor || pe instanceof UseCase))
         .map(Classifier.class::cast)
         .collect(Collectors.toList()));

      GCompartment packageChildCompartment = buildPackageOrComponentChildCompartment(childELements, umlPackage);
      b.add(packageChildCompartment);

      return b.build();
   }

   protected GNode create(final UseCase umlUseCase) {
      GNodeBuilder b = new GNodeBuilder(Types.USECASE) //
         .id(toId(umlUseCase)) //
         .layout(GConstants.Layout.VBOX) //
         .addCssClass(CSS.NODE)
         .addCssClass(CSS.ELLIPSE)
         .add(buildHeader(umlUseCase));

      if (umlUseCase.getExtensionPoints().size() > 0) {
         GCompartment extensionPointCompartment = buildUsecaseExtensionPointCompartment(umlUseCase);
         b.add(extensionPointCompartment);
      }

      applyShapeData(umlUseCase, b);
      return b.build();
   }

   protected GNode create(final Actor umlActor) {
      GNodeBuilder b = new GNodeBuilder(Types.ACTOR) //
         .id(toId(umlActor)) //
         .layout(GConstants.Layout.VBOX) //
         .addCssClass(CSS.NODE) //
         .add(buildHeader(umlActor));

      applyShapeData(umlActor, b);
      return b.build();
   }

   protected GNode create(final Comment umlComment) {
      GNodeBuilder b = new GNodeBuilder(Types.COMMENT) //
         .id(toId(umlComment)) //
         .layout(GConstants.Layout.VBOX) //
         .addCssClass(CSS.NODE); //

      GCompartmentBuilder commentHeaderBuilder = new GCompartmentBuilder(Types.COMP_HEADER)
         .layout(GConstants.Layout.HBOX)
         .id(UmlIDUtil.createHeaderId(toId(umlComment)));

      GLabel commentBodyText = new GLabelBuilder(Types.COMMENT_BODY)
         .id(UmlIDUtil.createCommentBodyId(toId(umlComment)))
         .text(umlComment.getBody()).build();
      commentHeaderBuilder.add(commentBodyText);

      b.add(commentHeaderBuilder.build());

      if (!umlComment.getAnnotatedElements().isEmpty()) {
         for (Element annotatedElement : umlComment.getAnnotatedElements()) {
            GEdge annotatedEdge = createCommentEdge(umlComment, annotatedElement);
            b.add(annotatedEdge);
         }
      }

      applyShapeData(umlComment, b);
      return b.build();
   }

   // endregion

   protected GCompartment buildHeader(final Package classifier) {
      GCompartmentBuilder classHeaderBuilder = new GCompartmentBuilder(Types.COMP_HEADER)
         .layout(GConstants.Layout.HBOX)
         .id(UmlIDUtil.createHeaderId(toId(classifier)));

      GCompartment classHeaderIcon = new GCompartmentBuilder(Types.ICON_CLASS)
         .id(UmlIDUtil.createHeaderIconId(toId(classifier))).build();
      classHeaderBuilder.add(classHeaderIcon);

      GLabel classHeaderLabel = new GLabelBuilder(Types.LABEL_NAME)
         .id(UmlIDUtil.createHeaderLabelId(toId(classifier)))
         .text(classifier.getName()).build();
      classHeaderBuilder.add(classHeaderLabel);

      return classHeaderBuilder.build();
   }

   protected GCompartment buildHeader(final Classifier classifier) {
      GCompartmentBuilder classHeaderBuilder = new GCompartmentBuilder(Types.COMP_HEADER)
         .layout(GConstants.Layout.HBOX)
         .id(UmlIDUtil.createHeaderId(toId(classifier)));

      GCompartment classHeaderIcon = new GCompartmentBuilder(Types.ICON_CLASS)
         .id(UmlIDUtil.createHeaderIconId(toId(classifier))).build();
      classHeaderBuilder.add(classHeaderIcon);

      GLabel classHeaderLabel = new GLabelBuilder(Types.LABEL_NAME)
         .id(UmlIDUtil.createHeaderLabelId(toId(classifier)))
         .text(classifier.getName()).build();
      classHeaderBuilder.add(classHeaderLabel);

      return classHeaderBuilder.build();
   }

   protected GCompartment buildHeaderWithOutIcon(final Classifier classifier) {
      GCompartmentBuilder classHeaderBuilder = new GCompartmentBuilder(Types.COMP_HEADER)
         .layout(GConstants.Layout.HBOX)
         .id(UmlIDUtil.createHeaderId(toId(classifier)));

      GLabel classHeaderLabel = new GLabelBuilder(Types.LABEL_NAME)
         .id(UmlIDUtil.createHeaderLabelId(toId(classifier)))
         .text(classifier.getName()).build();
      classHeaderBuilder.add(classHeaderLabel);

      return classHeaderBuilder.build();
   }

   protected static String getType(final Classifier classifier) {
      if (classifier instanceof Class) {
         return Types.ICON_CLASS;
      } else if (classifier instanceof Actor) {
         return Types.ICON_ACTOR;
      } else if (classifier instanceof UseCase) {
         return Types.ICON_USECASE;
      }

      return "Classifier not found";
   }

   protected static String getType(final Package p) {
      return Types.ICON_PACKAGE;
   }

   protected GCompartment buildClassPropertiesCompartment(final Collection<? extends Property> properties,
      final Classifier parent) {
      GCompartmentBuilder classPropertiesBuilder = new GCompartmentBuilder(Types.COMP)
         .id(UmlIDUtil.createChildCompartmentId(toId(parent))).layout(GConstants.Layout.VBOX);

      GLayoutOptions layoutOptions = new GLayoutOptions()
         .hAlign(GConstants.HAlign.LEFT)
         .resizeContainer(true);
      classPropertiesBuilder.layoutOptions(layoutOptions);

      List<GModelElement> propertiesLabels = properties.stream()
         .map(labelFactory::createPropertyLabel)
         .collect(Collectors.toList());
      classPropertiesBuilder.addAll(propertiesLabels);

      return classPropertiesBuilder.build();
   }

   protected GCompartment buildPackageOrComponentChildCompartment(final Collection<Classifier> childNodes,
      final EObject parent) {

      GCompartmentBuilder packageElementsBuilder = new GCompartmentBuilder(Types.COMP)
         .id(UmlIDUtil.createChildCompartmentId(toId(parent))).layout(GConstants.Layout.VBOX);

      List<GModelElement> childNodeGModelElements = childNodes.stream()
         .map(node -> this.create(node))
         .collect(Collectors.toList());
      packageElementsBuilder.addAll(childNodeGModelElements);

      return packageElementsBuilder.build();
   }

   protected GCompartment buildUsecaseExtensionPointCompartment(final UseCase parent) {
      GCompartmentBuilder extensionPointBuilder = new GCompartmentBuilder(Types.COMP)
         .id(UmlIDUtil.createChildCompartmentId(toId(parent))).layout(GConstants.Layout.VBOX);

      GLayoutOptions layoutOptions = new GLayoutOptions()
         .hAlign(GConstants.HAlign.LEFT)
         .resizeContainer(true);
      extensionPointBuilder.layoutOptions(layoutOptions);

      GLabel headingLabel = labelFactory.createUseCaseExtensionPointsHeading(parent);
      extensionPointBuilder.add(headingLabel);

      List<GModelElement> extensionPointsLabel = parent.getExtensionPoints().stream()
         .map(labelFactory::createUseCaseExtensionPointsLabel)
         .collect(Collectors.toList());
      extensionPointBuilder.addAll(extensionPointsLabel);

      return extensionPointBuilder.build();
   }

   protected GLabel createLabel(final String name, final double position, final String id, final String type,
      final String side) {
      return new GLabelBuilder(type)
         .edgePlacement(new GEdgePlacementBuilder()
            .side(side)
            .position(position)
            .offset(2d)
            .rotate(false)
            .build())
         .id(id)
         .text(name).build();
   }

   protected GEdge createCommentEdge(final Comment comment, final Element annotatedElement) {

      Element source = comment;
      String sourceId = toId(source);
      Element target = annotatedElement;
      String targetId = toId(target);

      GEdgeBuilder builder = new GEdgeBuilder(Types.ASSOCIATION)
         .id(sourceId + "_" + targetId + "_commentEdge")
         .addCssClass(CSS.EDGE)
         .sourceId(sourceId)
         .targetId(targetId)
         .routerKind(GConstants.RouterKind.MANHATTAN);

      return builder.build();
   }

}
