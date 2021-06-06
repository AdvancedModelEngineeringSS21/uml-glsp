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

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class RemoveCommentEdgeCommand extends UmlSemanticElementCommand {

   protected final String commentSemanticUriFragment;
   protected final String otherSemanticUriFragment;

   public RemoveCommentEdgeCommand(final EditingDomain domain, final URI modelUri,
      final String commentSemanticUri, final String otherSemanticUri) {
      super(domain, modelUri);
      this.commentSemanticUriFragment = commentSemanticUri;
      this.otherSemanticUriFragment = otherSemanticUri;
   }

   @Override
   protected void doExecute() {
      Comment commentToRemove = UmlSemanticCommandUtil.getElement(umlModel, commentSemanticUriFragment,
         Comment.class);
      EObject other = UmlSemanticCommandUtil.getElement(umlModel, otherSemanticUriFragment);
      commentToRemove.getAnnotatedElements().remove(other);
   }

}
