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

import com.eclipsesource.uml.modelserver.commands.notation.RemoveIncludeEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveIncludeCommand;

public class RemoveIncludeCompoundCommand extends CompoundCommand {

   public RemoveIncludeCompoundCommand(final EditingDomain domain, final URI modelUri,
      final String semanticUriFragment) {
      this.append(new RemoveIncludeCommand(domain, modelUri, semanticUriFragment));
      this.append(new RemoveIncludeEdgeCommand(domain, modelUri, semanticUriFragment));
   }

}
