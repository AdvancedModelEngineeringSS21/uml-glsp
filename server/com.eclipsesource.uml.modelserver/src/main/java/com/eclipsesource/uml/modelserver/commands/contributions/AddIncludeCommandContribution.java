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

import com.eclipsesource.uml.modelserver.commands.compound.AddIncludeCompoundCommand;

public class AddIncludeCommandContribution extends UmlCompoundCommandContribution {

   public static final String TYPE = "addIncludeContribution";
   public static final String INCLUDING_USECASE_URI_FRAGMENT = "includingUsecaseUriFragment";
   public static final String INCLUDED_USECASE_URI_FRAGMENT = "includedUsecaseUriFragment";

   public static CCompoundCommand create(final String inlcudingUsecaseUri, final String inlcudedUsecaseUri) {
      CCompoundCommand inlcudedUsecaseCompundCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      inlcudedUsecaseCompundCommand.setType(TYPE);
      inlcudedUsecaseCompundCommand.getProperties().put(INCLUDING_USECASE_URI_FRAGMENT, inlcudingUsecaseUri);
      inlcudedUsecaseCompundCommand.getProperties().put(INCLUDED_USECASE_URI_FRAGMENT, inlcudedUsecaseUri);
      return inlcudedUsecaseCompundCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String includingUsecaseUri = command.getProperties().get(INCLUDING_USECASE_URI_FRAGMENT);
      String includededUsecaseUri = command.getProperties().get(INCLUDED_USECASE_URI_FRAGMENT);

      return new AddIncludeCompoundCommand(domain, modelUri, includingUsecaseUri, includededUsecaseUri);
   }

}
