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

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class SetCommentBodyCommand extends UmlSemanticElementCommand {

   protected String semanticUriFragment;
   protected String newBody;

   public SetCommentBodyCommand(final EditingDomain domain, final URI modelUri, final String semanticUriFragment,
      final String newBody) {
      super(domain, modelUri);
      this.semanticUriFragment = semanticUriFragment;
      this.newBody = newBody;
   }

   @Override
   protected void doExecute() {
      Comment comment = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment, Comment.class);
      comment.setBody(newBody);
   }

}
