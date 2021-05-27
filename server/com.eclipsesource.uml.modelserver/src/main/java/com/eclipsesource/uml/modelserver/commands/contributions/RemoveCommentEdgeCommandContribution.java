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
package com.eclipsesource.uml.modelserver.commands.contributions;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;

import com.eclipsesource.uml.modelserver.commands.semantic.RemoveCommentEdgeCommand;

public class RemoveCommentEdgeCommandContribution extends UmlSemanticCommandContribution {
   public static final String TYPE = "removeCommentEdge";

   public static CCompoundCommand create(final String semanticUri) {
      CCompoundCommand removeCommentEdgeCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      removeCommentEdgeCommand.setType(TYPE);
      removeCommentEdgeCommand.getProperties().put(SEMANTIC_URI_FRAGMENT, semanticUri);
      return removeCommentEdgeCommand;
   }

   @Override
   protected Command toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String semanticUriFragment = command.getProperties().get(SEMANTIC_URI_FRAGMENT);
      return new RemoveCommentEdgeCommand(domain, modelUri, semanticUriFragment);
   }

}
