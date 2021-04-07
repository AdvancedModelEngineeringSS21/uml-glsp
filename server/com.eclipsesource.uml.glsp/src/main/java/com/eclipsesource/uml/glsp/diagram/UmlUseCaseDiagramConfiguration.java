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
package com.eclipsesource.uml.glsp.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GraphPackage;
import org.eclipse.glsp.server.diagram.DiagramConfiguration;
import org.eclipse.glsp.server.diagram.EdgeTypeHint;
import org.eclipse.glsp.server.diagram.ShapeTypeHint;

import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.google.common.collect.Lists;

public class UmlUseCaseDiagramConfiguration implements DiagramConfiguration {

   @Override
   public String getDiagramType() { return "usecasediagram"; }

   @Override
   public List<EdgeTypeHint> getEdgeTypeHints() {
      return Lists.newArrayList(createDefaultEdgeTypeHint(Types.ASSOCIATION));
   }

   @Override
   public EdgeTypeHint createDefaultEdgeTypeHint(final String elementId) {
      List<String> allowed = Lists.newArrayList(Types.USECASE);
      return new EdgeTypeHint(elementId, true, true, true, allowed, allowed);
   }

   @Override
   public List<ShapeTypeHint> getNodeTypeHints() {
      List<ShapeTypeHint> hints = new ArrayList<>();
      hints.add(new ShapeTypeHint(DefaultTypes.GRAPH, false, false, false, false,
         List.of(Types.CLASS, Types.USECASE, Types.ACTOR)));
      hints.add(
         new ShapeTypeHint(Types.CLASS, true, true, false, false, List.of(Types.PROPERTY, Types.USECASE, Types.ACTOR))); // TODO:
                                                                                                                         // FELIX
                                                                                                                         // check
                                                                                                                         // if
                                                                                                                         // Actor
                                                                                                                         // can
                                                                                                                         // be
                                                                                                                         // inside
                                                                                                                         // system
      hints.add(new ShapeTypeHint(Types.PROPERTY, false, true, false, true));

      hints.add(new ShapeTypeHint(Types.USECASE, true, true, false, false));
      hints.add(new ShapeTypeHint(Types.ACTOR, true, true, false, false)); // TODO: LUKAS: Check Values!

      return hints;
   }

   @Override
   public Map<String, EClass> getTypeMappings() {
      Map<String, EClass> mappings = DefaultTypes.getDefaultTypeMappings();

      // TODO: FELIX: Check used Literals
      mappings.put(Types.LABEL_NAME, GraphPackage.Literals.GLABEL);
      mappings.put(Types.LABEL_TEXT, GraphPackage.Literals.GLABEL);
      mappings.put(Types.LABEL_EDGE_NAME, GraphPackage.Literals.GLABEL);
      mappings.put(Types.LABEL_EDGE_MULTIPLICITY, GraphPackage.Literals.GLABEL);
      mappings.put(Types.COMP, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.COMP_HEADER, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.LABEL_ICON, GraphPackage.Literals.GCOMPARTMENT);

      // UML Class

      mappings.put(Types.ICON_CLASS, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.CLASS, GraphPackage.Literals.GNODE);

      // UML Property
      mappings.put(Types.PROPERTY, GraphPackage.Literals.GLABEL);
      // UML Associations
      mappings.put(Types.ASSOCIATION, GraphPackage.Literals.GEDGE);

      // UML Use Case Diagram UseCases
      mappings.put(Types.ICON_USECASE, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.USECASE, GraphPackage.Literals.GNODE);

      mappings.put(Types.ICON_ACTOR, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.ACTOR, GraphPackage.Literals.GNODE);

      return mappings;
   }

}
