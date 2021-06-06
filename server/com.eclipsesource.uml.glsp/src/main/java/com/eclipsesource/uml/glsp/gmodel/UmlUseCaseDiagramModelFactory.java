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

import java.util.stream.Collectors;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.modelserver.unotation.Diagram;

public class UmlUseCaseDiagramModelFactory extends GModelFactory {

   public UmlUseCaseDiagramModelFactory(final UmlModelState modelState) {
      super(modelState);
   }

   @Override
   public GModelElement create(final EObject semanticElement) {
      GModelElement result = null;
      if (semanticElement instanceof Model) {
         result = create(semanticElement);
      } else if (semanticElement instanceof Package) {
         result = classifierNodeFactory.create((Package) semanticElement);
      } else if (semanticElement instanceof Component) {
         result = classifierNodeFactory.create((Component) semanticElement);
      } else if (semanticElement instanceof UseCase) {
         result = classifierNodeFactory.create((UseCase) semanticElement);
      } else if (semanticElement instanceof Actor) {
         result = classifierNodeFactory.create((Actor) semanticElement);
      } else if (semanticElement instanceof Class) {
         result = classifierNodeFactory.create((Class) semanticElement);
      } else if (semanticElement instanceof Relationship) {
         result = relationshipEdgeFactory.create((Relationship) semanticElement);
      } else if (semanticElement instanceof Comment) {
         result = classifierNodeFactory.create((Comment) semanticElement);
      } else if (semanticElement instanceof NamedElement) {
         result = labelFactory.create((NamedElement) semanticElement);
      }
      if (result == null) {
         throw createFailed(semanticElement);
      }
      return result;
   }

   @Override
   public GGraph create(final Diagram useCaseDiagram) {
      GGraph graph = getOrCreateRoot();

      if (useCaseDiagram.getSemanticElement().getResolvedElement() != null) {
         Model useCaseModel = (Model) useCaseDiagram.getSemanticElement().getResolvedElement();

         graph.setId(toId(useCaseModel));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()//
            .filter(Component.class::isInstance)//
            .map(Component.class::cast)//
            .map(this::create)//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()//
            .filter(Package.class::isInstance)//
            .map(Package.class::cast)//
            .map(this::create)//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()//
            .filter(UseCase.class::isInstance)//
            .filter(us -> us.eContainer().equals(useCaseModel))
            .map(UseCase.class::cast)//
            .map(this::create)//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()//
            .filter(Actor.class::isInstance)//
            .filter(a -> a.eContainer().equals(useCaseModel))
            .map(Actor.class::cast)//
            .map(this::create)//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream() //
            .filter(Association.class::isInstance)//
            .map(Association.class::cast)//
            .map(this::create)//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getOwnedComments().stream() //
            .filter(Comment.class::isInstance)//
            .map(Comment.class::cast)//
            .map(this::create)//
            .collect(Collectors.toList()));

         // ArrayList<PackageableElement> packagedElements = new ArrayList<>(useCaseModel.getPackagedElements());

         TreeIterator iterator = useCaseModel.eAllContents();
         while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof Relationship) {
               graph.getChildren().add(create((Relationship) next));
            }
         }

      }
      return graph;

   }
}
