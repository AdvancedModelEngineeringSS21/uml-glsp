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
import org.eclipse.uml2.uml.Include;

import com.eclipsesource.uml.modelserver.commands.notation.AddIncludeEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.AddIncludeCommand;

public class AddIncludeCompoundCommand extends CompoundCommand {

   public AddIncludeCompoundCommand(final EditingDomain domain, final URI modelUri,
      final String includingUsecaseUri, final String includedUsecaseUri) {

      // Chain semantic and notation command
      AddIncludeCommand command = new AddIncludeCommand(domain, modelUri, includingUsecaseUri,
         includedUsecaseUri);
      this.append(command);
      Supplier<Include> semanticResultSupplier = () -> command.getNewInclude();
      this.append(new AddIncludeEdgeCommand(domain, modelUri, semanticResultSupplier));
   }
}
