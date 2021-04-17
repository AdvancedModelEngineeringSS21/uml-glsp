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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GLabel;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Package;
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
      } else if (classifier instanceof Actor) {
         return create((Actor) classifier);
      } else if (classifier instanceof UseCase) {
         return create((UseCase) classifier);
      }

      return null;
   }

   protected void applyShapeData(final Classifier classifier, final GNodeBuilder builder) {
      modelState.getIndex().getNotation(classifier, Shape.class).ifPresent(shape -> {
         if (shape.getPosition() != null) {
            builder.position(GraphUtil.copy(shape.getPosition()));
         } else if (shape.getSize() != null) {
            builder.size(GraphUtil.copy(shape.getSize()));
         }
      });
   }

   protected GNode createClassNode(final Class umlClass) {
      GNodeBuilder classNodeBuilder = new GNodeBuilder(Types.CLASS)
         .id(toId(umlClass))
         .layout(GConstants.Layout.VBOX)
         .addCssClass(CSS.NODE);

      applyShapeData(umlClass, classNodeBuilder);

      GCompartment classHeader = buildClassHeader(umlClass);
      classNodeBuilder.add(classHeader);

      GCompartment classPropertiesCompartment = buildClassPropertiesCompartment(umlClass.getAttributes(), umlClass);
      classNodeBuilder.add(classPropertiesCompartment);

      return classNodeBuilder.build();
   }

   protected GCompartment buildClassHeader(final Class umlClass) {
      GCompartmentBuilder classHeaderBuilder = new GCompartmentBuilder(Types.COMP_HEADER)
         .layout(GConstants.Layout.HBOX)
         .id(UmlIDUtil.createHeaderId(toId(umlClass)));

      GCompartment classHeaderIcon = new GCompartmentBuilder(Types.ICON_CLASS)
         .id(UmlIDUtil.createHeaderIconId(toId(umlClass))).build();
      classHeaderBuilder.add(classHeaderIcon);

      GLabel classHeaderLabel = new GLabelBuilder(Types.LABEL_NAME)
         .id(UmlIDUtil.createHeaderLabelId(toId(umlClass)))
         .text(umlClass.getName()).build();
      classHeaderBuilder.add(classHeaderLabel);

      return classHeaderBuilder.build();
   }

   // region Use Case Diagram

   // TODO: FELIX Changes made here

   protected GNode create(final Package umlPackage) {
      GNodeBuilder b = new GNodeBuilder(Types.PACKAGE) //
         .id(toId(umlPackage)) //
         .layout(GConstants.Layout.VBOX) //
         .addCssClass(CSS.NODE)
         .add(new GCompartmentBuilder(Types.COMP_HEADER) //
            .layout("hbox") //
            .id(toId(umlPackage) + "_header").add(new GCompartmentBuilder(getType(umlPackage)) //
               .id(toId(umlPackage) + "_header_icon").build()) //
            .add(new GLabelBuilder(Types.LABEL_NAME) //
               .id(toId(umlPackage) + "_header_label").text(umlPackage.getName()) //
               .build()) //
            .build());

      modelState.getIndex().getNotation(umlPackage, Shape.class).ifPresent(shape -> {
         if (shape.getPosition() != null) {
            b.position(GraphUtil.copy(shape.getPosition()));
         } else if (shape.getSize() != null) {
            b.size(GraphUtil.copy(shape.getSize()));
         }
      });

      return b.build();
   }

   protected GNode create(final UseCase umlUseCase) {
      GNodeBuilder b = new GNodeBuilder(Types.USECASE) //
         .id(toId(umlUseCase)) //
         .layout(GConstants.Layout.VBOX) //
         .addCssClass(CSS.NODE)
         .addCssClass(CSS.ELLIPSE)
         .add(buildHeader(umlUseCase));

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

   // endregion

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

}
