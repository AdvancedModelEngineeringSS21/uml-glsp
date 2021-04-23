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

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;

import com.eclipsesource.uml.modelserver.commands.compound.RemoveExtendCompoundCommand;

public class RemoveExtendCommandContribution extends UmlCompoundCommandContribution {

   public static final String TYPE = "removeExtend";

   public static CCompoundCommand create(final String semanticUriFragment) {
      CCompoundCommand removeExtendCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      removeExtendCommand.setType(TYPE);
      removeExtendCommand.getProperties().put(SEMANTIC_URI_FRAGMENT, semanticUriFragment);
      return removeExtendCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String semanticUriFragment = command.getProperties().get(SEMANTIC_URI_FRAGMENT);
      return new RemoveExtendCompoundCommand(domain, modelUri, semanticUriFragment);
   }

}
