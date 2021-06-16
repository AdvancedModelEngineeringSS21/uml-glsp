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
package com.eclipsesource.uml.modelserver.commands.semantic;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Package;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class RemoveCommentCommand extends UmlSemanticElementCommand {

   protected final String semanticUriFragment;

   public RemoveCommentCommand(final EditingDomain domain, final URI modelUri, final String semanticUriFragment) {
      super(domain, modelUri);
      this.semanticUriFragment = semanticUriFragment;
   }

   @Override
   protected void doExecute() {
      Comment commentToRemove = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment, Comment.class);
      EObject container = commentToRemove.eContainer();
      if (container instanceof Package || container instanceof Component) {
         ((Element) container).getOwnedComments().remove(commentToRemove);
      }
      umlModel.getOwnedComments().remove(commentToRemove);
   }

   // TODO: should also be triggered, when element to whcih the comment is pointing is removed

}
