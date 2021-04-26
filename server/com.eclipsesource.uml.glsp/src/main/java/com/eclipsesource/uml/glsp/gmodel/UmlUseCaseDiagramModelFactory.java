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

import org.eclipse.glsp.graph.GGraph;
import org.eclipse.uml2.uml.Actor;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Include;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.modelserver.unotation.Diagram;

public class UmlUseCaseDiagramModelFactory extends GModelFactory {

   public UmlUseCaseDiagramModelFactory(final UmlModelState modelState) {
      super(modelState);
   }

   @Override
   public GGraph create(final Diagram useCaseDiagram) {
      GGraph graph = getOrCreateRoot();

      if (useCaseDiagram.getSemanticElement().getResolvedElement() != null) {
         Model useCaseModel = (Model) useCaseDiagram.getSemanticElement().getResolvedElement();

         graph.setId(toId(useCaseModel));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()//
            .filter(Class.class::isInstance)//
            .map(Class.class::cast)//
            .map(e -> classifierNodeFactory.create(e))//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()//
            .filter(Package.class::isInstance)//
            .map(Package.class::cast)//
            .map(e -> classifierNodeFactory.create(e))//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()//
            .filter(UseCase.class::isInstance)//
            .map(UseCase.class::cast)//
            .map(e -> classifierNodeFactory.create(e))//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()//
            .filter(Actor.class::isInstance)//
            .map(Actor.class::cast)//
            .map(e -> classifierNodeFactory.create(e))//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream() //
            .filter(Association.class::isInstance)//
            .map(Association.class::cast)//
            .map(e -> relationshipEdgeFactory.createAssociationEdge(e))//
            .collect(Collectors.toList()));

         // ArrayList<PackageableElement> packagedElements = new ArrayList<>(useCaseModel.getPackagedElements());

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()
            .flatMap(pe -> pe.getRelationships().stream())
            .filter(Include.class::isInstance)
            .map(Include.class::cast)//
            .map(e -> relationshipEdgeFactory.createIncludeEdge(e))//
            .collect(Collectors.toList()));
         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()
            .flatMap(pe -> pe.getRelationships().stream())
            .filter(Extend.class::isInstance)
            .map(Extend.class::cast)//
            .map(e -> relationshipEdgeFactory.createExtendEdge(e))//
            .collect(Collectors.toList()));

         // Your previous solution delivered the generalizations twice - this lead to the duplicate element ID in the
         // graph
         // I used getSourceDirectedRelationships - but please doublecheck if this is correct

         // List<Generalization> generalizations = useCaseModel.getPackagedElements().stream()
         // .filter(el -> Actor.class.isInstance(el) || UseCase.class.isInstance(el))
         // .flatMap(pe -> pe.getSourceDirectedRelationships().stream())
         // .filter(Generalization.class::isInstance)
         // .map(Generalization.class::cast)
         // .collect(Collectors.toList());

         graph.getChildren().addAll(useCaseModel.getPackagedElements().stream()
            .filter(el -> Actor.class.isInstance(el) || UseCase.class.isInstance(el))
            .flatMap(pe -> pe.getSourceDirectedRelationships().stream())
            .filter(Generalization.class::isInstance)
            .map(Generalization.class::cast)//
            .map(e -> relationshipEdgeFactory.createGeneralizationEdge(e))//
            .collect(Collectors.toList()));

      }
      return graph;

   }

}
