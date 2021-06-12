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
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.internal.impl.ComponentImpl;
import org.eclipse.uml2.uml.internal.impl.ModelImpl;
import org.eclipse.uml2.uml.internal.impl.PackageImpl;

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
      newComment.setBody("newComment");
      if (annotatedElementSemanticUri != null) {
         Element annotatedElement = UmlSemanticCommandUtil.getElement(umlModel, annotatedElementSemanticUri,
            Element.class);
         newComment.getAnnotatedElements().add(annotatedElement);
         Element container = annotatedElement;
         while (!(container instanceof ModelImpl || container instanceof PackageImpl
            || container instanceof ComponentImpl)) {
            container = (Element) annotatedElement.eContainer();
         }
         container.getOwnedComments().add(newComment);

      } else {
         umlModel.getOwnedComments().add(newComment);
      }
   }

   public Comment getNewComment() { return newComment; }

}
