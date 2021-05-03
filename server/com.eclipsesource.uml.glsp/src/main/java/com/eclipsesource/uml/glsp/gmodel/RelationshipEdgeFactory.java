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

import org.eclipse.emf.common.util.EList;
import org.eclipse.glsp.graph.GEdge;
import org.eclipse.glsp.graph.GLabel;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.graph.builder.impl.GEdgeBuilder;
import org.eclipse.glsp.graph.builder.impl.GEdgePlacementBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Include;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Relationship;
import org.eclipse.uml2.uml.UseCase;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.util.UmlConfig.CSS;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.eclipsesource.uml.glsp.util.UmlIDUtil;
import com.eclipsesource.uml.glsp.util.UmlLabelUtil;
import com.eclipsesource.uml.modelserver.unotation.Edge;

public class RelationshipEdgeFactory extends AbstractGModelFactory<Relationship, GEdge> {

   public RelationshipEdgeFactory(final UmlModelState modelState) {
      super(modelState);
   }

   @Override
   public GEdge create(final Relationship element) {
      if (element instanceof Association) {
         return createAssociationEdge((Association) element);
      } else if (element instanceof Extend) {
         return createExtendEdge((Extend) element);
      } else if (element instanceof Include) {
         return createIncludeEdge((Include) element);
      } else if (element instanceof Generalization) {
         return createGeneralizationEdge((Generalization) element);
      }
      return null;
   }

   protected GEdge createAssociationEdge(final Association association) {
      EList<Property> memberEnds = association.getMemberEnds();
      Property source = memberEnds.get(0);
      String sourceId = toId(source);
      Property target = memberEnds.get(1);
      String targetId = toId(target);

      GEdgeBuilder builder = new GEdgeBuilder(Types.ASSOCIATION)
         .id(toId(association))
         .addCssClass(CSS.EDGE)
         .sourceId(toId(source.getType()))
         .targetId(toId(target.getType()))
         .routerKind(GConstants.RouterKind.MANHATTAN);

      // TODO: Remove lable edge in use case diagram
      GLabel sourceNameLabel = createEdgeNameLabel(source.getName(), UmlIDUtil.createLabelNameId(sourceId), 0.1d);
      builder.add(sourceNameLabel);

      GLabel sourceMultiplicityLabel = createEdgeMultiplicityLabel(UmlLabelUtil.getMultiplicity(source),
         UmlIDUtil.createLabelMultiplicityId(sourceId), 0.1d);
      builder.add(sourceMultiplicityLabel);

      GLabel targetNameLabel = createEdgeNameLabel(target.getName(), UmlIDUtil.createLabelNameId(targetId), 0.9d);
      builder.add(targetNameLabel);

      GLabel targetMultiplicityLabel = createEdgeMultiplicityLabel(UmlLabelUtil.getMultiplicity(target),
         UmlIDUtil.createLabelMultiplicityId(targetId), 0.9d);
      builder.add(targetMultiplicityLabel);

      modelState.getIndex().getNotation(association, Edge.class).ifPresent(edge -> {
         if (edge.getBendPoints() != null) {
            ArrayList<GPoint> bendPoints = new ArrayList<>();
            edge.getBendPoints().forEach(p -> bendPoints.add(GraphUtil.copy(p)));
            builder.addRoutingPoints(bendPoints);
         }
      });
      return builder.build();
   }

   // region Use Case Diagram
   // TODO: Lukas Changes made here

   protected GEdge createExtendEdge(final Extend extend) {
      UseCase source = extend.getExtension();
      String sourceId = toId(source);
      UseCase target = extend.getExtendedCase();
      String targetId = toId(target);

      GEdgeBuilder builder = new GEdgeBuilder(Types.EXTEND)
         .id(toId(extend))
         .addCssClass(CSS.EDGE)
         .addCssClass(CSS.EDGE_DOTTED)
         .addCssClass(CSS.EDGE_DIRECTED_END)
         .sourceId(sourceId)
         .targetId(targetId)
         .routerKind(GConstants.RouterKind.MANHATTAN);

      GLabel extendLable = createEdgeNameLabel("<<extends>>", UmlIDUtil.createLabelNameId(targetId), 0.5d);
      builder.add(extendLable);

      modelState.getIndex().getNotation(extend, Edge.class).ifPresent(edge -> {
         if (edge.getBendPoints() != null) {
            ArrayList<GPoint> bendPoints = new ArrayList<>();
            edge.getBendPoints().forEach(p -> bendPoints.add(GraphUtil.copy(p)));
            builder.addRoutingPoints(bendPoints);
         }
      });
      return builder.build();
   }

   protected GEdge createIncludeEdge(final Include include) {
      UseCase source = include.getIncludingCase();
      String sourceId = toId(source);
      UseCase target = include.getAddition();
      String targetId = toId(target);

      GEdgeBuilder builder = new GEdgeBuilder(Types.INCLUDE)
         .id(toId(include))
         .addCssClass(CSS.EDGE)
         .addCssClass(CSS.EDGE_DASHED)
         .addCssClass(CSS.EDGE_DIRECTED_END)
         .sourceId(sourceId)
         .targetId(targetId)
         .routerKind(GConstants.RouterKind.MANHATTAN);

      GLabel includeLabel = createEdgeNameLabel("<<includes>>", UmlIDUtil.createLabelNameId(targetId), 0.5d);
      builder.add(includeLabel);

      modelState.getIndex().getNotation(include, Edge.class).ifPresent(edge -> {
         if (edge.getBendPoints() != null) {
            ArrayList<GPoint> bendPoints = new ArrayList<>();
            edge.getBendPoints().forEach(p -> bendPoints.add(GraphUtil.copy(p)));
            builder.addRoutingPoints(bendPoints);
         }
      });
      return builder.build();
   }

   protected GEdge createGeneralizationEdge(final Generalization generalization) {

      // temporary! please check types
      Classifier source = (Classifier) generalization.eContainer();
      String sourceId = toId(source);
      Classifier target = generalization.getGeneral();
      String targetId = toId(target);

      GEdgeBuilder builder = new GEdgeBuilder(Types.GENERALIZATION)
         .id(toId(generalization))
         .addCssClass(CSS.EDGE)
         .sourceId(sourceId)
         .targetId(targetId)
         .routerKind(GConstants.RouterKind.MANHATTAN);

      modelState.getIndex().getNotation(generalization, Edge.class).ifPresent(edge -> {
         if (edge.getBendPoints() != null) {
            ArrayList<GPoint> bendPoints = new ArrayList<>();
            edge.getBendPoints().forEach(p -> bendPoints.add(GraphUtil.copy(p)));
            builder.addRoutingPoints(bendPoints);
         }
      });
      return builder.build();
      // TODO: Implement
   }

   // end region

   protected GLabel createEdgeMultiplicityLabel(final String value, final String id, final double position) {
      return createEdgeLabel(value, position, id, Types.LABEL_EDGE_MULTIPLICITY, GConstants.EdgeSide.BOTTOM);
   }

   protected GLabel createEdgeNameLabel(final String name, final String id, final double position) {
      return createEdgeLabel(name, position, id, Types.LABEL_EDGE_NAME, GConstants.EdgeSide.TOP);
   }

   protected GLabel createEdgeLabel(final String name, final double position, final String id, final String type,
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

}
