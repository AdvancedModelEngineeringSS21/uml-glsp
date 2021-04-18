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

import org.apache.log4j.Logger;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.glsp.graph.GPoint;

import com.eclipsesource.uml.modelserver.commands.compound.AddActorCompoundCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlNotationCommandUtil;

/**
 * Command Contribution is responsible for registering commands on the modelserver so that the GLSP server can access
 * them.
 *
 * @author winterleitner
 *
 */
public class AddActorCommandContribution extends UmlCompoundCommandContribution {

   private static Logger LOGGER = Logger.getLogger(AddActorCommandContribution.class);

   public static final String TYPE = "addActorContribution";

   public static CCompoundCommand create(final GPoint position) {
      CCompoundCommand addActorCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      addActorCommand.setType(TYPE);
      addActorCommand.getProperties().put(UmlNotationCommandContribution.POSITION_X, String.valueOf(position.getX()));
      addActorCommand.getProperties().put(UmlNotationCommandContribution.POSITION_Y, String.valueOf(position.getY()));
      return addActorCommand;
   }

   /*
    * Adding Actor inside other element
    */
   public static CCompoundCommand create(final GPoint position, final String parentSemanticUri) {
      CCompoundCommand addActorCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      addActorCommand.setType(TYPE);
      addActorCommand.getProperties().put(UmlNotationCommandContribution.POSITION_X, String.valueOf(position.getX()));
      addActorCommand.getProperties().put(UmlNotationCommandContribution.POSITION_Y, String.valueOf(position.getY()));
      addActorCommand.getProperties().put(PARENT_SEMANTIC_URI_FRAGMENT, parentSemanticUri);
      return addActorCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      GPoint actorPosition = UmlNotationCommandUtil.getGPoint(
         command.getProperties().get(UmlNotationCommandContribution.POSITION_X),
         command.getProperties().get(UmlNotationCommandContribution.POSITION_Y));
      if (command.getProperties().containsKey(PARENT_SEMANTIC_URI_FRAGMENT)) {
         String parentUri = command.getProperties().get(PARENT_SEMANTIC_URI_FRAGMENT);
         return new AddActorCompoundCommand(domain, modelUri, actorPosition, parentUri);
      }
      return new AddActorCompoundCommand(domain, modelUri, actorPosition);
   }

}
