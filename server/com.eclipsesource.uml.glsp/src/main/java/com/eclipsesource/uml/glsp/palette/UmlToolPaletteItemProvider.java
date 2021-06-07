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
package com.eclipsesource.uml.glsp.palette;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.glsp.server.actions.TriggerEdgeCreationAction;
import org.eclipse.glsp.server.actions.TriggerNodeCreationAction;
import org.eclipse.glsp.server.features.toolpalette.PaletteItem;
import org.eclipse.glsp.server.features.toolpalette.ToolPaletteItemProvider;
import org.eclipse.glsp.server.model.GModelState;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.google.common.collect.Lists;

public class UmlToolPaletteItemProvider implements ToolPaletteItemProvider {

   private static Logger LOGGER = Logger.getLogger(UmlToolPaletteItemProvider.class.getSimpleName());

   @Override
   public List<PaletteItem> getItems(final Map<String, String> args, final GModelState modelState) {
      LOGGER.info("Create palette");

      var diagramType = ((UmlModelState) modelState).getUmlFacade().getDiagram().getDiagramType();

      switch (diagramType) {
         case USECASE: {
            return Lists.newArrayList(usecase_classifiers(), usecase_edges());
         }
         case CLASS: {
            return Lists.newArrayList(classifiers(), relations(), features());
         }
      }
      return Lists.newArrayList(classifiers(), relations(), features());
   }

   private PaletteItem usecase_classifiers() {
      PaletteItem createPackage = node(Types.PACKAGE, "Package", "umlpackage");
      PaletteItem createComponent = node(Types.COMPONENT, "Subsystem", "umlcomponent");
      PaletteItem createActor = node(Types.ACTOR, "Actor", "umlactor");
      PaletteItem createUsecase = node(Types.USECASE, "Usecase", "umlusecase");
      PaletteItem createComment = node(Types.COMMENT, "Comment", "umlcomment");

      List<PaletteItem> classifiers = Lists.newArrayList(createPackage, createComponent, createActor, createUsecase,
         createComment);
      return PaletteItem.createPaletteGroup("uml.classifier", "Classifier", classifiers, "fa-hammer");
   }

   private PaletteItem usecase_edges() {
      PaletteItem createAssociation = edge(Types.ASSOCIATION, "Association", "umlassociation");
      PaletteItem createExtend = edge(Types.EXTEND, "Extend", "umlextend");
      PaletteItem createInclude = edge(Types.INCLUDE, "Include", "umlinclude");
      PaletteItem createGeneralization = edge(Types.GENERALIZATION, "Generalization", "umlgeneralization");
      PaletteItem createCommentEdge = edge(Types.COMMENT_EDGE, "CommentEdge", "umlassociation");

      List<PaletteItem> edges = Lists.newArrayList(createAssociation, createExtend, createInclude,
         createGeneralization, createCommentEdge);
      return PaletteItem.createPaletteGroup("uml.relation", "Relation", edges, "fa-hammer");
   }

   private PaletteItem classifiers() {
      PaletteItem createClass = node(Types.CLASS, "Class", "umlclass");

      List<PaletteItem> classifiers = Lists.newArrayList(createClass);
      return PaletteItem.createPaletteGroup("uml.classifier", "Classifier", classifiers, "fa-hammer");
   }

   private PaletteItem relations() {
      PaletteItem createAssociation = edge(Types.ASSOCIATION, "Association", "umlassociation");

      List<PaletteItem> edges = Lists.newArrayList(createAssociation);
      return PaletteItem.createPaletteGroup("uml.relation", "Relation", edges, "fa-hammer");
   }

   private PaletteItem features() {
      PaletteItem createProperty = node(Types.PROPERTY, "Property", "umlproperty");

      List<PaletteItem> features = Lists.newArrayList(createProperty);

      return PaletteItem.createPaletteGroup("uml.feature", "Feature", features, "fa-hammer");
   }

   private PaletteItem node(final String elementTypeId, final String label, final String icon) {
      return new PaletteItem(elementTypeId, label, new TriggerNodeCreationAction(elementTypeId), icon);
   }

   private PaletteItem edge(final String elementTypeId, final String label, final String icon) {
      return new PaletteItem(elementTypeId, label, new TriggerEdgeCreationAction(elementTypeId), icon);
   }
}
