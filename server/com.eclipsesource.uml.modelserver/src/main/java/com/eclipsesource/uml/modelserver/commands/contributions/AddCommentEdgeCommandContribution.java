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

import com.eclipsesource.uml.modelserver.commands.semantic.SetCommentAnnotatedElementCommand;

public class AddCommentEdgeCommandContribution extends UmlCompoundCommandContribution {

   public static final String TYPE = "addCommentEdge";
   public static final String SOURCE_COMMENT_URI_FRAGMENT = "sourceCommentUriFragment";
   public static final String TARGET_CLASS_URI_FRAGMENT = "targetClassUriFragment";

   public static CCompoundCommand create(final String sourceCommentUriFragment, final String targetClassUriFragment) {
      CCompoundCommand addClassCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      addClassCommand.setType(TYPE);
      addClassCommand.getProperties().put(SOURCE_COMMENT_URI_FRAGMENT, sourceCommentUriFragment);
      addClassCommand.getProperties().put(TARGET_CLASS_URI_FRAGMENT, targetClassUriFragment);
      return addClassCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String sourceCommentUriFragment = command.getProperties().get(SOURCE_COMMENT_URI_FRAGMENT);
      String targetClassUriFragment = command.getProperties().get(TARGET_CLASS_URI_FRAGMENT);

      CompoundCommand setCommentAnnotatedElementCommand = new CompoundCommand();

      setCommentAnnotatedElementCommand
         .append(
            new SetCommentAnnotatedElementCommand(domain, modelUri, sourceCommentUriFragment, targetClassUriFragment));

      return setCommentAnnotatedElementCommand;

   }

}
