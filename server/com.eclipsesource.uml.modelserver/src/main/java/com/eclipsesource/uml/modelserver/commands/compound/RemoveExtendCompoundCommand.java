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
import org.eclipse.emf.edit.domain.EditingDomain;

import com.eclipsesource.uml.modelserver.commands.notation.RemoveExtendEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveCommentEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveExtendCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlCommentEdgeRemoveUtil;

public class RemoveExtendCompoundCommand extends CompoundCommand {

   public RemoveExtendCompoundCommand(final EditingDomain domain, final URI modelUri,
      final String semanticUriFragment) {
      for (RemoveCommentEdgeCommand c : UmlCommentEdgeRemoveUtil.removeIncomingCommentEdge(modelUri, domain,
         semanticUriFragment)) {
         this.append(c);
      }

      this.append(new RemoveExtendCommand(domain, modelUri, semanticUriFragment));
      this.append(new RemoveExtendEdgeCommand(domain, modelUri, semanticUriFragment));
   }
}
