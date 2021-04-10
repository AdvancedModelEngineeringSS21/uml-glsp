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

import com.eclipsesource.uml.modelserver.commands.compound.RemoveActorCompoundCommand;

public class RemoveActorCommandContribution extends UmlCompoundCommandContribution {

   public static final String TYPE = "removeActor";

   public static CCompoundCommand create(final String semanticUri) {
      CCompoundCommand removeActorCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      removeActorCommand.setType(TYPE);
      removeActorCommand.getProperties().put(SEMANTIC_URI_FRAGMENT, semanticUri);
      return removeActorCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String semanticUriFragment = command.getProperties().get(SEMANTIC_URI_FRAGMENT);
      return new RemoveActorCompoundCommand(domain, modelUri, semanticUriFragment);
   }

}
