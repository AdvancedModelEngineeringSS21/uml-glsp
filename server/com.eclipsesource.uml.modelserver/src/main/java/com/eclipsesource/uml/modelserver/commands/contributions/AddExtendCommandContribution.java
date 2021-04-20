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

import com.eclipsesource.uml.modelserver.commands.compound.AddExtendCompoundCommand;

public class AddExtendCommandContribution extends UmlCompoundCommandContribution {

   public static final String TYPE = "addExtendContribution";
   public static final String EXTENDING_USECASE_URI_FRAGMENT = "extendingUsecaseUriFragment";
   public static final String EXTENDED_USECASE_URI_FRAGMENT = "extendedUsecaseUriFragment";

   public static CCompoundCommand create(final String extendingUsecaseUri, final String extendedUsecaseUri) {
      CCompoundCommand extendUsecaseCompundCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      extendUsecaseCompundCommand.setType(TYPE);
      extendUsecaseCompundCommand.getProperties().put(EXTENDING_USECASE_URI_FRAGMENT, extendingUsecaseUri);
      extendUsecaseCompundCommand.getProperties().put(EXTENDED_USECASE_URI_FRAGMENT, extendedUsecaseUri);
      return extendUsecaseCompundCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String extendingUsecaseUri = command.getProperties().get(EXTENDING_USECASE_URI_FRAGMENT);
      String extendedUsecaseUri = command.getProperties().get(EXTENDED_USECASE_URI_FRAGMENT);

      return new AddExtendCompoundCommand(domain, modelUri, extendingUsecaseUri, extendedUsecaseUri);
   }

}
