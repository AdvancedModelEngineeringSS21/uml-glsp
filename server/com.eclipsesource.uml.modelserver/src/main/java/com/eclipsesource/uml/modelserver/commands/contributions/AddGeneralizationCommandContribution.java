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

import com.eclipsesource.uml.modelserver.commands.compound.AddGeneralizationCompoundCommand;

public class AddGeneralizationCommandContribution extends UmlCompoundCommandContribution {

   public static final String TYPE = "addGeneralizationContribution";
   public static final String GENERAL_CLASSIFIER_URI_FRAGMENT = "generalClassifierUriFragment";
   public static final String SPECIFIC_CLASSIFIER_URI_FRAGMENT = "specificClassifierUriFragment";

   public static CCompoundCommand create(final String generalClassifierUri, final String specificClassifierUri) {
      CCompoundCommand extendGeneralizationCompundCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      extendGeneralizationCompundCommand.setType(TYPE);
      extendGeneralizationCompundCommand.getProperties().put(GENERAL_CLASSIFIER_URI_FRAGMENT, generalClassifierUri);
      extendGeneralizationCompundCommand.getProperties().put(SPECIFIC_CLASSIFIER_URI_FRAGMENT, specificClassifierUri);
      return extendGeneralizationCompundCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String generalClassifierUri = command.getProperties().get(GENERAL_CLASSIFIER_URI_FRAGMENT);
      String specificClassifierUri = command.getProperties().get(SPECIFIC_CLASSIFIER_URI_FRAGMENT);

      return new AddGeneralizationCompoundCommand(domain, modelUri, generalClassifierUri, specificClassifierUri);
   }
}
