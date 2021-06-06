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

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class SetCommentAnnotatedElementCommand extends UmlSemanticElementCommand {

   protected String commentSemanticUriFragment;
   protected String annotatedElementSemanticUriFragement;

   public SetCommentAnnotatedElementCommand(final EditingDomain domain, final URI modelUri,
      final String semanticUriFragment,
      final String annotatedElementUriFragement) {
      super(domain, modelUri);
      this.commentSemanticUriFragment = semanticUriFragment;
      this.annotatedElementSemanticUriFragement = annotatedElementUriFragement;
   }

   @Override
   protected void doExecute() {
      Comment comment = UmlSemanticCommandUtil.getElement(umlModel, commentSemanticUriFragment, Comment.class);
      Element annotatedElement = UmlSemanticCommandUtil.getElement(umlModel, annotatedElementSemanticUriFragement,
         Element.class);
      // comment.getAnnotatedElements().clear();
      comment.getAnnotatedElements().add(annotatedElement);
   }

}
