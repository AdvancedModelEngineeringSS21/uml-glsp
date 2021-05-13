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
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddCommentCommand extends UmlSemanticElementCommand {

   protected final Comment newComment;
   protected final String annotatedElementSemanticUri;

   public AddCommentCommand(final EditingDomain domain, final URI modelUri) {
      super(domain, modelUri);
      this.newComment = UMLFactory.eINSTANCE.createComment();
      this.annotatedElementSemanticUri = null;
   }

   /**
    * For adding Comments directly to an annotated element inside parent package or component
    *
    * @param domain
    * @param modelUri
    * @param annotatedElementSemanticUri
    */
   public AddCommentCommand(final EditingDomain domain, final URI modelUri, final String annotatedElementSemanticUri) {
      super(domain, modelUri);
      this.newComment = UMLFactory.eINSTANCE.createComment();
      this.annotatedElementSemanticUri = annotatedElementSemanticUri;
   }

   /**
    * Adds a new comment to the UML model.
    */
   @Override
   protected void doExecute() {
      umlModel.getOwnedComments().add(newComment);
      if (annotatedElementSemanticUri != null) {
         EObject annotatedElement = UmlSemanticCommandUtil.getElement(umlModel, annotatedElementSemanticUri);
         newComment.getAnnotatedElements().add((Element) annotatedElement); // TODO: I dont know if this cast works
      }
   }

   public Comment getNewComment() { return newComment; }

}
