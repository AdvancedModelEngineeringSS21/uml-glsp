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
import org.eclipse.uml2.uml.Extend;

import com.eclipsesource.uml.modelserver.commands.notation.AddExtendEdgeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.AddExtendCommand;

public class AddExtendCompoundCommand extends CompoundCommand {

   public AddExtendCompoundCommand(final EditingDomain domain, final URI modelUri,
      final String extendingUsecaseUri, final String extendedUsecaseUri) {

      // Chain semantic and notation command
      AddExtendCommand command = new AddExtendCommand(domain, modelUri, extendingUsecaseUri,
         extendedUsecaseUri);
      this.append(command);
      Supplier<Extend> semanticResultSupplier = () -> command.getNewExtend();
      this.append(new AddExtendEdgeCommand(domain, modelUri, semanticResultSupplier));
   }

}
