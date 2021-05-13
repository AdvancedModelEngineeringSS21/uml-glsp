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

import java.util.function.Supplier;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.uml2.uml.Comment;

import com.eclipsesource.uml.modelserver.commands.notation.AddCommentShapeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.AddCommentCommand;

public class AddCommentCompoundCommand extends CompoundCommand {

   public AddCommentCompoundCommand(final EditingDomain domain, final URI modelUri, final GPoint commentPosition) {

      // Chain semantic and notation command
      AddCommentCommand command = new AddCommentCommand(domain, modelUri);
      this.append(command);
      Supplier<Comment> semanticResultSupplier = () -> command.getNewComment();
      this.append(new AddCommentShapeCommand(domain, modelUri, commentPosition, semanticResultSupplier));
   }

   /**
    * Adding Comment directly to other element
    *
    */
   public AddCommentCompoundCommand(final EditingDomain domain, final URI modelUri, final GPoint commentPosition,
      final String annotatedElementSemanticUri) {
      // Chain semantic and notation command
      AddCommentCommand command = new AddCommentCommand(domain, modelUri, annotatedElementSemanticUri);
      this.append(command);
      Supplier<Comment> semanticResultSupplier = () -> command.getNewComment();
      this.append(new AddCommentShapeCommand(domain, modelUri, commentPosition, semanticResultSupplier));
   }

}
