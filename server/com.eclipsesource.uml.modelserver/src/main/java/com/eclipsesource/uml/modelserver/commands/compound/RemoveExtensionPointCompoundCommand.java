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
package com.eclipsesource.uml.modelserver.commands.compound;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.DirectedRelationship;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.ExtensionPoint;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Relationship;

import com.eclipsesource.uml.modelserver.commands.semantic.RemoveExtensionPointCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlNotationCommandUtil;
import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;
import com.eclipsesource.uml.modelserver.unotation.Edge;

public class RemoveExtensionPointCompoundCommand extends CompoundCommand {

   public RemoveExtensionPointCompoundCommand(final EditingDomain domain, final URI modelUri,
      final String semanticUriFragment) {

      Model umlModel = UmlSemanticCommandUtil.getModel(modelUri, domain);
      ExtensionPoint extensionPointToRemove = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment,
         ExtensionPoint.class);
      EList<Relationship> list = extensionPointToRemove.getRelationships();
      EList<DirectedRelationship> list2 = extensionPointToRemove.getTargetDirectedRelationships();

      for (Relationship r : extensionPointToRemove.getRelationships()) {
         if (r instanceof Extend) {
            Extend e = (Extend) r;
            if (UmlNotationCommandUtil.getNotationElement(modelUri, domain,
               UmlSemanticCommandUtil.getSemanticUriFragment(e), Edge.class) == null) {
               continue;
            }
            if (e.getExtensionLocations().contains(extensionPointToRemove)) {
               this.append(
                  new RemoveExtendCompoundCommand(domain, modelUri, UmlSemanticCommandUtil.getSemanticUriFragment(e)));
            }
         }
      }

      this.append(new RemoveExtensionPointCommand(domain, modelUri, semanticUriFragment));
   }
}
