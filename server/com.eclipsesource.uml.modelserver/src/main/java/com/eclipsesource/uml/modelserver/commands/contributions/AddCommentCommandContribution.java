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
import org.eclipse.glsp.graph.GPoint;

import com.eclipsesource.uml.modelserver.commands.compound.AddCommentCompoundCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlNotationCommandUtil;

public class AddCommentCommandContribution extends UmlCompoundCommandContribution {

   public static final String TYPE = "addCommentContribution";

   public static CCompoundCommand create(final GPoint position) {
      CCompoundCommand addCommentCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      addCommentCommand.setType(TYPE);
      addCommentCommand.getProperties().put(UmlNotationCommandContribution.POSITION_X, String.valueOf(position.getX()));
      addCommentCommand.getProperties().put(UmlNotationCommandContribution.POSITION_Y, String.valueOf(position.getY()));
      return addCommentCommand;
   }

   /**
    * Add a new Comment directly to annotated element
    *
    * @param position
    * @param annotatedElementSemanticUri
    * @return
    */
   public static CCompoundCommand create(final GPoint position, final String annotatedElementSemanticUri) {
      CCompoundCommand addCommentCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      addCommentCommand.setType(TYPE);
      addCommentCommand.getProperties().put(UmlNotationCommandContribution.POSITION_X, String.valueOf(position.getX()));
      addCommentCommand.getProperties().put(UmlNotationCommandContribution.POSITION_Y, String.valueOf(position.getY()));
      addCommentCommand.getProperties().put(PARENT_SEMANTIC_URI_FRAGMENT, annotatedElementSemanticUri);
      return addCommentCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      GPoint commentPosition = UmlNotationCommandUtil.getGPoint(
         command.getProperties().get(UmlNotationCommandContribution.POSITION_X),
         command.getProperties().get(UmlNotationCommandContribution.POSITION_Y));

      if (command.getProperties().containsKey(PARENT_SEMANTIC_URI_FRAGMENT)) {
         String parentUri = command.getProperties().get(PARENT_SEMANTIC_URI_FRAGMENT);
         return new AddCommentCompoundCommand(domain, modelUri, commentPosition, parentUri);
      }

      return new AddCommentCompoundCommand(domain, modelUri, commentPosition);
   }
}
