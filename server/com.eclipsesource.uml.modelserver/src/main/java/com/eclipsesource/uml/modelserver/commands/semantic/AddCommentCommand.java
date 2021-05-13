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
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.UMLFactory;

public class AddCommentCommand extends UmlSemanticElementCommand {

   protected final Comment newComment;

   public AddCommentCommand(final EditingDomain domain, final URI modelUri) {
      super(domain, modelUri);
      this.newComment = UMLFactory.eINSTANCE.createComment();
   }

   @Override
   protected void doExecute() {
      newComment.setBody("newComment");
      umlModel.getOwnedComments().add(newComment);
   }

   public Comment getNewComment() { return newComment; }

}
