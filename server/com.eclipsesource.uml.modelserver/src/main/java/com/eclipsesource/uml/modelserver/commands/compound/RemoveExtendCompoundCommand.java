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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Extend;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;

import com.eclipsesource.uml.modelserver.commands.notation.RemoveExtendEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveCommentEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveExtendCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class RemoveExtendCompoundCommand extends CompoundCommand {

   public RemoveExtendCompoundCommand(final EditingDomain domain, final URI modelUri,
      final String semanticUriFragment) {

      Model umlModel = UmlSemanticCommandUtil.getModel(modelUri, domain);
      Extend extendToRemove = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment, Extend.class);
      for (Comment c : umlModel.getOwnedComments()) {
         if (c.getAnnotatedElements().size() > 0 && UmlSemanticCommandUtil
            .getSemanticUriFragment(c.getAnnotatedElements().get(0)).equals(semanticUriFragment)) {
            this.append(
               new RemoveCommentEdgeCommand(domain, modelUri, UmlSemanticCommandUtil.getSemanticUriFragment(c)));
         }
      }

      this.append(new RemoveExtendCommand(domain, modelUri, semanticUriFragment));
      this.append(new RemoveExtendEdgeCommand(domain, modelUri, semanticUriFragment));
   }

   protected boolean isAssociationTypeUsage(final Setting setting, final EObject eObject) {
      return eObject instanceof Comment
         && eObject.eContainer() instanceof Association
         && ((Property) eObject).getAssociation() != null;
   }

}
